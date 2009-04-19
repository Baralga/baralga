package com.remast.baralga.exporter.anukotimetracker.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.remast.baralga.exporter.anukotimetracker.model.AnukoActivity;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoError;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoInfo;
import com.remast.baralga.exporter.anukotimetracker.model.AnukoProject;

public class AnukoInfoResponseHandler implements ResponseHandler<AnukoInfo> {

    private final JdomResponseHandler handler = new JdomResponseHandler();
    
    private final DateTimeFormatter hoursFormat = DateTimeFormat.forPattern("HH:mm");
    
    @Override
    public AnukoInfo handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {
        Document doc = handler.handleResponse(response);
        
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        output.output(doc, System.out);
        
        if( doc != null ) {
            return parse(doc);
        }
        
        return new AnukoInfo();
    }

    @SuppressWarnings("unchecked")
    private AnukoInfo parse(Document doc) {
        
        AnukoInfo info = new AnukoInfo();
        
        Element projs = doc.getRootElement().getChild("projects");
        List<Element> projects = projs.getChildren("project");
        for(Element project : projects) {
            long id = Long.parseLong(project.getAttributeValue("id"));
            String name = project.getText();
            AnukoProject anukoProject = new AnukoProject(id, name);
            info.addProject(anukoProject);
        }
        
        Element acts = doc.getRootElement().getChild("activities");
        List<Element> activities = acts.getChildren("activity");
        for(Element activity : activities) {
            long id = Long.parseLong(activity.getAttributeValue("id"));
            String name = activity.getText();
            AnukoActivity anukoActivity = new AnukoActivity(id, name);
            
            String projectIds = activity.getAttributeValue("project");
            String[] ids = projectIds.split(",");
            for( String projectId : ids ) {
                AnukoProject project = info.getProjectById(Long.parseLong(projectId));
                project.addActivity(anukoActivity);
                anukoActivity.addProject(project);
            }
            
            info.addActivity(anukoActivity);
        }
        
        List<Element> completedTime = doc.getRootElement().getChild("records").getChildren("compleate");
        for( Element complete : completedTime ) {
            DateTime hourMinutes = hoursFormat.parseDateTime(complete.getAttributeValue("daily_time"));
            info.addDailyTime( hourMinutes.getMillisOfDay() );
        }
        
        List<Element> errors = doc.getRootElement().getChild("errors").getChildren("error");
        for(Element error : errors) {
            long id = Long.parseLong(error.getAttributeValue("id"));
            String message = error.getAttributeValue("message");
            AnukoError anukoError = new AnukoError(id, message);
            info.addError(anukoError);
        }
        
        return info;
    }
}
