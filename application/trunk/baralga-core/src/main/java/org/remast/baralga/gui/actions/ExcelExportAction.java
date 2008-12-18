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
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.export.ExcelExport;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExcelExportAction extends AbstractBaralgaAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExcelExportAction.class);

    /** The logger. */
    private static final Log log = LogFactory.getLog(ExcelExportAction.class);

    public ExcelExportAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("ExcelExportAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExcelExportAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        
        // Set selection to last export location
        chooser.setSelectedFile(new File(Settings.instance().getLastExcelExportLocation()));

        chooser.setFileFilter(new FileFilters.ExcelFileFilter());

        int returnVal = chooser.showSaveDialog(getOwner());

        if (JFileChooser.APPROVE_OPTION == returnVal) {
            File file = chooser.getSelectedFile();
            if(!file.getAbsolutePath().endsWith(textBundle.textFor("ExcelExportAction.ExcelFileExtension"))) { //$NON-NLS-1$
                file = new File(file.getAbsolutePath()+textBundle.textFor("ExcelExportAction.ExcelFileExtension")); //$NON-NLS-1$
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
                ExcelExport.export(model.getData(), model.getFilter(), file);
                
                // store export location in settings
                Settings.instance().setLastExcelExportLocation(file.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, textBundle.textFor("ExcelExportAction.IOException1") + file.getAbsolutePath() + ".", textBundle.textFor("ExcelExportAction.IOException2"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                log.error(e, e);
            }
            return null;
        }
    }
}
