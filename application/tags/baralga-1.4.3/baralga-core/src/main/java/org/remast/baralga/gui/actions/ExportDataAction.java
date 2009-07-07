package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.export.Exporter;
import org.remast.baralga.model.export.RawDataExporter;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Action to export data to a data file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ExportDataAction extends AbstractExportAction {
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(ExportDataAction.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportDataAction.class);
    
    /** File extension of data files. */
    private static final String DATA_FILE_EXTENSION = ".ptd.xml";
    
    /** File filter for data files. */
    private static final FileFilter DATA_FILE_FILTER = new FileFilters.DataFileFilter();


    public ExportDataAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("ExportDataAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-text-xml.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new RawDataExporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFileExtension() {
        return DATA_FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
        return DATA_FILE_FILTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLastExportLocation() {
        return UserSettings.instance().getLastDataExportLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setLastExportLocation(final String lastExportLocation) {
        UserSettings.instance().setLastDataExportLocation(lastExportLocation);
    }
}
