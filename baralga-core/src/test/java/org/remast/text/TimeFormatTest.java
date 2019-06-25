/*--------------------------------------------------------------------------
--
--   @(#) Version : [$CommitID$]
--   @(#) Pfad    : [$Source$]
--
--------------------------------------------------------------------------*/
package org.remast.text;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.LocalTime;

import org.junit.Test;

/**
 * TimeFormatTest
 */
public class TimeFormatTest
{
    
    @Test
    public void parseTime() throws ParseException {
        LocalTime dateTime =  TimeFormat.parseTime("10:23");
        assertEquals(10,  dateTime.getHour());
        assertEquals(23,  dateTime.getMinute());
    }

}
