package org.remast.baralga.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProTrackReader {

    private ProTrack proTrack;
    
    private File file;

    public ProTrackReader(File file) {
        setFile(file);
    }
    
    public void read() throws IOException {
        FileInputStream fis = new FileInputStream(getFile());
        
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.ID_REFERENCES);
        Annotations.configureAliases(xstream, ProTrack.class);
        Annotations.configureAliases(xstream, Project.class);
        Annotations.configureAliases(xstream, ProjectActivity.class);
        Object o = xstream.fromXML(fis);
        setProTrack((ProTrack) o);
    }
    
    /**
     * @return the proTrack
     */
    public ProTrack getProTrack() {
        return proTrack;
    }

    /**
     * @param proTrack the proTrack to set
     */
    private void setProTrack(ProTrack proTrack) {
        this.proTrack = proTrack;
    }

    /**
     * @return the file
     */
    private File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
}
