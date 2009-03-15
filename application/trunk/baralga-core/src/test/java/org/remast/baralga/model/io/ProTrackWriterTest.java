package org.remast.baralga.model.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.joda.time.DateTime;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import junit.framework.TestCase;

public class ProTrackWriterTest extends TestCase {
    
    /**
     * Tests that a predefined ProTrack data always results in the same output format.
     * 
     * I.e. a regression test against involuntarily changing the data format.
     * 
     * Adapt this test, when you make deliberate changes to the data model!
     */
    public void testProTrackWriting() throws IOException {
        ProTrack data = new ProTrack();
        Project a = new Project(4711, "foobar", "foo!");
        Project b = new Project(42, "The Answer", "To the question");
        data.add(a);
        data.add(b);
        
        data.setActiveProject(b);
        data.setActive(true);
        data.setStartTime(new DateTime(2009, 3, 14, 18, 0, 0, 0));
        
        ProjectActivity activity = new ProjectActivity(new DateTime(2009, 3, 13, 15, 0, 0, 0),
                new DateTime(2009, 3, 13, 17, 0, 0, 0), a);
        data.getActivities().add(activity);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ProTrackWriter writer = new ProTrackWriter(data);
        writer.write(baos);
        
        String written = baos.toString(IOConstants.FILE_ENCODING);
        String expected = "<proTrack id=\"1\" active=\"true\" startTime=\"2009-03-14 18:00:00.0 CET\">\n" + 
        		"  <activeProjects id=\"2\">\n" + 
        		"    <project id=\"3\">\n" + 
        		"      <id>4711</id>\n" + 
        		"      <title>foobar</title>\n" + 
        		"      <description>foo!</description>\n" + 
        		"    </project>\n" + 
        		"    <project id=\"4\">\n" + 
        		"      <id>42</id>\n" + 
        		"      <title>The Answer</title>\n" + 
        		"      <description>To the question</description>\n" + 
        		"    </project>\n" + 
        		"  </activeProjects>\n" + 
        		"  <projectsToBeDeleted id=\"5\"/>\n" + 
        		"  <activities id=\"6\">\n" + 
        		"    <projectActivity id=\"7\">\n" + 
        		"      <start id=\"8\">2009-03-13 15:00:00.0 CET</start>\n" + 
        		"      <end id=\"9\">2009-03-13 17:00:00.0 CET</end>\n" + 
        		"      <project reference=\"3\"/>\n" + 
        		"    </projectActivity>\n" + 
        		"  </activities>\n" + 
        		"  <activeProject reference=\"4\"/>\n" + 
        		"</proTrack>";
        
        // TODO: ignore line ends and formatting when comparing the strings!
        assertEquals(expected.trim(), written.trim());
    }
}
