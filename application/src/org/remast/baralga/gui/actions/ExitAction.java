package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ExitAction extends AbstractBaralgaAction {
     
    public ExitAction(PresentationModel model) {
        super(model);
        
        putValue(NAME, Messages.getString("ExitAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("ExitAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-close.png"))); //$NON-NLS-1$
        putValue(LONG_DESCRIPTION, Messages.getString("ExitAction.LongDescription")); //$NON-NLS-1$
    }
    
    public void actionPerformed(ActionEvent event) {
        System.exit(0);
    }

}
