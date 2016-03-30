package org.remast.baralga.model.export;

import com.google.common.base.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.FilterUtils;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.util.TextResourceBundle;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Exports data to Microsoft Excel format.
 * @author remast
 */
public class ExcelExporter implements Exporter {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ExcelExporter.class);

	/** Style for formatting headers. */
	private static CellStyle headingStyle;

	/** Style for formatting dates. */
	private static CellStyle dateStyle;

	/** Style for formatting times. */
	private static CellStyle timeStyle;
	
	/** Style for formatting the duration of an activity. */
	private static CellStyle durationStyle;

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
		Workbook workbook = new XSSFWorkbook();  // or new HSSFWorkbook();

		init(workbook);

		createFilteredReport(workbook, data, filter);

		final Sheet sheet = workbook.createSheet(textBundle.textFor("ExcelExporter.SheetTitleActivityRecords")); //$NON-NLS-1$

		int currentRow = 0;
		Row row = sheet.createRow(currentRow);

		int col = 0;
		Cell cell;

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.ProjectHeading"));

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.DateHeading"));

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.StartTimeHeading"));

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.EndTimeHeading"));

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.HoursHeading"));

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.DescriptionHeading"));

		col = 0;
		currentRow++;

		final List<ProjectActivity> activities = new ArrayList<ProjectActivity>(data);

		// Sort activities by default sort order (date) before export
		Collections.sort(activities);

		for (ProjectActivity actitivity : activities) {
			row = sheet.createRow(currentRow);

			row.createCell(col++).setCellValue(actitivity.getProject().getTitle());

			cell = row.createCell(col++);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(actitivity.getStart().toDate());

			cell = row.createCell(col++);
			cell.setCellStyle(timeStyle);
			cell.setCellValue(actitivity.getStart().toDate());


			cell = row.createCell(col++);
			cell.setCellStyle(timeStyle);
			cell.setCellValue(actitivity.getEnd().toDate());

			cell = row.createCell(col++);
			cell.setCellStyle(durationStyle);
			cell.setCellValue(actitivity.getDuration());

			// Description
			String description = org.remast.util.StringUtils.stripXmlTags(actitivity.getDescription());
			description = Strings.nullToEmpty(description).trim();
			row.createCell(col++).setCellValue(description);

			col = 0;
			currentRow++;
		}
		
		// reset col
		col = 0;

		// Format Cells
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);

		workbook.write(outputStream);
	}

	private static void init(Workbook workbook) {
		final CreationHelper createHelper = workbook.getCreationHelper();

		headingStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(font);
		headingStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("DD.MM.yyyy"));

		timeStyle = workbook.createCellStyle();
		timeStyle.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm"));

		durationStyle = workbook.createCellStyle();
		durationStyle.setDataFormat(createHelper.createDataFormat().getFormat("#0.00"));
	}

	private void createFilteredReport(final Workbook workbook, final Collection<ProjectActivity> data, final Filter filter) {      
		String reportName = textBundle.textFor("ExcelExporter.SheetTitleStart"); //$NON-NLS-1$
		if (filter != null) {
			reportName += FilterUtils.makeIntervalString(filter);
		}
		
		// Issue #99 : Report name may not contain character /.
		reportName = reportName.replace("/", "-");

		final Sheet sheet = workbook.createSheet(reportName);

		final AccumulatedActivitiesReport report = new AccumulatedActivitiesReport(data, filter);

		int currentRow = 0;
		Row row = sheet.createRow(currentRow);

		int col = 0;
		Cell cell;

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.DateHeading")); //$NON-NLS-1$

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.ProjectHeading")); //$NON-NLS-1$

		cell = row.createCell(col++);
		cell.setCellStyle(headingStyle);
		cell.setCellValue(textBundle.textFor("ExcelExporter.TimeHeading")); //$NON-NLS-1$

		currentRow++;
		col = 0;

		final List<AccumulatedProjectActivity> accumulatedActivitiesByDay = report.getAccumulatedActivitiesByDay();

		// Sort activities by default sort order (date) before export
		Collections.sort(accumulatedActivitiesByDay);

		for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
			row = sheet.createRow(currentRow);

			cell = row.createCell(col++);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(activity.getDay());

			row.createCell(col++).setCellValue(activity.getProject().getTitle());
			
			cell = row.createCell(col++);
			cell.setCellStyle(durationStyle);
			cell.setCellValue(activity.getTime());

			currentRow++;
			col = 0;
		}

		// reset col
		col = 0;

		// Format Cells
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
		sheet.autoSizeColumn(col++);
	}

	@Override
	public boolean isFullExport() {
		return false;
	}
}
