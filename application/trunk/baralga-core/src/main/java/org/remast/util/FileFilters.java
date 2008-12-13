package org.remast.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.remast.baralga.Messages;

/**
 * Container of misc {@link FileFilter}s for different file formats.
 * @author remast
 */
public abstract class FileFilters {

	/**
	 * Filter for Microsoft Excel files.
	 */
    public static final class ExcelFileFilter extends FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".xls"); //$NON-NLS-1$
        }

        @Override
        public String getDescription() {
            return Messages.getString("FileFilters.MicrosoftExcelFile"); //$NON-NLS-1$
        }

    }

}
