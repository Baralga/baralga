package com.remast.baralga.exporter.anukotimetracker.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("activity")
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
}
