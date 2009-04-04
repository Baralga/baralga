package org.remast.swing.util;

import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

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
    
    /**
     * Ensures that a window stays within the current screen's bounds
     * while changing the position of the window as little as possible.
     *
     * @param preferredLeftTop The preferred left-top location of the window
     * @param window The window
     */
    public static void keepInScreenBounds(final Point preferredLeftTop, final Window window) {
        Rectangle preferredBounds = new Rectangle(preferredLeftTop, window.getPreferredSize());
        window.setLocation(ScreenUtils.ensureOnScreen(preferredBounds).getLocation());
    }

}
