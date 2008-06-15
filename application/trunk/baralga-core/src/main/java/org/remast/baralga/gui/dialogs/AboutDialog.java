package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.utils.BrowserControlAction;
import org.remast.gui.dialog.EscapeDialog;
import org.remast.gui.util.Constants;

/**
 * Displays information about the application like version and
 * homepage.
 * @author remast
 */
@SuppressWarnings("serial")
public class AboutDialog extends EscapeDialog {

    public AboutDialog(final Frame owner) {
        super(owner);
        
        this.setName("aboutDialog");
        setTitle(Messages.getString("AboutDialog.AboutTitle")); //$NON-NLS-1$
        this.setAlwaysOnTop(true);
        setModal(true);
        setResizable(true);
        setBackground(Color.WHITE);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setLayout(new BorderLayout());
        
        JXImagePanel image = new JXImagePanel(getClass().getResource("/icons/Baralga-About.png"));
        image.setBackground(Constants.BEIGE);
        
        JXPanel aboutInfo = new JXPanel();
        aboutInfo.setBackground(Constants.BEIGE);
        double border = 5;
        double size[][] = { { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border*2} }; // Rows

        TableLayout tableLayout = new TableLayout(size);
        aboutInfo.setLayout(tableLayout);
        
        aboutInfo.add(new JLabel(Messages.getString("AboutDialog.HomepageLabel")), "1, 1");
        JXHyperlink hyperlinkHomepage = new JXHyperlink(new BrowserControlAction(Messages.getString("AboutDialog.HomepageUrl")));
        aboutInfo.add(hyperlinkHomepage, "3, 1");

        aboutInfo.add(new JLabel(Messages.getString("AboutDialog.BugLabel")), "1, 3");
        JXHyperlink hyperlinkBug = new JXHyperlink(new BrowserControlAction(Messages.getString("AboutDialog.BugUrl")));
        aboutInfo.add(hyperlinkBug, "3, 3");
        
        this.add(aboutInfo, BorderLayout.CENTER);
        
        JLabel versionLabel = new JXLabel("<html><font color=blue size=\"big\"><h2>" + Messages.getString("Global.Version") + " " + Messages.getString("Global.VersionNumber") + "</h2></font></html>", JLabel.CENTER);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        this.add(versionLabel, BorderLayout.SOUTH);
        
        this.getContentPane().setBackground(Constants.BEIGE);
        this.add(image, BorderLayout.NORTH);
        
        this.setSize(260, 280);
    }
    
}
