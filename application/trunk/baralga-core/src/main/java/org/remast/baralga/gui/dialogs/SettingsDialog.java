package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;

import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.swing.util.LabeledItem;
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

    /** The selected storage mode. */
    private JComboBox storageModelSelection;

    /** Item for normal storage mode. */
    private static final LabeledItem<Boolean> NORMAL_MODE = new LabeledItem<Boolean>(Boolean.FALSE, textBundle.textFor("Settings.DataStorage.NormalLabel")); //$NON-NLS-1$

    /** Item for portable storage mode. */
    private static final LabeledItem<Boolean> PORTABLE_MODE = new LabeledItem<Boolean>(Boolean.TRUE, textBundle.textFor("Settings.DataStorage.PortableLabel")); //$NON-NLS-1$

    /** All available storage modes. */
    private static final LabeledItem<Boolean> [] STORAGE_MODES = new LabeledItem [] {
        NORMAL_MODE, PORTABLE_MODE
    };

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

        final JXLabel storageModeLabel = new JXLabel(textBundle.textFor("Settings.DataStorage.Label")); //$NON-NLS-1$
        storageModeLabel.setToolTipText(textBundle.textFor("Settings.DataStorage.ToolTipText")); //$NON-NLS-1$
        this.add(storageModeLabel, "1, 3"); //$NON-NLS-1$
        storageModelSelection = new JComboBox(
                STORAGE_MODES
        );
        
        // Storage mode is read only
        storageModelSelection.setEditable(false);
        storageModelSelection.setEnabled(false);
        
        this.add(storageModelSelection, "3, 3"); //$NON-NLS-1$

        readFromModel();
    }

    /**
     * Reads the data displayed in the dialog from the model.
     */
    private void readFromModel() {
        // 1. Storage mode
        if (ApplicationSettings.instance().isStoreDataInApplicationDirectory()) {
            storageModelSelection.setSelectedItem(PORTABLE_MODE);
        } else {
            storageModelSelection.setSelectedItem(NORMAL_MODE);
        }
    }

}
