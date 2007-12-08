package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.remast.baralga.Messages;

@SuppressWarnings("serial")
public class UndoAction extends AbstractAction {

    public UndoAction() {
        putValue(NAME, Messages.getString("UndoAction.Name"));
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-undo-ltr.png"))); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        ProjectActivity activity = getModel().getFilter().applyFilters(getModel().getActivitiesList()).get(0);
//        activity.setEnd(new Date());
//        
//        getModel().fireProTrackActivityChangedEvent(activity);
    }

}
