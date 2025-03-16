package transcribe.core.core.diff;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

public class SimilarityEqualizer implements Equalizer<String> {

    private static final JaroWinklerSimilarity JWS = new JaroWinklerSimilarity();
    private final double threshold;

    public SimilarityEqualizer(double threshold) {
        Validate.inclusiveBetween(0, 1, threshold, "Threshold must be between 0 and 1");

        this.threshold = threshold;
    }

    @Override
    public boolean equals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }

        var leftLower = StringUtils.lowerCase(left);
        var rightLower = StringUtils.lowerCase(right);

        return JWS.apply(leftLower, rightLower) >= threshold;
    }

}
