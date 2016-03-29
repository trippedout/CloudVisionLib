package net.trippedout.cloudvisiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.camera2basic.Camera2BasicFragment;

import net.trippedout.cloudvisionlib.CloudVisionApi;
import net.trippedout.cloudvisionlib.CloudVisionService;
import net.trippedout.cloudvisionlib.ImageUtil;
import net.trippedout.cloudvisionlib.VisionCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera2BasicFragment mCameraFragment;

    private Retrofit mRetrofit;
    private CloudVisionService mVisionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mVisionService = CloudVisionApi.getCloudVisionService();

        mCameraFragment = Camera2BasicFragment.newInstance();

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Camera2BasicFragment.OnPictureTakenEvent event) {

        String encodedImage = ImageUtil.getEncodedImageData(event.getFile().getPath());

        // We can use a very basic callback wrapper to just get the data we need and handle error responses automatically
        mVisionService.getAnnotations(Secret.API_KEY, CloudVisionApi.getTestRequestAllFeatures(encodedImage))
                .enqueue(new VisionCallback(mRetrofit) {
                    @Override
                    public void onApiResponse(CloudVisionApi.VisionResponse response) {
                        CloudVisionApi.ImagePropsResponse imageProps
                                = (CloudVisionApi.ImagePropsResponse) response.getResponseByType(CloudVisionApi.FEATURE_TYPE_IMAGE_PROPERTIES);
                        Log.d(TAG, imageProps.toString());
                    }

                    @Override
                    public void onApiError(CloudVisionApi.Error error) {
                        Log.d(TAG, error.toString());
                    }
                });

        // Or of course you could handle it all yourself, like normal Retrofit2 handling:

//        mVisionService.getAnnotations(Secret.API_KEY, CloudVisionApi.getTestRequestAllFeatures(encodedImage))
//                .enqueue(new Callback<CloudVisionApi.VisionResponse>() {
//                    @Override
//                    public void onResponse(Call<CloudVisionApi.VisionResponse> call, Response<CloudVisionApi.VisionResponse> response) {
//                        if (response.isSuccessful()) {
//                            // Image props annotation seems to always return something no matter what, so lets trace it out
//                            CloudVisionApi.ImagePropsResponse imageProps
//                                    = (CloudVisionApi.ImagePropsResponse) response.body().getResponseByType(CloudVisionApi.FEATURE_TYPE_IMAGE_PROPERTIES);
//                            Log.d(TAG, imageProps.toString());
//                        } else {
//                            handleApiError(response);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<CloudVisionApi.VisionResponse> call, Throwable t) {
//                        Log.d(TAG, "failure: " + t.getMessage());
//                    }
//        });
    }

}
