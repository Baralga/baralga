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

	public ICalExportAction(Frame owner, PresentationModel model) {
		super(owner, model);
		
        putValue(NAME, textBundle.textFor("ExportICalAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ExportICalAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-application-vnd.ms-excel.png"))); //$NON-NLS-1$
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Exporter createExporter() {
		return new ICalExporter();
	}

	/* (non-Javadoc)
	 * @see org.remast.baralga.gui.actions.AbstractExportAction#getLastExportLocation()
	 */
	@Override
	protected String getLastExportLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.remast.baralga.gui.actions.AbstractExportAction#setLastExportLocation(java.lang.String)
	 */
	@Override
	protected void setLastExportLocation(String lastExportLocation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.remast.baralga.gui.actions.AbstractExportAction#getFileFilter()
	 */
	@Override
	protected FileFilter getFileFilter() {
		return new FileFilters.ICalFileFilter();
	}

	/* (non-Javadoc)
	 * @see org.remast.baralga.gui.actions.AbstractExportAction#getFileExtension()
	 */
	@Override
	protected String getFileExtension() {
		return ".ics";
	}

}
