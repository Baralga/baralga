package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.dialogs.AboutDialog;
import org.remast.baralga.gui.dialogs.SettingsDialog;
import org.remast.util.TextResourceBundle;

/**
 * Shows the about dialog.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class SettingsAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsAction.class);

    /**
     * Creates a new about action.
     * @param owner the owning frame
     */
    public SettingsAction(final Frame owner) {
        super(owner);

        putValue(NAME, textBundle.textFor("AboutAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-about.png"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("AboutAction.ShortDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display the about dialog
        final SettingsDialog settingsDialog = new SettingsDialog(getOwner());
        settingsDialog.setVisible(true);
    }

}
