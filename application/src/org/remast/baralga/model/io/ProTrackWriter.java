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
    
    private ProTrack proTrack;
    
    public ProTrackWriter(ProTrack proTrack) {
        setProTrack(proTrack);
    }
    
    public void write(File file) throws IOException {
        final FileOutputStream fileOut = new FileOutputStream(file);
        XStream xstream = new XStream(new DomDriver());
        Annotations.configureAliases(xstream, ProTrack.class);
        Annotations.configureAliases(xstream, Project.class);
        Annotations.configureAliases(xstream, ProjectActivity.class);
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.toXML(getProTrack(), fileOut);
    }

    /**
     * @return the proTrack
     */
    private ProTrack getProTrack() {
        return proTrack;
    }

    /**
     * @param proTrack the proTrack to set
     */
    private void setProTrack(ProTrack proTrack) {
        this.proTrack = proTrack;
    }
}
