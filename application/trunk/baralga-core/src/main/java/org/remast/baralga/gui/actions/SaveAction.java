package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;

/**
 * Saves the data.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class SaveAction extends AbstractBaralgaAction {

    /** The logger. */
    private static final Log log = LogFactory.getLog(SaveAction.class);

    public SaveAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
        
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-save.png"))); //$NON-NLS-1$
        putValue(NAME, Messages.getString("SaveAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("SaveAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK) );
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent arg0) {
        try {
            getModel().save();
        } catch (Exception e) {
            log.error(e, e);
            JOptionPane.showMessageDialog(
                    getOwner(), 
                    Messages.getString("SaveAction.ErrorText"), //$NON-NLS-1$
                    Messages.getString("SaveAction.ErrorTitle"), //$NON-NLS-1$
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
