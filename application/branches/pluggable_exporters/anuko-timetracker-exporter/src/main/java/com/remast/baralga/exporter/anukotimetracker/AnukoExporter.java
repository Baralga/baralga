package com.remast.baralga.exporter.anukotimetracker;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.ReadableBaralgaData;
import org.remast.baralga.model.export.Exporter;
import org.remast.baralga.model.filter.Filter;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoActivity;
import com.remast.baralga.exporter.anukotimetracker.util.AnukoAccess;

public class AnukoExporter implements Exporter {

    private Map<Project, AnukoActivity> mappings;
    private AnukoAccess anukoAccess;

    @Deprecated
    public AnukoExporter() {
    }
    
    public AnukoExporter( String url, String login, String password,
            Map<Project, AnukoActivity> mappings ) {
        this.mappings = mappings;
        
        this.anukoAccess = new AnukoAccess(url, login, password);
    }
    
    public void export(ReadableBaralgaData data, Filter filter,
            OutputStream outputStream) throws Exception {
        
        List<ProjectActivity> activities = filter.applyFilters(data.getActivities());
        anukoAccess.submitActivities( activities, this.mappings );
    }

}
