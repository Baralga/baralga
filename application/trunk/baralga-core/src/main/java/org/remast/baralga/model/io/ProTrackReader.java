package org.remast.baralga.model.io;

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
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Reader for ProTrack data files.
 * @author remast
 */
public class ProTrackReader {

    /** Encoding of input file. */
    private static final String INPUT_ENCODING = "UTF-8";

    /** The logger. */
    private static final Log log = LogFactory.getLog(ProTrackReader.class);

    /** The data read. */
    private ProTrack data;

    /**
     * Actually read the data from file.
     * @throws IOException
     */
    public void read(final File file) throws IOException {
        final InputStream fis = new FileInputStream(file);

        final XStream xstream = new XStream(new DomDriver(INPUT_ENCODING));
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.processAnnotations(
                new Class[] {ProTrack.class, Project.class, ProjectActivity.class}
        );
        xstream.autodetectAnnotations(true);

        Object o = null;
        try {
            o = xstream.fromXML(fis);
        } catch (Exception e)  {
            log.error(e, e);
            throw new IOException("The file " + (file != null ? file.getName() : "<null>") + " does not contain valid Baralga data.", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        try {
            data = (ProTrack) o;
        } catch (ClassCastException e) {
            log.error(e, e);
            throw new IOException("The file " + (file != null ? file.getName() : "<null>") + " does not contain valid Baralga data.", e);
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
