package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.model.edit.EditStack;

@SuppressWarnings("serial")
public class RedoAction extends AbstractEditAction {

    public RedoAction(final EditStack editStack) {
        super(editStack);

        putValue(NAME, Messages.getString("RedoAction.Name"));
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        redo();
    }

}
