package org.remast.baralga.model.filter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;
import org.remast.baralga.repository.FilterVO;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

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

    private static DateTime dateOf(String date) {
        return  DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(date);
    }
}
