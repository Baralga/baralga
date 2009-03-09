package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.io.ProTrackWriter;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Action to export data to a data file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ExportDataAction extends AbstractBaralgaAction {
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(ExportDataAction.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportDataAction.class);

    public ExportDataAction(final Frame owner, final PresentationModel model) {
        super(model);

        putValue(NAME, textBundle.textFor("ExportDataAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-text-xml.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        
        // Set selection to last export location
        chooser.setSelectedFile(new File(UserSettings.instance().getLastDataExportLocation()));
        
        chooser.setFileFilter(new FileFilters.DataFileFilter());

        int returnVal = chooser.showSaveDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            File file = chooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(textBundle.textFor("ExportDataAction.DataFileExtension"))) { //$NON-NLS-1$
                file = new File(file.getAbsolutePath() + textBundle.textFor("ExportDataAction.DataFileExtension")); //$NON-NLS-1$
            }

            final ExportWorker exportWorker = new ExportWorker(getModel(), file);
            exportWorker.execute();
        }

    }

    private static class ExportWorker extends SwingWorker<String, Object> {
        private PresentationModel model;
        private File file;

        public ExportWorker(final PresentationModel model, final File file) {
            this.model = model;
            this.file = file;
        }
        
        @Override
        public String doInBackground() {
            try {
                final ProTrackWriter writer = new ProTrackWriter(model.getData());
                writer.write(file);
                
                // Store export location in settings
                UserSettings.instance().setLastDataExportLocation(
                        file.getAbsolutePath()
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, textBundle.textFor("ExportDataAction.IOException.Message", file.getAbsolutePath()), textBundle.textFor("ExportDataAction.IOException.Heading"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                log.error(e, e);
            }
            return null;
        }
    }
}
