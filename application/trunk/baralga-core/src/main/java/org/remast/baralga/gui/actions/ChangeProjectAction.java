package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.*;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.baralga.gui.dialogs.StartActivityConfirmDialog;
import org.remast.baralga.model.Project;
import org.remast.util.TextResourceBundle;

/**
 * Action to change the active project.
 * @author remast
 */
public class ChangeProjectAction extends AbstractBaralgaAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ExportDataAction.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ChangeProjectAction.class);

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
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ChangeProjectAction.ShortDescription") + String.valueOf(newProject) + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent e) {
        getModel().changeProject(newProject);

        if( !getModel().isActive() ) {
            startActivityIfConfirmed();
        }
    }

    private void startActivityIfConfirmed() {
        // unfortunately Systray gives no hint where it is located, so we have to guess
        // by getting the current mouse location.
        Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
        String title = textBundle.textFor("StartActivityConfirmDialog.Title"); //$NON-NLS-1$
        String msg  = textBundle.textFor("StartActivityConfirmDialog.Message"); //$NON-NLS-1$
        
        StartActivityConfirmDialog dialog = new StartActivityConfirmDialog( title, msg, currentMousePosition );
        if( dialog.getSelectedValue() == JOptionPane.YES_OPTION ) {
            try {
                getModel().start();
            } catch (ProjectActivityStateException e1) {
                log.warn("Race condition in ChangeProjectAction?", e1 ); //$NON-NLS-1$
            }
        }
    }
    
}
