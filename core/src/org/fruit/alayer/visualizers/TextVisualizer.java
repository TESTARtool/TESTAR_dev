/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */

package org.fruit.alayer.visualizers; // refactored by urueda

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.State;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.exceptions.PositionException;

public final class TextVisualizer implements Visualizer {
	
	private static final long serialVersionUID = 9156304220974950751L;
	private final Position pos;
	private final String text;
	private final Pen pen;
	
	public TextVisualizer(Position pos, String text, Pen pen) {
		Assert.notNull(pos, text, pen);
		this.pos = pos;
		this.text = text;
		this.pen = pen;
	}
	
	public void run(State state, Canvas cv, Pen pen) {
		Assert.notNull(state, cv, pen);
		pen = Pen.merge(pen, this.pen);
		try { // by urueda
			Point p = pos.apply(state);			
			Pair<Double, Double> m = cv.textMetrics(pen, text);
			cv.text(pen, p.x() - m.left() / 2, p.y() - m.right() / 2, 0, text);
		} catch (PositionException pe) {
			pe.printStackTrace();
		}			
	}
}
