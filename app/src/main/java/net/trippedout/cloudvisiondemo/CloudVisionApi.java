package net.trippedout.cloudvisiondemo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.trippedout.cloudvisiondemo.api.ImagePropsFeature;
import net.trippedout.cloudvisiondemo.api.LabelFeature;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * (Almost) All of the POJOs needed for GSON requests and responses via Retrofit.
 *
 * See the documentation at https://cloud.google.com/vision/reference/rest/v1/images/annotate
 */
public class CloudVisionApi {

    /**
     * Main request for all Vision related APIs, typically handled by helper methods below
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

    private static final int DEFAULT_MAX_RESULTS = 5;

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
     * Base class for the responses we expect back from the Vision API service.
     */
    public static class VisionResponse {
        public final ResponseList responses;
        public final Error error;

        public VisionResponse(ResponseList responses, Error error) {
            this.responses = responses;
            this.error = error;
        }

        @Override
        public String toString() {
            return "VisionResponse{" +
                    "responses=" + responses +
                    ", error=" + error +
                    '}';
        }
    }

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

                LabelResponse labels = gson.fromJson(responses, LabelResponse.class);
                if (labels != null) {
                    Log.d(TAG, "add labels: " + labels);
                    list.add(labels);
                }

                ImagePropsResponse imageProps = gson.fromJson(responses, ImagePropsResponse.class);
                if (imageProps != null) {
                    Log.d(TAG, "add imageProps: " + imageProps);
                    list.add(imageProps);
                }
            }

            return list;
        }
    }

    /**
     * Base class that all possible feature responses extend from. It's empty but its mainly for our deserializer to
     * make our lives easier.
     */
    public static class Response {

    }

    /**
     * Base class for the ease of use in our deserializer
     */
    public static class ResponseList extends ArrayList<Response> {

    }

    /**
     * labelAnnotations as a part of a response from {@link #FEATURE_TYPE_LABEL_DETECTION}
     */
    public static class LabelResponse extends Response {
        public final List<LabelFeature.LabelAnnotation> labelAnnotations;

        public LabelResponse(List<LabelFeature.LabelAnnotation> labelAnnotations) {
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

    public static class Error {
        public final int code;
        public final String message;
        public final String status;

        public Error(int code, String message, String status) {
            this.code = code;
            this.message = message;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}
