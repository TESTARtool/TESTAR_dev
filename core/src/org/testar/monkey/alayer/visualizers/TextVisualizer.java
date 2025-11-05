/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.visualizers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.Assert;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.Point;
import org.testar.monkey.alayer.Position;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.exceptions.PositionException;

public final class TextVisualizer implements Visualizer {

	private static final long serialVersionUID = 9156304220974950751L;

	protected static final Logger logger = LogManager.getLogger();

	final Position pos;
	final String text;
	final Pen pen;

	public TextVisualizer(Position pos, String text, Pen pen){
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
