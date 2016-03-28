package net.trippedout.cloudvisiondemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.android.camera2basic.Camera2BasicFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera2BasicFragment mCameraFragment;

    private CloudVisionService mVisionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setupRetrofit();

        mCameraFragment = Camera2BasicFragment.newInstance();

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        }
    }

    private void setupRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CloudVisionApi.ResponseList.class, new CloudVisionApi.ResponseDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://vision.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mVisionService = retrofit.create(CloudVisionService.class);
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

        // half image size just to save space on bytearray post
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(event.getFile().getPath(), options);
        Log.d(TAG, "bitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        mVisionService.getAnnotations(Secret.API_KEY, CloudVisionApi.getTestRequestAllFeatures(encodedImage))
                .enqueue(new Callback<CloudVisionApi.VisionResponse>() {
                    @Override
                    public void onResponse(Call<CloudVisionApi.VisionResponse> call, Response<CloudVisionApi.VisionResponse> response) {
                        Log.d(TAG, response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<CloudVisionApi.VisionResponse> call, Throwable t) {
                        Log.d(TAG, "failure: " + t.getMessage());
                    }
        });
    }
}
