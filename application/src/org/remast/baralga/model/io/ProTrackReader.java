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

    private ProTrack data;
    
    private File file;

    public ProTrackReader(final File file) {
        this.file = file;
    }
    
    public void read() throws IOException {
        final FileInputStream fis = new FileInputStream(getFile());
        
        final XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.ID_REFERENCES);
        
        Annotations.configureAliases(xstream, ProTrack.class);
        Annotations.configureAliases(xstream, Project.class);
        Annotations.configureAliases(xstream, ProjectActivity.class);
        
        final Object o = xstream.fromXML(fis);
        setData((ProTrack) o);
    }
    
    /**
     * @return the proTrack
     */
    public ProTrack getData() {
        return data;
    }

    /**
     * @param proTrack the proTrack to set
     */
    private void setData(final ProTrack data) {
        this.data = data;
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
    public void setFile(final File file) {
        this.file = file;
    }
}
