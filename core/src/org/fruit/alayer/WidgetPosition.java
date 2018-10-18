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
package org.fruit.alayer;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.PositionException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;

public final class WidgetPosition extends AbstractPosition {

	private static final long serialVersionUID = -6963490602249863461L;
	private final Finder finder;
	private final double relX, relY;
	private final Tag<? extends Shape> shapeTag;
	private final boolean hitTest;
	private transient Point cachedWidgetPoint = null; // by urueda

	public static WidgetPosition fromFinder(Finder finder){
		return fromFinder(finder, 0.5, 0.5);
	}

	public static WidgetPosition fromFinder(Finder finder, double relX, double relY){
		return new WidgetPosition(finder, Tags.Shape, relX, relY, true);
	}

	public WidgetPosition(Finder finder, Tag<? extends Shape> shapeTag, double relX, double relY, boolean hitTest){
		Assert.notNull(finder, shapeTag);		
		this.shapeTag = shapeTag;
		this.finder = finder;
		// begin by urueda
		Widget cachedWidget = finder.getCachedWidget();
		if (cachedWidget != null)
			cachedWidgetPoint = Util.relToAbs(cachedWidget.get(shapeTag), relX, relY);
		// end by urueda
		this.relX = relX;
		this.relY = relY;
		this.hitTest = hitTest;
	}
	
	@Override // by urueda
	public Point apply(State state) throws PositionException {
		try{
			Widget widget = finder.apply(state);
			//if(hitTest && !Util.hitTest(widget, relX, relY))
			if(hitTest && !Util.hitTest(widget, relX, relY,this.obscuredByChildEnabled))
				throw new PositionException("Widget found, but hittest failed!");
			//return Util.relToAbs(widget.get(shapeTag), relX, relY);
			// start by urueda
			cachedWidgetPoint = Util.relToAbs(widget.get(shapeTag), relX, relY);
			return cachedWidgetPoint;
			// end by uureda
		}catch(WidgetNotFoundException wnfe){
			throw new PositionException(wnfe);
		}catch(NoSuchTagException pue){
			throw new PositionException(pue);
		}
	}
	
	@Override // by urueda
	public String toString(){
		//return "WidgetPosition (" + relX + ", " + relY + ")";
		// start by urueda
		//return "WidgetPosition" +
		//		((cachedWidgetPoint == null) ? "" : cachedWidgetPoint.toString()) +
		//		" (" + relX + ", " + relY + ")";
		if (cachedWidgetPoint == null)
			return "(" + relX + "," + relY + ")";
		else
			return cachedWidgetPoint.toString();
		// end by urueda
	}
}
