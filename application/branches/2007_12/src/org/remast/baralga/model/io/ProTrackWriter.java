package org.remast.baralga.model.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProTrackWriter {
    
    /** The data to write. */
    private ProTrack data;
    
    /**
     * Create a write for given data.
     * @param data the data
     */
    public ProTrackWriter(final ProTrack data) {
        this.data = data;
    }
    
    /**
     * Write the data to the given file.
     * @param file the file to write to
     * @throws IOException
     */
    public void write(final File file) throws IOException {
        synchronized(data) {
            final FileOutputStream fileOut = new FileOutputStream(file);
            final XStream xstream = new XStream(new DomDriver());
            
            Annotations.configureAliases(xstream, ProTrack.class);
            Annotations.configureAliases(xstream, Project.class);
            Annotations.configureAliases(xstream, ProjectActivity.class);
            
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.toXML(data, fileOut);
        }
    }
}
