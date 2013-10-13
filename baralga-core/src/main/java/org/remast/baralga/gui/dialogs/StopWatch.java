package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.Border;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.ActivityPanelStopWatch;
import org.remast.baralga.gui.panels.MotionPanel;
import org.remast.baralga.gui.settings.UserSettings;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class StopWatch extends JWindow {

	/** The model. */
	private final PresentationModel model;

	/**
	 * The panel with details about the current activity. Like the current
	 * project and description.
	 */
	private JPanel currentActivityPanel = null;

	public StopWatch(PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		initialize();
	}

	@Subscribe
	public void update(final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;

		if (event.getType() == BaralgaEvent.STOPWATCH_VISIBILITY_CHANGED) {
			this.setVisible(UserSettings.instance().isShowStopwatch());
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

		this.setSize(250, 32);
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
		final double[][] size = { { TableLayout.FILL }, // Columns
				{ TableLayout.FILL } // Rows
		};

		final TableLayout tableLayout = new TableLayout(size);
		this.setLayout(tableLayout);
		this.add(getCurrentActivityPanel(), "0, 0");

		MotionPanel motionPanel = new MotionPanel(this);
		this.add(motionPanel, "0, 0");

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
}
