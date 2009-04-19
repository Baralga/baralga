package com.remast.baralga.exporter.anukotimetracker.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.ReadableBaralgaData;
import org.remast.baralga.model.filter.Filter;
import org.remast.swing.util.LabeledItem;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoActivity;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoProject;

/**
 * A panel which helps matching the Baralga {@link Project}s to
 * the Anuko activities.
 */
@SuppressWarnings("serial")
public class ProjectMappingPanel extends JXPanel {

    private final AnukoInfo info;
    private final ReadableBaralgaData baralgaData;
    private final Filter filter;
    private final JButton exportButton;
    private SortedSet<Project> baralgaProjects;
    
    private final Map<Project, AnukoActivity> mappings = new HashMap<Project, AnukoActivity>();

    
    public ProjectMappingPanel(AnukoInfo info, ReadableBaralgaData baralgaData,
            Filter filter, JButton exportButton) {
        super();
        this.info = info;
        this.baralgaData = baralgaData;
        this.filter = filter;
        this.exportButton = exportButton;
        
        initialize();
    }

    private void initialize() {
        final double borderBig = 8;
        final double border = 3;
        
        int numberOfRows = getBaralgaProjects().size();
        double[] rowLayout = new double[numberOfRows*2 + 1];
        for( int i=0; i < numberOfRows; i++ ) {
            int row = i*2;
            rowLayout[row] = border;
            rowLayout[row+1] = TableLayout.PREFERRED;
        }
        
        if( rowLayout.length >= 2 ) {
            rowLayout[rowLayout.length-2] = TableLayout.FILL;
            rowLayout[rowLayout.length-1] = border;
        }
        
        final double size[][] = {
                { border, TableLayout.PREFERRED, borderBig, TableLayout.FILL}, // Columns
                rowLayout };
        this.setLayout(new TableLayout(size));
        
        int row = 1;
        for( Project p : getBaralgaProjects() ) {
            JXLabel label = new JXLabel(p.getTitle());
            label.setToolTipText(p.getDescription());
            this.add(label, "1, " + row); //$NON-NLS-1$
            this.add(getProjectSelector(p, info.getActivities()), "3, " + row); //$NON-NLS-1$
            row = row+2;
        }
    }
    
    private SortedSet<Project> getBaralgaProjects() {
        if( this.baralgaProjects == null ) {
            if( filter == null ) {
                this.baralgaProjects = new TreeSet<Project>(this.baralgaData.getProjects());
            } else {
                // return only those projects which have matching activities
                List<ProjectActivity> activities = this.baralgaData.getActivities();
                activities = filter.applyFilters(activities);
                SortedSet<Project> projects = new TreeSet<Project>();
                for( ProjectActivity activity : activities ) {
                    projects.add(activity.getProject());
                }
                this.baralgaProjects = projects;
            }
        }
        return this.baralgaProjects;
    }
    
    private static final AnukoActivity NO_SELECTION = new AnukoActivity(-4711, "*"); //$NON-NLS-1$

    private static final LabeledItem<AnukoActivity> NO_SELECTION_ITEM = new LabeledItem<AnukoActivity>(NO_SELECTION, " "); //$NON-NLS-1$
    
    
    private JComboBox getProjectSelector(final Project p, Collection<AnukoActivity> activities) {
        
        EventList<LabeledItem<AnukoActivity>> eventList =
            new BasicEventList<LabeledItem<AnukoActivity>>();
        eventList.add(NO_SELECTION_ITEM);
        for( AnukoActivity activity : activities ) {
            eventList.add( new LabeledItem<AnukoActivity>(activity, activity.getName()) );
        }
        SortedList<LabeledItem<AnukoActivity>> projectList = new SortedList<LabeledItem<AnukoActivity>>(eventList);
            
        
        JComboBox projectFilterSelector = new JComboBox(
                new EventComboBoxModel<LabeledItem<AnukoActivity>>(projectList)
        );
        
        projectFilterSelector.setRenderer(new ComboTooltipRenderer() );

        // Select first entry
        projectFilterSelector.setSelectedIndex(0);

        projectFilterSelector.addActionListener(new ActionListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                LabeledItem<AnukoActivity> item = (LabeledItem<AnukoActivity>)
                    ((JComboBox)e.getSource()).getSelectedItem();
                if( item == NO_SELECTION_ITEM ) {
                    removeMapping(p);
                } else {
                    addMapping(p, item.getItem());
                }
            }
            
        });
        return projectFilterSelector;
    }
    
    private void addMapping( Project p, AnukoActivity a) {
        this.mappings.put(p, a);
        if( mappings.size() == getBaralgaProjects().size() ) {
            this.exportButton.setEnabled(true);
        }
    }
    
    private void removeMapping( Project p ) {
        this.mappings.remove(p);
        this.exportButton.setEnabled(false);
    }
    
    public static class ComboTooltipRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JComponent comp = (JComponent) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            StringBuilder toolTipText = null;
            
            if (value != null) {
                @SuppressWarnings("unchecked")
                LabeledItem<AnukoActivity> label = (LabeledItem<AnukoActivity>)value;
                AnukoActivity activity = label.getItem();
                if( activity.getProjects().size() > 0) {
                    toolTipText = new StringBuilder("Project(s): ");
                    for(AnukoProject p : activity.getProjects()) {
                        toolTipText.append( p.getName() ).append( ", ");
                    }
                    toolTipText.delete(toolTipText.length()-2, toolTipText.length());
                }
            }
            comp.setToolTipText(toolTipText != null ? toolTipText.toString() : null);
            return comp;
        }
    }
    
    public Map<Project, AnukoActivity> getMappings() {
        return this.mappings;
    }
}
