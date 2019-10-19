package org.remast.swing.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;

/**
 * A helper class for screen related stuff.
 * 
 * A partial copy of the JIDE {@link com.jidesoft.utils.PortingUtils}
 * but:
 * - fixes ensureOnScreen() to handle the taskbar on Windows correctly
 * - cleaned up initialization
 * - threw out some methods we definitely don't need
 */
public class ScreenUtils {
    private static final Area SCREEN_AREA;
    
    /**
     * List all all screen bounds NOT considering any insets (e.g. taskbars)
     */
    private static final Rectangle[] SCREENS;
    
    /**
     * List all all screen insets (e.g. taskbars)
     */
    private static Insets[] INSETS;

    /**
     * List all all screen bounds considering any insets (e.g. taskbars)
     */
    private static final Rectangle[] SCREENS_WITH_INSETS;

    private static final Rectangle SCREEN_BOUNDS;
    
    /**
     * Determines the bounds and the insets of all available screens.
     */
    static {
        Area screenArea = new Area();
        Rectangle screenBounds = new Rectangle();
        GraphicsEnvironment environment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        List<Rectangle> screensList = new ArrayList<>();
        List<Insets> insetsList = new ArrayList<>();
        List<Rectangle> screensWithInsets = new ArrayList<>();
        GraphicsDevice[] screenDevices = environment.getScreenDevices();
        for (GraphicsDevice device : screenDevices) {
            GraphicsConfiguration configuration = device
                    .getDefaultConfiguration();
            Rectangle deviceBounds = configuration.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
                    configuration);
            Rectangle screenBoundsWithInsets = new Rectangle(
                    deviceBounds.x + insets.left, deviceBounds.y + insets.top,
                    deviceBounds.width - insets.right, deviceBounds.height - insets.bottom);
            screensList.add(deviceBounds);
            insetsList.add(insets);
            screensWithInsets.add(screenBoundsWithInsets);
            screenArea.add(new Area(screenBoundsWithInsets));
            screenBounds = screenBounds.union(deviceBounds);
        }
        SCREEN_AREA = screenArea;
        SCREEN_BOUNDS = screenBounds;
        SCREENS = screensList.toArray(new Rectangle[screensList.size()]);
        INSETS = insetsList.toArray(new Insets[screensList.size()]);
        SCREENS_WITH_INSETS = screensWithInsets.toArray(new Rectangle[screensWithInsets.size()]);
    }

    private ScreenUtils() {
        // hide constructor
    }
    
    /**
     * To make sure the rectangle is within the screen bounds.
     *
     * @param invoker
     * @param rect
     * @return the rectangle that is in the screen bounds.
     */
    public static Rectangle containsInScreenBounds(final Component invoker, final Rectangle rect) {
        Rectangle screenBounds = getScreenBounds(invoker);
        Point p = rect.getLocation();
        if (p.x + rect.width > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - rect.width;
        }
        if (p.y + rect.height > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - rect.height;
        }
        if (p.x < screenBounds.x) {
            p.x = screenBounds.x;
        }
        if (p.y < screenBounds.y) {
            p.y = screenBounds.y;
        }
        return new Rectangle(p, rect.getSize());
    }

    /**
     * To make sure the rectangle has overlap with the screen bounds.
     *
     * @param invoker
     * @param rect
     * @return the rectangle that has overlap with the screen bounds.
     */
    public static Rectangle overlapWithScreenBounds(final Component invoker, final Rectangle rect) {
        Rectangle screenBounds = getScreenBounds(invoker);
        Point p = rect.getLocation();
        if (p.x > screenBounds.x + screenBounds.width) {
            p.x = screenBounds.x + screenBounds.width - rect.width;
        }
        if (p.y > screenBounds.y + screenBounds.height) {
            p.y = screenBounds.y + screenBounds.height - rect.height;
        }
        if (p.x + rect.width < screenBounds.x) {
            p.x = screenBounds.x;
        }
        if (p.y + rect.height < screenBounds.y) {
            p.y = screenBounds.y;
        }
        return new Rectangle(p, rect.getSize());
    }

    /**
     * Gets the screen size. In JDK1.4+, the returned size will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen size.
     */
    public static Dimension getScreenSize(final Component invoker) {
        // to handle multi-display case
        Dimension screenSize = SCREEN_BOUNDS.getSize();

        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(invoker.getGraphicsConfiguration());
            screenSize.width -= insets.left + insets.right;
            screenSize.height -= insets.top + insets.bottom;
        }

        return screenSize;
    }

    /**
     * Gets the screen size. In JDK1.4+, the returned size will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen size.
     */
    public static Dimension getLocalScreenSize(final Component invoker) {
        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            // to handle multi-display case
            GraphicsConfiguration gc = invoker.getGraphicsConfiguration();
            Rectangle bounds = gc.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
            return bounds.getSize();
        } else {
            return getScreenSize(invoker);
        }
    }

    /**
     * Gets the screen bounds. In JDK1.4+, the returned bounds will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen bounds.
     */
    public static Rectangle getScreenBounds(final Component invoker) {
        // to handle multi-display case
        Rectangle bounds = (Rectangle) SCREEN_BOUNDS.clone();

        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(invoker.getGraphicsConfiguration());
            bounds.x += insets.left;
            bounds.y += insets.top;
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
        }

        return bounds;
    }

    /**
     * Gets the local monitor's screen bounds.
     *
     * @return the screen bounds.
     */
    public static Rectangle getLocalScreenBounds() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return e.getMaximumWindowBounds();
    }

    /**
     * Ensures the rectangle is visible on the screen.
     *
     * @param invoker the invoking component
     * @param bounds  the input bounds
     * @return the modified bounds.
     */
    public static Rectangle ensureVisible(final Component invoker, final Rectangle bounds) {
        Rectangle mainScreenBounds = ScreenUtils.getLocalScreenBounds(); // this is fast. Only if it is outside this bounds, we try the more expensive one.
        if (!mainScreenBounds.contains(bounds.getLocation())) {
            Rectangle screenBounds = ScreenUtils.getScreenBounds(invoker);
            if (bounds.x > screenBounds.x + screenBounds.width || bounds.x < screenBounds.x) {
                bounds.x = mainScreenBounds.x;
            }
            if (bounds.y > screenBounds.y + screenBounds.height || bounds.y < screenBounds.y) {
                bounds.y = mainScreenBounds.y;
            }
        }
        return bounds;
    }

    /**
     * Modifies the position of rect so that it is completely on screen if that is possible.
     *
     * @param rect The rectangle to move onto a single screen
     * @return rect after its position has been modified
     */
    public static Rectangle ensureOnScreen(final Rectangle rect) {
        // optimize it so that it is faster for most cases
        Rectangle localScreenBounds = getLocalScreenBounds();
        if (localScreenBounds.contains(rect)) {
            return rect;
        }

        // see if the top left is on any of the screens
        Rectangle containgScreen = null;
        Point rectPos = rect.getLocation();
        for (Rectangle screenBounds : SCREENS_WITH_INSETS) {
            if (screenBounds.contains(rectPos)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // if not see if rect partial on any screen
        for (Rectangle screenBounds : SCREENS_WITH_INSETS) {
            if (screenBounds.intersects(rect)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // check if it was on any screen
        if (containgScreen == null) {
            // it was not on any of the screens so center it on the first screen
            rect.x = (SCREENS_WITH_INSETS[0].width - rect.width) / 2;
            rect.y = (SCREENS_WITH_INSETS[0].width - rect.width) / 2;
            return rect;
        } else {
            // move rect so it is completely on a single screen
            // check X
            int rectRight = rect.x + rect.width;
            int screenRight = containgScreen.x + containgScreen.width;
            if (rectRight > screenRight) {
                rect.x = screenRight - rect.width;
            }
            if (rect.x < containgScreen.x) rect.x = containgScreen.x;
            // check Y
            int rectBottom = rect.y + rect.height;
            int screenBottom = containgScreen.y + containgScreen.height;
            if (rectBottom > screenBottom) {
                rect.y = screenBottom - rect.height;
            }
            if (rect.y < containgScreen.y) rect.y = containgScreen.y;
            // return corrected rect
            return rect;
        }
    }

    /**
     * Gets the screen bounds that contains the rect. The screen bounds consider the screen insets if any.
     *
     * @param rect           the rect of the component.
     * @param considerInsets if consider the insets. The insets is for thing like Windows Task Bar.
     * @return the screen bounds that contains the rect.
     */
    public static Rectangle getContainingScreenBounds(final Rectangle rect, final boolean considerInsets) {
        // check if rect is total on screen
//        if (SCREEN_AREA.contains(rect)) return SCREEN_AREA;

        // see if the top left is on any of the screens
        Rectangle containgScreen = null;
        Insets insets = null;
        Point rectPos = rect.getLocation();
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.contains(rectPos)) {
                containgScreen = screenBounds;
                insets = INSETS[i];
                break;
            }
        }
        // if not see if rect partial on any screen
        for (int i = 0; i < SCREENS.length; i++) {
            Rectangle screenBounds = SCREENS[i];
            if (screenBounds.intersects(rect)) {
                containgScreen = screenBounds;
                insets = INSETS[i];
                break;
            }
        }

        // fall back to the first screen
        if (containgScreen == null) {
            containgScreen = SCREENS[0];
            insets = INSETS[0];
        }

        Rectangle bounds = new Rectangle(containgScreen);
        if (considerInsets) {
            bounds.x += insets.left;
            bounds.y += insets.top;
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
        }
        return bounds;
    }


    /**
     * Get screen area of all monitors.
     *
     * @return Union of all screens
     */
    public static Area getScreenArea() {
        return SCREEN_AREA;
    }
}
