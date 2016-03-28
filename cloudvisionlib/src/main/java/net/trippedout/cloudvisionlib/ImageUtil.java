package net.trippedout.cloudvisionlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Helper to get the proper encoded data for use with the Cloud Vision API.
 *
 * https://cloud.google.com/vision/reference/rest/v1/images/annotate#Image
 */
public class ImageUtil {

    /**
     * Returns encoded image data for with sample size set to 2 for half the
     * data of a full sized image. Easy to transfer/process, but less results
     */
    public static String getEncodedImageData(String path) {
        // half image size just to save space on bytearray post
        return getEncodedImageData(path, 2);
    }

    public static String getEncodedImageData(String path, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        return getEncodedImageData(BitmapFactory.decodeFile(path, options));
    }

    public static String getEncodedImageData(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
