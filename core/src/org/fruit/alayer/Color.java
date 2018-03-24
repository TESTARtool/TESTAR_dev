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

import java.io.Serializable;

import org.fruit.Assert;

/**
 * Color objects represent a color and are used together with <code>Pen</code>'s and
 * <code>Canvas</code> objects.
 * 
 * @see Pen
 * @see Canvas
 */
public final class Color implements Serializable {
	private static final long serialVersionUID = 7465037502136479951L;

	public static final Color Red = from(255, 0, 0, 255);
	public static final Color DeepPink = from(255, 20, 147, 255);
	public static final Color Aqua = from(0, 128, 255, 255);
	public static final Color Moss = from(0, 128, 64, 255);
	public static final Color Salmon = from(255, 102, 102, 255);
	public static final Color Blue = from(0, 0, 255, 255);
	public static final Color Navy = from(0, 0, 128, 255);
	public static final Color CornflowerBlue = from(100, 149, 237, 255);
	public static final Color SteelBlue = from(70, 130, 180, 255);
	public static final Color BlueViolet = from(138, 43, 226, 255);
	public static final Color Green = from(0, 255, 0, 255);
	public static final Color LimeGreen = from(50, 205, 50, 255);
	public static final Color Yellow = from(255, 255, 0, 255);
	public static final Color Gold = from(255, 215, 0, 255);
	public static final Color White = from(255, 255, 255, 255);
	public static final Color Black = from(0, 0, 0, 255);
	
	public static Color from(int red, int green, int blue, int alpha){ return new Color(red, green, blue, alpha); }

	private final int red, green, blue, alpha, argb32;

	private Color(int red, int green, int blue, int alpha){
		Assert.isTrue(red >= 0 && green >= 0 && blue >= 0 && alpha >= 0 &&
				red <= 255 && green <= 255 && blue <= 255 && alpha <= 255);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.argb32 = red + (green << 8) + (blue << 16) + (alpha << 24);
	}

	public int red(){ return red; }
	public int green(){ return green; }
	public int blue(){ return blue; }
	public int alpha(){ return alpha; }
	public int argb32(){ return argb32; }
	public int hashCode(){ return argb32();	}

	public boolean equals(Object o){
		if(this == o)
			return true;

		if(o instanceof Color){
			Color co = (Color) o;
			return co.alpha == alpha && co.red == red && co.green == green && co.blue == blue;
		}
		return false;
	}
	
	public String toString(){ 
		return "Color (red: " + red() + " green: " + green() +
			" blue: " + blue() + " alpha: " + alpha() + ")";
	}
}