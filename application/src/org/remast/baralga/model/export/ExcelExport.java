package org.remast.baralga.model.export;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import org.remast.baralga.Messages;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.baralga.model.report.FilteredReport;
import org.remast.baralga.model.utils.ProTrackUtils;

public class ExcelExport {
    
    private static WritableCellFormat headingFormat;

    public static void export(final ProTrack data, Filter<ProjectActivity> filter, final File file) throws Exception {
            init();
            
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            createMonthlyReport(workbook, data, filter);
            
            WritableSheet sheet = workbook.createSheet(Messages.getString("ExcelExport.SheetTitleActivityRecords"), 1); //$NON-NLS-1$
            
            int row = 0;
            int col = 0;
            
            sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.ProjectHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.DateHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.StartTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.EndTimeHeading"), headingFormat)); //$NON-NLS-1$
            sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.HoursHeading"), headingFormat)); //$NON-NLS-1$
            
            col = 0;
            row++;

            List<ProjectActivity> activities = data.getActivities();
            if(filter != null)
                activities = filter.applyFilters(data.getActivities());
            
            for (ProjectActivity actitivity : activities) {
                sheet.addCell(new Label(col++, row, actitivity.getProject().getTitle()));
                sheet.addCell(makeDateCell(col++, row, actitivity.getStart()));
                sheet.addCell(makeTimeCell(col++, row, actitivity.getStart()));
                
                WritableCell c = makeTimeCell(col++, row, actitivity.getEnd());
                sheet.addCell(c);
                sheet.addCell(makeNumberCell(col++, row, ProTrackUtils.calculateDuration(actitivity)));
                
                col = 0;
                row ++;
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
            
            workbook.write();
            workbook.close();
    }
    
    private static void init() throws JXLException {
        WritableFont arial16 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, true);
        arial16.setColour(Colour.DARK_BLUE);
        headingFormat = new WritableCellFormat(arial16);
        headingFormat.setBackground(Colour.GRAY_25);
    }

    private static void createMonthlyReport(WritableWorkbook workbook, ProTrack data, Filter<ProjectActivity> filter) throws JXLException {
        int month = GregorianCalendar.getInstance().get(Calendar.MONTH);
        int year = GregorianCalendar.getInstance().get(Calendar.YEAR);
        
        WritableSheet sheet = workbook.createSheet(Messages.getString("ExcelExport.SheetTitleStart") + year + "-" + month, 0); //$NON-NLS-1$ //$NON-NLS-2$

        FilteredReport report = new FilteredReport(data);
        report.setFilter(filter);

        int row = 0;
        int col = 0;
        
        sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.DateHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.ProjectHeading"), headingFormat)); //$NON-NLS-1$
        sheet.addCell(new Label(col++, row, Messages.getString("ExcelExport.TimeHeading"), headingFormat)); //$NON-NLS-1$

        row++;
        col = 0;

        List<AccumulatedProjectActivity> accumulatedActivitiesByDay = report.getAccumulatedActivitiesByDay();
        for(AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            sheet.addCell(makeDateCell(col++, row, activity.getDay()));


            sheet.addCell(new Label(col++, row, activity.getProject().getTitle()));
            sheet.addCell(makeNumberCell(col++, row, activity.getTime()));

            row++;
            col = 0;
        }

        col = 0;

        // Format Cells
        CellView v = new CellView();
        v.setAutosize(true);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
        sheet.setColumnView(col++, v);
    }

    private static jxl.write.Number makeNumberCell(int col, int row, double number) {
        WritableCellFormat floatFormat = new WritableCellFormat (NumberFormats.FLOAT); 
        return new jxl.write.Number(col, row, number, floatFormat); 
        }

    private static WritableCell makeTimeCell(int col, int row, Date date) {
        DateFormat customDateFormat = new DateFormat ("hh:mm"); //$NON-NLS-1$
        WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat);
        return new DateTime(col, row, date, dateFormat); 
    }

    private static DateTime makeDateCell(int i, int j, Date date) {
        DateFormat customDateFormat = new DateFormat ("DD.MM.yyyy"); //$NON-NLS-1$
        WritableCellFormat dateFormat = new WritableCellFormat (customDateFormat);
        return new DateTime(i, j, date, dateFormat); 
    }

}
