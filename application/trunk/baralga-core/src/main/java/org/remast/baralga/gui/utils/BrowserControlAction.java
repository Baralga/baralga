package org.remast.baralga.gui.utils;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.lang.StringUtils;

/**
 * Simple action for opening URLs in the Browser.
 * @author remast
 */
@SuppressWarnings("serial")
public class BrowserControlAction extends AbstractAction {

    /** The url to be opened. */
    private String url;

    public BrowserControlAction(String url) {
        super(url);
        this.url = url;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (StringUtils.isNotBlank(url)) {
            BrowserControl.displayURL(url);
        }

    }

}
