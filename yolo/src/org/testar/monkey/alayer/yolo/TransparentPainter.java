package org.testar.monkey.alayer.yolo;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TransparentPainter extends JFrame {
	private static final long serialVersionUID = 4415385329940275018L;

	private static final int DOT_SIZE = 10;

	private DotPanel dotPanel;
	private List<Point2D> dotPositions;

	public TransparentPainter() {
		setUndecorated(true);
		setResizable(false);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setOpacity(0.5f);
		setBackground(new Color(0, 0, 0, 0));  // Transparent background

		dotPositions = new ArrayList<>();
		dotPanel = new DotPanel(dotPositions);

		setAlwaysOnTop(true);  // Set the frame to be always on top

		setLocationRelativeTo(null);  // Center the window
	}

	public void clearDotPositions() {
		dotPositions = new ArrayList<>();
		remove(dotPanel);
	}

	public void addDotPosition(Point2D dotPosition) {
		dotPositions.add(dotPosition);
	}

	public void repaint() {
		dotPanel = new DotPanel(dotPositions);
		add(dotPanel);
	}

	private class DotPanel extends JPanel {
		private static final long serialVersionUID = 6749704912061704282L;

		private List<Point2D> dotPositions;

		public DotPanel(List<Point2D> dotPositions) {
			this.dotPositions = dotPositions;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			for (Point2D position : dotPositions) {
				g2d.setColor(Color.GREEN);
				g2d.fill(new Ellipse2D.Double(position.getX(), position.getY(), DOT_SIZE, DOT_SIZE));
			}
		}
	}
}
