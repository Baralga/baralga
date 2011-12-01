package org.remast.baralga.model.export;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jxl.CellView;
import jxl.JXLException;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.FilterUtils;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.util.TextResourceBundle;

import com.google.common.base.Strings;

/**
 * Exports data to Microsoft Excel format.
 * @author remast
 */
public class ExcelExporter implements Exporter {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExcelExporter.class);

    public ExcelExporter() { }

    public final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM"); //$NON-NLS-1$

    public final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy"); //$NON-NLS-1$

    private static WritableCellFormat headingFormat;

    /**
     * Exports the given data as Microsoft Excel to the 
     * <code>OutputStream</code> under consideration of the given filter.
     * @param data the data to be exported
     * @param filter the current filter
     * @param outputStream the stream to write to
     * @throws Exception exception during data export
     */
    @Override
    public void export(final Collection<ProjectActivity> data, final Filter filter, final OutputStream outputStream) throws Exception {
            init();
            
            final WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            createFilteredReport(workbook, data, filter);
            
            final WritableSheet sheet = workbook.createSheet(textBundle.textFor("ExcelExporter.SheetTitleActivityRecords"), 1); //$NON-NLS-1$
            
            int row = 0;
            int col = 0;
            
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.ProjectHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DateHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.StartTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.EndTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.HoursHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DescriptionHeading"), headingFormat)); //$NON-NLS-1$
            
            col = 0;
            row++;

            final List<ProjectActivity> activities = new ArrayList<ProjectActivity>(data);
            
            // Sort activities by default sort order (date) before export
            Collections.sort(activities);
            
            for (ProjectActivity actitivity : activities) {
                sheet.addCell(new Label(col++, row, actitivity.getProject().getTitle()));
                sheet.addCell(makeDateCell(col++, row, actitivity.getStart().toDate()));
                sheet.addCell(makeTimeCell(col++, row, actitivity.getStart().toDate()));
                
                WritableCell c = makeTimeCell(col++, row, actitivity.getEnd().toDate());
                sheet.addCell(c);
                sheet.addCell(makeNumberCell(col++, row, actitivity.getDuration()));

                // Description
                String description = org.remast.util.StringUtils.stripXmlTags(actitivity.getDescription());
                description = Strings.nullToEmpty(description).trim();
                sheet.addCell(new Label(col++, row, description));
                
                col = 0;
                row++;
            }
            // reset col
            col = 0;
            
            // Format Cells
            CellView v = new CellView();
            v.setAutosize(true);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            sheet.setColumnView(col++, v);
            
            workbook.write();
            workbook.close();
    }
    
    private static void init() throws JXLException {
        final WritableFont arial16 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, true);
        arial16.setColour(Colour.DARK_BLUE);
        headingFormat = new WritableCellFormat(arial16);
        headingFormat.setBackground(Colour.GRAY_25);
    }

    private void createFilteredReport(final WritableWorkbook workbook, final Collection<ProjectActivity> data, final Filter filter) throws JXLException {      
        String reportName = textBundle.textFor("ExcelExporter.SheetTitleStart"); //$NON-NLS-1$
        if (filter != null) {
            reportName += FilterUtils.makeIntervalString(filter);
        }
        
        final WritableSheet sheet = workbook.createSheet(reportName, 0);

        final AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(data, filter);

        int row = 0;
        int col = 0;
        
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.DateHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.ProjectHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, textBundle.textFor("ExcelExporter.TimeHeading"), headingFormat)); //$NON-NLS-1$

        row++;
        col = 0;

        final List<AccumulatedProjectActivity> accumulatedActivitiesByDay = report.getAccumulatedActivitiesByDay();
        
        // Sort activities by default sort order (date) before export
        Collections.sort(accumulatedActivitiesByDay);

        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            sheet.addCell(makeDateCell(col++, row, activity.getDay()));
            sheet.addCell(new Label(col++, row, activity.getProject().getTitle()));
            sheet.addCell(makeNumberCell(col++, row, activity.getTime()));

            row++;
            col = 0;
        }

        col = 0;

        // Format Cells
        final CellView v = new CellView();
        v.setAutosize(true);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
    }

    private static jxl.write.Number makeNumberCell(final int col, final int row, final double number) {
        final WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT); 
        return new jxl.write.Number(col, row, number, floatFormat); 
        }

    private static WritableCell makeTimeCell(final int col, final int row, final Date date) {
        final DateFormat customDateFormat = new DateFormat("hh:mm"); //$NON-NLS-1$
        final WritableCellFormat dateFormat = new WritableCellFormat(customDateFormat);
        return new DateTime(col, row, date, dateFormat); 
    }

    private static DateTime makeDateCell(final int i, final int j, final Date date) {
        final DateFormat customDateFormat = new DateFormat("DD.MM.yyyy"); //$NON-NLS-1$
        final WritableCellFormat dateFormat = new WritableCellFormat(customDateFormat);
        return new DateTime(i, j, date, dateFormat); 
    }
    
	@Override
	public boolean isFullExport() {
		return false;
	}
}
