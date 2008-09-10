package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;

/**
 * Action to change the active project.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ChangeProjectAction extends AbstractBaralgaAction {

    /**
     * The project to be activated when the action is performed.
     */
    private Project newProject;

    public ChangeProjectAction(final PresentationModel model, final Project newProject) {
        super(model);
        this.newProject = newProject;
        
        // Highlight the currently selected project
        String projectName = String.valueOf(newProject);
        if (model.getSelectedProject() != null && model.getSelectedProject().equals(newProject)) {
            projectName = "* " + projectName;
        }
        
        putValue(NAME, projectName);
        putValue(SHORT_DESCRIPTION, Messages.getString("ChangeProjectAction.ShortDescription") + String.valueOf(newProject) + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        getModel().changeProject(newProject);
    }
    
}
