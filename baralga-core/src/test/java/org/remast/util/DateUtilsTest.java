package org.remast.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.remast.util.DateUtils.*;

public class DateUtilsTest {

    @Test
    public void startOfQuarter() {
        assertEquals(quarterStartFor(dateOf("2011/02/02")), dateOf("2011/01/01"));
        assertEquals(quarterStartFor(dateOf("2011/01/01")), dateOf("2011/01/01"));
        assertEquals(quarterStartFor(dateOf("2011/02/02")), dateOf("2011/01/01"));
        assertEquals(quarterStartFor(dateOf("2011/04/01")), dateOf("2011/04/01"));
        assertEquals(quarterStartFor(dateOf("2011/07/01")), dateOf("2011/07/01"));
        assertEquals(quarterStartFor(dateOf("2011/12/19")), dateOf("2011/10/01"));
    }

    private static DateTime dateOf(String date) {
        return  DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(date);
    }
}
