package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.export.Exporter;
import org.remast.util.TextResourceBundle;

/**
 * Base action for all data exports.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractExportAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AbstractExportAction.class);

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(AbstractExportAction.class);

    /**
     * Creates a new export action.
     * @param owner the owning frame
     * @param model the model to be exported
     */
    public AbstractExportAction(final Frame owner, final PresentationModel model) {
        super(owner, model);
    }

    /**
     * Creates a new instance of the exporter.
     * @return the new exporter instance
     */
    public abstract Exporter createExporter();

    /**
     * Getter for the last export location.
     * @return the location of the last export
     */
    protected abstract String getLastExportLocation();

    /**
     * Setter for the last export location.
     * @param lastExportLocation the last export location to set
     */
    protected abstract void setLastExportLocation(final String lastExportLocation);

    /**
     * Getter for the file filter to be used for the export.
     * @return the file filter to be used for the export
     */
    protected abstract FileFilter getFileFilter();

    /**
     * Getter for the file extension to be used for the export.
     * @return the file extension to be used for the export
     */
    protected abstract String getFileExtension();

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();

        // Set selection to last export location
        if (getLastExportLocation() != null) {
        	chooser.setSelectedFile(new File(getLastExportLocation()));
        }

        chooser.setFileFilter(getFileFilter());

        int returnVal = chooser.showSaveDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            File file = chooser.getSelectedFile();
            if (!file.getAbsolutePath().endsWith(getFileExtension())) {
                file = new File(file.getAbsolutePath() + getFileExtension());
            }

            final ExportWorker exportWorker = new ExportWorker(
                    getModel(), 
                    createExporter(), 
                    getFileFilter(),
                    getOwner(),
                    file);
            exportWorker.execute();
        }

    }

    /**
     * Worker thread to perform the actual export in the background.
     * @author remast
     */
    private class ExportWorker extends SwingWorker<String, Object> {
        
        /** The model to be exported. */
        private PresentationModel model;
        
        /** The file to export to. */
        private File file;
        
        /** The actual exporter. */
        private Exporter exporter;
        
        /** The owning frame. */
        private Frame owner;
        
        /** The filter for the export file. */
        private FileFilter fileFilter;

        public ExportWorker(final PresentationModel model, final Exporter exporter, final FileFilter fileFilter, final Frame owner, final File file) {
            this.model = model;
            this.exporter = exporter;
            this.fileFilter = fileFilter;
            this.owner = owner;
            this.file = file;
        }

        @Override
        public String doInBackground() {
            try (OutputStream outputStream = new FileOutputStream(file)) {

                // Get activities for export
                Collection<ProjectActivity> activitiesForExport;
                if (exporter.isFullExport()) {
                    activitiesForExport = model.getAllActivitiesList();
                } else {
                    activitiesForExport = model.getActivitiesList();
                }

                synchronized (activitiesForExport) {
                    exporter.export(
                            activitiesForExport,
                            model.getFilter(),
                            outputStream
                    );
                }

                // Make sure everything is written.
                outputStream.flush();

                // Store export location in settings
                setLastExportLocation(file.getAbsolutePath());
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                JOptionPane.showMessageDialog(
                        owner,
                        textBundle.textFor("AbstractExportAction.IOException.Message", file.getAbsolutePath(), e.getLocalizedMessage()), //$NON-NLS-1$
                        textBundle.textFor("AbstractExportAction.IOException.Heading", fileFilter.getDescription()), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );
            }
            // Ignore
            return null;
        }
    }

}
