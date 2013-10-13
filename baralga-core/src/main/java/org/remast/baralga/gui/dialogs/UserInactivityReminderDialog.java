package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.SelectLastActionPanel;
import org.remast.baralga.gui.panels.SelectNextActionPanel;
import org.remast.util.TextResourceBundle;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class UserInactivityReminderDialog extends JDialog implements ActionListener {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(UserInactivityReminderDialog.class);
	
	private PresentationModel model;

	private SelectLastActionPanel lastActionPanel;
	
	private SelectNextActionPanel nextActionPanel;

	public UserInactivityReminderDialog(PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		setAlwaysOnTop(true);
		setLocationByPlatform(true);
		setVisible(false);
		
		this.setTitle(textBundle.textFor("UserInactivityReminderDialog.Title")); //$NON-NLS-1$

		final double border = 1;
		final double[][] size = { { border, TableLayout.PREFERRED, border }, // Columns
				{ border, TableLayout.PREFERRED, 30, TableLayout.PREFERRED, 30, TableLayout.PREFERRED, border } // Rows
		};

		setLayout(new TableLayout(size));

		lastActionPanel = new SelectLastActionPanel(model);
		nextActionPanel = new SelectNextActionPanel(model);
		JButton okButton = getOKButton();

		add(lastActionPanel, "1, 1");
		add(nextActionPanel, "1, 3");
		add(okButton, "1, 5");

		pack();
	}

	private JButton getOKButton() {
		JButton okBtn = new JButton("OK");
		okBtn.setVerticalTextPosition(AbstractButton.CENTER);
		okBtn.setHorizontalTextPosition(AbstractButton.LEADING);
		okBtn.setMnemonic(KeyEvent.VK_ENTER);
		okBtn.setActionCommand("OK");
		okBtn.addActionListener(this);

		return okBtn;
	}

	@Subscribe
	public void update(final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;

		switch (event.getType()) {
		case BaralgaEvent.PROJECT_ACTIVITY_STARTED:
		case BaralgaEvent.PROJECT_ACTIVITY_STOPPED:
		case BaralgaEvent.PROJECT_CHANGED:
		case BaralgaEvent.START_CHANGED:
			resetInactivity();
			break;

		case BaralgaEvent.USER_IS_INACTIVE:
			if (!isVisible()) {
				setVisible(true);
			}
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("OK".equals(e.getActionCommand())) {
			try {
				lastActionPanel.beginSaving();
				nextActionPanel.beginSaving();

				resetInactivity();
				lastActionPanel.save();
				nextActionPanel.save();
			} finally {
				lastActionPanel.endSaving();
				nextActionPanel.endSaving();
			}
		}
	}

	private void resetInactivity() {
		model.resetUserInactivityState();
		setVisible(false);
	}
}
