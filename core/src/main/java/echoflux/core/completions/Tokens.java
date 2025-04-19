package echoflux.core.completions;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record Tokens(@Min(0) long in, @Min(0) long out) {

    public static Tokens empty() {
        return new Tokens(0, 0);
    }

    public static Tokens add(Tokens t1, Tokens t2) {
        return new Tokens(t1.in() + t2.in(), t1.out() + t2.out());
    }

}
