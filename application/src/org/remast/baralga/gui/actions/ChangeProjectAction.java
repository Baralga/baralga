package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;

/**
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ChangeProjectAction extends AbstractProTrackAction {

    private Project newProject;

    public ChangeProjectAction(PresentationModel model, Project newProject) {
        super(model);
        this.newProject = newProject;
        
        putValue(NAME, getNewProject().toString());
        putValue(SHORT_DESCRIPTION, Messages.getString("ChangeProjectAction.ShortDescription") + getNewProject().toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void actionPerformed(ActionEvent e) {
        getModel().changeProject(getNewProject());
    }

    /**
     * @return the newProject
     */
    private Project getNewProject() {
        return newProject;
    }

}
