package org.remast.baralga.gui.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.Action;

/**
 * @author remast
 */
public abstract class AWTUtils {
    
    private static Map<String, Properties> pp = new HashMap<String, Properties>();

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
    
    public static void saveState(final Component component) {
        Properties properties = new Properties();
        properties.put("name", component.getName());
        properties.put("size", component.getSize());
        properties.put("location", component.getLocation());
        
        pp.put(component.getName(), properties);
    }
    
    public static void restoreState(final Component component) {
        Properties properties = pp.get(component.getName());
        if (properties == null)
            return;
        
        Dimension size = (Dimension) properties.get("size");
        component.setSize(size);
        
        Point location = (Point) properties.get("location");
        component.setLocation(location);
    }

}
