/*--------------------------------------------------------------------------
--
--   @(#) Version : [$CommitID$]
--   @(#) Pfad    : [$Source$]
--
--------------------------------------------------------------------------*/
package org.remast.util;

import java.time.LocalDateTime;

/**
 * Interval
 */
public class Interval {
    
    private LocalDateTime start;

    private LocalDateTime end;
    
    public Interval(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    
    public LocalDateTime getStart() {
        return start;
    }
    
    public LocalDateTime getEnd() {
        return end;
    }

    public boolean contains(LocalDateTime dateTime) {
        return (start.isBefore(dateTime) || start.isEqual(dateTime)) && (end.isAfter(dateTime) || end.isEqual(dateTime));
    }

}
