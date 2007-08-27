package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.dialogs.ManageProjectsDialog;
import org.remast.baralga.model.PresentationModel;

public final class ManageProjectsAction extends AbstractProTrackAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
