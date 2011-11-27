package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.export.CsvExporter;
import org.remast.baralga.model.export.Exporter;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Exports all project activities into a CSV (Comma Separated Value) file.
 * @author kutzi
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public final class ExportCsvAction extends AbstractExportAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportCsvAction.class);

    /** The logger. */
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ExportCsvAction.class);
    
    /** File extension of CSV files. */
    private static final String CSV_FILE_EXTENSION = ".csv";
    
    /** File filter for CSV files. */
    private static final FileFilter CSV_FILE_FILTER = new FileFilters.CsvFileFilter();

    public ExportCsvAction(final Frame owner, final PresentationModel model) {
        super(owner, model);

        putValue(NAME, textBundle.textFor("CsvExportAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("CsvExportAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-text.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Exporter createExporter() {
        return new CsvExporter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFileExtension() {
        return CSV_FILE_EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
        return CSV_FILE_FILTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLastExportLocation() {
        return UserSettings.instance().getLastCsvExportLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setLastExportLocation(final String lastExportLocation) {
        UserSettings.instance().setLastCsvExportLocation(lastExportLocation);
    }

}
