package org.remast.baralga.gui.model;

import java.awt.Frame;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.remast.baralga.gui.actions.AbstractBaralgaAction;

public interface PluggableExporter {
    public AbstractBaralgaAction getExportAction(Frame owner, PresentationModel model);
    public PropertiesConfiguration getSettings();
}
