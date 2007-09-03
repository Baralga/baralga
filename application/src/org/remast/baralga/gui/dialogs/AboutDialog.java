package org.remast.baralga.gui.dialogs;

import java.awt.Color;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXLabel;
import org.remast.baralga.Messages;

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
     * This method initializes this
     * 
     * @return void
     */
    /**
     * 
     */
    private void initialize() {
        JXLabel label = new JXLabel(Messages.getString("Global.Title") + " " + Messages.getString("Global.Version") + " " + Messages.getString("Global.VersionNumber"));
        this.add(label);
        this.setSize(200, 100);
        label.setBackground(Color.WHITE);
    }
    
}
