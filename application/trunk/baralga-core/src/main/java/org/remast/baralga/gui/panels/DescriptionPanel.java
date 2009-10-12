package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.swing.util.GuiConstants;

/**
 * Display and edit the descriptions of all project activities.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanel extends JXPanel implements Observer {

    /** The model. */
    private final PresentationModel model;

    /** Cache for all entries by activity. */
    private final Map<ProjectActivity, DescriptionPanelEntry> entriesByActivity;

    /** The applied filter. */
    private Filter filter;

    private JPanel container;

    public DescriptionPanel(final PresentationModel model) {
        super();
        this.setLayout(new BorderLayout());
        this.model = model;
        this.entriesByActivity = new HashMap<ProjectActivity, DescriptionPanelEntry>();
        this.model.addObserver(this);
        this.filter = model.getFilter();

        initialize();
    }

    private void initialize() {
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        this.add(new JScrollPane(container), BorderLayout.CENTER);

        applyFilter();
    }

    /**
     * Applies the filter to all activities so that only descriptions
     * of activities which match the filter are shown.
     */
    private void applyFilter() {
        final Runnable applyFilterRunnable = new Runnable() {

            @Override
            public void run() {
                // clear filtered activities
                entriesByActivity.clear();

                // Remove old description panels.
                container.removeAll();

                for (final ProjectActivity activity : DescriptionPanel.this.model.getActivitiesList()) {
                    final DescriptionPanelEntry descriptionPanelEntry = new DescriptionPanelEntry(activity, DescriptionPanel.this.model);

                    // Alternate background color
                    if (DescriptionPanel.this.model.getActivitiesList().indexOf(activity) % 2 == 0) {
                        descriptionPanelEntry.setBackground(Color.WHITE);
                    } else {
                        descriptionPanelEntry.setBackground(GuiConstants.BEIGE);
                    }

                    // Save entry
                    entriesByActivity.put(activity, descriptionPanelEntry);

                    // Display entry
                    container.add(descriptionPanelEntry);

                }
            }
        };

        SwingUtilities.invokeLater(applyFilterRunnable);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;
        ProjectActivity activity;

        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            applyFilter();
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
        {
            activity = (ProjectActivity) event.getData();
            final boolean matchesFilter = model.getFilter().matchesCriteria(activity);

            if (entriesByActivity.containsKey(activity)) {
                // If activity matches the current filter it is updated
                // if not the activity is removed
                if (matchesFilter) {
                    entriesByActivity.get(activity).update();
                } else {
                    final DescriptionPanelEntry entryPanel = entriesByActivity.get(activity);
                    this.container.remove(entryPanel);
                }
            }
            
            break;
        }

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            final Collection<ProjectActivity> projectActivities = (Collection<ProjectActivity>) event.getData();
            for (ProjectActivity projectActivity : projectActivities) {
                if (entriesByActivity.containsKey(projectActivity)) {
                    final DescriptionPanelEntry entryPanel = entriesByActivity.get(projectActivity);
                    this.container.remove(entryPanel);
                }
            }
            break;

        case BaralgaEvent.PROJECT_CHANGED:
            for (Entry<ProjectActivity, DescriptionPanelEntry> entry : entriesByActivity.entrySet()) {
                entry.getValue().update();
            }
            break;

        case BaralgaEvent.FILTER_CHANGED:
            final Filter newFilter = (Filter) event.getData();
            setFilter(newFilter);
            break;
        }
    }

    private void setFilter(final Filter filter) {
        this.filter = filter;
        applyFilter();
    }
}
