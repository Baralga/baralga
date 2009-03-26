package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
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
    private static final Log log = LogFactory.getLog(AbstractExportAction.class);


    public AbstractExportAction(Frame owner, PresentationModel model) {
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
     * Setter for the last export location
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
        chooser.setSelectedFile(new File(getLastExportLocation()));

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
        
        private PresentationModel model;
        
        private File file;
        
        private Exporter exporter;
        
        private Frame owner;
        
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
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                synchronized ( model.getData() ) {
                    exporter.export(
                            model.getData(),
                            model.getFilter(),
                            outputStream
                    );
                }
                
                // Make sure everything is written.
                outputStream.flush();

                // Store export location in settings
                setLastExportLocation(file.getAbsolutePath());
            } catch (Exception e) {
                log.error(e, e);
                JOptionPane.showMessageDialog(
                        owner, 
                        textBundle.textFor("AbstractExportAction.IOException.Message", file.getAbsolutePath(), e.getLocalizedMessage()), //$NON-NLS-1$
                        textBundle.textFor("AbstractExportAction.IOException.Heading", fileFilter.getDescription()), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );
            } finally {
                if (outputStream != null) {
                    IOUtils.closeQuietly(outputStream);
                }
            }
            return null;
        }
    }

}
