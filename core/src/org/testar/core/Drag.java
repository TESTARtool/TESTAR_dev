/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core;

public class Drag {

    private final double fromX, fromY, toX, toY;

    public Drag(double fromX, double fromY, double toX, double toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public double getFromX() {
        return fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public double getToX() {
        return toX;
    }

    public double getToY() {
        return toY;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Drag)) {
            return false;
        }
        Drag other = (Drag) obj;
        return Double.compare(fromX, other.fromX) == 0 &&
                Double.compare(fromY, other.fromY) == 0 &&
                Double.compare(toX, other.toX) == 0 &&
                Double.compare(toY, other.toY) == 0;
    }
}
