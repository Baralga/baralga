package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;

import org.apache.commons.lang.StringUtils;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.ActivityPanelStopWatch;
import org.remast.baralga.gui.panels.MotionPanel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.swing.JTextEditor;
import org.remast.swing.util.GuiConstants;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class StopWatch extends JFrame implements ActionListener {

	private static final String BUTTON_ARROW = "BUTTON_ARROW";

    private static final int MIN_WINDOWS_HIGHT = 40;
    private static final int MAX_WINDOWS_HIGHT = 150;
    private static final int WINDOWS_WIDTH = 280;

    /** The model. */
	private final PresentationModel model;

	/** The panel with details about the current activity. Like the current project and description */
	private JPanel currentActivityPanel = null;
	
	/** Button to collapse/extend the Desciption area */
	private BasicArrowButton buttonArrow;

    /** The description editor. */
    private JTextEditor descriptionEditor;

	public StopWatch(PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);
		
		this.setUndecorated(true);
		this.setType(javax.swing.JFrame.Type.UTILITY);

		initialize();
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
            break;

        case BaralgaEvent.STOPWATCH_VISIBILITY_CHANGED:
            this.setVisible(UserSettings.instance().isShowStopwatch());
            break;

        case BaralgaEvent.PROJECT_REMOVED:
            break;

        case BaralgaEvent.START_CHANGED:
            break;

//        case BaralgaEvent.DESCRIPTION_CHANGED:
//            //Do not handle events, fired from my own descriptionEditor
//            if (descriptionEditor.equals(event.getData())) {
//                return;
//            }
//            
//            //We are only interested in description changes, sent by JTextEditor
//            if (!event.getData().getClass().equals(JTextEditor.class)) {
//                return;
//            }
//
//            //Check wheter text is already set to avoid seesaw changes
//            JTextEditor sendingEditor = (JTextEditor)event.getData();
//            if (descriptionEditor.getText().equals(sendingEditor.getText())) {
//                return;
//            }
//            
//            descriptionEditor.setText(sendingEditor.getText());
//            break;
        }
    }

	private void initialize() {
		// setUndecorated(true); // Remove title bar
		setAlwaysOnTop(true);

		Border raisedBorder = BorderFactory.createRaisedBevelBorder();

		JComponent cont = (JComponent) getContentPane();
		cont.setBorder(raisedBorder);
		cont.setLayout(new FlowLayout());

		setLocationByPlatform(true);
		pack();

		this.setSize(WINDOWS_WIDTH, MIN_WINDOWS_HIGHT);
		if (UserSettings.instance().isRememberWindowSizeLocation()) {
			// this.setSize(UserSettings.instance().getStopwatchWindowSize());
			// //Currently stopwatch is not resizeable
			this.setLocation(UserSettings.instance().getStopwatchWindowLocation());
		}

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentMoved(final ComponentEvent e) {
				UserSettings.instance().setStopwatchWindowLocation(StopWatch.this.getLocation());
			}

			@Override
			public void componentResized(final ComponentEvent e) {
				UserSettings.instance().setStopwatchWindowSize(StopWatch.this.getSize());
			}

		});

		// 2. Set layout
		final double[][] size = { { TableLayout.FILL, 5, TableLayout.PREFERRED }, // Columns
				{ TableLayout.PREFERRED, 5, TableLayout.FILL, 1 } // Rows
		};

		final TableLayout tableLayout = new TableLayout(size);
		this.setLayout(tableLayout);
		
		buttonArrow = new BasicArrowButton(BasicArrowButton.SOUTH);
		buttonArrow.addActionListener(this);
		buttonArrow.setActionCommand(BUTTON_ARROW);
		
        descriptionEditor = new JTextEditor(true);
        descriptionEditor.setBorder(
                BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY)
        );
        descriptionEditor.addTextObserver(new JTextEditor.TextChangeObserver() {

            public void onTextChange() {
                final String description = descriptionEditor.getText();

//                //Check wheter text is already set to avoid seesaw changes
//                if (description.equals(model.getDescription())) {
//                    return;
//                }

                // Store in model
                model.setDescription(description);

                // Save description in settings.
                UserSettings.instance().setLastDescription(description);
//                model.fireDescriptionChangedEvent(descriptionEditor); 
            }
        });

        descriptionEditor.setText(model.getDescription());
        descriptionEditor.setEditable(model.isActive());
		
		
		this.add(getCurrentActivityPanel(), "0, 0");
		this.add(buttonArrow, "2, 0");
		
		this.add(descriptionEditor, "0, 2, 2, 2");

		MotionPanel motionPanel = new MotionPanel(this);
		this.add(motionPanel, "0, 0");
	}

	/**
	 * This method initializes currentPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCurrentActivityPanel() {
		if (currentActivityPanel == null) {
			currentActivityPanel = new ActivityPanelStopWatch(model);
		}
		return currentActivityPanel;
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
        case BUTTON_ARROW:
            switch (buttonArrow.getDirection()) {
            case BasicArrowButton.SOUTH:
                this.setSize(WINDOWS_WIDTH, MAX_WINDOWS_HIGHT);
                buttonArrow.setDirection(BasicArrowButton.NORTH);
                break;
            default:
                this.setSize(WINDOWS_WIDTH, MIN_WINDOWS_HIGHT);
                buttonArrow.setDirection(BasicArrowButton.SOUTH);
            }
            break;
        }

    }


    /**
     * Executed on start event.
     */
    private void updateStart() {
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);
    }
}
