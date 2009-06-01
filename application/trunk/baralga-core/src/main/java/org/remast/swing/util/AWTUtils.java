package org.remast.swing.util;

import java.awt.Container;
import java.awt.Frame;
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
    private AWTUtils() { }

    /**
     * Create AWT MenuItem from Swing Action.
     * @param action the action to create AWT menu item for
     * @return the menu item for the given action
     */
    public static MenuItem createFromAction(final Action action) {
        final MenuItem item = new MenuItem(
                action.getValue(Action.NAME).toString()
        );
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
        final Rectangle preferredBounds = new Rectangle(
                preferredLeftTop, 
                window.getPreferredSize()
        );
        window.setLocation(
                ScreenUtils.ensureOnScreen(preferredBounds).getLocation()
        );
    }

    /**
     * Looks for the parent frame in the hierarchy of the given container.
     * @param container the container for whom to look for a frame
     * @return the frame that is the parent of the container or 
     * <code>null</code> if there is none
     */
    public static Frame getFrame(final Container container) {
        if (container == null) {
            return null;
        }

        if (container instanceof Frame) {
            return (Frame) container;
        }

        return getFrame(container.getParent());
    }

}
