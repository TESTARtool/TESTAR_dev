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

	public static final int DEFAULT_FONT_SIZE = 16; 
	
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
	public static Color darken(Color base, double percent) {
		return Color.from((int)(base.red() * percent),
						  (int)(base.green() * percent),
						  (int)(base.blue() * percent),
						  base.alpha());
	}
	
	// end by urueda
	
	public static Builder newPen() { 
		return new Builder(null); 
	}
	
	public static Builder startFrom(Pen base) { 
		return new Builder(base); 
	}
	
	public static Pen merge(Pen priorityPen, Pen backupPen) { 
		return new MergedPen(priorityPen, backupPen); 
	}

	public static final class Builder {
		private Color color;
		private Double strokeWidth, fontSize;
		private String font;
		private StrokePattern strokePattern;
		private FillPattern fillPattern;
		private StrokeCaps strokeCaps;
		private Pen base;

		private Builder(Pen base) { 
			this.base = base; 
		}

		public Builder setColor(Color c) {
			Assert.notNull(c);
			color = c;
			return this;
		}

		public Builder setFont(String font) {
			Assert.notNull(font);
			this.font = font;
			return this;
		}

		public Builder setStrokeWidth(double width) {
			Assert.isTrue(width >= 0);
			strokeWidth = width;
			return this;
		}

		public Builder setFontSize(double size) {
			Assert.isTrue(size >= 0);
			fontSize = size;
			return this;
		}

		public Builder setStrokePattern(StrokePattern pattern) {
			Assert.notNull(pattern);
			this.strokePattern = pattern;
			return this;
		}

		public Builder setFillPattern(FillPattern pattern) {
			Assert.notNull(pattern);
			this.fillPattern = pattern;
			return this;
		}

		public Builder setStrokeCaps(StrokeCaps caps) {
			Assert.notNull(caps);
			this.strokeCaps = caps;
			return this;
		}

		public Pen build() {
			return base == null ? new StdPen(this) : merge(new StdPen(this), base);
		}
	}
	
	private Pen() {}
	public abstract Color color();
	public abstract Double strokeWidth();
	public abstract Double fontSize();
	public abstract String font();
	public abstract StrokePattern strokePattern();
	public abstract FillPattern fillPattern();
	public abstract StrokeCaps strokeCaps();


	private static final class StdPen extends Pen {
		private static final long serialVersionUID = -7928196200263289513L;
		private final Color color;
		private final Double strokeWidth, fontSize;
		private final String font;
		private final StrokePattern strokePattern;
		private final FillPattern fillPattern;
		private final StrokeCaps strokeCaps;

		private StdPen(Builder b) {
			color = b.color;
			strokeWidth = b.strokeWidth;
			fontSize = b.fontSize;
			font = b.font;
			strokeCaps = b.strokeCaps;
			fillPattern = b.fillPattern;
			strokePattern = b.strokePattern;
		}

		public Color color() { 
			return color; 
		}
		
		public Double strokeWidth() { 
			return strokeWidth; 
		}
		
		public Double fontSize() { 
			return fontSize; 
		}
		
		public String font() { 
			return font; 
		}
		
		public StrokePattern strokePattern() { 
			return strokePattern; 
		}
		
		public FillPattern fillPattern() { 
			return fillPattern; 
		}
		
		public StrokeCaps strokeCaps() { 
			return strokeCaps; 
		}
	}
	
	
	private static final class MergedPen extends Pen {
		private static final long serialVersionUID = 6526439728542225584L;
		private final Pen pen1, pen2;
		private MergedPen(Pen pen1, Pen pen2) {
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
