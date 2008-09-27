package org.remast.baralga;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXFrame;
import org.remast.swing.text.TextEditor;

public class TextEditorTest {
	
	public static void main(String[] args) {
		JXFrame frame = new JXFrame();
		frame.add(new TextEditor());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

}
