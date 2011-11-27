//---------------------------------------------------------
// $Id$ 
// 
// (c) 2010 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.swing.util;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.util.TextResourceBundle;

/**
 * Utitility classes and methods for handling exceptions of the Swing GUI.
 * @author remast
 */
public class ExceptionUtils {
	
	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	/** The logger. */
	private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

	/**
	 * Shows an error dialog for the given exception.
	 * @param t the exception
	 */
	public static void showErrorDialog(final Throwable t) {
		// Catch any uncaught GUI Exceptions
		try {
			final ErrorInfo errorInfo = new ErrorInfo(
					textBundle.textFor("BaralgaMain.FatalError.Title"),  //$NON-NLS-1$, 
					textBundle.textFor("BaralgaMain.FatalError.Message", BaralgaMain.getLogFileName()),  //$NON-NLS-1$, 
					null, // detailedErrorMessage
					null, // category
					t, 
					Level.SEVERE, 
					null);
			JXErrorPane.showDialog(null, errorInfo);
		} catch (Exception innerEx) {
			log.error(innerEx.getLocalizedMessage(), innerEx);
		}
	}
	
	public static final class ExceptionHandler implements UncaughtExceptionHandler {
		
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			showErrorDialog(e);		}
	};
	
	public static final class ExceptionHandlingEventProcessor extends EventQueue {
 
		protected void dispatchEvent(final AWTEvent evt) {
			try {
				super.dispatchEvent(evt);
			} catch (final Exception e) {
				showErrorDialog(e);
			} catch (final Error e) {
				showErrorDialog(e);
			}
		}
	}
}
