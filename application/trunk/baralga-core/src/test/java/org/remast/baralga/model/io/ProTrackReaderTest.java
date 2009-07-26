package org.remast.baralga.model.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.joda.time.DateTime;
import org.junit.Test;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.ProjectActivity;

public class ProTrackReaderTest {
    
    /**
     * Tests that a 'legacy' data file can be read in correctly. I.e. a file with:
     * 
     * - an activity with end < start
     * - an activity which ends on a later day than start
     * 
     * These 'features' are not supported anymore in Baralga.
     * Nevertheless, existing data files should be read in correctly. 
     */
    @Test
    public void testProTrackReadLegacyData() throws IOException {
        InputStream in = ProTrackReaderTest.class.getResourceAsStream("/ProTrack_legacy.ptd.xml");
        assertNotNull(in);
        
        ProTrackReader reader = new ProTrackReader();
        reader.read(in);
        assertNotNull(reader.getData());
        
        ProTrack data = reader.getData();
        System.out.println(data);
        assertEquals(3, data.getProjects().size());
        assertEquals("Testing", data.getActiveProject().getTitle());
        assertFalse(data.isActive());
        DateTime startTime = new DateTime(2009, 1, 28, 19, 24, 0, 0);
        assertEquals(startTime, data.getStart());
        
        assertEquals(5, data.getActivities().size());
        
        {
            ProjectActivity activity = data.getActivities().get(0);
            assertEquals("Testing", activity.getProject().getTitle());
            DateTime activityStartTime = new DateTime(2009, 1, 28, 20, 0, 0, 0);
            DateTime activityEndTime = new DateTime(2009, 1, 28, 19, 0, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(-1.0, activity.getDuration(), 0);
            
            String description = "Activity which ends before it starts";
            assertEquals(description, activity.getDescription().trim());
        }
        
        {
            ProjectActivity activity = data.getActivities().get(1);
            assertEquals("Development", activity.getProject().getTitle());
            DateTime activityStartTime = new DateTime(2009, 1, 28, 15, 0, 0, 0);
            DateTime activityEndTime = new DateTime(2009, 1, 29, 17, 30, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(24 + 2.5, activity.getDuration(), 0);
            
            String description = "Activity which ends on next day";
            assertEquals(description, activity.getDescription().trim());
        }
        
        {
            ProjectActivity activity = data.getActivities().get(2);
            assertEquals("Bugfixing", activity.getProject().getTitle());
            DateTime activityStartTime = new DateTime(2008, 11, 25, 0, 0, 0, 0);
            DateTime activityEndTime = new DateTime(2008, 11, 29, 15, 15, 0, 0);
            assertEquals(activityStartTime, activity.getStart());
            assertEquals(activityEndTime, activity.getEnd());
            assertEquals(4 * 24 + 15.25, activity.getDuration(), 0);
            
            String description = "Activity which ends several days later";
            assertEquals(description, activity.getDescription().trim());
        }
    }
}
