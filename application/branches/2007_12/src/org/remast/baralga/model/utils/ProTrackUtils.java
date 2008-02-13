package org.remast.baralga.model.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.remast.baralga.gui.Settings;
import org.remast.baralga.model.ProjectActivity;

public class ProTrackUtils {

    /**
     * Calculate the duration of the given activity in decimal hours.
     * 
     * @param activity
     *            the activity to calculate duration for
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */
    public static double calculateDuration(final ProjectActivity activity) {
        Date start = activity.getStart();
        Date end = activity.getEnd();

        final Calendar calStart = new GregorianCalendar();
        calStart.setTime(start);

        final Calendar calEnd = new GregorianCalendar();
        calEnd.setTime(end);

        long timeMilliSec = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        long timeMin = timeMilliSec / 1000 / 60;
        long hours = timeMin / 60;

        long mins = timeMin % 60;
        double minsD = Math.round(mins * (1 + 2.0 / 3.0)) / 100.0;

        return hours + minsD;
    }

    public static void checkOrCreateProTrackDir() {
        final File proTrackDir = Settings.getProTrackDirectory();
        if (!proTrackDir.exists()) {
            proTrackDir.mkdir();
        }
    }

}
