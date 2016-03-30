package net.trippedout.cloudvisionlib;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Contains all the Request and Response classes necessary for making calls to the Cloud Vision API.
 *
 * See the documentation at https://cloud.google.com/vision/reference/rest/v1/images/annotate
 */
public class CloudVisionApi {

    private static final String BASE_URL = "https://vision.googleapis.com";

    private static Retrofit mRetrofit;

    public static CloudVisionService getCloudVisionService() {
        return getCloudVisionService(true);
    }

    public static CloudVisionService getCloudVisionService(boolean useLogging) {
        // handle logging
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (useLogging) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }
        OkHttpClient client = clientBuilder.build();

        // handle custom return type
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(CloudVisionApi.ResponseList.class, new CloudVisionApi.ResponseDeserializer())
                .create();

        mRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return mRetrofit.create(CloudVisionService.class);
    }

    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * Main request for all Vision related APIs.
     *
     * You can pass along as many requests as needed, each {@link Request} contains a single image.
     *
     * Refer to {@link #getTestRequest(String)} and {@link #getTestRequestAllFeatures(String)}
     * for implementation details.
     */
    public static class VisionRequest {
        public final List<Request> requests;

        public VisionRequest(List<Request> requests) {
            this.requests = requests;
        }

        @Override
        public String toString() {
            return "VisionRequest{" +
                    "requests=" + requests +
                    '}';
        }
    }

    /**
     * An individual request. Each Request is loaded with a single image, but can be given
     * as many {@link Feature}s as necessary.
     *
     * Refer to {@link #getTestRequest(String)} and {@link #getTestRequestAllFeatures(String)}
     * for implementation details.
     */
    public static class Request {
        public final Image image;
        public final List<Feature> features;

        public Request(Image image, List<Feature> features) {
            this.image = image;
            this.features = features;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "image=" + image +
                    ", features=" + features +
                    '}';
        }
    }

    /**
     * A single images content, which is {@link android.util.Base64} encoded data.
     * <p>
     * <code>
     *   Bitmap bitmap = BitmapFactory.decodeFile("imagePath|resId", options);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
         String content = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
     * </code>
     * </p>
     */
    public static class Image {
        public final String content;

        public Image(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "content='" + content + '\'' +
                    '}';
        }
    }

    /**
     * All of the possible feature types
     */
    public static final String FEATURE_TYPE_TYPE_UNSPECIFIED       = "TYPE_UNSPECIFIED";
    public static final String FEATURE_TYPE_FACE_DETECTION         = "FACE_DETECTION";
    public static final String FEATURE_TYPE_LANDMARK_DETECTION     = "LANDMARK_DETECTION";
    public static final String FEATURE_TYPE_LOGO_DETECTION         = "LOGO_DETECTION";
    public static final String FEATURE_TYPE_LABEL_DETECTION        = "LABEL_DETECTION";
    public static final String FEATURE_TYPE_TEXT_DETECTION         = "TEXT_DETECTION";
    public static final String FEATURE_TYPE_SAFE_SEARCH_DETECTION  = "SAFE_SEARCH_DETECTION";
    public static final String FEATURE_TYPE_IMAGE_PROPERTIES       = "IMAGE_PROPERTIES";

    private static final int DEFAULT_MAX_RESULTS = 10;

    public static class Feature {
        public final String type;
        public final int maxResults;

        public Feature(String type, int maxResults) {
            this.type = type;
            this.maxResults = maxResults;
        }

        @Override
        public String toString() {
            return "Feature{" +
                    "type='" + type + '\'' +
                    ", maxResults=" + maxResults +
                    '}';
        }
    }

    /**
     * A list with all possible features except for {@link #FEATURE_TYPE_TYPE_UNSPECIFIED}
     */
    public static final List<Feature> ALL_FEATURES = new ArrayList<>(7);
    static {
        ALL_FEATURES.add(0, new Feature(FEATURE_TYPE_FACE_DETECTION, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(1, new Feature(FEATURE_TYPE_IMAGE_PROPERTIES, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(2, new Feature(FEATURE_TYPE_LABEL_DETECTION, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(3, new Feature(FEATURE_TYPE_LANDMARK_DETECTION, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(4, new Feature(FEATURE_TYPE_LOGO_DETECTION, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(5, new Feature(FEATURE_TYPE_SAFE_SEARCH_DETECTION, DEFAULT_MAX_RESULTS));
        ALL_FEATURES.add(6, new Feature(FEATURE_TYPE_TEXT_DETECTION, DEFAULT_MAX_RESULTS));
    }


    /**
     * Returns a simple VisionRequest dealing with label detection
     */
    public static VisionRequest getTestRequest(String base64Image) {
        List<CloudVisionApi.Request> list = new ArrayList<>();
        list.add(
                new Request(
                        new Image(base64Image),
                        Arrays.asList(new CloudVisionApi.Feature(FEATURE_TYPE_LABEL_DETECTION, DEFAULT_MAX_RESULTS))
                )
        );
        return new VisionRequest(list);
    }

    public static VisionRequest getTestRequestAllFeatures(String base64Image) {
        List<CloudVisionApi.Request> list = new ArrayList<>();
        list.add(
                new Request(
                        new Image(base64Image),
                        ALL_FEATURES
                )
        );
        return new VisionRequest(list);
    }

    /**
     * Base class for the responses we expect back from the Vision API service.
     *
     * Because of a somewhat strange API design, our {@link ResponseList} only returns one object,
     * which is then further broken up into separate Response objects by key. Our {@link ResponseDeserializer}
     * handles breaking these in to the proper list, but will need updating if the API changes.
     *
     * For now, we map the responses to their proper types for easier access while using this API.
     */
    public static class VisionResponse {
        private static final String TAG = VisionResponse.class.getSimpleName();

        private ResponseList responses;

        @Override
        public String toString() {
            return "VisionResponse{" +
                    "mResponseMap=" + getResponseMap() +
                    "responses=" + responses +
                    '}';
        }

        private Map<String, Response> mResponseMap;

        private void mapResponses() {
            mResponseMap = new HashMap<>(NUMBER_OF_RESPONSE_TYPES);
            for(Response response : responses) {
                if (response instanceof LabelResponse)
                    mResponseMap.put(FEATURE_TYPE_LABEL_DETECTION, response);
                if (response instanceof LandmarkResponse)
                    mResponseMap.put(FEATURE_TYPE_LANDMARK_DETECTION, response);
                if (response instanceof LogoResponse)
                    mResponseMap.put(FEATURE_TYPE_LOGO_DETECTION, response);
                if (response instanceof TextResponse)
                    mResponseMap.put(FEATURE_TYPE_TEXT_DETECTION, response);
                if (response instanceof ImagePropsResponse)
                    mResponseMap.put(FEATURE_TYPE_IMAGE_PROPERTIES, response);
                if (response instanceof FaceDetectResponse)
                    mResponseMap.put(FEATURE_TYPE_FACE_DETECTION, response);
                if (response instanceof LabelResponse)
                    mResponseMap.put(FEATURE_TYPE_SAFE_SEARCH_DETECTION, response);
            }
        }

        private Map<String, Response> getResponseMap() {
            if(mResponseMap == null) {
                mapResponses();
            }
            return mResponseMap;
        }

        /**
         * Gets the response for the specified feature type. Pass in a
         * feature like {@link #FEATURE_TYPE_FACE_DETECTION} to see results.
         *
         *
         * @return a {@link Response} you can cast to the proper type, or null if it doesn't exist.
         */
        public Response getResponseByType(String featureType) {
            return getResponseMap().get(featureType);
        }
    }

    /**
     * Static int for the total number of response types below
     */
    private static final int NUMBER_OF_RESPONSE_TYPES = 7;

    /**
     * labelAnnotations as a part of a response from {@link #FEATURE_TYPE_LABEL_DETECTION}
     */
    public static class LabelResponse extends Response {
        public final List<Shared.EntityAnnotation> labelAnnotations;

        public LabelResponse(List<Shared.EntityAnnotation> labelAnnotations) {
            this.labelAnnotations = labelAnnotations;
        }

        @Override
        public String toString() {
            return "LabelResponse{" +
                    "labelAnnotations=" + labelAnnotations +
                    '}';
        }
    }

    /**
     * landmarkAnnotations as a part of a response from {@link #FEATURE_TYPE_LANDMARK_DETECTION}
     */
    public static class LandmarkResponse extends Response {
        public final List<Shared.EntityAnnotation> landmarkAnnotations;

        public LandmarkResponse(List<Shared.EntityAnnotation> landmarkAnnotations) {
            this.landmarkAnnotations = landmarkAnnotations;
        }

        @Override
        public String toString() {
            return "LandmarkResponse{" +
                    "landmarkAnnotations=" + landmarkAnnotations +
                    '}';
        }
    }

    /**
     * logoAnnotations as a part of a response from {@link #FEATURE_TYPE_LOGO_DETECTION}
     */
    public static class LogoResponse extends Response {
        public final List<Shared.EntityAnnotation> logoAnnotations;

        public LogoResponse(List<Shared.EntityAnnotation> logoAnnotations) {
            this.logoAnnotations = logoAnnotations;
        }

        @Override
        public String toString() {
            return "LogoResponse{" +
                    "logoAnnotations=" + logoAnnotations +
                    '}';
        }
    }

    /**
     * textAnnotations as a part of a response from {@link #FEATURE_TYPE_TEXT_DETECTION}
     */
    public static class TextResponse extends Response {
        public final List<Shared.EntityAnnotation> textAnnotations;

        public TextResponse(List<Shared.EntityAnnotation> textAnnotations) {
            this.textAnnotations = textAnnotations;
        }

        @Override
        public String toString() {
            return "TextResponse{" +
                    "textAnnotations=" + textAnnotations +
                    '}';
        }
    }

    /**
     * imagePropertiesAnnotation as part of a response from {@link #FEATURE_TYPE_IMAGE_PROPERTIES}
     */
    public static class ImagePropsResponse extends Response {
        public final ImagePropsFeature.ImagePropsAnnotation imagePropertiesAnnotation;

        public ImagePropsResponse(ImagePropsFeature.ImagePropsAnnotation imagePropertiesAnnotation) {
            this.imagePropertiesAnnotation = imagePropertiesAnnotation;
        }

        @Override
        public String toString() {
            return "ImagePropsResponse{" +
                    "imagePropertiesAnnotation=" + imagePropertiesAnnotation +
                    '}';
        }
    }

    /**
     * faceAnnotations as part of a response from {@link #FEATURE_TYPE_FACE_DETECTION}
     */
    public static class FaceDetectResponse extends Response {
        public final List<FacesFeature.FaceAnnotations> faceAnnotations;

        public FaceDetectResponse(List<FacesFeature.FaceAnnotations> faceAnnotations) {
            this.faceAnnotations = faceAnnotations;
        }

        @Override
        public String toString() {
            return "FaceDetectResponse{" +
                    "faceAnnotations=" + faceAnnotations +
                    '}';
        }
    }

    /**
     * safeSearchAnnotations as part of a response from {@link #FEATURE_TYPE_SAFE_SEARCH_DETECTION}
     */
    public static class SafeSearchResponse extends Response {
        public final SafeSearchAnnotation safeSearchAnnotation;

        public SafeSearchResponse(SafeSearchAnnotation safeSearchAnnotation) {
            this.safeSearchAnnotation = safeSearchAnnotation;
        }

        @Override
        public String toString() {
            return "SafeSearchResponse{" +
                    "safeSearchAnnotation=" + safeSearchAnnotation +
                    '}';
        }

        public String getAdultLikelihood() { return safeSearchAnnotation.adult; }
        public String getSpoofLikelihood() { return safeSearchAnnotation.spoof; }
        public String getMedicalLikelihood() { return safeSearchAnnotation.medical; }
        public String getViolenceLikelihood() { return safeSearchAnnotation.violence; }

        class SafeSearchAnnotation {
            public final String adult;
            public final String spoof;
            public final String medical;
            public final String violence;

            public SafeSearchAnnotation(String adult, String spoof, String medical, String violence) {
                this.adult = adult;
                this.spoof = spoof;
                this.medical = medical;
                this.violence = violence;
            }

            @Override
            public String toString() {
                return "SafeSearchAnnotation{" +
                        "adult='" + adult + '\'' +
                        ", spoof='" + spoof + '\'' +
                        ", medical='" + medical + '\'' +
                        ", violence='" + violence + '\'' +
                        '}';
            }
        }
    }

    /**
     * Error class for handling statuses that fail
     */
    public static class Error {
        public final Container error;

        public Error() {
            this.error = new Container(-1, null, null);
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + error.code +
                    ", message='" + error.message + '\'' +
                    ", status='" + error.status + '\'' +
                    '}';
        }

        class Container {
            public final int code;
            public final String message;
            public final String status;

            public Container(int code, String message, String status) {
                this.code = code;
                this.message = message;
                this.status = status;
            }
        }
    }


    /**
     * Base class that all possible feature responses extend from. It's empty but its mainly for our deserializer to
     * make our lives easier.
     */
    protected static class Response {

    }

    /**
     * Base class for the ease of use in our deserializer
     */
    public static class ResponseList extends ArrayList<Response> {

    }


    /**
     * The information returned by the Vision API is structured slightly awkward, so we needed to create
     * a custom Deserializer to handle this input
     */
    public static class ResponseDeserializer implements JsonDeserializer<ResponseList> {
        private static final String TAG = ResponseDeserializer.class.getSimpleName();

        @Override
        public ResponseList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ResponseList list = new ResponseList();

            JsonArray jsonArray = json.getAsJsonArray();
            Gson gson = new Gson();

            for(int i = 0; i < jsonArray.size(); i++) {
                // for some reason this is always one object even tho there are many potential results, so
                // just to future proof, gonna treat is as an array and hope for the best
                JsonElement responses = jsonArray.get(i);

                FaceDetectResponse faces = gson.fromJson(responses, FaceDetectResponse.class);
                if (faces != null && faces.faceAnnotations != null) {
                    Log.d(TAG, "add faces: " + faces);
                    list.add(faces);
                }

                LandmarkResponse landmarks = gson.fromJson(responses, LandmarkResponse.class);
                if (landmarks != null && landmarks.landmarkAnnotations != null) {
                    Log.d(TAG, "add landmarks: " + landmarks);
                    list.add(landmarks);
                }

                LogoResponse logos = gson.fromJson(responses, LogoResponse.class);
                if (logos != null && logos.logoAnnotations != null) {
                    Log.d(TAG, "add logos: " + logos);
                    list.add(logos);
                }

                LabelResponse labels = gson.fromJson(responses, LabelResponse.class);
                if (labels != null && labels.labelAnnotations != null) {
                    Log.d(TAG, "add labels: " + labels);
                    list.add(labels);
                }

                TextResponse text = gson.fromJson(responses, TextResponse.class);
                if (text != null && text.textAnnotations != null) {
                    Log.d(TAG, "add text: " + text);
                    list.add(text);
                }

                SafeSearchResponse safeSearch = gson.fromJson(responses, SafeSearchResponse.class);
                if (safeSearch != null && safeSearch.safeSearchAnnotation != null) {
                    Log.d(TAG, "add safeSearch: " + safeSearch);
                    list.add(safeSearch);
                }

                ImagePropsResponse imageProps = gson.fromJson(responses, ImagePropsResponse.class);
                if (imageProps != null && imageProps.imagePropertiesAnnotation != null) {
                    Log.d(TAG, "add imageProps: " + imageProps);
                    list.add(imageProps);
                }
            }

            return list;
        }
    }
}
