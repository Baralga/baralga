package org.remast.baralga;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeFormatter {
    private static final String TIME_FORMAT = "HH:mm";
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);

    public static DateTimeFormatter timeFormatter() {
        return timeFormatter;
    }
}
