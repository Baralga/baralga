package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.dialogs.SettingsDialog;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.TextResourceBundle;

/**
 * Shows the settings dialog of the application.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class SettingsAction extends AbstractBaralgaAction {
    private final PresentationModel model;

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsAction.class);

    /**
     * Creates a new settings action.
     * @param owner the owning frame
     */
    public SettingsAction(final Frame owner, final PresentationModel model) {
        super(owner);
        this.model=null;

        putValue(NAME, textBundle.textFor("SettingsAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/stock_folder-properties.png"))); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("SettingsAction.ShortDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display the settings dialog
        final SettingsDialog settingsDialog = new SettingsDialog(getOwner(), this.model);
        settingsDialog.setVisible(true);
    }

}
