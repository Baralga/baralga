package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXButton;
import org.joda.time.Period;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.MainFrame;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.google.common.eventbus.Subscribe;

/**
 * Panel for capturing new activities.
 * 
 * @author remast
 */
@SuppressWarnings("serial")
public class ActivityPanelStopWatch extends JPanel implements ActionListener {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

	/** Color for highlighted time and duration. */
	private static final Color HIGHLIGHT_COLOR = new Color(51, 0, 102);

	/** Big bold font for labels. */
	private static final Font FONT_BIG_BOLD = new Font("Sans Serif", Font.BOLD, 14);

	/** Text for inactive duration. */
	private static final String DURATION_INACTIVE = "-:-- h";

	/** The model. */
	private final PresentationModel model;

	/** Starts/stops the active project. */
	private JXButton startStopButton = null;

	private static final Icon ICON_START_ENABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Play-Hot-icon_small.png"));

	private static final Icon ICON_START_DISABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Play-Disabled-icon_small.png"));

	private static final Icon ICON_STOP_ENABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Stop-Normal-Blue-icon_small.png"));

	private static final Icon ICON_STOP_DISABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Stop-Disabled-icon_small.png"));

	/**
	 * The list of projects. The selected project is the currently active
	 * project.
	 */
	private JComboBox<Project> projectSelector = null;

	/** Timer for the time passed since activity was started. */
	private Timer timer;

	/** Displays the duration of the running activity. */
	private JLabel duration;

	/** Format for minutes. */
	private static NumberFormat MINUTE_FORMAT = new DecimalFormat("##00");

	/**
	 * Create a new panel for the given model.
	 * @param model the model
	 */
	public ActivityPanelStopWatch(final PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		// Fire timer event every minute
		this.timer = new Timer(1000 * 60, this);

		initialize();
	}

	/**
	 * Set up GUI components.
	 */
	protected void initialize() {
		// 1. Init start-/stop-Buttons
		if (this.model.isActive()) {
			getStartStopButton().setAction(new StopAction(this.model));
		} else {
			getStartStopButton().setAction(new StartAction(null, this.model));
		}

		getStartStopButton().setText("");
		getStartStopButton().setRolloverEnabled(true);
		getStartStopButton().setIcon(ICON_START_DISABLED);
		getStartStopButton().setRolloverIcon(ICON_START_ENABLED);

		// 2. Restore selected project if set.
		if (!this.model.getProjectList().isEmpty()) {
			getProjectSelector().setSelectedItem(this.model.getProjectList().get(0));
		}

		final double[][] size = { { 0, 30, 0, 150, 5, 50, 30 }, // Columns
				{ 1, TableLayout.FILL, 1 } // Rows
		};

		this.setLayout(new TableLayout(size));

		this.add(getStartStopButton(), "1, 1"); //$NON-NLS-1$
		this.add(getProjectSelector(), "3, 1"); //$NON-NLS-1$
		this.add(getDurationLabel(), "5, 1"); //$NON-NLS-1$
	}

	private JLabel getDurationLabel() {
		if (duration == null) {
			duration = new JLabel();
			duration.setFont(FONT_BIG_BOLD);
			duration.setForeground(HIGHLIGHT_COLOR);
		}

		// Restore current activity
		if (model.isActive()) {
			duration.setEnabled(true);
			updateDuration();
		} else {
			duration.setEnabled(false);
			duration.setText(DURATION_INACTIVE);
		}
		duration.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText"));

		return duration;
	}

	/**
	 * This method initializes startStopButton.
	 * @return javax.swing.JButton
	 */
	private JButton getStartStopButton() {
		if (startStopButton == null) {
			startStopButton = new JXButton(new StartAction(null, this.model));
			startStopButton.setContentAreaFilled(false);
			startStopButton.setIcon(ICON_START_DISABLED);

		}
		return startStopButton;
	}

	/**
	 * This method initializes projectSelector.
	 * @return javax.swing.JComboBox
	 */
	@SuppressWarnings("unchecked")
	private JComboBox<Project> getProjectSelector() {
		if (projectSelector == null) {
			projectSelector = new JComboBox<>();
			projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText")); //$NON-NLS-1$
			projectSelector.setModel(new EventComboBoxModel<>(this.model.getProjectList()));

			/* Handling of selection events: */
			projectSelector.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					// 1. Set current project to the just selected project.
					final Project selectedProject = (Project) projectSelector.getSelectedItem();
					ActivityPanelStopWatch.this.model.changeProject(selectedProject);
				}
			});
		}
		return projectSelector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Subscribe
	public final void update(final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;

		switch (event.getType()) {

		case BaralgaEvent.PROJECT_ACTIVITY_STARTED:
			this.updateStart();
			break;

		case BaralgaEvent.PROJECT_ACTIVITY_STOPPED:
			this.updateStop();
			break;

		case BaralgaEvent.PROJECT_CHANGED:
			this.updateProjectChanged(event);
			break;

		case BaralgaEvent.PROJECT_ADDED:
			break;

		case BaralgaEvent.PROJECT_REMOVED:
			break;

		case BaralgaEvent.START_CHANGED:
			updateDuration();
			break;
		}
	}

	/**
	 * Executed on project changed event.
	 * @param event the event of the project change
	 */
	private void updateProjectChanged(final BaralgaEvent event) {
		getProjectSelector().setSelectedItem(event.getData());

		if (model.isActive()) {
			updateDuration();
		}
	}

	/**
	 * Executed on start event.
	 */
	private void updateStart() {
		timer.start();

		// Clear description in settings.
		UserSettings.instance().setLastDescription(StringUtils.EMPTY);

		// Change button from start to stop
		getStartStopButton().setAction(new StopAction(this.model));
		getStartStopButton().setText("");
		getStartStopButton().setIcon(ICON_STOP_DISABLED);
		getStartStopButton().setRolloverIcon(ICON_STOP_ENABLED);

		updateDuration();
		duration.setEnabled(true);
	}

	/**
	 * Executed on stop event.
	 */
	private void updateStop() {
		timer.stop();

		// Clear description in settings.
		UserSettings.instance().setLastDescription(StringUtils.EMPTY);

		getStartStopButton().setAction(new StartAction(null, this.model));
		getStartStopButton().setText("");
		getStartStopButton().setIcon(ICON_START_DISABLED);
		getStartStopButton().setRolloverIcon(ICON_START_ENABLED);

		// Reset duration
		duration.setText(DURATION_INACTIVE);
		duration.setEnabled(false);
	}

	/**
	 * Timer event during running activity.
	 * @param event the timer event
	 */
	@Override
	public final void actionPerformed(final ActionEvent event) {
		updateDuration();
	}

	/**
	 * Updates the GUI with the current duration.
	 */
	private void updateDuration() {
		final Period period = new Period(this.model.getStart(), DateUtils.getNowAsDateTime());
		final String durationPrint = period.getHours() + ":" + MINUTE_FORMAT.format(period.getMinutes()) + " h";

		// Display duration
		duration.setText(durationPrint);
	}
}
