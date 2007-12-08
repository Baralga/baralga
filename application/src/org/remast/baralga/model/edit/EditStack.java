/**
 * 
 */
package org.remast.baralga.model.edit;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.actions.RedoAction;
import org.remast.baralga.gui.actions.UndoAction;
import org.remast.baralga.gui.events.ProTrackEvent;

/**
 * @author remast
 */
public class EditStack implements Observer {
    
    private UndoAction undoAction;

    private RedoAction redoAction;
    
    public EditStack() {
       this.undoAction = new UndoAction();
       this.redoAction = new RedoAction();
    }

    @Override
    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

                // :INFO: Start and stop can not be undone.
                // ProTrackEvent.START
                // ProTrackEvent.STOP
                
                case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                    System.out.println("Activity Added");
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_CHANGED:
                    System.out.println("Activity Changed: " + event.getPropertyChangeEvent());
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                    System.out.println("Activity Removed");
                    break;

                case ProTrackEvent.PROJECT_CHANGED:
                    System.out.println("Changed");
                    break;

                case ProTrackEvent.PROJECT_ADDED:
                    System.out.println("Added");
                    break;

                case ProTrackEvent.PROJECT_REMOVED:
                    System.out.println("Removed");
                    break;
            }
        }
    }

    /**
     * @param undoAction the undoAction to set
     */
    public void setUndoAction(final UndoAction undoAction) {
        this.undoAction = undoAction;
    }

    /**
     * @param redoAction the redoAction to set
     */
    public void setRedoAction(final RedoAction redoAction) {
        this.redoAction = redoAction;
    }

    /**
     * @return the undoAction
     */
    public UndoAction getUndoAction() {
        return undoAction;
    }

    /**
     * @return the redoAction
     */
    public RedoAction getRedoAction() {
        return redoAction;
    }

}
