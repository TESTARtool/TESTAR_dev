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

import java.util.Iterator;

import org.testar.monkey.Assert;
import org.testar.monkey.UnFunc;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.Point;
import org.testar.monkey.alayer.Position;
import org.testar.monkey.alayer.SplineTrajectory;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.StrokeCaps;
import org.testar.monkey.alayer.Visualizer;

public class TrajectoryVisualizer implements Visualizer {

	private static final long serialVersionUID = 1107281202398264314L;
	final UnFunc<State, Iterable<Point>> trajectory;
	final Pen pen;

	public TrajectoryVisualizer(Pen pen, Position... positions){
		this(new SplineTrajectory(10, positions), pen);
	}
	
	public TrajectoryVisualizer(UnFunc<State, Iterable<Point>> trajectory, Pen pen){
		Assert.notNull(trajectory, pen);
		Assert.isTrue(pen.strokeWidth() != null);
		this.trajectory = trajectory;		
		this.pen = pen;
	}
	
	public void run(State state, Canvas canvas, Pen pen) {
		Assert.notNull(state, canvas, pen);
		pen = Pen.merge(pen, this.pen);
		Iterator<Point> iter = trajectory.apply(state).iterator();
		Point last = iter.next();
		
		while(iter.hasNext()){
			Point current = iter.next();
			
			if(!iter.hasNext() && (pen.strokeCaps() == StrokeCaps._Arrow || pen.strokeCaps() == StrokeCaps.Arrow_))
				Util.arrow(canvas, pen, last.x(), last.y(), current.x(), current.y(), 5 * pen.strokeWidth(), 5 * pen.strokeWidth());
			else
				canvas.line(pen, last.x(), last.y(), current.x(), current.y());
			
			last = current;
		}
	}
}
