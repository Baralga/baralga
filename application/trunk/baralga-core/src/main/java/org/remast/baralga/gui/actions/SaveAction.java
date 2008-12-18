package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.util.TextResourceBundle;

/**
 * Saves the data.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class SaveAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SaveAction.class);

    /** The logger. */
    private static final Log log = LogFactory.getLog(SaveAction.class);

    public SaveAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$
        putValue(NAME, textBundle.textFor("SaveAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("SaveAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        try {
            getModel().save();
        } catch (Exception e) {
            log.error(e, e);
            JOptionPane.showMessageDialog(
                    getOwner(), 
                    textBundle.textFor("SaveAction.ErrorText"), //$NON-NLS-1$
                    textBundle.textFor("SaveAction.ErrorTitle"), //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
