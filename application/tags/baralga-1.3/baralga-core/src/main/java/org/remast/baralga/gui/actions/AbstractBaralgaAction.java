package org.remast.baralga.gui.actions;

import java.awt.Frame;

import javax.swing.AbstractAction;

import org.remast.baralga.gui.model.PresentationModel;

/**
 * Abstract basic class for all Baralga actions.
 * @author remast
 */
public abstract class AbstractBaralgaAction extends AbstractAction {

    /** The model. */
    private final PresentationModel model;

    /** The owning frame. */
    private final Frame owner;

    /**
     * Creates a new action for the given model.
     * @param model the model to create action for
     */
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
    protected PresentationModel getModel() {
        return model;
    }

    /**
     * Getter for the owning frame of this action.
     * @return the owning frame of this action
     */
    protected Frame getOwner() {
        return owner;
    }

    /**
     * Get the mnemonic key which is the first character of the actions name.
     * @return the mnemonic key character or '-' if the action has no name
     */
    public char getMnemonic() {
        if (getValue(NAME) != null) {
            final String name = (String) getValue(NAME);
            try {
                return name.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                // Ignore
            }
        }

        return '-';
    }
}
