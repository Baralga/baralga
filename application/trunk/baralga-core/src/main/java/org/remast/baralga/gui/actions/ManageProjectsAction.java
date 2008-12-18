package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.dialogs.ManageProjectsDialog;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.TextResourceBundle;

/**
 * Displays the dialog to manage the projects.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ManageProjectsAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ManageProjectsAction.class);

    public ManageProjectsAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        putValue(NAME, textBundle.textFor("ManageProjectsAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ManageProjectsAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-edit.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        // Display dialog to manage projects
        final ManageProjectsDialog manageProjectsDialog = new ManageProjectsDialog(getOwner(), getModel());
        manageProjectsDialog.setVisible(true);
    }

}
