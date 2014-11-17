package org.remast.text;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

@SuppressWarnings("serial")
public class DurationFormat extends NumberFormat {

    public static final NumberFormat HOUR_OR_MINUTES_FORMAT = new DecimalFormat("#00"); //$NON-NLS-1$

    public static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.00"); //$NON-NLS-1$

    @Override
    public StringBuffer format(double duration, StringBuffer toAppendTo, FieldPosition pos) {
        if (displayAsHoursAndMinutes()) {
            toAppendTo.append(formatAsHoursAndMinutes(duration));
        } else {
            toAppendTo.append(DURATION_FORMAT.format(duration));
        }
        return toAppendTo;
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        throw new RuntimeException("Not implemented.");
    }

    private boolean displayAsHoursAndMinutes() {
        return false;
    }

    private String formatAsHoursAndMinutes(final double duration) {
        final double hours = Math.floor(duration);
        final double minutes = (duration % 1) * 60;
        return HOUR_OR_MINUTES_FORMAT.format(hours) + ":" + HOUR_OR_MINUTES_FORMAT.format(minutes);
    }

}
