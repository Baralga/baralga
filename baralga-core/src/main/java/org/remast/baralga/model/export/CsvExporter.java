package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.TextResourceBundle;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Strings;

/**
 * Exports the data into Commma Separated Value (CSV) format.
 * @author remast
 */
public class CsvExporter implements Exporter {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(CsvExporter.class);

    private static final char SEPARATOR_CHARACTER = ';';

    /** Header of CSV file containing the column headings. */
    private static final String[] CSV_HEADER;
    static {
        CSV_HEADER = new String[] {
                textBundle.textFor("CsvExporter.ProjectHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.DateHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.StartTimeHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.EndTimeHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.HoursHeading"), //$NON-NLS-1$
                textBundle.textFor("CsvExporter.DescriptionHeading") //$NON-NLS-1$
        };
    }

    /**
     * Exports the given data as Comma Separated Value (CSV) to the 
     * <code>OutputStream</code> under consideration of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    @Override
    public void export(final Collection<ProjectActivity> data, final Filter filter, final OutputStream outputStream) throws Exception {
        final List<ProjectActivity> activities = new ArrayList<ProjectActivity>(data);
        
        // Sort activities by default sort order (date) before export
        Collections.sort(activities);

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(
                    new OutputStreamWriter(outputStream),
                    SEPARATOR_CHARACTER
            );

            writer.writeNext(CSV_HEADER);

            for (ProjectActivity activity : activities) {
                writer.writeNext(makeCsvLine(activity));
                writer.flush();
            }
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
    }

    /**
     * Creates a line of the CSV file from the given activity.
     * @param activity the activity to create line for
     * @return the CSV line for the given activity
     */
    private String[] makeCsvLine(final ProjectActivity activity) {
        if (activity == null) {
            return null;
        }

        final String[] csvLine = new String[6];

        csvLine[0] = activity.getProject().getTitle();
        csvLine[1] = FormatUtils.formatDate(activity.getStart());
        csvLine[2] = FormatUtils.formatTime(activity.getStart());
        csvLine[3] = FormatUtils.formatTime(activity.getEnd());
        csvLine[4] = NumberFormat.getNumberInstance().format(activity.getDuration());

        // Description
        String description = org.remast.util.StringUtils.stripXmlTags(activity.getDescription());
        description = Strings.nullToEmpty(description).trim();
        csvLine[5] = description;

        return csvLine;
    }

	@Override
	public boolean isFullExport() {
		return false;
	}
	
}
