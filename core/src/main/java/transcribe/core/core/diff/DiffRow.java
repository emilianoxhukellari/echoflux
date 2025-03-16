package transcribe.core.core.diff;

import org.apache.commons.lang3.StringUtils;

public record DiffRow(DiffTag tag, String oldLine, String newLine) {

    public static DiffRow newInsert(String newLine) {
        return new DiffRow(DiffTag.INSERT, StringUtils.EMPTY, newLine);
    }

    public static DiffRow newDelete(String oldLine) {
        return new DiffRow(DiffTag.DELETE, oldLine, StringUtils.EMPTY);
    }

    public static DiffRow newChange(String oldLine, String newLine) {
        return new DiffRow(DiffTag.CHANGE, oldLine, newLine);
    }

    public static DiffRow newEqual(String line) {
        return new DiffRow(DiffTag.EQUAL, line, line);
    }

    public static DiffRow newSimilar(String oldLine, String newLine) {
        return new DiffRow(DiffTag.SIMILAR, oldLine, newLine);
    }

}
