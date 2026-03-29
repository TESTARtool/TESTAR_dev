/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import org.testar.core.Assert;

public final class Rect implements Shape {

    private static final long serialVersionUID = 678389946638512272L;

    private final double x, y, width, height;

    public static boolean intersect(Rect r1, Rect r2) {
        Assert.notNull(r1, r2);
        return !(r1.x() + r1.width() < r2.x() ||
                r1.y() + r1.height() < r2.y() ||
                r2.x() + r2.width() < r1.x() ||
                r2.y() + r2.height() < r1.y()); 
    }

    public static boolean overlap(Rect r1, Rect r2) {
        Assert.notNull(r1, r2);
        return !(r1.x() + r1.width() <= r2.x() ||
                r1.y() + r1.height() <= r2.y() ||
                r2.x() + r2.width() <= r1.x() ||
                r2.y() + r2.height() <= r1.y());
    }

    public static boolean contains(Rect r1, Rect r2) {
        Assert.notNull(r1, r2);
        return r2.x() >= r1.x() && r2.x() + r2.width() <= r1.x() + r1.width() && 
                r2.y() >= r1.y() && r2.y() + r2.height() <= r1.y() + r1.height();
    }
    
    public static double area(Rect rect) {
        Assert.notNull(rect);
        return rect.width() * rect.height();
    }

    public static Rect intersection(Rect r1, Rect r2) {
        if (r2 == null) {
            return r1;
        }
        if (r1 == null) {
            return null;
        }
        double x1 = Math.max(r1.x(), r2.x());
        double x2 = Math.min(r1.x() + r1.width(), r2.x() + r2.width());
        double y1 = Math.max(r1.y(), r2.y());
        double y2 = Math.min(r1.y() + r1.height(), r2.y() + r2.height());
        if (y2 < y1 || x2 < x1) {
            return null;
        }
        return Rect.fromCoordinates(x1, y1, x2, y2);
    }


    public static Rect from(double x, double y, double width, double height) {
        return new Rect(x, y, width, height);
    }

    public static Rect fromCoordinates(double x1, double y1, double x2, double y2) {
        return new Rect(x1, y1, x2 - x1, y2 - y1);
    }

    private Rect(double x, double y, double width, double height) {
        Assert.isTrue(width >= 0 && height >= 0,
                      "The width and height of a rectangle cannot be negative!: " +
                      "WIDTH=" + width + " x HEIGHT=" + height);
        this.x = x;
        this.width = width;
        this.y = y;
        this.height = height;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double width() {
        return this.width;
    }

    public double height() {
        return this.height;
    }

    public boolean contains(double x, double y) {
        if (x < this.x || y < this.y) {
            return false;
        }
        if (x > this.x + width || y > this.y + height) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "Rect [x:" + x() + " y:" + y() + " w:" + width() + " h:" + height() + "]";
    }

    public void paint(Canvas canvas, Pen pen) {
        canvas.rect(pen, x, y, width, height);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Rect)) {
            return false;
        }
        Rect r = (Rect) o;
        return r.x == this.x && r.y == this.y && r.width == this.width && r.height == this.height;
    }
}
