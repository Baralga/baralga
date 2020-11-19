package org.remast.baralga.repository;

import org.joda.time.Interval;

public class FilterVO {

    private Interval timeInterval;

    public Interval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Interval timeInterval) {
        this.timeInterval = timeInterval;
    }
}
