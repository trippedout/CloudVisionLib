package net.trippedout.cloudvisionlib;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Simple abstract interface for easy handling of responses and errors from the Cloud Vision API
 */
public abstract class VisionCallback implements Callback<CloudVisionApi.VisionResponse> {

    private final Retrofit mRetroFit;

    public VisionCallback(Retrofit retrofitInstance) {
        mRetroFit = retrofitInstance;
    }

    @Override
    public void onResponse(Call<CloudVisionApi.VisionResponse> call, Response<CloudVisionApi.VisionResponse> response) {
        if (response.isSuccessful()) {
            onApiResponse(response.body());
        } else {
            Converter<ResponseBody, CloudVisionApi.Error> converter
                    = mRetroFit.responseBodyConverter(CloudVisionApi.Error.class, new Annotation[0]);
            CloudVisionApi.Error error = new CloudVisionApi.Error();

            try {
                error = converter.convert(response.errorBody());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                onApiError(error);
            }
        }
    }

    @Override
    public void onFailure(Call<CloudVisionApi.VisionResponse> call, Throwable t) {
        // do nothing but can override if needed
    }

    public abstract void onApiResponse(CloudVisionApi.VisionResponse response);

    public abstract void onApiError(CloudVisionApi.Error error);
}
