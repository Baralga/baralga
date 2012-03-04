package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.MainFrame;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.swing.JTextEditor;
import org.remast.swing.util.GuiConstants;
import org.remast.text.SmartTimeFormat;
import org.remast.text.TimeFormat;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.google.common.eventbus.Subscribe;

/**
 * Panel for capturing new activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class ActivityPanel extends JPanel implements ActionListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    /** Big font for labels. */
    private static final Font FONT_BIG = new Font("Sans Serif", Font.PLAIN, 14);

    /** Color for highlighted time and duration. */
    private static final Color HIGHLIGHT_COLOR = new Color(51, 0, 102);

    /** Big bold font for labels. */
    private static final Font FONT_BIG_BOLD = new Font("Sans Serif", Font.BOLD, 14);

    /** Text for inactive start field. */
    private static final String START_INACTIVE = "--:--";

    /** Text for inactive duration. */
    private static final String DURATION_INACTIVE = "-:-- h";

    /** The model. */
    private final PresentationModel model;

    /** Starts/stops the active project. */
    private JXButton startStopButton = null;
    
    private static final Icon ICON_START_ENABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Play-Hot-icon.png"));
    
    private static final Icon ICON_START_DISABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Play-Disabled-icon.png"));
    
    private static final Icon ICON_STOP_ENABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Stop-Normal-Blue-icon.png"));
    
    private static final Icon ICON_STOP_DISABLED = new ImageIcon(BaralgaMain.class.getResource("/icons/Stop-Disabled-icon.png"));
	
	/** The list of projects. The selected project is the currently active project. */
    private JComboBox projectSelector = null;

    /** The description editor. */
    private JTextEditor descriptionEditor;

    /** Timer for the time passed since activity was started. */
    private Timer timer;

    /** Displays the duration of the running activity. */
    private JLabel duration;

    /** Displays the start time of the running activity. */
    private JFormattedTextField start;

    /** Format for minutes. */
    private static NumberFormat MINUTE_FORMAT = new DecimalFormat("##00");

    /** Format for start time. */
	private TimeFormat timeFormat = new SmartTimeFormat();

    /**
     * Create a new panel for the given model.
     * @param model the model
     */
    public ActivityPanel(final PresentationModel model) {
        this.model = model;
        this.model.getEventBus().register(this);

        // Fire timer event every minute
        this.timer = new Timer(1000 * 60, this);

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
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
//        if (this.model.getData().getActiveProject() != null) {
//            this.getProjectSelector().setSelectedItem(
//                    this.model.getData().getActiveProject()
//            );
//        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(
                        this.model.getProjectList().get(0)
                );
            }
//        }

        final double border = 5;
        final double[][] size = {
                { border, 0.45, border, 0.55, border }, // Columns
                { 0, TableLayout.PREFERRED, border, TableLayout.FILL, 0 }  // Rows
        };

        this.setLayout(new TableLayout(size));

        descriptionEditor = new JTextEditor(true);
        descriptionEditor.setBorder(
                BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY)
        );
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

        final JXPanel buttonPanel = new JXPanel();

        final double[][] buttonPanelSize = {
                { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                { 0, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.PREFERRED, border * 2 } // Rows
        };

        buttonPanel.setLayout(new TableLayout(buttonPanelSize));

        buttonPanel.add(getStartStopButton(), "1, 1, 3, 1"); //$NON-NLS-1$
        buttonPanel.add(getProjectSelector(), "1, 3, 3, 3"); //$NON-NLS-1$

        start = new JFormattedTextField(new SmartTimeFormat());
        start.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));
        start.setBorder(BorderFactory.createEmptyBorder());
        start.setFont(FONT_BIG_BOLD);
        start.setForeground(HIGHLIGHT_COLOR);

        // Restore current activity
        if (model.isActive()) {
            start.setEnabled(true);
            start.setValue(this.model.getStart().toDate());
        } else {
            start.setText(START_INACTIVE);
            start.setEnabled(false);
        }

        start.setEditable(true);

        start.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent event) {
                changeStartTime();
            }

        });

        final int borderSmall = 3;

        final JXPanel startPanel = new JXPanel();
        final double[][] startPanelSize = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        startPanel.setLayout(new TableLayout(startPanelSize));

        final JLabel startLabel = new JLabel(textBundle.textFor("ActivityPanel.StartLabel"));
        startLabel.setFont(FONT_BIG);
        startLabel.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));

        startPanel.add(startLabel, "0, 0"); //$NON-NLS-1$
        startPanel.add(start, "2, 0"); //$NON-NLS-1$

        buttonPanel.add(startPanel, "1, 5"); //$NON-NLS-1$

        duration = new JLabel();
        duration.setFont(FONT_BIG_BOLD);
        duration.setForeground(HIGHLIGHT_COLOR);

        // Restore current activity
        if (model.isActive()) {
            duration.setEnabled(true);
            updateDuration();
        } else {
            duration.setEnabled(false);
            duration.setText(DURATION_INACTIVE);
        }
        duration.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText"));

        final JXPanel timerPanel = new JXPanel();
        final double [][] doublePanelSize = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        timerPanel.setLayout(new TableLayout(doublePanelSize));

        final JLabel durationLabel = new JLabel(textBundle.textFor("ActivityPanel.DurationLabel")); //$NON-NLS-1$
        durationLabel.setFont(FONT_BIG);
        durationLabel.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText")); //$NON-NLS-1$

        timerPanel.add(durationLabel, "0, 0"); //$NON-NLS-1$
        timerPanel.add(duration, "2, 0"); //$NON-NLS-1$

        buttonPanel.add(timerPanel, "3, 5"); //$NON-NLS-1$

        this.add(new JXHeader(textBundle.textFor("ActivityPanel.ActivityLabel"), null), "0, 1, 3, 1"); //$NON-NLS-1$ $NON-NLS-2$
        this.add(buttonPanel, "1, 3"); //$NON-NLS-1$
        this.add(descriptionEditor, "3, 3"); //$NON-NLS-1$
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
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox();
            projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText")); //$NON-NLS-1$
            projectSelector.setModel(new EventComboBoxModel<Project>(this.model.getProjectList()));

            /* Handling of selection events: */
            projectSelector.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // 1. Set current project to the just selected project.
                    final Project selectedProject = (Project) projectSelector.getSelectedItem();
                    ActivityPanel.this.model.changeProject(selectedProject);

                    // 2. Clear the description.
                    if (descriptionEditor != null) {
                        descriptionEditor.setText(StringUtils.EMPTY);
                    }
                }
            });
        }
        return projectSelector;
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe public final void update(final Object eventObject) {
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
        getProjectSelector().setSelectedItem((Project) event.getData());

        if (model.isActive()) {
            start.setValue(this.model.getStart().toDate());
            updateDuration();
        }
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        timer.start();

        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);

        // Change button from start to stop
        getStartStopButton().setAction(new StopAction(this.model));
        getStartStopButton().setText("");
        getStartStopButton().setIcon(ICON_STOP_DISABLED);
        getStartStopButton().setRolloverIcon(ICON_STOP_ENABLED);

        start.setValue(this.model.getStart().toDate());
        start.setEnabled(true);

        updateDuration();
        duration.setEnabled(true);
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        timer.stop();

        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);

        getStartStopButton().setAction(new StartAction(null, this.model));
        getStartStopButton().setText("");
        getStartStopButton().setIcon(ICON_START_DISABLED);
        getStartStopButton().setRolloverIcon(ICON_START_ENABLED);

        // Reset start time
        start.setValue(null);
        start.setText(START_INACTIVE);
        start.setEnabled(false);

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
        final Period period = new Period(
                this.model.getStart(), 
                DateUtils.getNowAsDateTime()
        );
        final String durationPrint = period.getHours() + ":" + MINUTE_FORMAT.format(period.getMinutes()) + " h";

        // Display duration
        duration.setText(durationPrint);
    }

    /**
     * Changes the start time to the time entered by the user manually.
     * The start time is validated so that it is before the current time.
     */
    private void changeStartTime() {
        if (StringUtils.isEmpty(start.getText())) {
            return;
        }

        // If new start time is equal to current start time there's nothing to do
        if (StringUtils.equals(start.getText(), FormatUtils.formatTime(model.getStart()))) {
            return;
        }

        // New start time must be before the current time.
        try {
            final Date newStartTime = timeFormat.parse(start.getText());
            final DateTime newStart = DateUtils.adjustToSameDay(
                    DateUtils.getNowAsDateTime(), 
                    new DateTime(newStartTime), 
                    false
            );

            final boolean correct = DateUtils.isBeforeOrEqual(
                    newStart, 
                    DateUtils.getNowAsDateTime()
            );

            if (correct) {
                model.setStart(newStart);
            } else {
                JOptionPane.showMessageDialog(
                        ActivityPanel.this, 
                        textBundle.textFor("ActivityPanel.StartTimeError.Message"),  //$NON-NLS-1$
                        textBundle.textFor("ActivityPanel.StartTimeError.Title"),  //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );

                start.setText(FormatUtils.formatTime(model.getStart()));
            }
        } catch (ParseException e) {
            return;
        }
    }

}
