package org.remast.baralga.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXLabel;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.utils.Constants;

/**
 * @author remast
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

    public AboutDialog(final Frame owner) {
        super(owner);
        
        setTitle(Messages.getString("AboutDialog.AboutTitle")); //$NON-NLS-1$
        this.setAlwaysOnTop(true);
        setModal(true);
        setResizable(false);
        setBackground(Color.WHITE);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setLayout(new BorderLayout());
        
        JXImagePanel image = new JXImagePanel(getClass().getResource("/resource/icons/Baralga-About.png"));
        image.setBackground(Constants.BEIGE);

        JEditorPane aboutInfo = new JEditorPane();
        aboutInfo.setContentType("text/html");
        aboutInfo.setEditable(false);
        aboutInfo.setText(Messages.getString("AboutInfo"));
        aboutInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        aboutInfo.setBackground(Constants.BEIGE);
        this.add(aboutInfo, BorderLayout.CENTER);
        
        JLabel versionLabel = new JXLabel("<html><font color=blue size=\"big\"><h2>" + Messages.getString("Global.Version") + " " + Messages.getString("Global.VersionNumber") + "</h2></font></html>", JLabel.CENTER);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.add(versionLabel, BorderLayout.SOUTH);
        
        this.getContentPane().setBackground(Constants.BEIGE);
        this.add(image, BorderLayout.NORTH);

        
        this.setSize(260, 280);
    }
    
}
