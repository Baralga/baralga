package org.remast.baralga.gui.actions;

import java.awt.Frame;
import javax.swing.AbstractAction;
import org.remast.baralga.gui.model.PresentationModel;

/**
 * Abstract base class for all Baralga actions.
 * @author remast
 */
@SuppressWarnings("serial")
public abstract class AbstractBaralgaAction extends AbstractAction {

    /** The owning frame. */
    private final Frame owner;

    /**
     * Creates a new action for the given owning frame.
     * @param owner the owning frame
     */
    public AbstractBaralgaAction(final Frame owner) {
        this.owner = owner;
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