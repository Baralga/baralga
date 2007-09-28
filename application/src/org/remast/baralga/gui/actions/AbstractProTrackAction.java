package org.remast.baralga.gui.actions;

import javax.swing.AbstractAction;

import org.remast.baralga.model.PresentationModel;

/**
 * @author remast
 */
public abstract class AbstractProTrackAction extends AbstractAction {

    private PresentationModel model;

    public AbstractProTrackAction(PresentationModel model) {
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
}
