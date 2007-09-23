package org.remast.baralga.gui.utils;

import java.awt.MenuItem;

import javax.swing.Action;

/**
 * @author Jan Stamer
 */
public abstract class AWTUtils {

    public static MenuItem createFromAction(Action action) {
        MenuItem item = new MenuItem(action.getValue(Action.NAME).toString());
        item.addActionListener(action);
        return item;
    }
}
