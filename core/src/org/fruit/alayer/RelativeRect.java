/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRelativeRect, INDIRelativeRect, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.fruit.alayer;

import org.fruit.Assert;

public final class RelativeRect implements RelativePosition {
    private static final long serialVersionUID = 6086845748577099698L;
    private final double x, y, width, height;

    public static boolean intersect(RelativeRect r1, RelativeRect r2) {
        Assert.notNull(r1, r2);
        return !(r1.x() + r1.width() < r2.x() ||
                r1.y() + r1.height() < r2.y() ||
                r2.x() + r2.width() < r1.x() ||
                r2.y() + r2.height() < r1.y()); 
    }

    public static boolean contains(RelativeRect r1, RelativeRect r2) {
        Assert.notNull(r1, r2);
        return r2.x() >= r1.x() && r2.x() + r2.width() <= r1.x() + r1.width() && 
                r2.y() >= r1.y() && r2.y() + r2.height() <= r1.y() + r1.height();
    }

    public static double area(RelativeRect relativeRect){
        Assert.notNull(relativeRect);
        return relativeRect.width() * relativeRect.height();
    }

    public static RelativeRect intersection(RelativeRect r1, RelativeRect r2){
        if(r2 == null) return r1;
        if(r1 == null) return null;
        double x1 = Math.max(r1.x(), r2.x());
        double x2 = Math.min(r1.x() + r1.width(),r2.x() + r2.width());
        double y1 = Math.max(r1.y(), r2.y());
        double y2 = Math.min(r1.y() + r1.height(), r2.y() + r2.height());
        if(y2 < y1 || x2 < x1) return null;
        return RelativeRect.fromCoordinates(x1, y1, x2, y2);
    }

    public static RelativeRect union(RelativeRect r1, RelativeRect r2){
        if(r2 == null) return r1;
        if(r1 == null) return null;
        double x1 = Math.min(r1.x(), r2.x());
        double x2 = Math.max(r1.x() + r1.width(),r2.x() + r2.width());
        double y1 = Math.min(r1.y(), r2.y());
        double y2 = Math.max(r1.y() + r1.height(), r2.y() + r2.height());
        if(y2 < y1 || x2 < x1) return null;
        return RelativeRect.fromCoordinates(x1, y1, x2, y2);
    }

    public static RelativeRect from(double x, double y, double width, double height){ return new RelativeRect(x, y, width, height); }
    public static RelativeRect fromCoordinates(double x1, double y1, double x2, double y2){ return new RelativeRect(x1, y1, x2 - x1, y2 - y1); }
    public static RelativeRect fromWidgetStateRect(double x1, double y1, double x2, double y2, Rect stateRect){ 
        return new RelativeRect(x1, y1, x2 - x1, y2 - y1, stateRect); 
    }

    private RelativeRect(double x, double y, double width, double height){
        Assert.isTrue(width >= 0 && height >= 0,
                "The width and height of a RelativeRectangle cannot be negative!: " +
                        "WIDTH=" + width + " x HEIGHT=" + height);
        this.x = x;
        this.width = width;
        this.y = y;
        this.height = height;
    }
    
    private RelativeRect(double x, double y, double width, double height, Rect stateRect){
        this.x = x - stateRect.x();
        this.width = width;
        this.y = y - stateRect.y();
        this.height = height;
    }

    public double x(){  return this.x; }
    public double y() { return this.y; }
    public double width() { return this.width; }
    public double height() { return this.height; }

    public boolean contains(double x, double y) {
        if(x < this.x || y < this.y) return false;
        if(x > this.x + width || y > this.y + height) return false;
        return true;
    }

    public String toString(){
        return "RelativeRect [x:" + x() + " y:" + y() + " w:" + width() + " h:" + height() + "]";
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof RelativeRect)) return false;
        RelativeRect r = (RelativeRect) o;
        return r.x == this.x && r.y == this.y && r.width == this.width && r.height == this.height;
    }
}
