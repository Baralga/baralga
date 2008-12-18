package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.TextResourceBundle;

/**
 * Action to exit the application.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ExitAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExitAction.class);

    public ExitAction(final Frame owner, final PresentationModel model) {
        super(model);

        putValue(NAME, textBundle.textFor("ExitAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExitAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-quit.png"))); //$NON-NLS-1$
        putValue(LONG_DESCRIPTION, textBundle.textFor("ExitAction.LongDescription")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        boolean quit = true;

        // If activity is running, then double check with user.
        if (getModel().isActive()) {
            final int dialogResult = JOptionPane.showConfirmDialog(
                    getOwner(), 
                    textBundle.textFor("ExitConfirmDialog.Message"),  //$NON-NLS-1$
                    textBundle.textFor("ExitConfirmDialog.Title"),  //$NON-NLS-1$
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            quit = JOptionPane.YES_OPTION == dialogResult;
        } 

        if (quit) {
            System.exit(0);
        }
    }

}
