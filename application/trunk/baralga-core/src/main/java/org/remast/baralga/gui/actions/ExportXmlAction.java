package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.export.Exporter;
import org.remast.baralga.model.export.XmlExporter;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Exports all accumulated activities and all project activities 
 * into a Microsoft Excel file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExportXmlAction extends AbstractExportAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportXmlAction.class);

    /** The logger. */
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(ExportXmlAction.class);
    
    /** File extension of MS Excel files. */
    private static final String DATA_FILE_EXTENSION = ".baralga.xml";
    
    /** File filter for MS Excel files. */
    private static final FileFilter DATA_FILE_FILTER = new FileFilters.DataFileFilter();

    public ExportXmlAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("ExportDataAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new XmlExporter();
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
