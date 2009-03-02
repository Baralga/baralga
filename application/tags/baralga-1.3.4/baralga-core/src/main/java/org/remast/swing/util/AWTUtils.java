package org.remast.swing.util;

import java.awt.MenuItem;

import javax.swing.Action;

/**
 * @author remast
 */
public abstract class AWTUtils {
    
    /** Hide constructor. */
    private AWTUtils() {}

    /**
     * Create AWT MenuItem from Swing Action.
     * @param action
     * @return 
     */
    public static MenuItem createFromAction(final Action action) {
        final MenuItem item = new MenuItem(action.getValue(Action.NAME).toString());
        item.addActionListener(action);
        return item;
    }

}
