package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.Messages;
import org.remast.baralga.model.edit.EditStack;

@SuppressWarnings("serial")
public class RedoAction extends AbstractEditAction {

    public RedoAction(final EditStack editStack) {
        super(editStack);

        putValue(NAME, Messages.getString("RedoAction.Name"));
        putValue(SHORT_DESCRIPTION, Messages.getString("RedoAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK) );
    }

    public void actionPerformed(ActionEvent e) {
        redo();
    }

}
