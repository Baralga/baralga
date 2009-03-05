package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;

import javax.swing.JComboBox;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.remast.swing.dialog.EscapeDialog;

/**
 * The settings dialog for editing both application and user settings.
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog {

    public SettingsDialog(final Frame owner) {
        super(owner);

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

        this.setSize(300, 200);

        this.add(new JXHeader("Application Settings", null), "0, 0, 3, 1");

        this.add(new JXLabel("Data storage mode"), "1, 3");
        this.add(new JComboBox(new Object [] {"normal", "portable"}), "3, 3");

    }

}
