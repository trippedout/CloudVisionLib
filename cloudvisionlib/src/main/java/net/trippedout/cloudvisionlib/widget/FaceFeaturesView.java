package net.trippedout.cloudvisionlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
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

    private float mScaleX;
    private float mScaleY;

    private Paint mFacesBoundingPolyPaint;
    private Paint mFacesFdBoundingPolyPaint;

    private ArrayList<Path> mFaceBoundingBoxesPaths;
    private ArrayList<Path> mFaceFdBoundingBoxesPaths;

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

        Log.d("DBG", "["+origW+","+origH+"] -> ["+actW+","+actH+"] & scales: x="+mScaleX+" y="+mScaleY);
    }

    public void setFaceAnnotations(List<FacesFeature.FaceAnnotations> faceAnnotations) {
        mFaceBoundingBoxesPaths = new ArrayList<>();
        mFaceFdBoundingBoxesPaths = new ArrayList<>();

        for(FacesFeature.FaceAnnotations face : faceAnnotations) {
            mFaceBoundingBoxesPaths.add(face.boundingPoly.getPath(mScaleX, mScaleY));
            mFaceFdBoundingBoxesPaths.add(face.fdBoundingPoly.getPath(mScaleX, mScaleY));
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mFaceBoundingBoxesPaths != null) {
            for(Path boundingFace : mFaceBoundingBoxesPaths) {
                canvas.drawPath(boundingFace, mFacesBoundingPolyPaint);
            }

            for(Path boundingFdFace : mFaceFdBoundingBoxesPaths) {
                canvas.drawPath(boundingFdFace, mFacesFdBoundingPolyPaint);
            }
        }
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

}
