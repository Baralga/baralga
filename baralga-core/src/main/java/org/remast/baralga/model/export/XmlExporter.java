//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.remast.baralga.model.BaralgaDAO;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports all data to XML format.
 * @author remast
 */
public class XmlExporter implements Exporter {

	@Override
	public void export(Collection<ProjectActivity> data, Filter filter, OutputStream outputStream) throws Exception {
		// Gather all projects of the given activities
		final Set<Project> projects = new HashSet<>();
		for (ProjectActivity activity : data) {
			projects.add(activity.getProject());
		}
		
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		documentBuilderFactory.setExpandEntityReferences(false);
		documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.newDocument();

        // Create root element with db version
        Element root = document.createElement("baralga");
        root.setAttribute("version", String.valueOf(BaralgaDAO.LATEST_DATABASE_VERSION));
        document.appendChild(root);

        // Create a comment and put it in the root element
        Comment comment = document.createComment("Baralga data export created at " +  new DateTime() + ".");
        root.appendChild(comment);

        Element projectsElement = document.createElement("projects");
        root.appendChild(projectsElement);
        
        // Export projects
        for (Project project : projects) {
        	Element projectElement = document.createElement("project");
        	projectElement.setAttribute("id", String.valueOf(project.getId()));
            projectElement.setAttribute("active", String.valueOf(project.isActive()));

        	Element titleElement = document.createElement("title");
        	titleElement.appendChild(document.createTextNode(StringEscapeUtils.escapeXml10(project.getTitle())));
        	projectElement.appendChild(titleElement);
        	
        	Element descriptionElement = document.createElement("description");
        	descriptionElement.appendChild(document.createTextNode(StringEscapeUtils.escapeXml10(project.getDescription())));
        	projectElement.appendChild(descriptionElement);

        	projectsElement.appendChild(projectElement);
        }
        
        // Export project activities
        Element activitiesElement = document.createElement("activities");
        root.appendChild(activitiesElement);
        
        for (ProjectActivity activity : data) {
        	final Element activityElement = document.createElement("activity");
         	activityElement.setAttribute("id", String.valueOf(activity.getId()));
         	activityElement.setAttribute("projectReference", String.valueOf(activity.getProject().getId()));

        	activityElement.setAttribute("start", StringEscapeUtils.escapeXml10(ISODateTimeFormat.dateHourMinute().print(activity.getStart())));
        	activityElement.setAttribute("end", StringEscapeUtils.escapeXml10(ISODateTimeFormat.dateHourMinute().print(activity.getEnd())));

        	Element descriptionElement = document.createElement("description");
        	descriptionElement.appendChild(document.createTextNode(StringEscapeUtils.escapeXml10(org.apache.commons.lang3.StringUtils.defaultString(activity.getDescription()))));
        	activityElement.appendChild(descriptionElement);

        	activitiesElement.appendChild(activityElement);
        }
        
        // Output the XML
        TransformerFactory transfac = TransformerFactory.newInstance();
		transfac.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer trans = transfac.newTransformer();
//        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(outputStream);
        DOMSource source = new DOMSource(document);
        trans.transform(source, result);
	}
	
	@Override
	public boolean isFullExport() {
		return true;
	}

}
