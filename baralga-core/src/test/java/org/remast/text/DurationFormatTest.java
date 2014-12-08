package org.remast.text;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

import static org.junit.Assert.assertEquals;

public class DurationFormatTest {

    private DurationFormat format = new DurationFormat();

    private UserSettings.DurationFormat initialDurationFormat;

    @Before
    public void before() {
        initialDurationFormat = UserSettings.instance().getDurationFormat();
    }

    @Test
    public void formatDurationInHoursAndMinutes() {
        UserSettings.instance().setDurationFormat(UserSettings.DurationFormat.HOURS_AND_MINUTES);
        ProjectActivity act;
        DateTime startTime = new DateTime(DateUtils.getNow());

        act = new ProjectActivity(startTime, startTime.plusMinutes(45), null);
        assertEquals("00:45", format.format(act.getDuration()));

        act = new ProjectActivity(startTime, startTime.plusMinutes(30), null);
        assertEquals("00:30", format.format(act.getDuration()));

        act = new ProjectActivity(startTime, startTime.plusHours(1).plusMinutes(30), null);
        assertEquals("01:30", format.format(act.getDuration()));

        act = new ProjectActivity(startTime, startTime.plusMinutes(20), null);
        assertEquals("00:20", format.format(act.getDuration()));

        act = new ProjectActivity(startTime, startTime.plusHours(7).plusMinutes(15), null);
        assertEquals("07:15", format.format(act.getDuration()));
    }

    @After
    public void after() {
        UserSettings.instance().setDurationFormat(initialDurationFormat);
    }

}
