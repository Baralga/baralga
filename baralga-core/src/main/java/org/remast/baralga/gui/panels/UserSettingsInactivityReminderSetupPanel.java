package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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

import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class UserSettingsInactivityReminderSetupPanel extends JPanel implements ActionListener {
	
    private static final String ENABLE = "ENABLE";

    private static final String TODAY = "TODAY";

    private static final String DISABLE = "DISABLE";

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(UserSettingsInactivityReminderSetupPanel.class);
    
	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /** The label, showing a text reminding on the last selected project */
    private JLabel introLabel = null;

	/** The RadioButton that will be checked when no activity for the inactivity time should be tracked */
	private JRadioButton buttonEnableInactivityReminder;

    /** The Radio button, that will be checked when another Project ( or Activity) should be tracked */
    private JRadioButton buttonDisableInactivityReminderToday;

    /** The Radio button, that will be checked when another Project ( or Activity) should be tracked */
    private JRadioButton buttonDisableInactivityReminder;

	/**
	 * Create a new panel for the given model.
	 * @param model the model
	 */
	public UserSettingsInactivityReminderSetupPanel() {
	    final double border = 5;
        final double[][] size = { {TableLayout.PREFERRED}, // Columns
                { border, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL, border } // Rows
        };

        this.setLayout(new TableLayout(size));
        
        this.introLabel = new JLabel(textBundle.textFor("UserSettingsInactivityReminderSetupPanel.IntroductionLabel.Title")); //$NON-NLS-1$
        
        // Create the radio buttons.
        buttonEnableInactivityReminder = new JRadioButton(textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonEnableInactivityReminder.Title")); //$NON-NLS-1$
        buttonEnableInactivityReminder.setToolTipText((textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonEnableInactivityReminder.Hint"))); //$NON-NLS-1$
        buttonEnableInactivityReminder.setMnemonic(KeyEvent.VK_E);
        buttonEnableInactivityReminder.setActionCommand(ENABLE);
        buttonEnableInactivityReminder.addActionListener(this);

        buttonDisableInactivityReminderToday = new JRadioButton(textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonDisableInactivityReminderToday.Title")); //$NON-NLS-1$
        buttonDisableInactivityReminderToday.setToolTipText((textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonDisableInactivityReminderToday.Hint"))); //$NON-NLS-1$
        buttonDisableInactivityReminderToday.setMnemonic(KeyEvent.VK_T);
        buttonDisableInactivityReminderToday.setActionCommand(TODAY);
        buttonDisableInactivityReminderToday.addActionListener(this);

        buttonDisableInactivityReminder = new JRadioButton(textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonDisableActivitreminder.Title")); //$NON-NLS-1$
        buttonDisableInactivityReminder.setToolTipText((textBundle.textFor("UserSettingsInactivityReminderSetupPanel.ButtonDisableActivitreminder.Hint"))); //$NON-NLS-1$
        buttonDisableInactivityReminder.setMnemonic(KeyEvent.VK_D);
        buttonDisableInactivityReminder.setActionCommand(DISABLE);
        buttonDisableInactivityReminder.addActionListener(this);
        
        // Group the radio buttons.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(buttonEnableInactivityReminder);
        buttonGroup.add(buttonDisableInactivityReminderToday);
        buttonGroup.add(buttonDisableInactivityReminder);
        

        this.add(introLabel, "0, 1");
        this.add(buttonEnableInactivityReminder, "0, 3");
        this.add(buttonDisableInactivityReminder, "0, 5");
        this.add(buttonDisableInactivityReminderToday, "0, 7");	    
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
        case DISABLE:
            UserSettings.instance().setInactivityReminderDate(new DateMidnight(UserSettings.DEFAULT_INACTIVITYREMINDER_INACTIVATION_DATE));
            break;
            
        case ENABLE:
            UserSettings.instance().setInactivityReminderDate(new DateMidnight(UserSettings.DEFAULT_INACTIVITYREMINDER_ACTIVATION_DATE));
            break;

        case TODAY:
            UserSettings.instance().setInactivityReminderDate(DateMidnight.now());
            break;
        }

    }
    
    public void setDisplayValues() {
        DateMidnight savedValue = UserSettings.instance().getInactivityReminderDate();
        DateMidnight today = DateMidnight.now();
        
        buttonDisableInactivityReminder.setSelected(savedValue.equals(UserSettings.DEFAULT_INACTIVITYREMINDER_INACTIVATION_DATE));
        buttonDisableInactivityReminderToday.setSelected(today.equals(savedValue));
        buttonEnableInactivityReminder.setSelected(! (buttonDisableInactivityReminder.isSelected() || buttonDisableInactivityReminderToday.isSelected()));
    }
}
