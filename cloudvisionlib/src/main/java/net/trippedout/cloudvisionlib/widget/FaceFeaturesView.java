package net.trippedout.cloudvisionlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.params.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import net.trippedout.cloudvisionlib.FacesFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple view to draw the objects returned by the {@link net.trippedout.cloudvisionlib.CloudVisionApi#FEATURE_TYPE_FACE_DETECTION} features
 */
public class FaceFeaturesView extends ImageView {
    private static final String TAG = FaceFeaturesView.class.getSimpleName();

    private float mScaleX;
    private float mScaleY;
    private float mOffsetX;
    private float mOffsetY;

    private Paint mFacesBoundingPolyPaint;
    private Paint mFacesFdBoundingPolyPaint;

    private List<FacesFeature.FaceAnnotations> mFaceAnnotations;

    private boolean mHasFaceAnnotations = false;
    private boolean mShouldDrawFaceBoundingPoly = true;
    private boolean mShouldDrawFaceFdBoundingPoly = true;
    private boolean mShouldDrawFaceLandmarks = true;

    public FaceFeaturesView(Context context) {
        super(context);
        init();
    }

    public FaceFeaturesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceFeaturesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFacesBoundingPolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFacesBoundingPolyPaint.setStyle(Paint.Style.STROKE);
        mFacesBoundingPolyPaint.setColor(Color.BLUE);
        mFacesBoundingPolyPaint.setStrokeWidth(4);

        mFacesFdBoundingPolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFacesFdBoundingPolyPaint.setStyle(Paint.Style.STROKE);
        mFacesFdBoundingPolyPaint.setColor(0xff3300aa);
        mFacesFdBoundingPolyPaint.setStrokeWidth(4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        mScaleX = f[Matrix.MSCALE_X];
        mScaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * mScaleX);
        final int actH = Math.round(origH * mScaleY);

        mOffsetX = (getWidth() - actW) * 0.5f;
        mOffsetY = (getHeight() - actH) * 0.5f;

        Log.d(TAG, "offsets["+mOffsetX+","+mOffsetY+"] : actualWidth["+actW+","+actH+"] & scales: x="+mScaleX+" y="+mScaleY);
    }

    public void setFaceAnnotations(List<FacesFeature.FaceAnnotations> faceAnnotations) {
        mFaceAnnotations = faceAnnotations;

        for(FacesFeature.FaceAnnotations face : faceAnnotations) {
            face.setScaleAndOffsets(mScaleX, mScaleY, mOffsetX, mOffsetY);
//            mFaceLandmarks.add(new LandmarkView(getContext(), face.landmarks, mScaleX, mScaleY, mOffsetX, mOffsetY));
        }

        mHasFaceAnnotations = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // don't try to draw unless we have data
        if (!mHasFaceAnnotations) return;

        for (FacesFeature.FaceAnnotations face : mFaceAnnotations) {
            if (mShouldDrawFaceBoundingPoly)
                canvas.drawPath(face.boundingPoly.getPath(), mFacesBoundingPolyPaint);

            if (mShouldDrawFaceFdBoundingPoly)
                canvas.drawPath(face.fdBoundingPoly.getPath(),mFacesFdBoundingPolyPaint);

            if (mShouldDrawFaceLandmarks)
                face.drawLandmarks(canvas);
        }
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public void setShouldDrawFaceBoundingPoly(boolean shouldDraw) {
        this.mShouldDrawFaceBoundingPoly = shouldDraw;
    }

    public void setShouldDrawFaceFdBoundingPoly(boolean shouldDraw) {
        this.mShouldDrawFaceFdBoundingPoly = shouldDraw;
    }
}
