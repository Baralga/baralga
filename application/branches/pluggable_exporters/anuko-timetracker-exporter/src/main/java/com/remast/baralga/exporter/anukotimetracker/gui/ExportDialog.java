package com.remast.baralga.exporter.anukotimetracker.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.ReadableBaralgaData;
import org.remast.baralga.model.filter.Filter;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.util.TextResourceBundle;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoActivity;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;
import com.remast.baralga.exporter.anukotimetracker.util.AnukoAccess;

/**
 * Dialog for exporting to Anuko time tracker
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class ExportDialog extends EscapeDialog {

    private static final Log log = LogFactory.getLog(ExportDialog.class);
    
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExportDialog.class);

    private final AnukoAccess anukoAccess;
    
    private AnukoInfo anukoInfo;
    
    private final ReadableBaralgaData data;

    private final Filter filter;

    private JTextField url;
    
    private ProjectMappingPanel panel;
    
    private JLabel warningLabel;
    
    private JButton exportButton;

    private DateTime startDate;

    private DateTime endDate;

    /**
     * Create a new dialog.
     * @param owner
     * @param model
     */
    public ExportDialog(final Frame owner, final String url,
            final ReadableBaralgaData data, final Filter filter ) {
        super(owner);
        if( filter == null ) {
            throw new NullPointerException("filter must not be null!");
        }
        this.anukoAccess = new AnukoAccess( url, "kutzi_user", "moin" );
        this.data = data;
        this.filter = filter;

        initialize();
    }
    
    private void initRelevantDates() {
        List<ProjectActivity> activities = this.filter.applyFilters(this.data.getActivities());

        if( ! activities.isEmpty() ) {
            Collections.sort(activities);
            // activities are now sorted in reverse order!
            // I.e. the latest activity is the first element!

            this.endDate = activities.get(0).getDay();
            this.startDate = activities.get(activities.size() - 1).getDay();
        }
    }

    /**
     * Sets up GUI components.
     */
    private void initialize() {
        initRelevantDates();
        getWarningLabel();
        updateInfo();
        
        setLocationRelativeTo(getOwner());
        //this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
        this.setMinimumSize(new Dimension(250, 100));
        setTitle(textBundle.textFor("ExportDialog.Title")); //$NON-NLS-1$
        setModal(true);

        initializeLayout();

        // Set default Button to AddActtivityButton.
        this.getRootPane().setDefaultButton(exportButton);
    }

    private void updateInfo() {
        if( this.startDate != null ) {
            try {
                this.anukoInfo = this.anukoAccess.getMergedAnukoInfo(this.startDate, this.endDate);
                
                if( anukoInfo.getDailyTime().isLongerThan(Duration.ZERO)) {
                    
                    DecimalFormat df = new DecimalFormat("0.00");
                    double hours = anukoInfo.getDailyTime().getStandardSeconds() / (60D * 60);
                    this.warningLabel.setText("<html><b>" +
                    		"You have already " + df.format(hours) + "h recorded for that period!<br>" +
                    		"Exported activities will be added to the existing!</b></html>");
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        } else {
            this.anukoInfo = new AnukoInfo();
        }
    }
    
    private void initializeLayout() {
        final double border = 5;
        final double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border,
                    TableLayout.PREFERRED, border, TableLayout.PREFERRED, border } }; // Rows

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        
        this.add( new JLabel("URL :"), "1, 1");
        this.add( getUrlTextField(), "3, 1");
        
        this.add(getMappingPanel(), "0, 3, 4, 3");
        
        this.add(getWarningLabel(), "1, 5, 3, 5");

        this.add(getExportButton(), "1, 7, 3, 7");
    }

    private JTextField getUrlTextField() {
        if( this.url == null ) {
            String text = this.anukoAccess.getUrl();
            this.url = new JTextField(text);
            this.url.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    anukoAccess.setUrl( url.getText() );
                    updateInfo();
                }
            });
        }
        return this.url;
    }
    
    private JLabel getWarningLabel() {
        if( warningLabel == null ) {
            warningLabel = new JLabel();
            warningLabel.setForeground(Color.RED);
            warningLabel.setText("<html><br></html>");
        }
        return warningLabel;
    }

    private JPanel getMappingPanel() {
        if( panel == null ) {
            panel = new ProjectMappingPanel( this.anukoInfo, this.data, this.filter,
                    getExportButton() );
        }
        return panel;
    }
    
    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText(textBundle.textFor("ExportDialog.ExportLabel")); //$NON-NLS-1$
            //addActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$

            // Confirm with 'Enter' key
            exportButton.setMnemonic(KeyEvent.VK_ENTER);

            exportButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    System.out.println( "Mappings: ");
                    Map<Project, AnukoActivity> mappings = panel.getMappings();
                    for( Map.Entry<Project, AnukoActivity> e : mappings.entrySet()) {
                        System.out.println( e.getKey() + " => " + e.getValue().getName());
                    }
                    ExportDialog.this.dispose();
                }
            });
            exportButton.setEnabled(false);

            exportButton.setDefaultCapable(true);
        }
        return exportButton;
    }

    public String getLastUrl() {
        return this.url.getText();
    }
}
