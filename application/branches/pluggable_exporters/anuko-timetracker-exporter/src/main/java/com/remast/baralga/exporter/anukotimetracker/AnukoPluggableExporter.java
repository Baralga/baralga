package com.remast.baralga.exporter.anukotimetracker;

import java.awt.Frame;

import org.apache.commons.configuration.Configuration;
import org.remast.baralga.gui.actions.AbstractBaralgaAction;
import org.remast.baralga.gui.model.PluggableExporter;
import org.remast.baralga.gui.model.PresentationModel;

public class AnukoPluggableExporter implements PluggableExporter {

    private Configuration settings;
    
    @Override
    public AbstractBaralgaAction getExportAction(Frame owner, PresentationModel model) {
        return new AnukoExporterAction(owner, model, settings);
    }

    @Override
    public void setConfiguration( Configuration configuration ) {
        this.settings = configuration;
    }

}
