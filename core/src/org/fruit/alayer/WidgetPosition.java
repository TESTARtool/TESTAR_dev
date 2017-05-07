/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

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