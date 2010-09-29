package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.dialogs.DailyActivitiesDialog;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

/**
 * Displays the dialog to manage the projects.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ShowTodaysActivitiesAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ShowTodaysActivitiesAction.class);

    public ShowTodaysActivitiesAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        putValue(NAME, textBundle.textFor("ShowTodaysActivitiesAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ShowTodaysActivitiesAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-edit.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        // Display dialog to manage projects
        final DailyActivitiesDialog manageProjectsDialog = new DailyActivitiesDialog(getOwner(), getModel());
        manageProjectsDialog.setDay(DateUtils.getNowAsDateTime());
        manageProjectsDialog.setVisible(true);
    }

}
