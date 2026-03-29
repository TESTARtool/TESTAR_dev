/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
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
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.core.state;

import org.testar.core.Assert;
import org.testar.core.alayer.AbstractPosition;
import org.testar.core.alayer.Finder;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Shape;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.exceptions.PositionException;
import org.testar.core.exceptions.WidgetNotFoundException;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

public final class WidgetPosition extends AbstractPosition {

    private static final long serialVersionUID = -6963490602249863461L;
    private final Finder finder;
    private final double relX, relY;
    private final Tag<? extends Shape> shapeTag;
    private final boolean hitTest;
    private transient Point cachedWidgetPoint = null;

    public static WidgetPosition fromFinder(Finder finder) {
        return fromFinder(finder, 0.5, 0.5);
    }

    public static WidgetPosition fromFinder(Finder finder, double relX, double relY) {
        return new WidgetPosition(finder, Tags.Shape, relX, relY, true);
    }

    public WidgetPosition(Finder finder, Tag<? extends Shape> shapeTag, double relX, double relY, boolean hitTest) {
        Assert.notNull(finder, shapeTag);
        this.shapeTag = shapeTag;
        this.finder = finder;

        Widget cachedWidget = finder.getCachedWidget();
        if (cachedWidget != null) {
            cachedWidgetPoint = Util.relToAbs(cachedWidget.get(shapeTag), relX, relY);
        }

        this.relX = relX;
        this.relY = relY;
        this.hitTest = hitTest;
    }

    @Override
    public Point apply(State state) throws PositionException {
        try {
            Widget widget = finder.apply(state);

            if (hitTest && !Util.hitTest(widget, relX, relY, this.obscuredByChildEnabled)) {
                throw new PositionException("Widget found, but hittest failed!");
            }

            cachedWidgetPoint = Util.relToAbs(widget.get(shapeTag), relX, relY);
            return cachedWidgetPoint;

        } catch (WidgetNotFoundException wnfe) {
            throw new PositionException(wnfe);
        } catch (NoSuchTagException pue) {
            throw new PositionException(pue);
        }
    }

    @Override
    public String toString() {
        if (cachedWidgetPoint == null) {
            return "(" + relX + "," + relY + ")";
        } else {
            return cachedWidgetPoint.toString();
        }
    }
}
