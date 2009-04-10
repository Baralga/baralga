package com.remast.baralga.exporter.anukotimetracker;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.filechooser.FileFilter;

import org.remast.baralga.gui.actions.AbstractExportAction;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.export.Exporter;

@SuppressWarnings("serial")
public class AnukoExporterAction extends AbstractExportAction {

    public AnukoExporterAction(Frame owner, PresentationModel model) {
        super(owner, model);
    }

    public Exporter createExporter() {
        return new AnukoExporter();
    }

    protected String getFileExtension() {
        // not needed here
        return null;
    }

    protected FileFilter getFileFilter() {
        // not needed here
        return null;
    }

    protected String getLastExportLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void setLastExportLocation(String lastExportLocation) {
        // TODO Auto-generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        // TODO
    }

}
