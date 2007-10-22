package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

public class DescriptionPanel extends JXPanel implements Observer {

    /** The model. */
    private final PresentationModel model;


    /** The list of activities. */
    private SortedList<ProjectActivity> filteredActivitiesList;

    /** The applied filter. */
    private Filter filter;


    private JXTaskPaneContainer taskPaneContainer;

    public DescriptionPanel(PresentationModel model) {
        super();
        this.setLayout(new BorderLayout());
        this.model = model;
        this.filteredActivitiesList = new SortedList<ProjectActivity>(new BasicEventList<ProjectActivity>());
        this.model.addObserver(this);
        this.filter = model.getFilter();
        
        initialize();
    }

    private void initialize() {
        taskPaneContainer = new JXTaskPaneContainer();
        
        JXPanel sortByPanel = new JXPanel();
        sortByPanel.add(new JComboBox(new String [] {"by Project", "by Date"}));
        
//        this.add(sortByPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(taskPaneContainer), BorderLayout.CENTER);

        applyFilter();
    }
    
    private void applyFilter() {
        // clear filtered activities
        filteredActivitiesList.clear();

        if(filter != null) {
            List<ProjectActivity> filteredResult = filter.applyFilters(model.getActivitiesList());
            filteredActivitiesList.addAll(filteredResult);
        } else {
            filteredActivitiesList.addAll(model.getActivitiesList());
        }
        
        taskPaneContainer.removeAll();
        
        for (final ProjectActivity activity : filteredActivitiesList) {
            JXTaskPane tp = new JXTaskPane();
            tp.setTitle(String.valueOf(activity));
            final JXTextEditor editor = new JXTextEditor();
            editor.setText(activity.getDescription());
            tp.add(editor);
            
            editor.addTextObserver(new JXTextEditor.TextChangeObserver(){

                @Override
                public void onTextChange() {
                    activity.setDescription(editor.getText());
                }
            });
            
            taskPaneContainer.add(tp);
        }
    }

    /**
     * Update the panel from observed event.
     */
    public void update(Observable source, Object eventObject) {
        if (eventObject instanceof ProTrackEvent) {
            ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

            case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                applyFilter();
                break;

            case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                applyFilter();
                break;
            }
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;        
        applyFilter();
    }
}
