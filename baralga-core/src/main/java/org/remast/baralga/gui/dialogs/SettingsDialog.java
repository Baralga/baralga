package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXHeader;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.UserSettingsInactivityReminderSetupPanel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.util.TextResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The settings dialog for editing both application and user settings.
 * 
 * @author remast
 */
@SuppressWarnings("serial")
public class SettingsDialog extends EscapeDialog implements ActionListener {

	/** The logger. */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SettingsDialog.class);

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(SettingsDialog.class);

	/** Component to edit setting to remember window size and location. */
	private JCheckBox rememberWindowSizeLocation;

	/** Component to edit setting to show stopwatch. */
	private JCheckBox showStopwatch;

    /** Component to edit setting to show stopwatch. */
	private UserSettingsInactivityReminderSetupPanel inactivityReminderSetupPanel;
	
	/** The model. */
	private final PresentationModel model;

	/**
	 * Creates a new settings dialog.
	 * 
	 * @param owner
	 *            the owning frame
	 * @param model
	 *            the model
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
		final double[][] size = { { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } // Rows
		};

		final TableLayout tableLayout = new TableLayout(size);
		this.setLayout(tableLayout);

		this.setSize(600, 300);

		this.setName("SettingsDialog"); //$NON-NLS-1$
		this.setTitle(textBundle.textFor("SettingsDialog.Title")); //$NON-NLS-1$
		final ImageIcon icon = new ImageIcon(getClass().getResource("/icons/stock_folder-properties.png")); //$NON-NLS-1$
		this.add(new JXHeader(textBundle.textFor("SettingsDialog.UserSettingsTitle"), null, icon), "0, 0, 3, 1"); //$NON-NLS-1$ //$NON-NLS-2$

		rememberWindowSizeLocation = new JCheckBox(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.Title")); //$NON-NLS-1$
		rememberWindowSizeLocation.setToolTipText(textBundle.textFor("SettingsDialog.Setting.RememberWindowSizeLocation.ToolTipText")); //$NON-NLS-1$
		rememberWindowSizeLocation.addActionListener(this);
		this.add(rememberWindowSizeLocation, "1, 3, 3, 3"); //$NON-NLS-1$

		showStopwatch = new JCheckBox(textBundle.textFor("SettingsDialog.ShowStopwatch.Title")); //$NON-NLS-1$
		showStopwatch.setToolTipText(textBundle.textFor("SettingsDialog.ShowStopwatch.ToolTipText")); //$NON-NLS-1$
		showStopwatch.addActionListener(this);
		showStopwatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.changeStopWatchVisibility();
			}
		});

		this.add(showStopwatch, "1, 5, 1, 1"); //$NON-NLS-1$
        
        inactivityReminderSetupPanel = new UserSettingsInactivityReminderSetupPanel();
        this.add(inactivityReminderSetupPanel, "1, 7"); //$NON-NLS-1$

		final JButton resetButton = new JButton(textBundle.textFor("SettingsDialog.ResetButton.Title")); //$NON-NLS-1$
		resetButton.setToolTipText(textBundle.textFor("SettingsDialog.ResetButton.ToolTipText")); //$NON-NLS-1$
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				resetSettings();
			}

		});		

		this.add(resetButton, "1, 9, 3, 5"); //$NON-NLS-1$

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
		showStopwatch.setSelected(UserSettings.instance().isShowStopwatch());
		inactivityReminderSetupPanel.setDisplayValues();
	}

	/**
	 * Writes the data displayed in the dialog to the model.
	 */
	private void writeToModel() {
		// Remember window size and location
		UserSettings.instance().setRememberWindowSizeLocation(rememberWindowSizeLocation.isSelected());
		UserSettings.instance().setShowStopwatch(showStopwatch.isSelected());
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		writeToModel();
	}

}
