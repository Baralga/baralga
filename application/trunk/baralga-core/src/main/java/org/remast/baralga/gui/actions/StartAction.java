package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.util.TextResourceBundle;

/**
 * Starts a new project activity.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StartAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(StartAction.class);

    public StartAction(final PresentationModel model) {
        super(model);

        putValue(NAME, textBundle.textFor("StartAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("StartAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-ok.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent arg0) {
        try {
            getModel().start();
        } catch (ProjectActivityStateException e1) {
            // :TODO: Show error dialog.
            e1.printStackTrace();
        }
    }

}
