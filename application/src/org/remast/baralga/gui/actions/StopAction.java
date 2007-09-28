/**
 * 
 */
package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProjectStateException;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class StopAction extends AbstractProTrackAction {

    public StopAction(PresentationModel model) {
        super(model);
        
        putValue(NAME, Messages.getString("StopAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("StopAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-stop.png"))); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            getModel().stop();
        } catch (ProjectStateException e1) {
            e1.printStackTrace();
        }
    }

}
