package org.fruit.example.linux;

import javax.swing.*;
import java.awt.*;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

public class ClickThroughWindow {

    public static void main(String[] args) {

        // Determine if the GraphicsDevice supports translucency.
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        //If translucent windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
            System.err.println(
                    "Translucency is not supported");
            System.exit(0);
        }

        new ClickThroughWindow();
    }


    public ClickThroughWindow() {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }

            JFrame frame = new JFrame("Testing");
            frame.setUndecorated(true);
            frame.setResizable(false);
            frame.setType(Window.Type.UTILITY);
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TestPane());
            frame.setAlwaysOnTop(true);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public class TestPane extends JPanel {

        public TestPane() {
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(getBackground());
            g2d.setColor(Color.RED);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2d.dispose();
        }

    }

}