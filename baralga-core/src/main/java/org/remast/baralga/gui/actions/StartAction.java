package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

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

    /**
     * Creates a new {@link StartAction}.
     * @param owner the owning frame
     * @param model the model
     */
    public StartAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("StartAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("StartAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/Play-Hot-icon_small.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent arg0) {
        try {
            getModel().start();
        } catch (ProjectActivityStateException exception) {
            JOptionPane.showConfirmDialog(
                    getOwner(),
                    exception.getLocalizedMessage(),
                    textBundle.textFor("StartAction.ErrorDialog.Title"),  //$NON-NLS-1$
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );        
        }
    }

}
