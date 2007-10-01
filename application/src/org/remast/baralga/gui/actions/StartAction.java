package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.model.ProjectStateException;
import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StartAction extends AbstractProTrackAction {

    public StartAction(PresentationModel model) {
        super(model);

        putValue(NAME, Messages.getString("StartAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("StartAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-ok.png"))); //$NON-NLS-1$
    }


    public void actionPerformed(ActionEvent arg0) {
        try {
            getModel().start();
        } catch (ProjectStateException e1) {
            // :TODO: Show error dialog.
            e1.printStackTrace();
        }
    }

}
