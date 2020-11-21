package org.remast.baralga.repository;

public interface BaralgaRepository extends ProjectRepository, ActivityRepository {

    void close();

    void initialize();

    /**
     * Gathers some statistics about the tracked activities.
     */
    void gatherStatistics();

    /**
     * Removes all projects and activities from the database.
     */
    void clearData();
}
