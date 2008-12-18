package org.remast.baralga;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXFrame;
import org.remast.swing.text.TextEditor;

/**
 * Test frame for the text editor component.
 * @author remast
 */
public class TextEditorTest {
	
	public static void main(final String[] args) {
		JXFrame frame = new JXFrame();
		frame.add(new TextEditor());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

}
