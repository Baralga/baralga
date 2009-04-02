package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.Period;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.MainFrame;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.swing.text.TextEditor;
import org.remast.swing.util.GuiConstants;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * Panel for capturing new activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class ActivityPanel extends JPanel implements Observer, ActionListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    /** Big font for labels. */
    private static final Font FONT_BIG = new Font("Sans Serif", Font.PLAIN, 14);

    /** Big bold font for labels. */
    private static final Font FONT_BIG_BOLD = new Font("Sans Serif", Font.BOLD, 14);

    /** Text for inactive start field. */
    private static final String START_INACTIVE = "--:--";

    /** Text for inactive duration. */
    private static final String DURATION_INACTIVE = "-:-- h";

    /** Text for started duration. */
    private static final String DURATION_START_TIME = "0:00 h";

    /** The model. */
    private final PresentationModel model;

    /** Starts/stops the active project. */
    private JButton startStopButton = null;

    /** The list of projects. The selected project is the currently active project. */
    private JComboBox projectSelector = null;

    /** The description editor. */
    private TextEditor descriptionEditor;

    /** Timer for the time passed since activity was started. */
    private Timer timer;

    /** Displays the duration of the running activity. */
    private JLabel duration;

    /** Displays the start time of the running activity. */
    private JFormattedTextField start;

    /** Format for minutes. */
    private static NumberFormat MINUTE_FORMAT = new DecimalFormat("##00");

    /**
     * Create a new panel for the given model.
     * @param model the model
     */
    public ActivityPanel(final PresentationModel model) {
        this.model = model;
        this.model.addObserver(this);

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

        // 2. Restore selected project if set.
        if (this.model.getData().getActiveProject() != null) {
            this.getProjectSelector().setSelectedItem(
                    this.model.getData().getActiveProject()
            );
        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(
                        this.model.getProjectList().get(0)
                );
            }
        }

        final double border = 5;
        final double size[][] = {
                { border, 0.4, border, 0.6, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }  // Rows
        };

        this.setLayout(new TableLayout(size));

        descriptionEditor = new TextEditor(true);
        descriptionEditor.setBorder(
                BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY)
        );
        descriptionEditor.setPreferredSize(new Dimension(200, 100));
        descriptionEditor.setCollapseEditToolbar(false);
        descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

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

        final double buttonPanelSize [][] = {
                { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                { 0, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL, border * 2 } // Rows
        };

        buttonPanel.setLayout(new TableLayout(buttonPanelSize));

        buttonPanel.add(getStartStopButton(), "1, 1, 3, 1");
        buttonPanel.add(getProjectSelector(), "1, 3, 3, 3");

        start = new JFormattedTextField(FormatUtils.createTimeFormat());
        start.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));
        start.setBorder(BorderFactory.createEmptyBorder());
        start.setText(START_INACTIVE);
        start.setFont(FONT_BIG_BOLD);
        start.setForeground(Color.BLUE);
        start.setEnabled(false);

        // Do not enable edit for now
        start.setEditable(false);

        final int borderSmall = 3;

        final JXPanel startPanel = new JXPanel();
        final double startPanelSize [][] = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        startPanel.setLayout(new TableLayout(startPanelSize));

        final JLabel startLabel = new JLabel(textBundle.textFor("ActivityPanel.StartLabel"));
        startLabel.setFont(FONT_BIG);
        startLabel.setToolTipText(textBundle.textFor("ActivityPanel.Start.ToolTipText"));

        startPanel.add(startLabel, "0, 0");
        startPanel.add(start, "2, 0");

        buttonPanel.add(startPanel, "1, 5");

        duration = new JLabel(DURATION_INACTIVE);
        duration.setFont(FONT_BIG_BOLD);
        duration.setForeground(Color.BLUE);
        duration.setEnabled(false);
        duration.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText"));
        
        final JXPanel timerPanel = new JXPanel();
        final double [][] doublePanelSize = new double [][] {
                { TableLayout.PREFERRED, borderSmall, TableLayout.FILL }, // Columns
                { TableLayout.FILL } // Rows
        };
        timerPanel.setLayout(new TableLayout(doublePanelSize));

        final JLabel durationLabel = new JLabel(textBundle.textFor("ActivityPanel.DurationLabel"));
        durationLabel.setFont(FONT_BIG);
        durationLabel.setForeground(Color.DARK_GRAY);
        durationLabel.setToolTipText(textBundle.textFor("ActivityPanel.Duration.ToolTipText"));

        timerPanel.add(durationLabel, "0, 0");
        timerPanel.add(duration, "2, 0");

        buttonPanel.add(timerPanel, "3, 5");

        this.add(new JXTitledSeparator(textBundle.textFor("ActivityPanel.ActivityLabel")), "1, 1, 3, 1");
        this.add(buttonPanel, "1, 3");
        this.add(descriptionEditor, "3, 3");
    }

    /**
     * This method initializes startStopButton.
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton(new StartAction(null, this.model));
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
            projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText"));
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

    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
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
            }
        }
    }

    /**
     * Executed on project changed event.
     */
    private void updateProjectChanged(final BaralgaEvent event) {
        getProjectSelector().setSelectedItem((Project) event.getData());

        start.setValue(this.model.getStart().toDate());

        duration.setText(DURATION_START_TIME);
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

        start.setValue(this.model.getStart().toDate());
        start.setEnabled(true);

        duration.setText(DURATION_START_TIME);
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
     */
    @Override
    public final void actionPerformed(final ActionEvent event) {
        final Period period = new Period(
                this.model.getStart(), 
                DateUtils.getNowAsDateTime()
        );
        final String durationPrint = period.getHours() + ":" + MINUTE_FORMAT.format(period.getMinutes()) + " h";

        // Display duration
        duration.setText(durationPrint);
    }

}
