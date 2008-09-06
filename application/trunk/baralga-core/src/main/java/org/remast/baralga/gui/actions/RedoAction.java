package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.edit.EditStack;

/**
 * Redoes the last edit activity using the {@link EditStack}.
 * @author remast
 */
@SuppressWarnings("serial")
public class RedoAction extends AbstractEditAction {

    public RedoAction(final EditStack editStack) {
        super(editStack);

        putValue(NAME, Messages.getString("RedoAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("RedoAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK) );
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        redo();
    }

}
