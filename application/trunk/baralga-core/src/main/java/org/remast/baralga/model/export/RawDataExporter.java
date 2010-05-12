package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.util.List;

import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

/**
 * Exports the raw data of the application.
 * @author remast
 */
public class RawDataExporter implements Exporter {

    /**
     * Exports the given raw data to the <code>OutputStream</code> under consideration of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    @Override
    public void export(final List<ProjectActivity> data, final Filter filter, final OutputStream outputStream) throws Exception {
//        final ProTrackWriter writer = new ProTrackWriter(data);
//        writer.write(outputStream);
//        outputStream.flush();
    }

}
