package org.testar.visualvalidation;

import java.awt.Rectangle;

public class Location extends Rectangle {
    public Location(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Location() {

    }

    @Override
    public String toString() {
        return String.format("[x=%d, y=%d, width=%d, height=%d]", x, y, width, height);
    }
}
