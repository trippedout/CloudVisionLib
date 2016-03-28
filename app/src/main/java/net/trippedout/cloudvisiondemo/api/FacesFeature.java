package net.trippedout.cloudvisiondemo.api;

import java.util.List;

/**
 * Created by atripaldi on 3/28/16.
 */
public class FacesFeature {
    public static class FaceAnnotations {
        public final Shared.BoundingPoly boundingPoly;
        public final Shared.BoundingPoly fdBoundingPoly;
        public final List<Landmark> landmarks;
        public final float rollAngle;
        public final float panAngle;
        public final float tiltAngle;
        public final float detectionConfidence;
        public final float landmarkingConfidence;
        public final String joyLikelihood;
        public final String sorrowLikelihood;
        public final String angerLikelihood;
        public final String surpriseLikelihood;
        public final String underExposedLikelihood;
        public final String blurredLikelihood;
        public final String headwearLikelihood;

        public FaceAnnotations(Shared.BoundingPoly boundingPoly, Shared.BoundingPoly fdBoundingPoly, List<Landmark> landmarks,
                               float rollAngle, float panAngle, float tiltAngle, float detectionConfidence, float landmarkingConfidence,
                               String joyLikelihood, String sorrowLikelihood, String angerLikelihood,
                               String surpriseLikelihood, String underExposedLikelihood,
                               String blurredLikelihood, String headwearLikelihood) {
            this.boundingPoly = boundingPoly;
            this.fdBoundingPoly = fdBoundingPoly;
            this.landmarks = landmarks;
            this.rollAngle = rollAngle;
            this.panAngle = panAngle;
            this.tiltAngle = tiltAngle;
            this.detectionConfidence = detectionConfidence;
            this.landmarkingConfidence = landmarkingConfidence;
            this.joyLikelihood = joyLikelihood;
            this.sorrowLikelihood = sorrowLikelihood;
            this.angerLikelihood = angerLikelihood;
            this.surpriseLikelihood = surpriseLikelihood;
            this.underExposedLikelihood = underExposedLikelihood;
            this.blurredLikelihood = blurredLikelihood;
            this.headwearLikelihood = headwearLikelihood;
        }

        @Override
        public String toString() {
            return "FaceAnnotations{" +
                    "boundingPoly=" + boundingPoly +
                    ", fdBoundingPoly=" + fdBoundingPoly +
                    ", landmarks=" + landmarks +
                    ", rollAngle=" + rollAngle +
                    ", panAngle=" + panAngle +
                    ", tiltAngle=" + tiltAngle +
                    ", detectionConfidence=" + detectionConfidence +
                    ", landmarkingConfidence=" + landmarkingConfidence +
                    ", joyLikelihood='" + joyLikelihood + '\'' +
                    ", sorrowLikelihood='" + sorrowLikelihood + '\'' +
                    ", angerLikelihood='" + angerLikelihood + '\'' +
                    ", surpriseLikelihood='" + surpriseLikelihood + '\'' +
                    ", underExposedLikelihood='" + underExposedLikelihood + '\'' +
                    ", blurredLikelihood='" + blurredLikelihood + '\'' +
                    ", headwearLikelihood='" + headwearLikelihood + '\'' +
                    '}';
        }
    }


    public static class Landmark {
        public final String type;
        public final Shared.Position position;

        public Landmark(String type, Shared.Position position) {
            this.type = type;
            this.position = position;
        }

        @Override
        public String toString() {
            return "Landmark{" +
                    "type='" + type + '\'' +
                    ", position=" + position +
                    '}';
        }
    }
}
