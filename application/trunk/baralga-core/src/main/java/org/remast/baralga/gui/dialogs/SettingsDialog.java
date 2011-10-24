package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXHeader;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.util.TextResourceBundle;

/**
 * The settings dialog for editing both application and user settings.
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog implements ActionListener {

    /** The logger. */
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(SettingsDialog.class);

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);
    
    /** Component to edit setting to remember window size and location. */
    private JCheckBox rememberWindowSizeLocation;

    /** The model. */
    @SuppressWarnings("unused")
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
        final double[][] size = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border}  // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);

        this.setSize(340, 160);
        
        this.setName("SettingsDialog"); //$NON-NLS-1$
        this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
        final ImageIcon icon = new ImageIcon(getClass().getResource("/icons/stock_folder-properties.png"));
        this.add(new JXHeader(textBundle.textFor("SettingsDialog.UserSettingsTitle"), null, icon), "0, 0, 3, 1"); //$NON-NLS-1$ //$NON-NLS-2$

        rememberWindowSizeLocation = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.Title"));
        rememberWindowSizeLocation.setToolTipText(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.ToolTipText"));
        rememberWindowSizeLocation.addActionListener(this);
        this.add(rememberWindowSizeLocation, "1, 3, 3, 3"); //$NON-NLS-1$
               
        final JButton resetButton = new JButton(textBundle.textFor("SettingsDialog.ResetButton.Title")); //$NON-NLS-1$
        resetButton.setToolTipText(textBundle.textFor("SettingsDialog.ResetButton.ToolTipText")); //$NON-NLS-1$
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                resetSettings();
            }
            
        });
        this.add(resetButton, "1, 5, 3, 5"); //$NON-NLS-1$

        readFromModel();
    }
    
    /**
     * Resets all settings to default values.
     */
    private void resetSettings() {
        UserSettings.instance().reset();
        readFromModel();
    }

    /**
     * Reads the data displayed in the dialog from the model.
     */
    private void readFromModel() {
        // Read window size and location
        rememberWindowSizeLocation.setSelected(UserSettings.instance().isRememberWindowSizeLocation());
    }

    /**
     * Writes the data displayed in the dialog to the model.
     */
    private void writeToModel() {
        // Remember window size and location
        UserSettings.instance().setRememberWindowSizeLocation(rememberWindowSizeLocation.isSelected());
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        writeToModel();
    }

}
