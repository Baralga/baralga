/**
 * 
 */
package org.remast.baralga.gui.model.edit;

import java.util.List;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.remast.baralga.gui.actions.RedoAction;
import org.remast.baralga.gui.actions.UndoAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import com.google.common.eventbus.Subscribe;

/**
 * Edit stack for undoing and redoing edit actions. The stack observes
 * the model and keeps track of undoable and redoable events.
 * @author remast
 */
public class EditStack {

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
    private final Stack<BaralgaEvent> undoStack = new Stack<BaralgaEvent>();

    /**
     * The redoable edit events.
     */
    private final Stack<BaralgaEvent> redoStack = new Stack<BaralgaEvent>();

    /** The model. */
    private PresentationModel model;

    /**
     * Creates a new edit stack for the given model.
     * @param model the edited model to create stack for
     */
    public EditStack(final PresentationModel model) {
        this.model = model;
        this.undoAction = new UndoAction(this);
        this.redoAction = new RedoAction(this);

        updateActions();
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe public void update(final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        // Ignore our own events
        if (this == event.getSource()) {
            return;
        }

        if (event.canBeUndone()) {
            undoStack.push(event);
            updateActions();
        }
    }

    /**
     * Enable or disable actions.
     * @param event 
     */
    private void updateActions() {
        if (CollectionUtils.isNotEmpty(undoStack)) {
            undoAction.setEnabled(true);
            undoAction.setText(undoStack.peek().getUndoText());
        } else {
            undoAction.setEnabled(false);
            undoAction.resetText();
        }

        if (CollectionUtils.isNotEmpty(redoStack)) {
            redoAction.setEnabled(true);
            redoAction.setText(redoStack.peek().getRedoText());
        } else {
            redoAction.setEnabled(false);
            redoAction.resetText();
        }
    }

    /**
     * @return the undoAction
     */
    public final UndoAction getUndoAction() {
        return undoAction;
    }

    /**
     * @return the redoAction
     */
    public final RedoAction getRedoAction() {
        return redoAction;
    }

    /**
     * Undo last edit action.
     */
    public final void undo() {
        if (CollectionUtils.isEmpty(undoStack)) {
            return;
        }

        final BaralgaEvent event = undoStack.pop();
        redoStack.push(event);

        executeUndo(event);

        updateActions();
    }

    /**
     * Redo last edit action.
     */
    public final void redo() {
        if (CollectionUtils.isEmpty(redoStack)) {
            return;
        }

        final BaralgaEvent event = redoStack.pop();
        undoStack.push(event);

        executeRedo(event);

        updateActions();
    }

    /**
     * Undoes the given event.
     * @param event the event to undo
     */
    @SuppressWarnings("unchecked")
    private void executeUndo(final BaralgaEvent event) {
        if (BaralgaEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
            model.addActivities(
                    (List<ProjectActivity>) event.getData(),
                    this
            );
        } else if (BaralgaEvent.PROJECT_ACTIVITY_ADDED == event.getType()) {
            model.removeActivities(
                    (List<ProjectActivity>) event.getData(),
                    this
            );
        }
    }

    /**
     * Redoes the given event.
     * @param event the event to redo
     */
    @SuppressWarnings("unchecked")
    private void executeRedo(final BaralgaEvent event) {
        if (BaralgaEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
            model.removeActivities(
                    (List<ProjectActivity>) event.getData(), 
                    this
            );
        } else if (BaralgaEvent.PROJECT_ACTIVITY_ADDED == event.getType()) {
            model.addActivities(
                    (List<ProjectActivity>) event.getData(), 
                    this
            );
        }
    }

}
