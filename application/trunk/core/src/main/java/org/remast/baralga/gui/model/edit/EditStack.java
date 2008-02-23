/**
 * 
 */
package org.remast.baralga.gui.model.edit;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.remast.baralga.gui.actions.RedoAction;
import org.remast.baralga.gui.actions.UndoAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

/**
 * @author remast
 */
public class EditStack implements Observer {

    /**
     * The action for undoing an edit activity.
     */
    private UndoAction undoAction;

    /**
     * The action for redoing an edit activity.
     */
    private RedoAction redoAction;

    /**
     * The undoable edit events.
     */
    private Stack<ProTrackEvent> undoStack = new Stack<ProTrackEvent>();

    /**
     * The redoable edit events.
     */
    private Stack<ProTrackEvent> redoStack = new Stack<ProTrackEvent>();

    /** The model. */
    private PresentationModel model;

    public EditStack(final PresentationModel model) {
        this.model = model;
        this.undoAction = new UndoAction(this);
        this.redoAction = new RedoAction(this);

        updateActions();
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;

            // Ignore our own events
            if (this == event.getSource()) {
                return;
            }

            if (event.canBeUndone()) {
                undoStack.push(event);
                updateActions();
            }

        }
    }

    /**
     * Enable or disable actions.
     * @param event 
     */
    private void updateActions() {
        undoAction.setEnabled(CollectionUtils.isNotEmpty(undoStack));
        redoAction.setEnabled(CollectionUtils.isNotEmpty(redoStack));

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

    /**
     * Undo last edit action.
     */
    public void undo() {
        if (CollectionUtils.isEmpty(undoStack)) {
            return;
        }

        final ProTrackEvent event = undoStack.pop();
        redoStack.push(event);

        executeUndo(event);

        updateActions();
    }

    /**
     * Redo last edit action.
     */
    public void redo() {
        if (CollectionUtils.isEmpty(redoStack)) {
            return;
        }

        final ProTrackEvent event = redoStack.pop();
        undoStack.push(event);

        executeRedo(event);

        updateActions();
    }

    private void executeUndo(final ProTrackEvent event) {
        if (ProTrackEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
            model.addActivity((ProjectActivity) event.getData(), this);
        }
    }

    private void executeRedo(final ProTrackEvent event) {
        if (ProTrackEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
            model.removeActivity((ProjectActivity) event.getData(), this);
        }
    }

}
