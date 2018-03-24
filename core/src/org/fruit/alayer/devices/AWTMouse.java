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
package org.fruit.alayer.devices;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;

import org.fruit.FruitException;
import org.fruit.alayer.Point;

public final class AWTMouse implements Mouse {
	public static AWTMouse build() throws FruitException{ return new AWTMouse(); }
	private final Robot robot;

	private AWTMouse() throws FruitException{
		try{
			robot = new Robot();
		}catch(AWTException awte){
			throw new FruitException(awte);
		}
	}

	public String toString() { return "AWT Mouse"; }

	public void press(MouseButtons k) { 
		//System.out.println("lc down [AWTMouse]");
		robot.mousePress(k.code());}

	public void release(MouseButtons k) { 
		//System.out.println("lc up [AWTMouse]");
		robot.mouseRelease(k.code());
	}	
	
	public void isPressed(MouseButtons k) {
		throw new UnsupportedOperationException("AWT Mouse cannot poll the mouse's state!");
	}

	public void setCursor(double x, double y) { robot.mouseMove((int)x, (int)y); }

	public Point cursor() {
		PointerInfo info = MouseInfo.getPointerInfo();
		if(info == null)
			throw new RuntimeException("MouseInfo.getPointerInfo() returned null! This seeems to be undocumented Java library behavior... " +
					"Consider using a platform specific Mouse Implementation instead of AWTMouse!");
		java.awt.Point p = info.getLocation();
		Point ret = Point.from(p.x, p.y);
		return ret;
	}
}