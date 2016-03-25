package net.trippedout.cloudvisiondemo.api;

/**
 * Created by atripaldi on 3/25/16.
 */
public class LabelFeature {

    public static class LabelAnnotation {
        public final String mid;
        public final String description;
        public final float score;

        public LabelAnnotation(String mid, String description, float score) {
            this.mid = mid;
            this.description = description;
            this.score = score;
        }

        @Override
        public String toString() {
            return "LabelAnnotation{" +
                    "mid='" + mid + '\'' +
                    ", description='" + description + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

}
