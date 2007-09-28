package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.dialogs.AddActivityDialog;
import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AddActivityAction extends AbstractProTrackAction {

    public AddActivityAction(PresentationModel model) {
        super(model);
        
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-add.png"))); //$NON-NLS-1$
        putValue(NAME, Messages.getString("AddActivityAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("AddActivityAction.ShortDescription")); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK) );
    }

    public void actionPerformed(ActionEvent event) {
        AddActivityDialog addActivityDialog = new AddActivityDialog(null, getModel());
        addActivityDialog.setVisible(true);
    }

}
