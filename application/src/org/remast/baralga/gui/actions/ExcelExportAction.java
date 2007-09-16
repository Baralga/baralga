package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.export.ExcelExport;
import org.remast.util.FileFilters;

@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExcelExportAction extends AbstractProTrackAction {

    public ExcelExportAction(PresentationModel model) {
        super(model);

        putValue(NAME, Messages.getString("ExcelExportAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, Messages.getString("ExcelExportAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
    }

    public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser();
        
        // Set selection to last export location
        chooser.setSelectedFile(new File(Settings.instance().getLastExcelExportLocation()));

        chooser.setFileFilter(new FileFilters.ExcelFileFilter());

        int returnVal = chooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if(!file.getAbsolutePath().endsWith(Messages.getString("ExcelExportAction.ExcelFileExtension"))) { //$NON-NLS-1$
                file = new File(file.getAbsolutePath()+Messages.getString("ExcelExportAction.ExcelFileExtension")); //$NON-NLS-1$
            }

            try {
                ExcelExport.export(getModel().getData(), getModel().getFilter(), file);
                
                // store export location in settings
                Settings.instance().setLastExcelExportLocation(file.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, Messages.getString("ExcelExportAction.IOException1") + file.getAbsolutePath() + ".", Messages.getString("ExcelExportAction.IOException2"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

    }

}
