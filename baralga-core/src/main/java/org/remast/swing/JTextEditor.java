package org.remast.swing;

import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextArea;
import org.remast.util.StringUtils;
import org.remast.util.TextResourceBundle;

/**
 * Simple editor for html formatted text.
 * @author remast
 */
@SuppressWarnings("serial")
public class JTextEditor extends JXPanel {
	
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(JTextEditor.class);

    /**
     * A interface that allows to listen for text changes.
     */
    public interface TextChangeObserver {
        void onTextChange();
    }

    private List<TextChangeObserver> textObservers = new ArrayList<>();

    JXTextArea textArea;

    private boolean scrollable = false;

    private void notifyTextObservers() {
        for (TextChangeObserver txtObserver : textObservers) {
            txtObserver.onTextChange();
        }
    }

    public void addTextObserver(final TextChangeObserver txtObserver) {
        textObservers.add(txtObserver);
    }

    public JTextEditor() {
        initialize();
    }

    public JTextEditor(final boolean scrollable) {
        this.scrollable = scrollable;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        
        // Use the same font as a text field
        UIManager.put("TextArea.font", UIManager.get("TextField.font"));
        textArea = new JXTextArea();
        textArea.setPrompt(textBundle.textFor("TextEditor.prompt"));
        textArea.setLineWrap(true);
        textArea.setEnabled(true);
        textArea.setEditable(true);

        setTabBehavior();

        textArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                // nothing to do
            }

            @Override
            public void focusLost(FocusEvent e) {
                notifyTextObservers();
            }
        });

        if (scrollable) {
            this.add(new JScrollPane(textArea), BorderLayout.CENTER);
        } else {
            this.add(textArea, BorderLayout.CENTER);
        }
    }

    private void setTabBehavior() {
        // focus next pane with TAB instead of CTRL+TAB
        Set<KeyStroke> key = new HashSet<>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

        int forwardTraversal = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
        textArea.setFocusTraversalKeys(forwardTraversal, key);

        // focus previous pane with SHIFT+TAB instead of SHIFT+CTRL+TAB
        key = new HashSet<>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));

        final int backwardTraversal = KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS;
        textArea.setFocusTraversalKeys(backwardTraversal, key);

        final int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        final KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shortcutKey);
        
        // insert tab with CTRL+TAB instead of TAB
        textArea.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlTab, DefaultEditorKit.insertTabAction);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(final String description) {
        textArea.setText(StringUtils.stripXmlTags(description));
    }

    public void setEditable(final boolean active) {
        textArea.setEnabled(active);
        textArea.setEditable(active);
        textArea.setVisible(active);
    }

}
