package transcribe.core.core.diff;

@FunctionalInterface
public interface Equalizer<T>  {

    boolean equals(T left, T right);

}
