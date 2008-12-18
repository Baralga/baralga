package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.gui.dialogs.AddActivityDialog;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.TextResourceBundle;

/**
 * Displays the dialog to add a new project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AddActivityAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AddActivityAction.class);

    public AddActivityAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$
        putValue(NAME, textBundle.textFor("AddActivityAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("AddActivityAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        // Display dialog to add activity
        final AddActivityDialog addActivityDialog = new AddActivityDialog(getOwner(), getModel());
        addActivityDialog.setVisible(true);
    }

}
