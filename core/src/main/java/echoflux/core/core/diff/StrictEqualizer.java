package echoflux.core.core.diff;

import java.util.Objects;

public class StrictEqualizer implements Equalizer<String> {

    @Override
    public boolean equals(String left, String right) {
        return Objects.equals(left, right);
    }

}
