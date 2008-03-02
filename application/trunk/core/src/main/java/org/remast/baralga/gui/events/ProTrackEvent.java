package org.remast.baralga.gui.events;

import java.beans.PropertyChangeEvent;

/**
 * Events of Baralga.
 * @author remast
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

    /** The data of the event. */
    private Object data;

    /** A property hint of the event. */
    private PropertyChangeEvent propertyChangeEvent;

    /** The source that fired the event. */
    private Object source;

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     */
    public ProTrackEvent(final int type) {
        this.type = type;
    }

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     * @param source the source that fired the event
     */
    public ProTrackEvent(final int type, final Object source) {
        this.type = type;
        this.source = source;
    }

    public boolean canBeUndone() {
        // INFO: For now only adding / removing activities can be undone.
        return this.type == PROJECT_ACTIVITY_REMOVED || 
        this.type == PROJECT_ACTIVITY_ADDED;
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

    /**
     * @return the propertyHint
     */
    public PropertyChangeEvent getPropertyChangeEvent() {
        return propertyChangeEvent;
    }

    /**
     * @param propertyHint the propertyHint to set
     */
    public void setPropertyChangeEvent(PropertyChangeEvent propertyHint) {
        this.propertyChangeEvent = propertyHint;
    }

    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Object source) {
        this.source = source;
    }

}
