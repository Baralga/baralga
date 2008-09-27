package org.remast.swing.text;

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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXPanel;

/**
 * Simple editor for html formatted text.
 * @author remast
 */
@SuppressWarnings("serial")
public class TextEditor extends JXPanel {

	/**
	 * A interface that allows to listen for text changes to the card side text panes. Use
	 * {@link CardPanel#addTextObserver} method to hook it to the CardPanel.
	 */
	public interface TextChangeObserver {
		public void onTextChange();
	}

	private List<TextChangeObserver> textObservers = new ArrayList<TextChangeObserver>();

	JTextPane textPane;

	private JToolBar toolbar;

	private JXCollapsiblePane collapsiblePane;

	private boolean collapseEditToolbar = true;

	private boolean scrollable = false;

	private StyleSheet styleSheet;

	private HTMLEditorKit editorKit;

	private static class BoldAction extends javax.swing.text.StyledEditorKit.BoldAction {

		public BoldAction() {
			super();
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/text_bold.png"))); //$NON-NLS-1$
		}

	}

	private static class ItalicAction extends javax.swing.text.StyledEditorKit.ItalicAction {

		public ItalicAction() {
			super();
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/icons/text_italic.png"))); //$NON-NLS-1$
		}

	}

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

	public TextEditor() {
		initialize();
	}

	public TextEditor(final boolean scrollable) {
		this.scrollable = scrollable;
		initialize();
	}

	public TextEditor(final boolean scrollable, final boolean collapseEditToolbar) {
		this.scrollable = scrollable;
		this.collapseEditToolbar = collapseEditToolbar;
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		textPane = new JTextPane();

		styleSheet = new StyleSheet();
		styleSheet.addRule("body {font-family: Tahoma; font-size: 11pt; font-style: normal; font-weight: normal;}");

		editorKit = new HTMLEditorKit();
		editorKit.setStyleSheet(styleSheet);
		textPane.setEditorKit(editorKit);

		textPane.setEnabled(true);
		textPane.setEditable(true);

		setTabBehavior();
		textPane.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				if (collapseEditToolbar) {
					collapsiblePane.setCollapsed(false);
				}
			}

			public void focusLost(FocusEvent e) {
				if (collapseEditToolbar) {
					if (e.getOppositeComponent() != null && e.getOppositeComponent().getParent() != toolbar) {
						collapsiblePane.setCollapsed(true);
					}
				}
			}

		});

		textPane.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				notifyTextObservers();
			}

			public void insertUpdate(DocumentEvent e) {
				notifyTextObservers();
			}

			public void removeUpdate(DocumentEvent e) {
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
			this.add(new JScrollPane(textPane), BorderLayout.CENTER);
		} else {
			this.add(textPane, BorderLayout.CENTER);
		}
	}

	private void setTabBehavior() {
		// focus next pane with TAB instead of CTRL+TAB
		Set<KeyStroke> key = new HashSet<KeyStroke>();
		key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));

		int forwardTraversal = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
		textPane.setFocusTraversalKeys(forwardTraversal, key);

		// focus previous pane with SHIFT+TAB instead of SHIFT+CTRL+TAB
		key = new HashSet<KeyStroke>();
		key.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));

		final int backwardTraversal = KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS;
		textPane.setFocusTraversalKeys(backwardTraversal, key);

		final int shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		final KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shortcutKey);
		// insert tab with CTRL+TAB instead of TAB
		textPane.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlTab, DefaultEditorKit.insertTabAction);
	}

	/**
	 * Creates the toolbar with actions for editing text.
	 */
	private void createToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);

		// Add edit actions
		toolbar.add(new BoldAction());
		toolbar.add(new ItalicAction());
		toolbar.add(new CopyAction());
		toolbar.add(new PasteAction());
	}

	public String getText() {
		return textPane.getText();
	}

	public void setText(final String description) {
		textPane.setText(description);
	}

	public void setEditable(final boolean active) {
		textPane.setEnabled(active);
		textPane.setEditable(active);
	}

	public boolean isCollapseEditToolbar() {
		return collapseEditToolbar;
	}

	public void setCollapseEditToolbar(boolean collapseEditToolbar) {
		this.collapseEditToolbar = collapseEditToolbar;

		if (!collapseEditToolbar) {
			collapsiblePane.setCollapsed(false);
		}
	}

}
