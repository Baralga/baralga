package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.io.XmlDataReader;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Action to import data from a data file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ImportXmlAction extends AbstractBaralgaAction {

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(ImportXmlAction.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ImportXmlAction.class);

    public ImportXmlAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("ImportDataAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ImportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-text-xml.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilters.DataFileFilter());

        final int dialogReturnValue = chooser.showOpenDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == dialogReturnValue) {
            final File file = chooser.getSelectedFile();
            final XmlDataReader reader = new XmlDataReader();
            try {
                reader.read(file);
                final Collection<Project> projectsForImport = reader.getProjects();
                final Collection<ProjectActivity> activitiesForImport = reader.getActivities();

                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        textBundle.textFor("ImportDataAction.Message"),  //$NON-NLS-1$
                        textBundle.textFor("ImportDataAction.Title"),  //$NON-NLS-1$
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                boolean doImport = JOptionPane.YES_OPTION == dialogResult;

                if (doImport) {
                    getModel().importData(projectsForImport, activitiesForImport);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, textBundle.textFor("ImportDataAction.IOException.Message", file.getAbsolutePath()), textBundle.textFor("ImportDataAction.IOException.Heading"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                log.error(e.getLocalizedMessage(), e);
            }

        }

    }

}
