package org.remast.swing.dialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * A dialog that is closed when ESC is pressed.
 * @see http://www.java2s.com/Code/JavaAPI/javax.swing/extendsJDialogPressEscapeKeytocloseadialog.htm
 * @author remast
 */
@SuppressWarnings("serial")
public class EscapeDialog extends JDialog {

    public EscapeDialog(Frame owner) {
        super(owner, true);
    }

    protected JRootPane createRootPane() {
        final JRootPane rootPane = new JRootPane();
        final KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

        final Action actionListener = new AbstractAction() {
            
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
            
        };

        final InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

        return rootPane;
    }
}