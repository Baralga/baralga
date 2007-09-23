package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.dialogs.ManageProjectsDialog;
import org.remast.baralga.model.PresentationModel;

/**
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ManageProjectsAction extends AbstractProTrackAction {

    public ManageProjectsAction(PresentationModel model) {
        super(model);
        putValue(NAME, Messages.getString("ManageProjectsAction.Name")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-edit.png"))); //$NON-NLS-1$
    }

    public void actionPerformed(ActionEvent arg0) {
        ManageProjectsDialog mp = new ManageProjectsDialog(null, getModel());
        mp.setVisible(true);
    }

}
