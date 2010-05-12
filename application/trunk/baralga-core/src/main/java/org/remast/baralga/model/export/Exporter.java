package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.util.List;

import org.remast.baralga.model.ProjectActivity;
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
    void export(final List<ProjectActivity> data, final Filter filter, final OutputStream outputStream) throws Exception;

}
