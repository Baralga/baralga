package org.remast.baralga.gui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.io.ProTrackReader;
import org.remast.swing.util.FileFilters;
import org.remast.util.TextResourceBundle;

/**
 * Action to import data from a data file.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ImportDataAction extends AbstractBaralgaAction {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ImportDataAction.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ImportDataAction.class);

    public ImportDataAction(final Frame owner, final PresentationModel model) {
        super(model);

        putValue(NAME, textBundle.textFor("ImportDataAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("ImportDataAction.ShortDescription")); //$NON-NLS-1$
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gnome-mime-text-xml.png"))); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilters.DataFileFilter());

        int returnVal = chooser.showOpenDialog(getOwner());
        if (JFileChooser.APPROVE_OPTION == returnVal) {
            final File file = chooser.getSelectedFile();
            final ProTrackReader reader = new ProTrackReader();
            try {
                reader.read(file);
                final ProTrack data = reader.getData();

                boolean doImport = true;
                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        textBundle.textFor("ImportDataAction.Message"),  //$NON-NLS-1$
                        textBundle.textFor("ImportDataAction.Title"),  //$NON-NLS-1$
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                doImport = JOptionPane.YES_OPTION == dialogResult;

                if (doImport) {
                    getModel().setData(data);
                    getModel().setDirty(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, textBundle.textFor("ImportDataAction.IOException.Message", file.getAbsolutePath()), textBundle.textFor("ImportDataAction.IOException.Heading"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        JOptionPane.ERROR_MESSAGE);
                log.error(e, e);
            }

        }

    }

}
