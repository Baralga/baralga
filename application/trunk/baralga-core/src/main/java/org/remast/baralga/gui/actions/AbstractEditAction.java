package org.remast.baralga.gui.actions;

import javax.swing.AbstractAction;

import org.remast.baralga.gui.model.edit.EditStack;

/**
 * Base class for edit actions that can be redone and undone.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractEditAction extends AbstractAction {

    /** The stack to manage edit actions. */
    private EditStack editStack;

    /**
     * Creates an AbstractEditAction for undoing and redoing edit actions.
     * @param editStack the actions to be undone and redone
     */
    public AbstractEditAction(final EditStack editStack) {
        this.editStack = editStack;
    }

    /**
     * Undo the last edit action.
     */
    protected final void undo() {
        this.editStack.undo();
    }
    
    /**
     * Redo the last edit action.
     */
    protected final void redo() {
        this.editStack.redo();
    }
}
