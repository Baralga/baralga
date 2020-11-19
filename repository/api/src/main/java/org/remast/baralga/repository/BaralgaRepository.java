package org.remast.baralga.repository;

import java.util.List;

public interface BaralgaRepository extends ProjectRepository, ActivityRepository {

    void close();

    void initialize();

    /**
     * Provides a list of all months with activities.
     */
    List<Integer> getMonthList();


    /**
     * Gathers some statistics about the tracked activities.
     */
    void gatherStatistics();

    /**
     * Removes all projects and activities from the database.
     */
    void clearData();
}
