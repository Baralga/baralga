package org.remast.swing;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.prompt.BuddyButton;
import org.jdesktop.swingx.prompt.BuddySupport;
import org.remast.util.TextResourceBundle;

/**
 * @author remast
 */
@SuppressWarnings("serial")
public class JSearchField extends JXTextField {
	
    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(JSearchField.class);
	
	public JSearchField() {
		setPrompt(textBundle.textFor("SearchField.prompt"));

		BuddyButton b = new BuddyButton();
		b.setIcon(new ImageIcon(getClass().getResource("/icons/Start-Menu-Search-icon.png")));
		BuddySupport.addGap(5, BuddySupport.Position.LEFT, this);
		BuddySupport.addLeft(b, this);
		BuddySupport.addGap(5, BuddySupport.Position.LEFT, this);
	}

}
