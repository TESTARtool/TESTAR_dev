/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Position;
import org.testar.core.state.State;
import org.testar.core.exceptions.PositionException;

public final class TextVisualizer implements Visualizer {

    private static final long serialVersionUID = 9156304220974950751L;

    protected static final Logger logger = LogManager.getLogger();

    final Position pos;
    final String text;
    final Pen pen;

    public TextVisualizer(Position pos, String text, Pen pen) {
        Assert.notNull(pos, text, pen);
        this.pos = pos;
        this.text = text;
        this.pen = pen;
    }
    
    public String getText() {
        return text;
    }
    
    public TextVisualizer withText(String newText, Pen newPen) {
        Assert.notNull(newText, newPen);
        return new TextVisualizer(this.pos, newText, newPen);
    }

    public void run(State state, Canvas cv, Pen pen) {
        Assert.notNull(state, cv, pen);
        pen = Pen.merge(pen, this.pen);
        try {
            Point p = pos.apply(state);
            Pair<Double, Double> m = cv.textMetrics(pen, text);
            cv.text(pen, p.x() - m.left() / 2, p.y() - m.right() / 2, 0, text);
        } catch (PositionException pe) {
            logger.log(Level.ERROR, pe);
        } catch (NullPointerException ne) {
            logger.log(Level.ERROR, ne);
        }
    }
}
