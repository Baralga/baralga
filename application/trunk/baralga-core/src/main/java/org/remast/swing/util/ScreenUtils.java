package org.remast.swing.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for screen related stuff.
 * 
 * A partial copy of the JIDE {@link com.jidesoft.utils.PortingUtils}
 * but this one fixes ensureOnScreen() to handle the taskbar on Windows correctly.
 */
public class ScreenUtils {
    private static Rectangle SCREEN_BOUNDS = null;

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
    public static Rectangle containsInScreenBounds(Component invoker, Rectangle rect) {
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
    public static Rectangle overlapWithScreenBounds(Component invoker, Rectangle rect) {
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
    public static Dimension getScreenSize(Component invoker) {
        ensureScreenBounds();

        // to handle multi-display case
        Dimension screenSize = SCREEN_BOUNDS.getSize();  // Toolkit.getDefaultToolkit().getScreenSize();

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
    public static Dimension getLocalScreenSize(Component invoker) {
        ensureScreenBounds();

        // jdk1.4 only
        if (invoker != null && !(invoker instanceof JApplet) && invoker.getGraphicsConfiguration() != null) {
            // to handle multi-display case
            GraphicsConfiguration gc = invoker.getGraphicsConfiguration();
            Rectangle bounds = gc.getBounds();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
            bounds.width -= insets.left + insets.right;
            bounds.height -= insets.top + insets.bottom;
            return bounds.getSize();
        }
        else {
            return getScreenSize(invoker);
        }
    }

    /**
     * Gets the screen bounds. In JDK1.4+, the returned bounds will exclude task bar area on Windows OS.
     *
     * @param invoker
     * @return the screen bounds.
     */
    public static Rectangle getScreenBounds(Component invoker) {
        ensureScreenBounds();

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

    private static void ensureScreenBounds() {
        if (SCREEN_BOUNDS == null) {
            SCREEN_BOUNDS = new Rectangle();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            for (GraphicsDevice gd : gs) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                SCREEN_BOUNDS = SCREEN_BOUNDS.union(gc.getBounds());
            }
        }
    }

    private static Area SCREEN_AREA;
    private static Rectangle[] SCREENS;
    private static Insets[] INSETS;

    private static Thread _initializationThread = null;

    /**
     * If you use methods such as {@link #ensureOnScreen(java.awt.Rectangle)}, {@link
     * #getContainingScreenBounds(java.awt.Rectangle,boolean)} or {@link #getScreenArea()} for the first time, it will
     * take up to a few seconds to run because it needs to get device information. To avoid any slowness, you can call
     * {@link #initializeScreenArea()} method in the class where you will use those three methods. This method will
     * spawn a thread to retrieve device information thus it will return immediately. Hopefully, when you use the three
     * methods, the thread is done so user will not notice any slowness.
     */
    synchronized public static void initializeScreenArea() {
        initializeScreenArea(Thread.NORM_PRIORITY);
    }

    /**
     * If you use methods such as {@link #ensureOnScreen(java.awt.Rectangle)}, {@link
     * #getContainingScreenBounds(java.awt.Rectangle,boolean)} or {@link #getScreenArea()} for the first time, it will
     * take up to a couple of seconds to run because it needs to get device information. To avoid any slowness, you can
     * call {@link #initializeScreenArea()} method in the class where you will use those three methods. This method will
     * spawn a thread to retrieve device information thus it will return immediately. Hopefully, when you use the three
     * methods, the thread is done so user will not notice any slowness.
     *
     * @param priority as we will use a thread to calculate the screen area, you can use this parameter to control the
     *                 priority of the thread. If you are waiting for the result before the next step, you should use
     *                 normal priority (which is 5). If you just want to calculate when app starts, you can use a lower
     *                 priority (such as 3). For example, AbstractComboBox needs screen size so that the popup doesn't
     *                 go beyond the screen. So when AbstractComboBox is used, we will kick off the thread at priority
     *                 3. If user clicks on the drop down after the thread finished, there will be no time delay.
     */
    synchronized public static void initializeScreenArea(int priority) {
        if (_initializationThread == null) {
            _initializationThread = new Thread() {
                @Override
                public void run() {
                    SCREEN_AREA = new Area();
                    SCREEN_BOUNDS = new Rectangle();
                    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    List<Rectangle> screensList = new ArrayList<Rectangle>();
                    List<Insets> insetsList = new ArrayList<Insets>();
                    GraphicsDevice[] screenDevices = environment.getScreenDevices();
                    for (GraphicsDevice device : screenDevices) {
                        GraphicsConfiguration configuration = device.getDefaultConfiguration();
                        Rectangle screenBounds = configuration.getBounds();
                        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
                        screensList.add(screenBounds);
                        insetsList.add(insets);
                        SCREEN_AREA.add(new Area(screenBounds));
                        SCREEN_BOUNDS = SCREEN_BOUNDS.union(screenBounds);
                    }
                    SCREENS = screensList.toArray(new Rectangle[screensList.size()]);
                    INSETS = insetsList.toArray(new Insets[screensList.size()]);
                }
            };
            _initializationThread.setPriority(priority);
            if (INITIALIZE_SCREEN_AREA_USING_THREAD) {
                _initializationThread.start();
            }
            else {
                _initializationThread.run();
            }
        }
    }

    public static boolean INITIALIZE_SCREEN_AREA_USING_THREAD = true;

    public static boolean isInitializationThreadAlive() {
        return _initializationThread != null && _initializationThread.isAlive();
    }

    public static boolean isInitalizationThreadStarted() {
        return _initializationThread != null;
    }

    private static void waitForInitialization() {
        initializeScreenArea();

        while (_initializationThread.isAlive()) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * Ensures the rectangle is visible on the screen.
     *
     * @param invoker the invoking component
     * @param bounds  the input bounds
     * @return the modified bounds.
     */
    public static Rectangle ensureVisible(Component invoker, Rectangle bounds) {
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
    public static Rectangle ensureOnScreen(Rectangle rect) {
        // optimize it so that it is faster for most cases
        Rectangle localScreenBounds = getLocalScreenBounds();
        if (localScreenBounds.contains(rect)) {
            return rect;
        }

        waitForInitialization();

        // see if the top left is on any of the screens
        Rectangle containgScreen = null;
        Point rectPos = rect.getLocation();
        for (Rectangle screenBounds : SCREENS) {
            if (screenBounds.contains(rectPos)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // if not see if rect partial on any screen
        for (Rectangle screenBounds : SCREENS) {
            if (screenBounds.intersects(rect)) {
                containgScreen = screenBounds;
                break;
            }
        }
        // check if it was on any screen
        if (containgScreen == null) {
            // it was not on any of the screens so center it on the first screen
            rect.x = (SCREENS[0].width - rect.width) / 2;
            rect.y = (SCREENS[0].width - rect.width) / 2;
            return rect;
        }
        else {
            
            Rectangle screenToConsider = containgScreen;
            
            if( containgScreen.intersects(localScreenBounds)) {
                // if (partial) containing screen intersects the local screen
                // use the local screen (with taskbar being considered right!)
                // for computation
                screenToConsider = localScreenBounds;
            }
            containgScreen = null;
            
            // move rect so it is completely on a single screen
            // check X
            int rectRight = rect.x + rect.width;
            int screenRight = screenToConsider.x + screenToConsider.width;
            if (rectRight > screenRight) {
                rect.x = screenRight - rect.width;
            }
            if (rect.x < screenToConsider.x) rect.x = screenToConsider.x;
            // check Y
            int rectBottom = rect.y + rect.height;
            int screenBottom = screenToConsider.y + screenToConsider.height;
            if (rectBottom > screenBottom) {
                rect.y = screenBottom - rect.height;
            }
            if (rect.y < screenToConsider.y) rect.y = screenToConsider.y;
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
    public static Rectangle getContainingScreenBounds(Rectangle rect, boolean considerInsets) {
        waitForInitialization();
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
        waitForInitialization();
        return SCREEN_AREA;
    }
}
