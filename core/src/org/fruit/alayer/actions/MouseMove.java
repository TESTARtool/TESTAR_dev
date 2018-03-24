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
package org.fruit.alayer.actions;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.PositionException;

public final class MouseMove extends TaggableBase implements Action {

	private static final long serialVersionUID = 3689287467588080030L;
	private final Position position;
	private final double minDuration;
	
	public MouseMove(Point point){
		this(new AbsolutePosition(point), 0);
	}
	
	public MouseMove(double x, double y){
		this(new AbsolutePosition(x, y), 0);
	}
	
	public MouseMove(Position position){ this(position, 0); }
	
	public MouseMove(Position position, double minDuration){
		Assert.notNull(position);
		Assert.isTrue(minDuration >= 0);
		this.position = position;
		this.minDuration = minDuration;
	}
		
	public String toString() {
		return "Move mouse to " + position.toString() + ".";
	}

	// by urueda
	@Override
	public String toString(Role... discardParameters) {
		for (Role r : discardParameters){
			if (r.name().equals(ActionRoles.MouseMove.name()))
				return "Mouse moved";
		}
		return toString();
	}	

	public void run(SUT system, State state, double duration) {
		try{
			Assert.notNull(system, state);
			Point p = position.apply(state);
			Util.moveCursor(system.get(Tags.StandardMouse), p.x(), p.y(), Math.max(duration, minDuration));
		}catch(NoSuchTagException tue){
			throw new ActionFailedException(tue);
		}catch(PositionException pe){
			throw new ActionFailedException(pe);
		}
	}

	// by urueda
	@Override
	public String toShortString() {
		Role r = get(Tags.Role, null);
		if (r != null)
			return r.toString() + toParametersString();
		else
			return toString();
	}

	// by urueda
	@Override
	public String toParametersString() {
		//return position.toString();
		return "";
	}	
}