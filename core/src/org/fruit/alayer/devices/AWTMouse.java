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
	
	private double displayScale;

	private AWTMouse() throws FruitException{
		try{
			robot = new Robot();
			this.displayScale = 1.0;
		}catch(AWTException awte){
			throw new FruitException(awte);
		}
	}
	
	public void setCursorDisplayScale(double displayScale) {
		this.displayScale = displayScale;
	}

	public String toString() { return "AWT Mouse"; }

	public void press(MouseButtons k) { 
		robot.mousePress(k.code());}

	public void release(MouseButtons k) { 
		robot.mouseRelease(k.code());
	}	
	
	public void isPressed(MouseButtons k) {
		throw new UnsupportedOperationException("AWT Mouse cannot poll the mouse's state!");
	}

	public void setCursor(double x, double y) { robot.mouseMove((int)(x*displayScale), (int)(y*displayScale)); }

	public Point cursor() {
		PointerInfo info = MouseInfo.getPointerInfo();
		if(info == null)
			throw new RuntimeException("MouseInfo.getPointerInfo() returned null! This seeems to be undocumented Java library behavior... " +
					"Consider using a platform specific Mouse Implementation instead of AWTMouse!");
		java.awt.Point p = info.getLocation();
		Point ret = Point.from(p.x/displayScale, p.y/displayScale);
		return ret;
	}
}
