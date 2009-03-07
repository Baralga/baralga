package org.remast.baralga.gui.dialogs;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Display a confirmation dialog if an activity should be started.
 *
 * @author kutzi
 */
public class StartActivityConfirmDialog {

    private static final long serialVersionUID = 1L;

    private JOptionPane pane;
    private JDialog dialog;
    
    public StartActivityConfirmDialog(String title, String msg, Point point) {
        this.pane = new JOptionPane( msg, JOptionPane.QUESTION_MESSAGE,
               JOptionPane.YES_NO_OPTION );

        this.dialog = pane.createDialog(title);
        Dimension d = this.dialog.getPreferredSize();
        // TODO: check that dialog stays within screen boundaries
        this.dialog.setLocation( point.x - d.width, point.y - d.height );
    }

    /**
     * Displays the dialog and returns the chosen value which is one of
     * {@link JOptionPane#YES_OPTION}, {@link JOptionPane#NO_OPTION}
     * or {@link JOptionPane#CLOSED_OPTION}.
     */
    public int getSelectedValue() {
        this.dialog.setVisible(true);
        this.dialog.dispose();
        
        Object selectedValue = pane.getValue();

        if(selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        
        if(selectedValue instanceof Integer) {
            return ((Integer)selectedValue).intValue();
        }
        return JOptionPane.CLOSED_OPTION;
    }
}