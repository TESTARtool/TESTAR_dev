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
package org.testar.monkey.alayer.visualizers; // refactored by urueda

import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Visualizer;

public final class ShapeVisualizer implements Visualizer {
	private static final long serialVersionUID = -1411595441118761574L;
	private final Shape shape;
	private final String label;
	private final double labelX, labelY;
	private final Pen pen;
	
	public ShapeVisualizer(Pen pen, Shape shape, String label, double labelX, double labelY){
		Assert.notNull(shape, pen);
		this.shape = shape;
		this.pen = pen;
		this.label = label;
		this.labelX = labelX;
		this.labelY = labelY;
	}

	public void run(State state, Canvas c, Pen pen) {
		Assert.notNull(state, c, pen);
		pen = Pen.merge(pen, this.pen);
		shape.paint(c, pen);
		if(label != null)
			c.text(pen, shape.x() + shape.width() * labelX, shape.y() + shape.height() * labelY, 0, label);
	}
}
