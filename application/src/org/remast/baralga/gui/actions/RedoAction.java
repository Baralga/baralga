package org.remast.baralga.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.remast.baralga.model.PresentationModel;

@SuppressWarnings("serial")
public class RedoAction extends AbstractBaralgaAction {

    public RedoAction(PresentationModel model) {
        super(model);

        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/resource/icons/gtk-redo-ltr.png"))); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
