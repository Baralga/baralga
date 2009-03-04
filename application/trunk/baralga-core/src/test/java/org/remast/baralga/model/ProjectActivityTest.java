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
    
    
    
}
