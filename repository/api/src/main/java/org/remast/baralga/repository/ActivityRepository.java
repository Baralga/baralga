package org.remast.baralga.repository;

import java.util.Collection;
import java.util.List;

public interface ActivityRepository {

    /**
     * Provides all activities.
     * @return read-only view of the activities
     */
     List<ActivityVO> getActivities();

    /**
     * Adds a new activity.
     * @param activity the activity to add
     */
     void addActivity(final ActivityVO activity);
    /**
     * Removes an activity.
     * @param activity the activity to remove
     */
     void removeActivity(final ActivityVO activity);

    /**
     * Adds a bunch of activities.
     * @param activities the activities to add
     */
     void addActivities(final Collection<ActivityVO> activities);

    /**
     * Removes a bunch of activities.
     * @param activities the activities to remove
     */
     void removeActivities(final Collection<ActivityVO> activities);

    /**
     * Updates the activity in the database. Pending changes will be made persistent.
     * @param activity the activity to update
     */
    void updateActivity(final ActivityVO activity);

    /**
     * Provides all activities satisfying the given filter.
     * @param filter the filter for activities
     * @return read-only view of the activities
     */
    List<ActivityVO> getActivities(final FilterVO filter);

}
