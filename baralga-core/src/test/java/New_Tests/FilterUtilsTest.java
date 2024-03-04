package New_Tests;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.FilterUtils;
import org.remast.baralga.model.filter.SpanType;

public class FilterUtilsTest {

    //Create new object of filter class
    private Filter filter;

    @Before
    public void setUp() {
        filter = new Filter();
    }

    @Test
    public void moveToNextIntervalDayTest() {

        filter.setSpanType(SpanType.Day);
        DateTime begin = new DateTime().withTimeAtStartOfDay();
        Interval expected_begin = new Interval(begin.plusDays(1), begin.plusDays(2));
        filter.setTimeInterval(new Interval(begin, begin.plusDays(1)));
        FilterUtils.moveToNextInterval(filter);
        Assert.assertEquals(expected_begin, filter.getTimeInterval());
    }

    @Test
    public void moveToNextIntervalWeekTest() {
        filter.setSpanType(SpanType.Week);
        DateTime begin = new DateTime().withDayOfWeek(1).withTimeAtStartOfDay();
        Interval expected_begin = new Interval(begin.plusWeeks(1), begin.plusWeeks(2));
        filter.setTimeInterval(new Interval(begin, begin.plusWeeks(1)));

        FilterUtils.moveToNextInterval(filter);

        Assert.assertEquals(expected_begin, filter.getTimeInterval());
    }

    @Test
    public void moveToNextIntervalMonthTest() {
        filter.setSpanType(SpanType.Month);
        DateTime initialStart = new DateTime().withDayOfMonth(1).withTimeAtStartOfDay();
        Interval expected = new Interval(initialStart.plusMonths(1), initialStart.plusMonths(2));
        filter.setTimeInterval(new Interval(initialStart, initialStart.plusMonths(1)));
        FilterUtils.moveToNextInterval(filter);
        Assert.assertEquals(expected, filter.getTimeInterval());
    }

    @Test
    public void moveToNextIntervalQuarterTest() {
        filter.setSpanType(SpanType.Quarter);
        DateTime initialStart = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay(); // Assuming this is the start of a quarter
        Interval expected = new Interval(initialStart.plusMonths(3), initialStart.plusMonths(6));
        filter.setTimeInterval(new Interval(initialStart, initialStart.plusMonths(3)));
        FilterUtils.moveToNextInterval(filter);
        Assert.assertEquals(expected, filter.getTimeInterval());
    }

    @Test
    public void moveToNextIntervalYearTest() {
        filter.setSpanType(SpanType.Year);
        DateTime initialStart = new DateTime().withDayOfYear(1).withTimeAtStartOfDay();
        Interval expected = new Interval(initialStart.plusYears(1), initialStart.plusYears(2));
        filter.setTimeInterval(new Interval(initialStart, initialStart.plusYears(1)));
        FilterUtils.moveToNextInterval(filter);
        Assert.assertEquals(expected, filter.getTimeInterval());
    }

    @Test
    public void moveToNextIntervalNullTest() {

        //If null is passed and no exception is thrown, test passes
        FilterUtils.moveToNextInterval(null);

    }

}
