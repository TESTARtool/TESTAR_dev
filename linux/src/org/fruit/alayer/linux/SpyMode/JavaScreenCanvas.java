package org.fruit.alayer.linux.SpyMode;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;

import javax.swing.*;
import java.awt.*;


public class JavaScreenCanvas implements Canvas {


    //region Global variables


    private boolean _running;
    private DrawingPane _paneReference;
    private JFrame _frameReference;

    //endregion


    //region Canvas implementation


    //region Properties


    private double _width;
    @Override
    public double width() {
        return _width;
    }


    private double _height;
    @Override
    public double height() {
        return _height;
    }


    private double _x;
    @Override
    public double x() {
        return _x;
    }


    private double _y;
    @Override
    public double y() {
        return _y;
    }


    private Pen _defaultPen;
    @Override
    public Pen defaultPen() {
        return _defaultPen;
    }


    //endregion


    @Override
    public void begin() {
        runningCheck();
    }


    @Override
    public void end() {
        // Not much to do here.
    }


    @Override
    public void release() {

        if(!_running)
            return;
        _running = false;

        _frameReference.setVisible(false);

    }


    /**
     * Creates a pair of values for a text, most likely: a letter width of 2 and a height of 20.
     * @param pen A pen which is not needed.
     * @param text The text to get metrics on.
     * @return A pair of values.
     */
    @Override
    public Pair<Double, Double> textMetrics(Pen pen, String text) {
        Assert.notNull(pen, text);
        return Pair.from(text.length() * 2., 20.);
    }


    /**
     * Clears the drawing area.
     * @param x X location of the rectangle used to clear the drawing area - deprecated.
     * @param y Y location of the rectangle used to clear the drawing area - deprecated.
     * @param width Width of the rectangle used to clear the drawing area - deprecated.
     * @param height Height of the rectangle used to clear the drawing area - deprecated.
     */
    @Override
    public void clear(double x, double y, double width, double height) {

        runningCheck();
        //System.out.println("Clear: (" + x + ", " + y + ").");
        _paneReference.clearDrawableContent();

    }


    @Override
    public void line(Pen pen, double x1, double y1, double x2, double y2) {

        runningCheck();
        //System.out.println("Draw Line: (" + x1 + ", " + y1 + ").");
        _paneReference.addDrawableContent(new DrawableLine(new Point(new Double(x1).intValue(), new Double(y1).intValue()), pen, _defaultPen, new Point(new Double(x2).intValue(), new Double(y2).intValue())));

    }


    @Override
    public void text(Pen pen, double x, double y, double angle, String text) {

        runningCheck();
        //System.out.println("Draw Text: (" + x + ", " + y + ").");
        _paneReference.addDrawableContent(new DrawableText(new Point(new Double(x).intValue(), new Double(y).intValue()), pen, _defaultPen, text));

    }


    @Override
    public void ellipse(Pen pen, double x, double y, double width, double height) {

        runningCheck();
        //System.out.println("Draw Ellipse: (" + x + ", " + y + ").");
        _paneReference.addDrawableContent(new DrawableEllipse(new Point(new Double(x).intValue(), new Double(y).intValue()), pen, _defaultPen,
                new Rectangle(new Double(x).intValue(), new Double(y).intValue(), new Double(width).intValue(), new Double(height).intValue())));

    }


    @Override
    public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {
        //System.out.println("Draw Triangle: (" + x1 + ", " + y1 + ").");
        throw new UnsupportedOperationException();
    }

    @Override
    public void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth, int imageHeight) {

        runningCheck();
        //System.out.println("Draw Image: (" + x + ", " + y + ").");
        //_paneReference.addDrawableContent(new DrawableImage(new Point(new Double(x).intValue(), new Double(y).intValue()), pen, _defaultPen, image));
        throw new UnsupportedOperationException();
    }


    @Override
    public void rect(Pen pen, double x, double y, double width, double height) {

        runningCheck();
        //System.out.println("Draw Rect: (" + x + ", " + y + ").");
        _paneReference.addDrawableContent(new DrawableRect(new Point(new Double(x).intValue(), new Double(y).intValue()), pen, _defaultPen,
                new Rectangle(new Double(x).intValue(), new Double(y).intValue(), new Double(width).intValue(), new Double(height).intValue())));

    }


    //endregion


    //region Constructors


    private JavaScreenCanvas(int x, int y, int width, int height, Pen defaultPen) {


        Assert.notNull(defaultPen);
        Assert.isTrue(width >= 0 && height >= 0);

        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _defaultPen = defaultPen;

        EventQueue.invokeLater(() -> {


            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }


            // Set a title for reference.
            _frameReference = new JFrame("Testar - Spy window");


            // No title bar, no task bar, always on top.
            _frameReference.setUndecorated(true);
            _frameReference.setType(Window.Type.UTILITY);
            _frameReference.setAlwaysOnTop(true);
            _frameReference.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


            // Make the window invisible.
            _frameReference.setBackground(new Color(0, 0, 0, 0));


            // Set the size and location.
            _frameReference.setLocation(x, y);
            _frameReference.setSize(width, height);


            // Add content.
            _paneReference = new DrawingPane();
            _frameReference.add(_paneReference);


            // frame.pack();

            _frameReference.setLocationRelativeTo(null);
            _frameReference.setVisible(true);


            // Signal the window is currently active.
            _running = true;


        });

    }


    //endregion


    //region Other necessary methods


    /**
     * Creates a window covering the primary monitor.
     * @param defaultPen The default pen used to draw stuff.
     * @return An instance of a window to draw on in Spy- mode.
     */
    public static JavaScreenCanvas fromPrimaryMonitor(Pen defaultPen){

        // Get the primary monitor bounds and create a window covering the entire screen.
        Rectangle monitorBounds =  getPrimaryMonitorBounds();

        return new JavaScreenCanvas(monitorBounds.x, monitorBounds.y, monitorBounds.width, monitorBounds.height, defaultPen);

    }


    /**
     * Checks whether the Spy- window is showing. If not, it will throw an IllegalStateException.
     */
    private void runningCheck() {
        if(!_running)
            throw new IllegalStateException();
    }


    /**
     * Called by GC to clean up this object.
     */
    public void finalize() {
        release();
    }


    //endregion


    //region Helper methods


    /**
     * Gets the bounds of the primary monitor.
     * @return A rectangle containing the location and bounds of the primary monitor.
     */
    private static Rectangle getPrimaryMonitorBounds() {

        // Get primary monitor resolution.
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = g.getScreenDevices();

        for (GraphicsDevice device : devices) {

            Rectangle monitorBounds = device.getDefaultConfiguration().getBounds();

            if (monitorBounds.x == 0 && monitorBounds.y == 0) {
                // Primary monitor starts at (0, 0).
                return monitorBounds;
            }

        }

        System.out.println("Could not find primary monitor! Assume 1920 x 1080 @ (0, 0)");
        return new Rectangle(0,0, 1920, 1080);

    }


    //endregion


}