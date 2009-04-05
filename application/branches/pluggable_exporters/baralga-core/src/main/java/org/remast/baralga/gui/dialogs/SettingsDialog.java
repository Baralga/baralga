package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXHeader;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.util.TextResourceBundle;

/**
 * The settings dialog for editing both application and user settings.
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog {

    /** The logger. */
    private static final Log log = LogFactory.getLog(SettingsDialog.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);

    /** The model. */
    private final PresentationModel model;

    /**
     * Creates a new settings dialog.
     * @param owner the owning frame
     * @param model the model
     */
    public SettingsDialog(final Frame owner, final PresentationModel model) {
        super(owner);
        this.model = model;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());

        final double border = 5;
        final double size[][] = {
                { border, TableLayout.FILL, border, TableLayout.PREFERRED, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border}  // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.setSize(300, 105);

        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        this.add(new JXHeader(textBundle.textFor("SettingsDialog.ApplicationSettingsTitle"), null), "0, 0, 3, 1"); //$NON-NLS-1$ //$NON-NLS-2$

//        this.add(storageModeLabel, "1, 3"); //$NON-NLS-1$
//        this.add(storageModelSelection, "3, 3"); //$NON-NLS-1$

        readFromModel();
    }

    /**
     * Reads the data displayed in the dialog from the model.
     */
    private void readFromModel() {
    }

}
