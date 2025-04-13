package transcribe.core.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public final class TsDurations {

    private static final Pattern DAYS_PATTERN;
    private static final Pattern HOURS_PATTERN;
    private static final Pattern MINUTES_PATTERN;
    private static final Pattern SECONDS_PATTERN;
    private static final Pattern MILLIS_PATTERN;

    public static Optional<Duration> tryParse(String input) {
        if (StringUtils.isBlank(input)) {
            return Optional.empty();
        }

        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long millis = 0;
        boolean found = false;

        var dayMatcher = DAYS_PATTERN.matcher(input);
        if (dayMatcher.find()) {
            days = Long.parseLong(dayMatcher.group(1));
            found = true;
        }

        var hourMatcher = HOURS_PATTERN.matcher(input);
        if (hourMatcher.find()) {
            hours = Long.parseLong(hourMatcher.group(1));
            found = true;
        }

        var minuteMatcher = MINUTES_PATTERN.matcher(input);
        if (minuteMatcher.find()) {
            minutes = Long.parseLong(minuteMatcher.group(1));
            found = true;
        }

        var secondMatcher = SECONDS_PATTERN.matcher(input);
        if (secondMatcher.find()) {
            seconds = Long.parseLong(secondMatcher.group(1));
            found = true;
        }

        var millisMatcher = MILLIS_PATTERN.matcher(input);
        if (millisMatcher.find()) {
            millis = Long.parseLong(millisMatcher.group(1));
            found = true;
        }

        if (!found) {
            return Optional.empty();
        }

        var duration = Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds)
                .plusMillis(millis);

        return Optional.of(duration);
    }

    public static String format(Duration duration) {
        if (duration == null) {
            return TsStrings.EMPTY;
        }

        long days = duration.toDaysPart();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        var parts = new ArrayList<String>();

        if (days > 0) {
            parts.add("%dd".formatted(days));
        }
        if (hours > 0) {
            parts.add("%dh".formatted(hours));
        }
        if (minutes > 0) {
            parts.add("%dm".formatted(minutes));
        }
        if (seconds > 0) {
            parts.add("%ds".formatted(seconds));
        }
        if (millis > 0) {
            parts.add("%dms".formatted(millis));
        }
        if (parts.isEmpty()) {
            parts.add("0s");
        }

        return String.join(TsStrings.SPACE, parts);
    }

    static {
        DAYS_PATTERN = Pattern.compile("(\\d+)\\s*d(?:ay)?s?\\b", Pattern.CASE_INSENSITIVE);
        HOURS_PATTERN = Pattern.compile("(\\d+)\\s*(?:h(?:our)?s?|hrs?)\\b", Pattern.CASE_INSENSITIVE);
        MINUTES_PATTERN = Pattern.compile("(\\d+)\\s*(?:m\\b|min(?:ute)?s?\\b)", Pattern.CASE_INSENSITIVE);
        SECONDS_PATTERN = Pattern.compile("(\\d+)\\s*s(?:ec(?:ond)?)?s?\\b", Pattern.CASE_INSENSITIVE);
        MILLIS_PATTERN = Pattern.compile("(\\d+)\\s*(?:ms|millis(?:econds?)?)\\b", Pattern.CASE_INSENSITIVE);
    }

}
