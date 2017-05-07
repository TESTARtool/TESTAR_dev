package org.fruit.alayer.linux.SpyMode;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DrawingPane extends JPanel {


    private CopyOnWriteArrayList<DrawableObject> _content = new CopyOnWriteArrayList<>();


    /**
     * Adds an object to draw on the pane.
     * @param c The content to draw.
     * @return True if successfully added; False otherwise.
     */
    boolean addDrawableContent(DrawableObject c) {

        _content.add(c);
        this.repaint();
        return true;

    }


    /**
     * Clears the pane from content.
     */
    void clearDrawableContent() {
        _content.clear();
    }


    DrawingPane() {
        setOpaque(false);
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1920, 1080);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();


        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);


        for (DrawableObject c: _content) {
            c.draw(g2d);
        }

        g2d.dispose();

    }

}