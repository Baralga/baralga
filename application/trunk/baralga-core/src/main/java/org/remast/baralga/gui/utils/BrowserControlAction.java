package org.remast.baralga.gui.utils;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple action for opening URLs in the Browser.
 * @author remast
 */
@SuppressWarnings("serial")
public class BrowserControlAction extends AbstractAction {

	/** The logger. */
	private static final Log log = LogFactory.getLog(BrowserControlAction.class);

	/** The url to be opened. */
	private String url;

	public BrowserControlAction(String url) {
		super(url);
		this.url = url;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception ex) {
			log.error(ex, ex);
		}

	}

}
