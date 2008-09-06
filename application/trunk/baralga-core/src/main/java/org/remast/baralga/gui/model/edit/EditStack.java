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
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

/**
 * Edit stack for undoing and redoing edit actions. The stack observes
 * the model and keeps track of undoable and redoable events.
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
	private final Stack<BaralgaEvent> undoStack = new Stack<BaralgaEvent>();

	/**
	 * The redoable edit events.
	 */
	private final Stack<BaralgaEvent> redoStack = new Stack<BaralgaEvent>();

	/** The model. */
	private PresentationModel model;

	public EditStack(final PresentationModel model) {
		this.model = model;
		this.undoAction = new UndoAction(this);
		this.redoAction = new RedoAction(this);

		updateActions();
	}

	public void update(final Observable source, final Object eventObject) {
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
		undoAction.setEnabled(CollectionUtils.isNotEmpty(undoStack));
		redoAction.setEnabled(CollectionUtils.isNotEmpty(redoStack));
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

		final BaralgaEvent event = undoStack.pop();
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

		final BaralgaEvent event = redoStack.pop();
		undoStack.push(event);

		executeRedo(event);

		updateActions();
	}

	private void executeUndo(final BaralgaEvent event) {
		if (BaralgaEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
			model.addActivity((ProjectActivity) event.getData(), this);
		} else if (BaralgaEvent.PROJECT_ACTIVITY_ADDED == event.getType()) {
			model.removeActivity((ProjectActivity) event.getData(), this);
		}
	}

	private void executeRedo(final BaralgaEvent event) {
		if (BaralgaEvent.PROJECT_ACTIVITY_REMOVED == event.getType()) {
			model.removeActivity((ProjectActivity) event.getData(), this);
		} else if (BaralgaEvent.PROJECT_ACTIVITY_ADDED == event.getType()) {
			model.addActivity((ProjectActivity) event.getData(), this);
		}
	}

}
