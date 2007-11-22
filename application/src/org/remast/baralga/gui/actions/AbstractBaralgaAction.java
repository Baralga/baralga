package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
public abstract class AbstractBaralgaAction extends AbstractAction {

    /** The model. */
    private PresentationModel model;
    
    /** The owning frame. */
    private Frame owner;


    public AbstractBaralgaAction(final PresentationModel model) {
        this(null, model);
    }

    public AbstractBaralgaAction(final Frame owner) {
        this(owner, null);
    }

    public AbstractBaralgaAction(final Frame owner, final PresentationModel model) {
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
    public void setModel(final PresentationModel model) {
        this.model = model;
    }

    public Frame getOwner() {
        return owner;
    }

    public void setOwner(final Frame owner) {
        this.owner = owner;
    }
}
