package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.gui.model.edit.EditStack;
import org.remast.util.TextResourceBundle;

/**
 * Redoes the last edit activity using the {@link EditStack}.
 * @author remast
 */
@SuppressWarnings("serial")
public class RedoAction extends AbstractEditAction {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(RedoAction.class);

    public RedoAction(final EditStack editStack) {
        super(editStack);

        resetText();
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Y', InputEvent.CTRL_MASK));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void actionPerformed(final ActionEvent e) {
        redo();
    }

    public void setText(final String name) {
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
    }
    
    public void resetText() {
        putValue(NAME, textBundle.textFor("RedoAction.Name")); //$NON-NLS-1$
        putValue(SHORT_DESCRIPTION, textBundle.textFor("RedoAction.ShortDescription")); //$NON-NLS-1$
    }
}
