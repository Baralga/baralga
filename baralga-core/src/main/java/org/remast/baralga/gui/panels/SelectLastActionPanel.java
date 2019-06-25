package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.swing.JTextEditor;
import org.remast.swing.util.GuiConstants;
import org.remast.util.TextResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.common.eventbus.Subscribe;

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

@SuppressWarnings("serial")
public class SelectLastActionPanel extends JPanel {
	
    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(SelectLastActionPanel.class);
    
	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	private PresentationModel model;

	/** The description editor. */
	private JTextEditor descriptionEditor;

	/** The list of projects. The selected project is the currently active */
	private JComboBox<Project> projectSelector = null;

	/** The label, showing a text reminding on the last seected project */
	private JLabel lastProjectLabel = null;

	/** The label, showing how long user has been inactive */
	private JLabel awaySince = null;

	private Long lastActivity;

	private boolean isSaving = false;

	/**
	 * The RadioButton that will be checked when no activity for the inactivity
	 * time should be tracked
	 */
	private JRadioButton buttonNoAction;

	/**
	 * The Radio button, that will be checked when another Project ( or
	 * Activity) should be tracked
	 */
	private JRadioButton buttonAction;

	/**
	 * Create a new panel for the given model.
	 * @param model the model
	 */
	public SelectLastActionPanel(final PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		initialize();
	}

	private void initialize() {
		final double border = 5;
		final double[][] size = { { border, TableLayout.PREFERRED, border }, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } // Rows
		};

		this.setLayout(new TableLayout(size));

		JLabel introLabel = getIntroductionLabel();
		JLabel lastActionLabel = getLastProjectLabel();
		JLabel awaySinceLabel = getAwaySinceLabel();
		JPanel whatToTrackPanel = getWhatToTrackPanel();

		this.add(introLabel, "1, 1");
		this.add(lastActionLabel, "1, 3");
		this.add(awaySinceLabel, "1, 5");
		this.add(whatToTrackPanel, "1, 7");
	}

	@Subscribe
	public final void update(final Object eventObject) {
		if (isSaving) {
			return;
		}

		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;

		switch (event.getType()) {

		case BaralgaEvent.PROJECT_ACTIVITY_STARTED:
		case BaralgaEvent.PROJECT_ACTIVITY_STOPPED:
		case BaralgaEvent.PROJECT_CHANGED:
			this.updateProjectChanged(event);
			buttonAction.setSelected(true);
			break;

		case BaralgaEvent.USER_IS_INACTIVE:
			lastActivity = (Long) event.getData();
			updateInactivityTime(lastActivity);
			updateLastProjextLabel();
			break;
		}
	}

	private void updateLastProjextLabel() {
		Project project = model.getSelectedProject();

		String strProjectTitle = textBundle.textFor("SelectLastActionPanel.EmptyProject.Title"); //$NON-NLS-1$
		if (model.isActive()) {
			strProjectTitle = project.getTitle();
		}

		lastProjectLabel.setText(textBundle.textFor("SelectLastActionPanel.LastProjectLabel.Titel", strProjectTitle)); //$NON-NLS-1$
	}

	private JLabel getIntroductionLabel() {
		final JLabel introductionLabel = new JLabel(textBundle.textFor("SelectLastActionPanel.IntroductionLabel.Title")); //$NON-NLS-1$
		return introductionLabel;
	}

	private JPanel getWhatToTrackPanel() {
		final double border = 1;
		final double[][] size = { { border, TableLayout.PREFERRED, border, 400, border }, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } // Rows
		};
		JPanel panel = new JPanel(new TableLayout(size));

		// Create the radio buttons.
		buttonNoAction = new JRadioButton(textBundle.textFor("SelectLastActionPanel.ButtonNoAction.Title")); //$NON-NLS-1$
		buttonNoAction.setMnemonic(KeyEvent.VK_T);
		buttonNoAction.setSelected(true);

		buttonAction = new JRadioButton(textBundle.textFor("SelectLastActionPanel.ButtonAction.Title")); //$NON-NLS-1$
		buttonAction.setMnemonic(KeyEvent.VK_C);

		// Group the radio buttons.
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(buttonNoAction);
		buttonGroup.add(buttonAction);

		// Register a listener for the radio buttons.

		descriptionEditor = new JTextEditor(true);
		descriptionEditor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
		descriptionEditor.addTextObserver(new JTextEditor.TextChangeObserver() {

			public void onTextChange() {
				final String description = descriptionEditor.getText();

				// Store in model
				model.setDescription(description);

				// Save description in settings.
				UserSettings.instance().setLastDescription(description);
			}
		});

		descriptionEditor.setText(model.getDescription());
		descriptionEditor.setEditable(model.isActive());

		panel.add(buttonNoAction, "1, 1"); //$NON-NLS-1$
		panel.add(buttonAction, "1, 3"); //$NON-NLS-1$
		panel.add(getProjectSelector(), "3, 3"); //$NON-NLS-1$
		panel.add(descriptionEditor, "3, 5"); //$NON-NLS-1$

		return panel;
	}

	private JLabel getAwaySinceLabel() {
		awaySince = new JLabel(textBundle.textFor("SelectLastActionPanel.AwaySince.Title", 0, 0, 0)); //$NON-NLS-1$
		return awaySince;
	}

	private JLabel getLastProjectLabel() {
		lastProjectLabel = new JLabel();
		updateLastProjextLabel();
		return lastProjectLabel;
	}

	/**
	 * This method initializes projectSelector.
	 * @return javax.swing.JComboBox
	 */
	@SuppressWarnings("unchecked")
	private JComboBox<Project> getProjectSelector() {
		if (projectSelector == null) {
			projectSelector = new JComboBox<Project>();
			projectSelector.setToolTipText(textBundle.textFor("SelectLastActionPanel.ProjectSelector.Hint")); //$NON-NLS-1$
			projectSelector.setModel(new DefaultEventComboBoxModel<Project>(this.model.getProjectList()));
		}
		return projectSelector;
	}

	/**
	 * Executed on project changed event.
	 * @param event the event of the project change
	 */
	private void updateProjectChanged(final BaralgaEvent event) {
		getProjectSelector().setSelectedItem(model.getSelectedProject());
	}

	private void updateInactivityTime(long inactivityTime) {
		LocalDateTime dateTime =LocalDateTime.now().minusHours(1).minus(inactivityTime, ChronoUnit.MILLIS);

		DateTimeFormatter hoursFormat = DateTimeFormatter.ofPattern("HH");
		DateTimeFormatter minutesFormat = DateTimeFormatter.ofPattern("mm");
		DateTimeFormatter secondsFormat = DateTimeFormatter.ofPattern("ss");

		String hours = hoursFormat.format(dateTime);
		String minutes = minutesFormat.format(dateTime);
		String seconds = secondsFormat.format(dateTime);

		awaySince.setText(textBundle.textFor("SelectLastActionPanel.AwaySince.Title", hours, minutes, seconds)); //$NON-NLS-1$
	}

	public void save() {
		// If "no action" is selected, there is nothing to do.
		if (this.buttonNoAction.isSelected()) {
			return;
		}

		if (this.buttonAction.isSelected()) {
			try {
				if (!this.model.getSelectedProject().equals(projectSelector.getSelectedItem())) {
					if (model.isActive()) {
						LocalDateTime lastActivity = Instant.ofEpochMilli(this.lastActivity).atZone(ZoneId.systemDefault()).toLocalDateTime();;
						model.stop(lastActivity, false);
					}

					Project selectedProject = (Project) projectSelector.getSelectedItem();
					model.changeProject(selectedProject);

					LocalDateTime dateTime = Instant.ofEpochMilli(this.lastActivity).atZone(ZoneId.systemDefault()).toLocalDateTime();
					model.start(dateTime);
				} else {
					if (!model.isActive()) {
					    LocalDateTime dateTime = Instant.ofEpochMilli(this.lastActivity).atZone(ZoneId.systemDefault()).toLocalDateTime();
						model.start(dateTime);
					}
				}
			} catch (Exception ex) {
				log.error("Could not save action.", ex);
			}
		}
	}

	public void beginSaving() {
		this.isSaving = true;
	}

	public void endSaving() {
		this.isSaving = false;
	}
}
