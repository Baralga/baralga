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

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;

import org.jdesktop.swingx.JXCollapsiblePane;
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
     * A interface that allows to listen for text changes to the card side text panes. Use
     * {@link CardPanel#addTextObserver} method to hook it to the CardPanel.
     */
    public interface TextChangeObserver {
        void onTextChange();
    }

    private List<TextChangeObserver> textObservers = new ArrayList<TextChangeObserver>();

    JXTextArea textArea;

    private JToolBar toolbar;

    private JXCollapsiblePane collapsiblePane;

    private boolean collapseEditToolbar = true;

    private boolean scrollable = false;

    private List<Action> actions = new ArrayList<Action>();

    private static class CopyAction extends DefaultEditorKit.CopyAction {

        public CopyAction() {
            super();
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-copy.png"))); //$NON-NLS-1$
        }

    }

    private static class PasteAction extends DefaultEditorKit.PasteAction {

        public PasteAction() {
            super();
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/gtk-paste.png"))); //$NON-NLS-1$
        }

    }

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

    public JTextEditor(final boolean scrollable, final boolean collapseEditToolbar) {
        this.scrollable = scrollable;
        this.collapseEditToolbar = collapseEditToolbar;
        initialize();
    }

    private void initialize() {
        actions.add(new CopyAction());
        actions.add(new PasteAction());


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

            public void focusGained(final FocusEvent e) {
                if (collapseEditToolbar) {
                    collapsiblePane.setCollapsed(false);
                }
            }

            public void focusLost(final FocusEvent e) {
                if (collapseEditToolbar) {
                    if (e.getOppositeComponent() != null && e.getOppositeComponent().getParent() != toolbar) {
                        collapsiblePane.setCollapsed(true);
                    }
                }
            }

        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }

            public void insertUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }

            public void removeUpdate(final DocumentEvent e) {
                notifyTextObservers();
            }

        });

        createToolbar();

        collapsiblePane = new JXCollapsiblePane();
        collapsiblePane.add(toolbar);

        if (!collapseEditToolbar) {
            collapsiblePane.setCollapsed(false);
        } else {
            collapsiblePane.setCollapsed(true);
        }

        this.add(collapsiblePane, BorderLayout.NORTH);
        if (scrollable) {
            this.add(new JScrollPane(textArea), BorderLayout.CENTER);
        } else {
            this.add(textArea, BorderLayout.CENTER);
        }
    }

    private void setTabBehavior() {
        // focus next pane with TAB instead of CTRL+TAB
        Set<KeyStroke> key = new HashSet<KeyStroke>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

        int forwardTraversal = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
        textArea.setFocusTraversalKeys(forwardTraversal, key);

        // focus previous pane with SHIFT+TAB instead of SHIFT+CTRL+TAB
        key = new HashSet<KeyStroke>();
        key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));

        final int backwardTraversal = KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS;
        textArea.setFocusTraversalKeys(backwardTraversal, key);

        final int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        final KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shortcutKey);
        // insert tab with CTRL+TAB instead of TAB
        textArea.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlTab, DefaultEditorKit.insertTabAction);
    }

    /**
     * Creates the toolbar with actions for editing text.
     */
    private void createToolbar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Add edit actions
        for (Action action : actions) {
            toolbar.add(action);
        }
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
        toolbar.setEnabled(active);

        for (Action action : actions) {
            action.setEnabled(active);
        }
    }

    public boolean isCollapseEditToolbar() {
        return collapseEditToolbar;
    }

    public void setCollapseEditToolbar(final boolean collapseEditToolbar) {
        this.collapseEditToolbar = collapseEditToolbar;

        if (!collapseEditToolbar) {
            collapsiblePane.setCollapsed(false);
        }
    }

}
