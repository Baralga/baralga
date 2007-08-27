package org.remast.baralga.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExcelReportDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel settingsPanel = null;

    private JButton jButton = null;

    private JLabel fileLabel = null;

    private JPanel filejPanel = null;

    private JLabel projectLabel = null;

    private JComboBox projectComboBox = null;

    private JLabel monthLabel = null;

    private JComboBox monthComboBox = null;

    private JTextField fileTextField = null;

    private JButton fileSelectButton = null;

    /**
     * @param owner
     */
    public ExcelReportDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            projectLabel = new JLabel();
            projectLabel.setText("Year:");
            fileLabel = new JLabel();
            fileLabel.setText("JLabel");
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getSettingsPanel(), BorderLayout.CENTER);
            jContentPane.add(getJButton(), BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    /**
     * This method initializes settingsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSettingsPanel() {
        if (settingsPanel == null) {
            monthLabel = new JLabel();
            monthLabel.setText("Month:");
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(3);
            gridLayout.setColumns(2);
            settingsPanel = new JPanel();
            settingsPanel.setLayout(gridLayout);
            settingsPanel.add(fileLabel, null);
            settingsPanel.add(getFilejPanel(), null);
            settingsPanel.add(projectLabel, null);
            settingsPanel.add(getProjectComboBox(), null);
            settingsPanel.add(monthLabel, null);
            settingsPanel.add(getMonthComboBox(), null);
        }
        return settingsPanel;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("Export");
        }
        return jButton;
    }

    /**
     * This method initializes filejPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getFilejPanel() {
        if (filejPanel == null) {
            GridLayout gridLayout1 = new GridLayout();
            gridLayout1.setRows(1);
            filejPanel = new JPanel();
            filejPanel.setLayout(gridLayout1);
            filejPanel.add(getFileTextField(), null);
            filejPanel.add(getFileSelectButton(), null);
        }
        return filejPanel;
    }

    /**
     * This method initializes projectComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getProjectComboBox() {
        if (projectComboBox == null) {
            projectComboBox = new JComboBox();
        }
        return projectComboBox;
    }

    /**
     * This method initializes monthComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getMonthComboBox() {
        if (monthComboBox == null) {
            monthComboBox = new JComboBox();
        }
        return monthComboBox;
    }

    /**
     * This method initializes fileTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getFileTextField() {
        if (fileTextField == null) {
            fileTextField = new JTextField();
        }
        return fileTextField;
    }

    /**
     * This method initializes fileSelectButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getFileSelectButton() {
        if (fileSelectButton == null) {
            fileSelectButton = new JButton();
        }
        return fileSelectButton;
    }

}
