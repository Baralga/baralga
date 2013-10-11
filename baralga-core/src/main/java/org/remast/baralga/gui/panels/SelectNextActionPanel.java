package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class SelectNextActionPanel extends JPanel {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	PresentationModel model;

	/** The list of projects. The selected project is the currently active */
	private JComboBox<Project> projectSelector = null;

	/**
	 * The RadioButton to not start a project
	 */
	private JRadioButton btnNoAction;

	/**
	 * The Radio button to select a project to start
	 */
	private JRadioButton btnAction;

	private boolean isSaving = false;

	/**
	 * Create a new panel for the given model.
	 * 
	 * @param model
	 *            the model
	 */
	public SelectNextActionPanel(final PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		initialize();
	}

	private void initialize() {
		final double border = 5;
		final double[][] size = { { border, TableLayout.PREFERRED, border }, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } // Rows
		};

		this.setLayout(new TableLayout(size));

		JLabel introLabel = getIntroductionLabel();
		JPanel whatToTrackPanel = getProjectToStartPanel();

		this.add(introLabel, "1, 1");
		this.add(whatToTrackPanel, "1, 3");
	}

	/**
	 * {@inheritDoc}
	 */
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
			getProjectSelector().setSelectedItem(model.getSelectedProject());
			btnNoAction.setSelected(true);
			break;

		case BaralgaEvent.USER_IS_INACTIVE:
			break;
		}
	}

	private JLabel getIntroductionLabel() {
		final JLabel introductionLabel = new JLabel(textBundle.textFor("SelectNextActionPanel.IntroductionLabel.Title")); //$NON-NLS-1$
		// introductionLabel.setFont(FONT_BIG);

		return introductionLabel;
	}

	private JPanel getProjectToStartPanel() {
		final double border = 1;
		final double[][] size = { { border, TableLayout.PREFERRED, border, 400, border }, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } // Rows
		};
		JPanel panel = new JPanel(new TableLayout(size));

		// Create the radio buttons.
		btnNoAction = new JRadioButton( textBundle.textFor("SelectNextActionPanel.ButtonNoAction.Title")); //$NON-NLS-1$
		btnNoAction.setMnemonic(KeyEvent.VK_T);
		btnNoAction.setSelected(true);

		btnAction = new JRadioButton(textBundle.textFor("SelectNextActionPanel.ButtonAction.Title")); //$NON-NLS-1$
		btnAction.setMnemonic(KeyEvent.VK_C);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(btnNoAction);
		group.add(btnAction);

		panel.add(btnNoAction, "1, 1"); //$NON-NLS-1$
		panel.add(btnAction, "1, 3"); //$NON-NLS-1$
		panel.add(getProjectSelector(), "3, 3"); //$NON-NLS-1$

		return panel;
	}

	/**
	 * This method initializes projectSelector.
	 * 
	 * @return javax.swing.JComboBox
	 */
	@SuppressWarnings("unchecked")
	private JComboBox<Project> getProjectSelector() {
		if (projectSelector == null) {
			projectSelector = new JComboBox<Project>();
			projectSelector.setToolTipText(textBundle.textFor("SelectNextActionPanel.ProjectSelector.Hint")); //$NON-NLS-1$
			projectSelector.setModel(new EventComboBoxModel<Project>(this.model.getProjectList()));
		}
		return projectSelector;
	}

	public void save() {
		// If "no action" is selected, there is nothing to do.
		if (this.btnNoAction.isSelected()) {
			return;
		}

		if (this.btnAction.isSelected()) {
			try {
				Project selectedProject = (Project) projectSelector.getSelectedItem();
				model.changeProject(selectedProject);

				if (!model.isActive()) {
					model.start();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
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
