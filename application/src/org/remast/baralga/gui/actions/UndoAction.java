package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Date;

import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

@SuppressWarnings("serial")
public class UndoAction extends AbstractBaralgaAction {

    public UndoAction(PresentationModel model) {
        super(model);

        putValue(NAME, "UNDO"); //$NON-NLS-1$
}

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectActivity activity = getModel().getFilter().applyFilters(getModel().getActivitiesList()).get(0);
        activity.setEnd(new Date());
        
        getModel().fireProTrackActivityChangedEvent(activity);
    }

}
