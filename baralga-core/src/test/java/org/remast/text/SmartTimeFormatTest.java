package org.remast.text;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class SmartTimeFormatTest {

    @Test
    void parseToHourAndMinutesNormal() throws ParseException {
        int[] hoursAndMinutes = SmartTimeFormat.parseToHourAndMinutes("09:10");
        assertEquals(hoursAndMinutes[0], 9);
        assertEquals(hoursAndMinutes[1], 10);
    }

    @Test
    void parseToHourAndMinutesWithHoursOnly() throws ParseException {
        int[] hoursAndMinutes = SmartTimeFormat.parseToHourAndMinutes("9");
        assertEquals(hoursAndMinutes[0], 9);
        assertEquals(hoursAndMinutes[1], 0);
    }

    @Test
    void parseToHourAndMinutesWithDecimalMinutes() throws ParseException {
        int[] hoursAndMinutes = SmartTimeFormat.parseToHourAndMinutes("9,75");
        assertEquals(hoursAndMinutes[0], 9);
        assertEquals(hoursAndMinutes[1], 45);
    }

    @Test
    void parseToHourAndMinutesWithDecimalMinutesShort() throws ParseException {
        int[] hoursAndMinutes = SmartTimeFormat.parseToHourAndMinutes(",5");
        assertEquals(hoursAndMinutes[0], 0);
        assertEquals(hoursAndMinutes[1], 30);
    }

}