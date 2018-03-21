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
 * A pen is used to draw on a <code>Canvas</code> object. It determines
 * attributes such as line width, color and fill pattern.
 * 
 * @see Canvas
 */
public abstract class Pen implements Serializable {	
	private static final long serialVersionUID = 8309231237087967825L;

	public static final int DEFAULT_FONT_SIZE = 16; // by urueda
	
	public static final Pen PEN_IGNORE  = Pen.newPen().build();
	public static final Pen PEN_FILL    = Pen.newPen().setFillPattern(FillPattern.Solid).build();
	public static final Pen PEN_STROKE  = Pen.newPen().setStrokePattern(StrokePattern.Solid)
										   	 .setFillPattern(FillPattern.None).build();
	public static final Pen PEN_DEFAULT = Pen.newPen().setStrokePattern(StrokePattern.Solid)
											 .setFillPattern(FillPattern.None).setColor(Color.CornflowerBlue)
											 .setFont("Times New Roman").setFontSize(/*16*/DEFAULT_FONT_SIZE/*by urueda*/)
											 .setStrokeCaps(StrokeCaps.None).setStrokeWidth(5).build();
	// begin by urueda
	public static final Pen PEN_BLUE = Pen.newPen().setColor(Color.Blue).build(),
							PEN_RED = Pen.newPen().setColor(Color.Red).build(),
							PEN_BLACK = Pen.newPen().setColor(Color.Black).build(),

							PEN_WHITE_ALPHA = Pen.newPen().setColor(Color.from(255, 255, 255, 170)).setFillPattern(FillPattern.Solid).build(),
							PEN_BLACK_ALPHA = Pen.newPen().setColor(Color.from(0, 0, 0, 170)).setFillPattern(FillPattern.Solid).build(),

							PEN_RED_FILL = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).build(),
							PEN_BLUE_FILL = Pen.newPen().setColor(Color.Blue).setFillPattern(FillPattern.Solid).build(),
							PEN_GREEN_FILL = Pen.newPen().setColor(Color.Green).setFillPattern(FillPattern.Solid).build(),

							PEN_WHITE_1px = Pen.newPen().setColor(Color.White).setStrokeWidth(1).build(),
							PEN_GREY_1px = Pen.newPen().setColor(Color.from(128, 128, 128, 255)).setStrokeWidth(1).build(),
							PEN_RED_1px = Pen.newPen().setColor(Color.Red).setStrokeWidth(1).build(),
							PEN_GREEN_1px = Pen.newPen().setColor(Color.Green).setStrokeWidth(1).build(),
							PEN_BLUE_1px = Pen.newPen().setColor(Color.Blue).setStrokeWidth(1).build(),

							PEN_WHITE_1px_ALPHA = Pen.newPen().setColor(Color.from(255, 255, 255, 96)).setStrokeWidth(1).build(),
							PEN_GREY_1px_ALPHA = Pen.newPen().setColor(Color.from(128, 128, 128, 96)).setStrokeWidth(1).build(),
							PEN_RED_1px_ALPHA = Pen.newPen().setColor(Color.from(255, 128, 128, 96)).setStrokeWidth(1).build(),
							PEN_GREEN_1px_ALPHA = Pen.newPen().setColor(Color.from(128, 255, 128, 96)).setStrokeWidth(1).build(),
							PEN_BLUE_1px_ALPHA = Pen.newPen().setColor(Color.from(128, 128, 255, 96)).setStrokeWidth(1).build(),

							PEN_WHITE_TEXT_6px = Pen.newPen().setColor(Color.White).setFontSize(6).setStrokeWidth(1).build(),

							PEN_WHITE_TEXT_12px = Pen.newPen().setColor(Color.White).setFontSize(12).setStrokeWidth(1).build(),
							PEN_RED_TEXT_12px = Pen.newPen().setColor(Color.Red).setFontSize(12).setStrokeWidth(1).build(),
							PEN_GREEN_TEXT_12px = Pen.newPen().setColor(Color.Green).setFontSize(12).setStrokeWidth(1).build(),
							PEN_BLUE_TEXT_12px = Pen.newPen().setColor(Color.Blue).setFontSize(12).setStrokeWidth(1).build(),

							PEN_WHITE_TEXT_64px = Pen.newPen().setColor(Color.White).setFontSize(64).setStrokeWidth(1).build(),

							PEN_MARK_ALPHA = Pen.newPen().setColor(Color.from(255, 255, 0, 64)).setFillPattern(FillPattern.Solid).build(),
							PEN_MARK_BORDER = Pen.newPen().setColor(Color.from(0, 0, 0, 96)).setStrokePattern(StrokePattern.Solid).build(),

							PEN_FLASH_ORACLE = Pen.newPen().setColor(Color.from(0, 0, 255, 128)).setFillPattern(FillPattern.Solid).build();
	
	/**
	 * Retrieves a darker color. 
	 * @param base The base color.
	 * @param percent The darken percent in the range 0.0 (black) .. 1.0 (base). 
	 * @return The darker color.
	 */
	public static Color darken(Color base, double percent){
		return Color.from((int)(base.red() * percent),
						  (int)(base.green() * percent),
						  (int)(base.blue() * percent),
						  base.alpha());
	}
	
	// end by urueda
	
	public static Builder newPen(){ return new Builder(null); }
	public static Builder startFrom(Pen base){ return new Builder(base); }
	public static Pen merge(Pen priorityPen, Pen backupPen){ return new MergedPen(priorityPen, backupPen); }

	public static final class Builder{
		Color color;
		Double strokeWidth, fontSize;
		String font;
		StrokePattern strokePattern;
		FillPattern fillPattern;
		StrokeCaps strokeCaps;
		Pen base;

		private Builder(Pen base){ this.base = base; }

		public Builder setColor(Color c){
			Assert.notNull(c);
			color = c;
			return this;
		}

		public Builder setFont(String font){
			Assert.notNull(font);
			this.font = font;
			return this;
		}

		public Builder setStrokeWidth(double width){
			Assert.isTrue(width >= 0);
			strokeWidth = width;
			return this;
		}

		public Builder setFontSize(double size){
			Assert.isTrue(size >= 0);
			fontSize = size;
			return this;
		}

		public Builder setStrokePattern(StrokePattern pattern){
			Assert.notNull(pattern);
			this.strokePattern = pattern;
			return this;
		}

		public Builder setFillPattern(FillPattern pattern){
			Assert.notNull(pattern);
			this.fillPattern = pattern;
			return this;
		}

		public Builder setStrokeCaps(StrokeCaps caps){
			Assert.notNull(caps);
			this.strokeCaps = caps;
			return this;
		}

		public Pen build(){
			return base == null ? new StdPen(this) : merge(new StdPen(this), base);
		}
	}
	
	private Pen(){}
	public abstract Color color();
	public abstract Double strokeWidth();
	public abstract Double fontSize();
	public abstract String font();
	public abstract StrokePattern strokePattern();
	public abstract FillPattern fillPattern();
	public abstract StrokeCaps strokeCaps();


	private static final class StdPen extends Pen{
		private static final long serialVersionUID = -7928196200263289513L;
		final Color color;
		final Double strokeWidth, fontSize;
		final String font;
		final StrokePattern strokePattern;
		final FillPattern fillPattern;
		final StrokeCaps strokeCaps;

		private StdPen(Builder b){
			color = b.color;
			strokeWidth = b.strokeWidth;
			fontSize = b.fontSize;
			font = b.font;
			strokeCaps = b.strokeCaps;
			fillPattern = b.fillPattern;
			strokePattern = b.strokePattern;
		}

		public Color color(){ return color; }
		public Double strokeWidth(){ return strokeWidth; }
		public Double fontSize(){ return fontSize; }
		public String font(){ return font; }
		public StrokePattern strokePattern(){ return strokePattern; }
		public FillPattern fillPattern(){ return fillPattern; }
		public StrokeCaps strokeCaps(){ return strokeCaps; }
	}
	
	
	private final static class MergedPen extends Pen{
		private static final long serialVersionUID = 6526439728542225584L;
		final Pen pen1, pen2;
		private MergedPen(Pen pen1, Pen pen2){
			Assert.notNull(pen1, pen2);
			this.pen1 = pen1;
			this.pen2 = pen2;
		}
		public Color color() {
			Color c = pen1.color();
			return c == null ? pen2.color() : c;
		}
		public Double strokeWidth() {
			Double sw = pen1.strokeWidth();
			return sw == null ? pen2.strokeWidth() : sw;
		}
		public Double fontSize() {
			Double fs = pen1.fontSize();
			return fs == null ? pen2.fontSize() : fs;
		}
		public String font() {
			String f = pen1.font();
			return f == null ? pen2.font() : f;
		}
		public FillPattern fillPattern() {
			FillPattern fp = pen1.fillPattern();
			return fp == null ? pen2.fillPattern() : fp;
		}
		public StrokePattern strokePattern() {
			StrokePattern sp = pen1.strokePattern();
			return sp == null ? pen2.strokePattern() : sp;
		}
		public StrokeCaps strokeCaps() {
			StrokeCaps sc = pen1.strokeCaps();
			return sc == null ? pen2.strokeCaps() : sc;
		}
	}
}