package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXImagePanel;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.swing.action.OpenBrowserAction;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.swing.util.GuiConstants;
import org.remast.util.TextResourceBundle;

/**
 * Displays information about the application like version and homepage.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AboutDialog extends EscapeDialog {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(AboutDialog.class);

    /**
     * Creates a new dialog.
     * @param owner the owning frame
     */
    public AboutDialog(final Frame owner) {
        super(owner);

        this.setName("aboutDialog"); //$NON-NLS-1$
        setTitle(textBundle.textFor("AboutDialog.Title")); //$NON-NLS-1$
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

        final JXImagePanel image = new JXImagePanel(getClass().getResource("/images/baralga/Baralga-About.png")); //$NON-NLS-1$
        image.setBackground(GuiConstants.BEIGE);

        final JPanel aboutInfo = new JPanel();
        aboutInfo.setBackground(GuiConstants.BEIGE);
        final double border = 5;
        final double[][] size = { 
                {border * 3, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                {border * 5, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border * 2} // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        aboutInfo.setLayout(tableLayout);

        aboutInfo.add(new JLabel(textBundle.textFor("AboutDialog.HomepageLabel")), "1, 1"); //$NON-NLS-1$ //$NON-NLS-2$
        final JXHyperlink hyperlinkHomepage = new JXHyperlink(new OpenBrowserAction(textBundle.textFor("AboutDialog.HomepageUrl"))); //$NON-NLS-1$
        aboutInfo.add(hyperlinkHomepage, "3, 1"); //$NON-NLS-1$

        aboutInfo.add(new JLabel(textBundle.textFor("AboutDialog.BugLabel")), "1, 3"); //$NON-NLS-1$ //$NON-NLS-2$
        final JXHyperlink hyperlinkBug = new JXHyperlink(new OpenBrowserAction(textBundle.textFor("AboutDialog.BugUrl"))); //$NON-NLS-1$
        aboutInfo.add(hyperlinkBug, "3, 3"); //$NON-NLS-1$

        aboutInfo.add(new JLabel(textBundle.textFor("Global.Version") + ":"), "1, 5"); //$NON-NLS-1$ //$NON-NLS-2$
        final JLabel version = new JLabel(BaralgaMain.class.getPackage().getImplementationVersion()); //$NON-NLS-1$
        aboutInfo.add(version, "3, 5"); //$NON-NLS-1$

        aboutInfo.add(new JLabel(textBundle.textFor("AboutDialog.ModeLabel")), "1, 7"); //$NON-NLS-1$ //$NON-NLS-2$
        // Get storage mode from ApplicationSettings
        String storageMode = null;
        if (ApplicationSettings.instance().isStoreDataInApplicationDirectory()) {
            storageMode = textBundle.textFor("Settings.DataStorage.PortableLabel");
        } else {
            storageMode = textBundle.textFor("Settings.DataStorage.NormalLabel");
        }
        final JLabel mode = new JLabel(storageMode); //$NON-NLS-1$
        aboutInfo.add(mode, "3, 7"); //$NON-NLS-1$
        
        aboutInfo.add(new JLabel(textBundle.textFor("AboutDialog.LicenceLabel")), "1, 9"); //$NON-NLS-1$ //$NON-NLS-2$
        final JLabel licence = new JLabel("GNU Lesser General Public License (LGPL)"); //$NON-NLS-1$
        aboutInfo.add(licence, "3, 9"); //$NON-NLS-1$

        this.add(aboutInfo, BorderLayout.CENTER);

        this.getContentPane().setBackground(GuiConstants.BEIGE);
        this.add(image, BorderLayout.NORTH);

        this.setSize(340, 320);   
    }

}
