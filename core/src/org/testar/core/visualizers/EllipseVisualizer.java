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

package org.testar.core.visualizers;

import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Position;
import org.testar.core.state.State;
import org.testar.core.exceptions.PositionException;

public final class EllipseVisualizer implements Visualizer {

    private static final long serialVersionUID = -6006402344810634504L;
    private final double width, height;
    private final Pen pen;
    private final Position position;
    
    public EllipseVisualizer(Position position, Pen pen, double width, double height) {
        Assert.notNull(position, pen);
        this.width = width;
        this.height = height;
        this.pen = pen;
        this.position = position;
    }
    
    public void run(State state, Canvas canvas, Pen pen) {
        Assert.notNull(state, canvas, pen);
        pen = Pen.merge(pen, this.pen);
        try { // by urueda
            Point p = position.apply(state);
            canvas.ellipse(pen, p.x() - width * .5, p.y() - height * .5, width, height);
        } catch (PositionException pe) {
        }
    }
}
