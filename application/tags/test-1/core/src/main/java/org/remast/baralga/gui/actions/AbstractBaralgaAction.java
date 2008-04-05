package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import org.remast.baralga.gui.model.PresentationModel;

/**
 * Abstract basic class for all baralga actions.
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

    /**
     * Create a new action for the given owning frame.
     * @param owner the owning frame
     */
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
