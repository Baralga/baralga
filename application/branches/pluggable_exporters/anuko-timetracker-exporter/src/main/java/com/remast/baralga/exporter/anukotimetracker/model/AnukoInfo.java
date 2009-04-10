package com.remast.baralga.exporter.anukotimetracker.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class AnukoInfo {

    private List<AnukoProject> projects = new ArrayList<AnukoProject>();
    private List<AnukoActivity> activities = new ArrayList<AnukoActivity>();
    private List<Object> errors = new ArrayList<Object>();

    public List<AnukoProject> getProjects() {
        return projects;
    }
    public void addProject(AnukoProject project) {
        this.projects.add(project);
    }
    public List<AnukoActivity> getActivities() {
        return activities;
    }
    public void addActivity(AnukoActivity activity) {
        this.activities.add(activity);
    }

    public List<Object> getErrors() {
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
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
