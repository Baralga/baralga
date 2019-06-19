package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.remast.baralga.gui.model.edit.EditStack;
import org.remast.util.TextResourceBundle;

/**
 * Undoes the last edit activity using the {@link EditStack}.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
 public class UndoAction extends AbstractEditAction {

     /** The bundle for internationalized texts. */
     private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(UndoAction.class);

     public UndoAction(final EditStack editStack) {
         super(editStack);

         resetText();
         putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-undo-ltr.png"))); //$NON-NLS-1$
         putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK));
     }

     /**
      * {@inheritDoc}
      */
     @Override
     public final void actionPerformed(final ActionEvent e) {
         undo();
     }
     
     public void setText(final String name) {
         putValue(NAME, name);
         putValue(SHORT_DESCRIPTION, name);
     }
     
     public void resetText() {
         putValue(NAME, textBundle.textFor("UndoAction.Name")); //$NON-NLS-1$
         putValue(SHORT_DESCRIPTION, textBundle.textFor("UndoAction.ShortDescription")); //$NON-NLS-1$
     }
 }
