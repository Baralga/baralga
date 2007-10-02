package org.remast.baralga.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXLabel;
import org.remast.baralga.Messages;

/**
 * @author remast
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

    public AboutDialog() {
        super();
        
        setTitle(Messages.getString("AboutDialog.AboutTitle")); //$NON-NLS-1$
        setModal(true);
        setResizable(false);
        setBackground(Color.WHITE);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        
        JXLabel label = new JXLabel("<html><font color=\"blue\" size=\"big\"><h4>" +Messages.getString("Global.Title") + " <br/> " + Messages.getString("Global.Version") + " " + Messages.getString("Global.VersionNumber") + "</h4></font></html>", JLabel.CENTER);
        this.add(label, BorderLayout.CENTER);
        this.setSize(200, 100);
        label.setBackground(Color.WHITE);
    }
    
}
