package org.remast.baralga.gui.model;


/**
 * Exception for an illegal state of a project activity. For example
 * when an activity is not running but is stopped.
 * @author remast
 *
 */
@SuppressWarnings("serial")
public class ProjectActivityStateException extends Exception {

    /**
     * Creates a new exception with the given message.
     * @param message the exception message
     */
    public ProjectActivityStateException(final String message) {
        super(message);
    }
}
