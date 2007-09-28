package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class SaveAction extends AbstractProTrackAction {

    public SaveAction(PresentationModel model) {
        super(model);
        
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-save.png"))); //$NON-NLS-1$
        putValue(NAME, Messages.getString("SaveAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("SaveAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK) );
    }

    public void actionPerformed(ActionEvent arg0) {
        try {
            getModel().save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, Messages.getString("SaveAction.ErrorText"), Messages.getString("SaveAction.ErrorTitle"), //$NON-NLS-1$ //$NON-NLS-2$
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
