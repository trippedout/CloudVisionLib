package net.trippedout.cloudvisiondemo;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.trippedout.cloudvisionlib.CloudVisionApi;
import net.trippedout.cloudvisionlib.CloudVisionService;
import net.trippedout.cloudvisionlib.ImageUtil;
import net.trippedout.cloudvisionlib.VisionCallback;
import net.trippedout.cloudvisionlib.widget.FaceFeaturesView;

/**
 * Activity that brings in an image to an ImageView, passes to
 * the Cloud Vision API and renders out point data
 */
public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ImageActivity.class.getSimpleName();

    private FaceFeaturesView mFaceFeaturesView;
    private Matrix mImageMatrix;

    private CloudVisionService mCloudVisionService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image);

        mCloudVisionService = CloudVisionApi.getCloudVisionService();

        setupViews();
    }

    private void setupViews() {
        mFaceFeaturesView = (FaceFeaturesView) findViewById(R.id.image_view);
//        mFaceFeaturesView.setShouldDrawFaceBoundingPoly(false);

        findViewById(R.id.btn_get_faces).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_get_faces) {
            getFaces();
        }
    }

    private void getFaces() {
        mCloudVisionService.getAnnotations(
                Secret.API_KEY,
                CloudVisionApi.getTestRequestAllFeatures(
                        ImageUtil.getEncodedImageData(((BitmapDrawable) mFaceFeaturesView.getDrawable()).getBitmap())
                )
        ).enqueue(new VisionCallback(CloudVisionApi.getRetrofit()) {
            @Override
            public void onApiResponse(CloudVisionApi.VisionResponse response) {
                CloudVisionApi.FaceDetectResponse faceDetectResponse = (CloudVisionApi.FaceDetectResponse) response.getResponseByType(CloudVisionApi.FEATURE_TYPE_FACE_DETECTION);
                mFaceFeaturesView.setFaceAnnotations(faceDetectResponse.faceAnnotations);
            }

            @Override
            public void onApiError(CloudVisionApi.Error error) {

            }
        });
    }
}
