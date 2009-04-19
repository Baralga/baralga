package com.remast.baralga.exporter.anukotimetracker.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AnukoProject implements Comparable<AnukoProject> {
    private final long id;
    private final String name;
    private final List<AnukoActivity> activities = new ArrayList<AnukoActivity>();
    public AnukoProject(long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<AnukoActivity> getActivities() {
        return activities;
    }
    
    public void addActivity( AnukoActivity activity ) {
        this.activities.add(activity);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    @Override
    public int compareTo(AnukoProject o) {
        if( id < o.id ) {
            return -1;
        }
        if( o.id > id ) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnukoProject other = (AnukoProject) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
