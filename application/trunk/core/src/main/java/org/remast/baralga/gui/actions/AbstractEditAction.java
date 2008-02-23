package org.remast.baralga.gui.actions;

import javax.swing.AbstractAction;

import org.remast.baralga.gui.model.edit.EditStack;

public abstract class AbstractEditAction extends AbstractAction {

    private EditStack editStack;

    public AbstractEditAction(final EditStack editStack) {
        this.editStack = editStack;
    }

    public void undo() {
        this.editStack.undo();
    }
    
    public void redo() {
        this.editStack.redo();
    }
}
