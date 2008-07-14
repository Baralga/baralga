package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.dialogs.AboutDialog;

/**
 * Shows the about dialog.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AboutAction extends AbstractBaralgaAction {

    /**
     * Creates a new about action.
     * @param owner the owning frame
     */
    public AboutAction(final Frame owner) {
        super(owner);

        putValue(NAME, Messages.getString("AboutAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-about.png"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("AboutAction.ShortDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent event) {
        // Display the about dialog
        final AboutDialog aboutDialog = new AboutDialog(getOwner());
        aboutDialog.setVisible(true);
    }

}
