package New_Tests;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.remast.util.DateUtils;

public class DateUtilsTest {

    @Test
    public void isBeforeOrEqualTime1BeforeTime2Test() {
        DateTime time1 = new DateTime(2020, 1, 1, 10, 0);
        DateTime time2 = time1.plusHours(1);
        Assert.assertTrue(DateUtils.isBeforeOrEqual(time1, time2));
    }

    @Test
    public void isBeforeOrEqualTime1EqualToTime2Test() {
        DateTime time1 = new DateTime(2020, 1, 1, 10, 0);
        DateTime time2 = new DateTime(time1);
        Assert.assertTrue(DateUtils.isBeforeOrEqual(time1, time2));
    }

    @Test
    public void isBeforeOrEqual_Time1AfterTime2Test() {
        DateTime time1 = new DateTime(2020, 1, 1, 11, 0);
        DateTime time2 = time1.minusHours(1);
        Assert.assertFalse(DateUtils.isBeforeOrEqual(time1, time2));
    }

    @Test
    public void adjustToSameDayWithoutMidnightOnNextDayTest() {
        DateTime day = new DateTime(2020, 1, 1, 0, 0);
        DateTime timeToAdjust = new DateTime(2020, 1, 2, 23, 59);
        DateTime adjusted = DateUtils.adjustToSameDay(day, timeToAdjust, false);
        Assert.assertEquals(day.withTime(23, 59, 0, 0), adjusted);
    }

    @Test
    public void adjustToSameDayWithMidnightTreatedAsNextDayTest() {
        DateTime day = new DateTime(2020, 1, 1, 0, 0);
        DateTime timeToAdjust = new DateTime(2020, 1, 1, 0, 0);
        DateTime adjusted = DateUtils.adjustToSameDay(day, timeToAdjust, true);
        Assert.assertEquals(day.plusDays(1), adjusted);
    }

    @Test
    public void adjustToSameDayWithMidnightAsStartOfCurrentDayTest() {
        DateTime day = new DateTime(2020, 1, 1, 0, 0);
        DateTime timeToAdjust = new DateTime(2020, 1, 1, 0, 0);
        DateTime adjusted = DateUtils.adjustToSameDay(day, timeToAdjust, false);
        Assert.assertEquals(day, adjusted);
    }


}