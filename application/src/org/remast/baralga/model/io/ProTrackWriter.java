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
    
    private ProTrack data;
    
    public ProTrackWriter(final ProTrack data) {
        this.data = data;
    }
    
    public void write(final File file) throws IOException {
        synchronized(getData()) {
            final FileOutputStream fileOut = new FileOutputStream(file);
            final XStream xstream = new XStream(new DomDriver());
            
            Annotations.configureAliases(xstream, ProTrack.class);
            Annotations.configureAliases(xstream, Project.class);
            Annotations.configureAliases(xstream, ProjectActivity.class);
            
            xstream.setMode(XStream.ID_REFERENCES);
            xstream.toXML(getData(), fileOut);
        }
    }

    /**
     * @return the proTrack
     */
    private ProTrack getData() {
        return data;
    }

    /**
     * @param proTrack the proTrack to set
     */
    private void setData(ProTrack proTrack) {
        this.data = proTrack;
    }
}
