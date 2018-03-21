package org.fruit.alayer.linux;

import org.fruit.Pair;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;


/**
 * Represents a canvas for a linux screen on which can be painted.
 */
public class GdkScreenCanvas implements Canvas {


    //region Canvas implementation


    @Override
    public double width() {
        return 0;
    }

    @Override
    public double height() {
        return 0;
    }

    @Override
    public double x() {
        return 0;
    }

    @Override
    public double y() {
        return 0;
    }

    @Override
    public void begin() {

    }

    @Override
    public void end() {

    }

    @Override
    public void line(Pen pen, double x1, double y1, double x2, double y2) {

    }

    @Override
    public void text(Pen pen, double x, double y, double angle, String text) {

    }

    @Override
    public Pair<Double, Double> textMetrics(Pen pen, String text) {
        return null;
    }

    @Override
    public void clear(double x, double y, double width, double height) {

    }

    @Override
    public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {

    }

    @Override
    public void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth, int imageHeight) {

    }

    @Override
    public void ellipse(Pen pen, double x, double y, double width, double height) {

    }

    @Override
    public void rect(Pen pen, double x, double y, double width, double height) {

    }

    @Override
    public Pen defaultPen() {
        return null;
    }

    @Override
    public void release() {

    }


    //endregion


}