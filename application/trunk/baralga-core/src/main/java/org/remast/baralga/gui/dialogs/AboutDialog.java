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
import org.remast.swing.action.OpenBrowserAction;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.swing.util.GuiConstants;

/**
 * Displays information about the application like version and homepage.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AboutDialog extends EscapeDialog {

    public AboutDialog(final Frame owner) {
        super(owner);
        
        this.setName("aboutDialog"); //$NON-NLS-1$
        setTitle(Messages.getString("AboutDialog.Title")); //$NON-NLS-1$
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
        
        final JXImagePanel image = new JXImagePanel(getClass().getResource("/icons/Baralga-About.png")); //$NON-NLS-1$
        image.setBackground(GuiConstants.BEIGE);
        
        final JXPanel aboutInfo = new JXPanel();
        aboutInfo.setBackground(GuiConstants.BEIGE);
        final double border = 5;
        final double size[][] = { { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border*2} }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        aboutInfo.setLayout(tableLayout);
        
        aboutInfo.add(new JLabel(Messages.getString("AboutDialog.HomepageLabel")), "1, 1"); //$NON-NLS-1$ //$NON-NLS-2$
        final JXHyperlink hyperlinkHomepage = new JXHyperlink(new OpenBrowserAction(Messages.getString("AboutDialog.HomepageUrl"))); //$NON-NLS-1$
        aboutInfo.add(hyperlinkHomepage, "3, 1"); //$NON-NLS-1$

        aboutInfo.add(new JLabel(Messages.getString("AboutDialog.BugLabel")), "1, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        final JXHyperlink hyperlinkBug = new JXHyperlink(new OpenBrowserAction(Messages.getString("AboutDialog.BugUrl"))); //$NON-NLS-1$
        aboutInfo.add(hyperlinkBug, "3, 3"); //$NON-NLS-1$
        
        this.add(aboutInfo, BorderLayout.CENTER);
        
        final JLabel versionLabel = new JXLabel("<html><font color=blue size=\"big\"><h2>" + Messages.getString("Global.Version") + " " + Messages.getString("Global.VersionNumber") + "</h2></font></html>", JLabel.CENTER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        this.add(versionLabel, BorderLayout.SOUTH);
        this.getContentPane().setBackground(GuiConstants.BEIGE);
        this.add(image, BorderLayout.NORTH);
        
        this.setSize(260, 280);
    }
    
}
