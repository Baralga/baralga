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

import org.remast.baralga.repository.ServerNotAvailableException;
import org.remast.baralga.repository.UnauthorizedException;
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
	 * @param throwable the exception
	 */
	public static void showErrorDialog(final Throwable throwable) {
		ErrorInfo errorInfo = new ErrorInfo(
				textBundle.textFor("BaralgaMain.FatalError.Title"),  //$NON-NLS-1$,
				textBundle.textFor("BaralgaMain.FatalError.Message", BaralgaMain.getLogFileName()),  //$NON-NLS-1$,
				throwable.getMessage(),
				null,
				throwable,
				Level.SEVERE,
				null);

		if (ServerNotAvailableException.class.equals(throwable.getClass())) {
			ServerNotAvailableException serverNotAvailableException = (ServerNotAvailableException) throwable;
			errorInfo = new ErrorInfo(
					textBundle.textFor("BaralgaMain.ServerNotAvailable.Title"),  //$NON-NLS-1$,
					textBundle.textFor("BaralgaMain.ServerNotAvailable.Message", serverNotAvailableException.getBaseUrl()),  //$NON-NLS-1$,
					throwable.getMessage(),
					"Multiuser",
					throwable,
					Level.WARNING,
					null);
		} else if (UnauthorizedException.class.equals(throwable.getClass())) {
			UnauthorizedException unauthorizedException = (UnauthorizedException) throwable;
			errorInfo = new ErrorInfo(
					textBundle.textFor("BaralgaMain.Unauthorized.Title"),  //$NON-NLS-1$,
					textBundle.textFor("BaralgaMain.Unauthorized.Message", unauthorizedException.getUser()),  //$NON-NLS-1$,
					throwable.getMessage(),
					"Multiuser",
					throwable,
					Level.WARNING,
					null);
		}

		JXErrorPane.showDialog(null, errorInfo);
	}
	
	public static final class ExceptionHandler implements UncaughtExceptionHandler {
		
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			log.error(e.getLocalizedMessage(), e);
			showErrorDialog(e);
		}

	}
	
	public static final class ExceptionHandlingEventProcessor extends EventQueue {
 
		protected void dispatchEvent(final AWTEvent evt) {
			try {
				super.dispatchEvent(evt);
			} catch (final Exception | Error e) {
				log.error(e.getLocalizedMessage(), e);
				showErrorDialog(e);
			}
        }
	}
}
