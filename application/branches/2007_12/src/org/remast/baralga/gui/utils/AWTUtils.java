package org.remast.baralga.gui.utils;

import java.awt.MenuItem;

import javax.swing.Action;

/**
 * @author remast
 */
public abstract class AWTUtils {

    /**
     * Create AWT MenuItem from Swing Action.
     * @param action
     * @return 
     */
    public static MenuItem createFromAction(Action action) {
        MenuItem item = new MenuItem(action.getValue(Action.NAME).toString());
        item.addActionListener(action);
        return item;
    }
}
