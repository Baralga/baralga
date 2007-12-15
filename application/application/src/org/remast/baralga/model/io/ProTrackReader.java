package org.remast.baralga.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProTrackReader {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ProTrackReader.class);

    /** The data to write. */
    private ProTrack data;
   
    /**
     * Actually read the data from file.
     * @throws IOException
     */
    public void read(final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        
        final XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.ID_REFERENCES);
        
        Annotations.configureAliases(xstream, ProTrack.class);
        Annotations.configureAliases(xstream, Project.class);
        Annotations.configureAliases(xstream, ProjectActivity.class);
        
        final Object o = xstream.fromXML(fis);
        try {
            data = (ProTrack) o;
        } catch (ClassCastException e) {
            // :TODO: Internationalize error message.
            log.error("The file " + (file != null ? file.getName() : "<null>" ) + " does not contain valid Baralga data.", e);
        }
    }
    
    /**
     * Getter for the data read.
     * @return the proTrack
     */
    public ProTrack getData() {
        return data;
    }

}
