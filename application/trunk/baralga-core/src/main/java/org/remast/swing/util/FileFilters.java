package org.remast.swing.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.remast.util.TextResourceBundle;

/**
 * Container of misc {@link FileFilter}s for different file formats.
 * @author remast
 */
public abstract class FileFilters {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(FileFilters.class);

	/**
	 * Filter for iCal files.
	 */
    public static final class ICalFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".ics"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.ICalFile"); //$NON-NLS-1$
        }

    }
	/**
	 * Filter for Microsoft Excel files.
	 */
    public static final class ExcelFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".xlsx"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.MicrosoftExcelFile"); //$NON-NLS-1$
        }

    }
    
    /**
     * Filter for Comma Separated Value (CSV) files.
     */
    public static final class CsvFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".csv"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.CommaSeparatedValuesFile"); //$NON-NLS-1$
        }

    }
    
    /**
     * Filter for data files.
     */
    public static final class DataFileFilter extends FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || file.getName().endsWith(".baralga.xml"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return textBundle.textFor("FileFilters.DataFile"); //$NON-NLS-1$
        }

    }

}
