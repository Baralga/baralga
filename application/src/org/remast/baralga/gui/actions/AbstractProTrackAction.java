package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
public abstract class AbstractProTrackAction extends AbstractAction {

    private PresentationModel model;
    
    private Frame owner;


    public AbstractProTrackAction(PresentationModel model) {
        this.model = model;
    }

    public AbstractProTrackAction(final Frame owner, PresentationModel model) {
        this.owner = owner;
        this.model = model;
    }

    /**
     * @return the model
     */
    public PresentationModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(PresentationModel model) {
        this.model = model;
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(Frame owner) {
        this.owner = owner;
    }
}
