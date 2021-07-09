package org.remast.baralga.model.filter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.remast.baralga.repository.FilterVO;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    @BeforeAll
    static void before() {
        Locale.setDefault(new Locale("en", "EN"));
    }

    @Test
    void toVO() {
        // Arrange
        Filter filter = new Filter();

        // Act
        FilterVO filterVO = filter.toVO();

        // Assert
        assertEquals(filter.getTimeInterval(), filterVO.getTimeInterval());
    }

    @Test
    void initTimeIntervalWithQuarter() {
        // Arrange
        Filter filter = new Filter();
        filter.setSpanType(SpanType.Quarter);
        DateTime initDate = dateOf("2011/02/02");

        // Act
        filter.initTimeInterval(initDate);

        // Assert
        assertEquals(dateOf("2011/01/01"), filter.getTimeInterval().getStart());
        assertEquals(dateOf("2011/04/01"), filter.getTimeInterval().getEnd());
    }

    @Test
    void makeToolTipTextWithNullFilter() {
        // Arrange
        // Act
        String toolTipText = FilterUtils.makeToolTipText(null);

        // Assert
        assertEquals("", toolTipText);
    }

    @Test
    void makeToolTipTextForQuarter() {
        // Arrange
        Filter filter = new Filter();
        filter.setSpanType(SpanType.Quarter);
        DateTime initDate = dateOf("2011/02/02");
        filter.initTimeInterval(initDate);

        // Act
        String toolTipText = FilterUtils.makeToolTipText(filter);

        // Assert
        assertEquals("Jan 1, 2011 - Mar 31, 2011", toolTipText);
    }

    @Test
    void makeToolTipTextForDay() {
        // Arrange
        Filter filter = new Filter();
        filter.setSpanType(SpanType.Day);
        DateTime initDate = dateOf("2011/01/01");
        filter.initTimeInterval(initDate);

        // Act
        String toolTipText = FilterUtils.makeToolTipText(filter);

        // Assert
        assertEquals("Jan 1, 2011", toolTipText);
    }

    private static DateTime dateOf(String date) {
        return  DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(date);
    }
}
