package net.atos.zac.app.klanten;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public final class KlantenUtil {
    public static final String ONBEKEND = "<onbekend>";

    private static final char NBSP = '\u00A0'; // non breaking space

    private KlantenUtil() {}

    public static String hard(final String part) {
        return part == null ? null : part.replace(' ', NBSP);
    }

    public static String hard(final String... parts) {
        return hard(Arrays.stream(parts)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(" ")));
    }

    public static String soft(final String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
    }
}
