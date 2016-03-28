package net.trippedout.cloudvisiondemo.api;

import java.util.List;

/**
 * All POJOs that are re-used between annotations
 */
public class Shared {
    public static String LIKELIHOOD_UNKNOWN         = "UNKNOWN";
    public static String LIKELIHOOD_VERY_UNLIKELY   = "VERY_UNLIKELY";
    public static String LIKELIHOOD_UNLIKELY        = "UNLIKELY";
    public static String LIKELIHOOD_POSSIBLE        = "POSSIBLE";
    public static String LIKELIHOOD_LIKELY          = "LIKELY";
    public static String LIKELIHOOD_VERY_LIKELY     = "VERY_LIKELY";

    public static class BoundingPoly {
        public final List<Vertex> vertices;

        public BoundingPoly(List<Vertex> vertices) {
            this.vertices = vertices;
        }

        @Override
        public String toString() {
            return "BoundingPoly{" +
                    "vertices=" + vertices +
                    '}';
        }
    }

    public static class Vertex {
        public final float x;
        public final float y;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static class Position extends Vertex {
        public final float z;

        public Position(float x, float y, float z) {
            super(x, y);
            this.z = z;
        }
    }
}
