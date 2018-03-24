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
package org.fruit.alayer.windows;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.StrokeCaps;
import org.fruit.alayer.StrokePattern;

public final class GDIScreenCanvas implements Canvas {

	double x, y;
	long width, height;
	boolean running;
	long hwnd, gdiplusToken, hScreen, hDC, hBitmap, pGraphics, hClearBrush;
	int blendOp, blendFlags, blendSourceConstantAlpha, blendAlphaFormat;
	Thread bgt;

	Pen defaultPen;
	double fontSize, strokeWidth;
	String font;
	StrokePattern strokePattern;
	FillPattern fillPattern;
	StrokeCaps strokeCaps;
	Color color;
	long pPen, pBrush, pFont, pFontFamily;

	public static GDIScreenCanvas fromPrimaryMonitor(){
		return fromPrimaryMonitor(Pen.PEN_DEFAULT);
	}

	/**
	 * Creates a ScreenCanvas for the system's primary monitor
	 * @return ScreenCanvas for the primary monitor
	 */
	public static GDIScreenCanvas fromPrimaryMonitor(Pen defaultPen){
		long handle = Windows.GetPrimaryMonitorHandle();
		long mi[] = Windows.GetMonitorInfo(handle);
		long x = mi[1];
		long y = mi[2];
		long width = mi[3] - mi[1];
		long height = mi[4] - mi[2];
		return new GDIScreenCanvas(x, y, width, height, defaultPen);
	}

	private class BackgroundThread implements Runnable{
		public void run() {

			blendOp = 0;
			blendFlags = 0;
			blendSourceConstantAlpha = 255;
			blendAlphaFormat = (int)Windows.AC_SRC_ALPHA;

			// create overlay window
			hwnd = Windows.CreateWindowEx(Windows.WS_EX_LAYERED | Windows.WS_EX_TRANSPARENT  | Windows.WS_EX_TOOLWINDOW | 
					Windows.WS_EX_TOPMOST |	Windows.WS_EX_NOACTIVATE, null,	"OverlayWindow", Windows.WS_OVERLAPPEDWINDOW,
					(int)x, (int)y, width, height, 0,	0, Windows.GetCurrentModule(),	0);		

			gdiplusToken = Windows.GdiplusStartup();
			hScreen = Windows.GetDC(0);			
			hDC = Windows.CreateCompatibleDC(hScreen);			
			hBitmap = Windows.CreateCompatibleBitmap(hScreen, width, height);
			Windows.SelectObject(hDC, hBitmap);
			pGraphics = Windows.Gdiplus_Graphics_FromHDC(hDC);
			hClearBrush = Windows.Gdiplus_SolidBrush_Create(255, 0, 0, 0);
			running = true;

			long[] hMsg;

			while(running){
				while((hMsg = Windows.PeekMessage(hwnd, 0, 0, Windows.PM_NOREMOVE)) != null){
					Windows.GetMessage(hwnd, 0, 0 );
					Windows.TranslateMessage(hMsg);
					Windows.DispatchMessage(hMsg);
				}
				Util.pause(.1);
				Windows.SetWindowPos(hwnd, Windows.HWND_TOPMOST, (int)x, (int)y, width, height, Windows.SWP_SHOWWINDOW);
			}
		}
	}

	public GDIScreenCanvas(long x, long y, long width, long height){
		this(x, y, width, height, Pen.PEN_DEFAULT);
	}
	
	/**
	 * Create a ScreenCanvas with the given dimensions.
	 */
	public GDIScreenCanvas(double x, double y, long width, long height, Pen defaultPen){
		Assert.notNull(defaultPen);
		Assert.isTrue(width >= 0 && height >= 0);

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.defaultPen = defaultPen;

		bgt = new Thread(new BackgroundThread());
		bgt.setDaemon(true);  // we want the VM to shutdown even if our background thread is still running
		bgt.start();

		while(!running)
			Util.pause(0.01);
		
		pPen = Windows.Gdiplus_Pen_Create(0, 0, 0, 0, 1);
		pBrush = Windows.Gdiplus_SolidBrush_Create(0, 0, 0, 0);
		
		adjustPen(defaultPen);
	}

	public void release(){
		if(!running)
			return;
		running = false;
		
		try { bgt.join();}
		catch (InterruptedException e) { e.printStackTrace(); }

		if(pFont != 0)
			Windows.Gdiplus_Font_Destroy(pFont);
		if(pFontFamily != 0)
			Windows.Gdiplus_FontFamily_Destroy(pFontFamily);
		if(pPen != 0)
			Windows.Gdiplus_Pen_Destroy(pPen);
		if(pBrush != 0)
			Windows.Gdiplus_SolidBrush_Destroy(pBrush);
		if(hClearBrush != 0)
			Windows.Gdiplus_SolidBrush_Destroy(hClearBrush);
		if(gdiplusToken != 0)
			Windows.GdiplusShutdown(gdiplusToken);
	}

	public void finalize(){ release(); }

	private void check(){
		if(!running)
			throw new IllegalStateException();
	}

	public double width() { return width; }
	public double height() { return height; }
	public double x() { return x; }
	public double y() { return y; }
	public void begin() { check(); }
	public Pen defaultPen() { return defaultPen; }

	public void end() {
		check();
		Windows.UpdateLayeredWindow(hwnd, hScreen, (int)x, (int)y, width, height, hDC, 0, 0, 0, 
				blendOp, blendFlags, blendSourceConstantAlpha, blendAlphaFormat, 
				Windows.ULW_ALPHA);
	}
	
	public void line(Pen pen, double x1, double y1, double x2, double y2) {
		check();
		adjustPen(pen);
		Windows.Gdiplus_Graphics_DrawLine(pGraphics, pPen, x1 - this.x, y1 - this.y, x2 - this.x, y2 - this.y);
	}

	public void text(Pen pen, double x, double y, double angle, String text) {
		check();
		adjustPen(pen);
		Windows.Gdiplus_Graphics_DrawString(pGraphics, text, pFont, x - this.x, y - this.y, pBrush);
	}

	public void clear(double x, double y, double width, double height) {
		check();
		Windows.Gdiplus_Graphics_FillRectangle(pGraphics, hClearBrush, x - this.x, y - this.y, width, height);
	}

	public void image(Pen pen, double x, double y, double width, double height,
			int[] image, int imageWidth, int imageHeight) {
		check();
		adjustPen(pen);
		Windows.Gdiplus_Graphics_DrawImage(pGraphics, (int)(x - this.x), (int)(y - this.y), (int)width, (int)height, imageWidth, imageHeight, Windows.PixelFormat32bppARGB, image);
	}

	public void ellipse(Pen pen, double x, double y, double width, double height) {
		check();
		adjustPen(pen);
		if(fillPattern == FillPattern.Solid)
			Windows.Gdiplus_Graphics_FillEllipse(pGraphics, pBrush, x - this.x, y - this.y, width, height);		
		else
			Windows.Gdiplus_Graphics_DrawEllipse(pGraphics, pPen, x - this.x, y - this.y, width, height);
	}

	public void rect(Pen pen, double x, double y, double width, double height) {
		check();
		adjustPen(pen);
		if(fillPattern == FillPattern.Solid)
			Windows.Gdiplus_Graphics_FillRectangle(pGraphics, pBrush, x - this.x, y - this.y, width, height);
		else
			Windows.Gdiplus_Graphics_DrawRectangle(pGraphics, pPen, x - this.x, y - this.y, width, height);
	}
	
	public Pair<Double, Double> textMetrics(Pen pen, String text) {
		Assert.notNull(pen, text);
		return Pair.from(text.length() * 2., 20.);
	}
	
	private void adjustPen(Pen pen){
		Double tstrokeWidth = pen.strokeWidth();
		if(tstrokeWidth == null)
			tstrokeWidth = defaultPen.strokeWidth();
		
		StrokePattern tstrokePattern = pen.strokePattern();
		if(tstrokePattern == null)
			tstrokePattern = defaultPen.strokePattern();
		
		StrokeCaps tstrokeCaps = pen.strokeCaps();
		if(tstrokeCaps == null)
			tstrokeCaps = defaultPen.strokeCaps();

		if(!tstrokeWidth.equals(strokeWidth) || tstrokePattern != strokePattern || tstrokeCaps != strokeCaps){
			strokePattern = tstrokePattern;
			strokeWidth = tstrokeWidth;
			strokeCaps = tstrokeCaps;
			Windows.Gdiplus_Pen_SetWidth(pPen, strokeWidth);
		}
		
		Color tcolor = pen.color();
		if(tcolor == null)
			tcolor = defaultPen.color();
				
		if(!tcolor.equals(color)){
			color = tcolor;
			Windows.Gdiplus_Pen_SetColor(pPen, color.alpha(), color.red(), color.green(), color.blue());
			Windows.Gdiplus_SolidBrush_SetColor(pBrush, Math.min(color.alpha(), 254), color.red(), color.green(), color.blue());
		}
		
		String tfont = pen.font();
		if(tfont == null)
			tfont = defaultPen.font();
		
		Double tfontSize = pen.fontSize();
		if(tfontSize == null)
			tfontSize = defaultPen.fontSize();
		
		if(!tfont.equals(font) || !tfontSize.equals(fontSize)){
			font = tfont;
			fontSize = tfontSize;
			if(pFont != 0)
				Windows.Gdiplus_Font_Destroy(pFont);
			if(pFontFamily != 0)
				Windows.Gdiplus_FontFamily_Destroy(pFontFamily);
			
			pFontFamily = Windows.Gdiplus_FontFamily_Create(font);
			pFont = Windows.Gdiplus_Font_Create(pFontFamily, fontSize, Windows.Gdiplus_FontStyleRegular, Windows.Gdiplus_UnitPixel);
		}
		
		FillPattern tfillPattern = pen.fillPattern();
		if(tfillPattern == null)
			tfillPattern = defaultPen.fillPattern();
		
		if(tfillPattern != fillPattern){
			fillPattern = tfillPattern;
		}
	}

	public void triangle(Pen pen, double x1, double y1, double x2, double y2,
			double x3, double y3) {
		throw new UnsupportedOperationException();
	}

}
