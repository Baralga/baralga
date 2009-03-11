package org.remast.baralga.model;

import java.util.Date;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

public class ProjectActivityTest extends TestCase {

    public void testCalculateDuration() {
        ProjectActivity act = new ProjectActivity(new Date(0L), new Date(1000L * 60 * 60 * 30), null);
        DateTime startTime = new DateTime(DateUtils.getNow());
        
        act = new ProjectActivity(startTime.toDate(), startTime.plusMinutes(45).toDate(), null);
        assertEquals(0.75, act.getDuration());

        act = new ProjectActivity(startTime.toDate(), startTime.plusMinutes(30).toDate(), null);
        assertEquals(0.5, act.getDuration());

        act = new ProjectActivity(startTime.toDate(), startTime.plusHours(1).plusMinutes(30).toDate(), null);
        assertEquals(1.5, act.getDuration());
    }
    
    @SuppressWarnings("deprecation")
    public void testStartAndEndOnSameDay() {
        ProjectActivity act = new ProjectActivity(new Date(99, 1, 1), new Date(99, 1, 1, 23, 0), null);
        
        assertEquals( 1, act.getStart().getDate() );
        assertEquals( 1, act.getEnd().getDate() );
        
        // when end is at 0:00h it must be on the next day
        act.setEndHourMinutes(0, 0);
        assertEquals( 2, act.getEnd().getDate() );
        
        // otherwise it must be on the same day as start
        act.setEndHourMinutes(12, 0);
        assertEquals(1, act.getEnd().getDate());
    }

    @SuppressWarnings("deprecation")
    public void testStartNotAfterEnd() {
        try {
            ProjectActivity act = new ProjectActivity(new Date(99, 1, 1, 13, 0), new Date(99, 1, 1, 12, 0), null);
            // TODO: fail
        } catch( IllegalArgumentException e) {
            // ok, expected
        }
    }
}
