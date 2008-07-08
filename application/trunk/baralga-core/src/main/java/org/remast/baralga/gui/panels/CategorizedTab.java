package org.remast.baralga.gui.panels;

import java.awt.Component;

import javax.swing.Icon;

public class CategorizedTab {
    private String category;
    
    private String title;

    private Icon icon;

    private Component component;

    private String tip;

    public CategorizedTab(String category) {
        this.category = category;
    }
    
    public void setComponent(String title, Icon icon, Component component, String tip) {
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.tip = tip;
    }

    /**
     * @return the category
     */
    public final String getCategory() {
        return category;
    }

    /**
     * @return the title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * @return the icon
     */
    public final Icon getIcon() {
        return icon;
    }

    /**
     * @return the component
     */
    public final Component getComponent() {
        return component;
    }

    /**
     * @return the tip
     */
    public final String getTip() {
        return tip;
    }
}
