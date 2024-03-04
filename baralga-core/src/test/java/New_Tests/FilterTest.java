package New_Tests;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FilterTest {

    //Declare objects of Filter, Project and ProjectActivity class
    private Filter filter;
    private Project project;
    private ProjectActivity activity;

    @Before
    public void setUp() {
        filter = new Filter();
        project = new Project("1", "Project 1", "This is the first project");

        //Mock Project Activity object
        activity = Mockito.mock(ProjectActivity.class);
    }

    @Test
    public void matchesCriteriaActivityOutsideIntervalTest() {
        DateTime begin = DateTime.now().minusHours(2);
        DateTime ending = DateTime.now().minusHours(1);
        filter.setTimeInterval(new Interval(begin, ending));

        //Mock behaviour of Project Activity object
        Mockito.when(activity.getStart()).thenReturn(DateTime.now());
        Mockito.when(activity.getEnd()).thenReturn(DateTime.now().plusHours(1));

        //If criteria is outside the interval, it should not match
        Assert.assertFalse(filter.matchesCriteria(activity));
    }

    @Test
    public void matchesCriteriaNullTest() {
        Assert.assertFalse(filter.matchesCriteria(null));
    }


    @Test
    public void MatchesCriteriaWithinTimeIntervalTest() {
        DateTime begin = new DateTime();

        //Mock behaviour of Project Activity object
        when(activity.getStart()).thenReturn(begin);
        when(activity.getEnd()).thenReturn(begin.plusHours(1));
        when(activity.getProject()).thenReturn(project);

        //If within time interval, filter criteria should match
        assertTrue(filter.matchesCriteria(activity));
    }
}
