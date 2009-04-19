package com.remast.baralga.exporter.anukotimetracker.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AnukoActivity implements Comparable<AnukoActivity> {
    private final long id;
    private final String name;
    private final List<AnukoProject> projects = new ArrayList<AnukoProject>();

    public AnukoActivity(long id, String name) {
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

    public AnukoProject getFirstProject() {
        return this.projects.get(0);
    }
    
    public List<AnukoProject> getProjects() {
        return this.projects;
    }
    
    public void addProject(AnukoProject project) {
        this.projects.add(project);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int compareTo(AnukoActivity o) {
        Collator collator = Collator.getInstance();
        return collator.compare( this.name, o.name );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnukoActivity other = (AnukoActivity) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
