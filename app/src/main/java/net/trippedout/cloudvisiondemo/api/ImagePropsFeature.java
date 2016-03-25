package net.trippedout.cloudvisiondemo.api;

import java.util.List;

/**
 * Created by atripaldi on 3/25/16.
 */
public class ImagePropsFeature {
    public static class ImagePropsAnnotation {
        public final ColorsArray dominantColors;

        public ImagePropsAnnotation(ColorsArray dominantColors) {
            this.dominantColors = dominantColors;
        }

        @Override
        public String toString() {
            return "ImagePropsAnnotation{" +
                    "dominantColors=" + dominantColors +
                    '}';
        }
    }

    public static class ColorsArray {
        public final List<ColorObject> colors;

        public ColorsArray(List<ColorObject> colors) {
            this.colors = colors;
        }

        @Override
        public String toString() {
            return "ColorsArray{" +
                    "colors=" + colors +
                    '}';
        }
    }

    public static class ColorObject {
        public final Color color;
        public final float score;
        public final float pixelFraction;

        public ColorObject(Color color, float score, float pixelFraction) {
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
