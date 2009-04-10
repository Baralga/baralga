package com.remast.baralga.exporter.anukotimetracker;

import java.awt.Frame;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.remast.baralga.gui.actions.AbstractBaralgaAction;
import org.remast.baralga.gui.model.PluggableExporter;
import org.remast.baralga.gui.model.PresentationModel;

public class AnukaPluggableExporter implements PluggableExporter {

    private PropertiesConfiguration settings = new PropertiesConfiguration();
    
    public AbstractBaralgaAction getExportAction(Frame owner, PresentationModel model) {
        return new AnukoExporterAction(owner, model);
    }

    public PropertiesConfiguration getSettings() {
        return settings;
    }

}
