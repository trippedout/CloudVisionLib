package net.trippedout.cloudvisiondemo.api;

import java.util.List;

/**
 * https://cloud.google.com/vision/reference/rest/v1/images/annotate#ImageProperties
 */
public class ImagePropsFeature {
    public static class ImagePropsAnnotation {
        public final DominantColorsAnnotation dominantColors;

        public ImagePropsAnnotation(DominantColorsAnnotation dominantColors) {
            this.dominantColors = dominantColors;
        }

        @Override
        public String toString() {
            return "ImagePropsAnnotation{" +
                    "dominantColors=" + dominantColors +
                    '}';
        }
    }

    public static class DominantColorsAnnotation {
        public final List<ColorInfo> colors;

        public DominantColorsAnnotation(List<ColorInfo> colors) {
            this.colors = colors;
        }

        @Override
        public String toString() {
            return "ColorsArray{" +
                    "colors=" + colors +
                    '}';
        }
    }

    public static class ColorInfo {
        public final Color color;
        public final float score;
        public final float pixelFraction;

        public ColorInfo(Color color, float score, float pixelFraction) {
            this.color = color;
            this.score = score;
            this.pixelFraction = pixelFraction;
        }

        @Override
        public String toString() {
            return "ColorObject{" +
                    "color=" + color +
                    ", score=" + score +
                    ", pixelFraction=" + pixelFraction +
                    '}';
        }
    }

    public static class Color {
        public final int red;
        public final int blue;
        public final int green;

        public Color(int red, int blue, int green) {
            this.red = red;
            this.blue = blue;
            this.green = green;
        }

        @Override
        public String toString() {
            return "Color{" +
                    "red=" + red +
                    ", blue=" + blue +
                    ", green=" + green +
                    '}';
        }
    }

}
