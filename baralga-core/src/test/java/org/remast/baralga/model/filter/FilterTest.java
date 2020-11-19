package org.remast.baralga.model.filter;

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
}
