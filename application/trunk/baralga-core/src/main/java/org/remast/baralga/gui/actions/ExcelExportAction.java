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
import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.export.ExcelExport;
import org.remast.util.FileFilters;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExcelExportAction extends AbstractBaralgaAction {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ExcelExportAction.class);

    public ExcelExportAction(Frame owner, PresentationModel model) {
        super(owner, model);

        putValue(NAME, Messages.getString("ExcelExportAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("ExcelExportAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        
        // Set selection to last export location
        chooser.setSelectedFile(new File(Settings.instance().getLastExcelExportLocation()));

        chooser.setFileFilter(new FileFilters.ExcelFileFilter());

        int returnVal = chooser.showSaveDialog(getOwner());

        if (JFileChooser.APPROVE_OPTION == returnVal) {
            File file = chooser.getSelectedFile();
            if(!file.getAbsolutePath().endsWith(Messages.getString("ExcelExportAction.ExcelFileExtension"))) { //$NON-NLS-1$
                file = new File(file.getAbsolutePath()+Messages.getString("ExcelExportAction.ExcelFileExtension")); //$NON-NLS-1$
            }

            final ExportWorker exportWorker = new ExportWorker(getModel(), file);
            exportWorker.execute();
        }

    }

    private static class ExportWorker extends SwingWorker<String, Object> {
        private PresentationModel model;
        private File file;

        public ExportWorker(PresentationModel model, File file) {
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
                JOptionPane.showMessageDialog(null, Messages.getString("ExcelExportAction.IOException1") + file.getAbsolutePath() + ".", Messages.getString("ExcelExportAction.IOException2"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                log.error(e, e);
            }
            return null;
        }
    }
}
