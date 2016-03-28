package net.trippedout.cloudvisionlib;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit service for cloud vision api
 */
public interface CloudVisionService {

    @Headers({
            "Content-type: application/json"
    })
    @POST("v1/images:annotate")
    Call<CloudVisionApi.VisionResponse> getAnnotations(@Query("key") String apiKey, @Body CloudVisionApi.VisionRequest request);
}
