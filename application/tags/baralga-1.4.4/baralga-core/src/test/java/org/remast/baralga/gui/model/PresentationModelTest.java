package org.remast.baralga.gui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

/**
 * Tests for the presentation model.
 * @author remast
 * @see PresentationModel
 */
public class PresentationModelTest {

    /** The model under test. */
    private PresentationModel model = new PresentationModel();

    /** First test project. */
    private Project project1 = new Project(1, "Project1", "Project 1");

    /** Second test project. */
    private Project project2 = new Project(2, "Project2", "Project 2");

    @Before
    public void setUp() throws Exception {
        model.addProject(project1, this);
        model.addProject(project2, this);
    }

    /**
     * Test for an activity that goes on until after midnight.
     * @throws ProjectActivityStateException should never be thrown if test is ok
     * @see Issue <a href="http://baralga.origo.ethz.ch/node/87">#17</a>
     */
    @Test
    public void testAcitivityOverMidnight() throws ProjectActivityStateException {
        final Date nowTmp = DateUtils.getNow();

        final Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.setTime(nowTmp);
        yesterdayCal.set(Calendar.MINUTE, yesterdayCal.get(Calendar.MINUTE) - 5);
        yesterdayCal.set(Calendar.DAY_OF_MONTH, yesterdayCal.get(Calendar.DAY_OF_MONTH) -1);

        final Date yesterday = yesterdayCal.getTime();

        DateTime dateTmp = new DateTime(yesterday);
        dateTmp = dateTmp.plusDays(1);
        final Date midnight = dateTmp.toDateMidnight().toDate();

        // Set active project
        model.changeProject(project1);

        // Start activity on yesterday
        model.start(new DateTime(yesterday));

        // End activity today
        final Date now = DateUtils.getNow();
        model.stop();

        // Verify outcome
        assertEquals(2, model.getActivitiesList().size());

        for (ProjectActivity activity : model.getActivitiesList()) {
            assertEquals(project1, activity.getProject());
        }

        final ProjectActivity todaysActivity = model.getActivitiesList().get(0);
        final ProjectActivity yesterdaysActivity = model.getActivitiesList().get(1);

        // 1. Check yesterdays activity
        assertEquals(yesterday, yesterdaysActivity.getStart().toDate());
        assertEquals(midnight, yesterdaysActivity.getEnd().toDate());

        // 2. Check today activity
        assertEquals(midnight, todaysActivity.getStart().toDate());
        assertEquals(now, todaysActivity.getEnd().toDate());
    }

    /**
     * Test for changing the active project.
     * @see PresentationModel#changeProject(Project)
     */
    @Test
    public void testChangeProject() {
        model.setDirty(false);
        
        model.changeProject(project1);
        assertEquals(project1, model.getSelectedProject());
        assertEquals(true, model.isDirty());

        model.changeProject(project2);
        assertEquals(project2, model.getSelectedProject());

        model.changeProject(null);
        assertEquals(null, model.getSelectedProject());
    }

    @Test
    public void testExceptionOnDoubleStart() throws ProjectActivityStateException {
        model.setDirty(false);
        model.changeProject(project1);
        model.start();
        
        assertTrue(model.isActive());
        assertTrue(model.isDirty());
        
        try {
            model.start();
            fail("ProjectActivityStateException expected");
        } catch(ProjectActivityStateException e) {
            // ok, expected
        }
    }
}
