package org.remast.baralga.model.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Reader for ProTrack data files.
 * @author remast
 */
public class ProTrackReader {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ProTrackReader.class);

    /** The data read. */
    private ProTrack data;

    /**
     * Actually read the data from file.
     * @throws IOException
     */
    public void read(final File file) throws IOException {
        final InputStream fis = new BufferedInputStream(new FileInputStream(file));

        final XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.ID_REFERENCES);

        Annotations.configureAliases(xstream, ProTrack.class);
        Annotations.configureAliases(xstream, Project.class);
        Annotations.configureAliases(xstream, ProjectActivity.class);

        Object o = null;
        try {
            o = xstream.fromXML(fis);
        } catch (Exception e)  {
            log.error(e, e);
            throw new IOException("The file " + (file != null ? file.getName() : "<null>" ) + " does not contain valid Baralga data.", e);
        } finally {
          IOUtils.closeQuietly(fis);
        }

        try {
            data = (ProTrack) o;
        } catch (ClassCastException e) {
            log.error(e, e);
            throw new IOException("The file " + (file != null ? file.getName() : "<null>" ) + " does not contain valid Baralga data.", e);
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
