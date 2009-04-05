package org.remast.baralga.gui.events;

import java.beans.PropertyChangeEvent;

/**
 * Events of Baralga.
 * @author remast
 */
public class BaralgaEvent {

    //------------------------------------------------
    // Constants for ProTrack Events
    //------------------------------------------------

    /** A project has been changed. I.e. a new project is active now. */
    public static final int PROJECT_CHANGED = 0;

    /** A project activity has been started. */
    public static final int PROJECT_ACTIVITY_STARTED = 1;

    /** A project activity has been stopped. */
    public static final int PROJECT_ACTIVITY_STOPPED = 2;

    /** A project has been added. */
    public static final int PROJECT_ADDED = 3;

    /** A project has been removed. */
    public static final int PROJECT_REMOVED = 4;

    /** A project activity has been added. */
    public static final int PROJECT_ACTIVITY_ADDED = 5;

    /** A project activity has been removed. */
    public static final int PROJECT_ACTIVITY_REMOVED = 6;

    /** The filter has been changed. */
    public static final int FILTER_CHANGED = 8;

    /** The data has changed. */
    public static final int DATA_CHANGED = 9;

    /** The start time has changed. */
    public static final int START_CHANGED = 10;

    /** The type of the event. */
    private final int type;

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
    public BaralgaEvent(final int type) {
        this.type = type;
    }

    /**
     * Constructor for a new event.
     * @param type the type of the event.
     * @param source the source that fired the event
     */
    public BaralgaEvent(final int type, final Object source) {
        this.type = type;
        this.source = source;
    }

    /**
     * Checks whether the event can be undone.
     * @return <code>true</code> if undoing the event is possible else <code>false</code>
     */
    public final boolean canBeUndone() {
        // INFO: For now only adding / removing activities can be undone.
        return this.type == PROJECT_ACTIVITY_REMOVED 
        || this.type == PROJECT_ACTIVITY_ADDED;
    }

    /**
     * Getter for the data.
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Setter for the data.
     * @param data the data to set
     */
    public void setData(final Object data) {
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
    public void setPropertyChangeEvent(final PropertyChangeEvent propertyHint) {
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
    public void setSource(final Object source) {
        this.source = source;
    }

}
