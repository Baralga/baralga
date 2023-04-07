package org.remast.baralga;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeFormatterFactory {

    public static final String DAY_FORMAT = "EE, MMM d";
    private static final String TIME_FORMAT = "HH:mm";


    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
    private static final DateFormat dayFormatter = new SimpleDateFormat(DAY_FORMAT, Locale.getDefault());

    public static DateTimeFormatter timeFormatter() {
        return timeFormatter;
    }

    public static DateFormat dayFormatter() {
        return dayFormatter;
    }
}
