//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads data from an xml file.
 * @author remast
 */
public class XmlDataReader extends DefaultHandler {

	/** The imported projects. */
	private Collection<Project> projects = new ArrayList<>();

	/** The imported activites. */
	private Collection<ProjectActivity> activities = new ArrayList<>();
	
	/** Temporary string buffer. */
	private String currentBuffer;

	@SuppressWarnings("unused")
	private int version = -1;

	/** Temporary project. */
	private Project currentProject;
	
	/** Temporary activity. */
	private ProjectActivity currentActivity;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		currentBuffer = null;
		
		if ("baralga".equals(qName)) {
			version = Integer.valueOf(attributes.getValue("version"));
		} else if ("project".equals(qName)) {
			currentProject = new Project(Long.valueOf(attributes.getValue("id")), null, null);
		} else if ("activity".equals(qName)) {
			DateTime start = ISODateTimeFormat.dateHourMinute().parseDateTime(attributes.getValue("start"));
			DateTime end = ISODateTimeFormat.dateHourMinute().parseDateTime(attributes.getValue("end"));

			long projectId = Long.valueOf(attributes.getValue("projectReference"));
			Project project = null;
			for (Project tmpProject : projects) {
				if (tmpProject.getId() == projectId) {
					project = tmpProject;
					break;
				}
			}
			
			currentActivity = new ProjectActivity(start, end, project);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("project".equals(qName)) {
			projects.add(currentProject);
			currentProject = null;
		} else if ("title".equals(qName)) {
			if (currentProject != null) {
				currentProject.setTitle(currentBuffer);
			}
		} else if ("description".equals(qName)) {
			if (currentActivity != null) {
				currentActivity.setDescription(StringEscapeUtils.unescapeHtml4(currentBuffer));
			}
		} else if ("activity".equals(qName)) {
			activities.add(currentActivity);
			currentActivity = null;
		}
		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		final String currentCharacters = new String(ch, start, length);
		if (currentBuffer == null) {
			currentBuffer = currentCharacters;
		} else {
			currentBuffer += currentCharacters;
		}
	}

	/**
	 * Actually read the data from file.
	 * @throws IOException
	 */
	public void read(final File file) throws IOException {
		final InputStream fis = new FileInputStream(file);
		try {
			read(fis);
		} catch (IOException e) {
			throw new IOException("The file " + (file != null ? file.getName() : "<null>") + " does not contain valid Baralga data.", e);
		} finally {
			fis.close();
		}
	}

	/**
	 * Read the data from an {@link InputStream}.
	 * @throws IOException
	 */
	public void read(final InputStream in) throws IOException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(in, this);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Getter for the projects of the data file.
	 */
	public Collection<Project> getProjects() {
		return projects;
	}

	/**
	 * Getter for the activities of the data file.
	 */
	public Collection<ProjectActivity> getActivities() {
		return activities;
	}

}
