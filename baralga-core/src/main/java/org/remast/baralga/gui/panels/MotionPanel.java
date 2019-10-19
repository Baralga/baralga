package org.remast.baralga.gui.panels;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class MotionPanel extends JPanel {

	private Point initialClick;

	@SuppressWarnings("unused")
	private JWindow parentWindow;

	public MotionPanel(final JWindow parentWindow) {
		this.parentWindow = parentWindow;

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
				getComponentAt(initialClick);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// get location of Window
				int thisX = parentWindow.getLocation().x;
				int thisY = parentWindow.getLocation().y;

				// Determine how much the mouse moved since the initial click
				int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
				int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

				// Move window to this position
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				parentWindow.setLocation(X, Y);
			}
		});

	}

}