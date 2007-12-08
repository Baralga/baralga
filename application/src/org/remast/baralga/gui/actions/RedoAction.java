package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.remast.baralga.Messages;

@SuppressWarnings("serial")
public class RedoAction extends AbstractAction {

    public RedoAction() {
        putValue(NAME, Messages.getString("RedoAction.Name"));
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
