package org.remast.baralga.gui.actions;

import java.awt.Frame;
import javax.swing.AbstractAction;
import org.remast.baralga.gui.model.PresentationModel;

/**
 * Base class for all Baralga actions.
 * @param <T> the type of PresentationModel used by the action
 */
@SuppressWarnings("serial")
public abstract class BaralgaAction<T extends PresentationModel> extends AbstractBaralgaAction {

    /** The model for the action. */
    private final T model;

    /**
     * Creates a new action for the given model.
     * @param model the model to create action for
     */
    public BaralgaAction(final T model, final Frame owner) {
        super(owner);
        this.model = model;
    }

    /**
     * Getter for the model of this action.
     * @return the model of this action
     */
    protected T getModel() {
        return model;
    }

}