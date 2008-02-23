package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.Messages;
import org.remast.baralga.gui.dialogs.AboutDialog;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AboutAction extends AbstractBaralgaAction {

    public AboutAction(final Frame owner) {
        super(owner);

        putValue(NAME, Messages.getString("AboutAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-about.png"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("AboutAction.ShortDescription")); //$NON-NLS-1$
    }

    public void actionPerformed(ActionEvent event) {
        AboutDialog aboutDialog = new AboutDialog(getOwner());
        aboutDialog.setVisible(true);
    }

}
