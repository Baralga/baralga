package org.remast.baralga.model.export;

import java.io.OutputStream;

import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.filter.Filter;

/**
 * Interface for all data exporters (e.g. MS Excel, CSV).
 * @author kutzi
 * @author remast
 */
public interface Exporter {
    
    /**
     * Exports the given data to the <code>OutputStream</code> under consideration
     * of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    void export(final ProTrack data, final Filter filter, final OutputStream outputStream) throws Exception;

}
