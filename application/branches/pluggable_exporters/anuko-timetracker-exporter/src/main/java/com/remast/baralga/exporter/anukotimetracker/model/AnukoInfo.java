package com.remast.baralga.exporter.anukotimetracker.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.Duration;

public class AnukoInfo {

    private final Set<AnukoProject> projects = new HashSet<AnukoProject>();
    private final Set<AnukoActivity> activities = new HashSet<AnukoActivity>();
    private final Set<AnukoError> errors = new HashSet<AnukoError>();
    
    private Duration dailyWorked = new Duration(0);

    public Collection<AnukoProject> getProjects() {
        return projects;
    }
    public void addProject(AnukoProject project) {
        this.projects.add(project);
    }
    public Collection<AnukoActivity> getActivities() {
        return activities;
    }
    public void addActivity(AnukoActivity activity) {
        this.activities.add(activity);
    }

    public Collection<AnukoError> getErrors() {
        return errors;
    }
    
    public void addError(AnukoError error) {
        this.errors.add(error);
    }
    
    public AnukoProject getProjectById( long id ) {
        for(AnukoProject project : projects) {
            if(project.getId() == id ) {
                return project;
            }
        }
        return null;
    }
    
    public void addDailyTime(long millis) {
        this.dailyWorked = this.dailyWorked.plus(millis);
    }
    
    public Duration getDailyTime() {
        return this.dailyWorked;
    }
    
    /**
     * Merges the passed info into these one.
     * Thus creating a 'union' of the information of both infos.
     */
    public void merge(AnukoInfo info) {
        this.projects.addAll(info.getProjects());
        this.activities.addAll(info.getActivities());
        this.errors.addAll(info.getErrors());
        
        this.dailyWorked = this.dailyWorked.plus(info.getDailyTime());
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
