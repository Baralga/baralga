package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;

public final class SaveAction extends AbstractProTrackAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SaveAction(PresentationModel model) {
        super(model);
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-save.png"))); //$NON-NLS-1$
        putValue(NAME, "Save"); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, "Save tracked Project Activities."); //$NON-NLS-1$
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
