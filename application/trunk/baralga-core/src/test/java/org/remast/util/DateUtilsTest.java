package org.remast.util;

import java.text.ParseException;

import junit.framework.TestCase;

public class DateUtilsTest extends TestCase {
    
    public void testParseDateSmart() throws ParseException {
        assertNull(DateUtils.parseTimeSmart(null));
    }

}
