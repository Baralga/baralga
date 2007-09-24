package org.remast.baralga.gui.events;

/**
 * @author Jan Stamer
 */
public class ProTrackEvent {
    
    //------------------------------------------------
    // Constants for ProTrack Events
    //------------------------------------------------

    public static final int PROJECT_CHANGED = 0;

    public static final int START = 1;

    public static final int STOP = 2;

    public static final int PROJECT_ADDED = 3;

    public static final int PROJECT_REMOVED = 4;

    public static final int PROJECT_ACTIVITY_ADDED = 5;

    public static final int PROJECT_ACTIVITY_REMOVED = 6;

    public static final int PROJECT_ACTIVITY_CHANGED = 7;

    /** The type of the event. */
    final private int type;

    private Object data;

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     */
    public ProTrackEvent(final int type) {
        this.type = type;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

}
