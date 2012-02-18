//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.export.Exporter;
import org.remast.baralga.model.export.ICalExporter;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Exports all activities and into a iCalender file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ICalExportAction extends AbstractExportAction {
	
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ICalExportAction.class);
    
    /** File extension of iCal calendar files. */
    private static final String ICAL_FILE_EXTENSION = ".ics";
    
    /** File filter for iCal calendar files. */
    private static final FileFilter ICAL_FILE_FILTER = new FileFilters.ICalFileFilter();

	public ICalExportAction(Frame owner, PresentationModel model) {
		super(owner, model);
		
        putValue(NAME, textBundle.textFor("ExportICalAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExportICalAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/stock_calendar-view-week.png"))); //$NON-NLS-1$
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Exporter createExporter() {
		return new ICalExporter();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected String getLastExportLocation() {
        return UserSettings.instance().getLastICalExportLocation();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void setLastExportLocation(String lastExportLocation) {
        UserSettings.instance().setLastICalExportLocation(lastExportLocation);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileFilter getFileFilter() {
		return ICAL_FILE_FILTER;
	}

    /**
     * {@inheritDoc}
     */
    @Override
	protected String getFileExtension() {
		return ICAL_FILE_EXTENSION;
	}

}
