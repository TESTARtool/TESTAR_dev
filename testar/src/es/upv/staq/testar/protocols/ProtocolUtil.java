/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar.protocols;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;

import es.upv.staq.testar.serialisation.ScreenshotSerialiser;

/**
 * Utility class to enhance TESTAR protocol.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ProtocolUtil {

	public BufferedReader adhocTestServerReader = null;
	public BufferedWriter adhocTestServerWriter = null;

	private ServerSocket adhocTestServerSocket = null;
	private Socket adhocTestSocket = null;

	public ProtocolUtil(){
	}
	
	// ################
	//  Static methods
	// ################
	
    public static Widget getWidgetUnderCursor(State state, Mouse mouse){
    	if (state == null) return null;
		Point cursor = mouse.cursor();
        Assert.notNull(cursor);
		return Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);
    }	
	
	// ##############
	//	Adhoc Server
	// ##############
	
	public void startAdhocServer() {
		new Thread(){
			public void run(){
				int port = 47357;
				try {
					adhocTestServerSocket = new ServerSocket(port);
					System.out.println("AdhocTest Server started @" + port);
					adhocTestSocket = adhocTestServerSocket.accept();
					System.out.println("AdhocTest Client engaged");
					adhocTestServerReader = new BufferedReader(new InputStreamReader(adhocTestSocket.getInputStream()));
					adhocTestServerWriter = new BufferedWriter(new OutputStreamWriter(adhocTestSocket.getOutputStream()));
				} catch(Exception e){
					stopAdhocServer();
				}
			}
		}.start();
	}
	
	// by urueda
	public void stopAdhocServer(){
		if (adhocTestServerSocket != null){
			try {
				if (adhocTestServerReader != null)
						adhocTestServerReader.close();
				if (adhocTestServerWriter != null)
					adhocTestServerWriter = null;
				if (adhocTestSocket != null)
					adhocTestSocket.close();
				adhocTestServerSocket.close();
				adhocTestServerSocket = null;				
				System.out.println(" AdhocTest Server sttopped  " );		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object[] compileAdhocTestServerEvent(String event){				
		//Pattern p = Pattern.compile(BriefActionRolesMap.LC + "\\((\\d+.\\d+),(\\d+.\\d+)\\)");
		Pattern p = Pattern.compile("LC\\((\\d+.\\d+),(\\d+.\\d+)\\)");
		Matcher m = p.matcher(event);
		if (m.find())
			return new Object[]{MouseButtons.BUTTON1,new Double(m.group(1)),new Double(m.group(2))};
		
		//p = Pattern.compile(BriefActionRolesMap.RC + "\\((\\d+.\\d+),(\\d+.\\d+)\\)");
		p = Pattern.compile("RC\\((\\d+.\\d+),(\\d+.\\d+)\\)");
		m = p.matcher(event);
		if (m.find())
			return new Object[]{MouseButtons.BUTTON3,new Double(m.group(1)),new Double(m.group(2))};

		//p = Pattern.compile(BriefActionRolesMap.T + "\\((.*)\\)");
		p = Pattern.compile("T\\((.*)\\)");
		m = p.matcher(event);
		if (m.find()){
			String text = m.group(1);
			return new Object[]{ KBKeys.contains(text) ? KBKeys.valueOf(text) : text };
		}

		return null;
	}
	
	// ###################
	//	Ancestors marking
	// ###################
	
	public LinkedHashMap<String,Color> ancestorsMarkingColors = new LinkedHashMap<String,Color>(){
		private static final long serialVersionUID = 8186743549563447423L;
	{
		put("1. ===   black",	Color.from(  0,   0,   0, 255));
		put("2. ===   white",	Color.from(255, 255, 255, 255));
		put("3. ===     red",	Color.from(255,   0,   0, 255));
		put("4. ===    blue",	Color.from(  0,   0, 255, 255));
		put("5. ===  yellow",	Color.from(255, 255,   0, 255));
		put("6. ===    cyan",	Color.from(  0, 255, 255, 255));
		put("7. === magenta",	Color.from(255,   0, 255, 255));
		put("8. ===   green",	Color.from(  0, 128,   0, 255));
		put("9. === bluesky",	Color.from(  0, 128, 255, 255));
		put("a. ===  purple",	Color.from(128,   0, 128, 255));
		put("b. ===  orange",	Color.from(255, 128,   0, 255));
		put("c. ===   brown",	Color.from(139,  69,  19, 255));
		put("d. ===  silver",	Color.from(192, 192, 192, 255));
		put("e. ===    navy",	Color.from(  0,   0, 128, 255));
		put("f. ===    gray",	Color.from(128, 128, 128, 255));
	}};
	
	public int markParents(Canvas canvas ,Widget w, Iterator<String> it, int lvl, boolean print){
		Widget parent;
		if (!it.hasNext() || // marking colors exhausted
				(parent = w.parent()) == null)
			return lvl;		
		int margin = 4;
		String colorS = it.next();
		Pen mark = Pen.newPen().setColor(ancestorsMarkingColors.get(colorS))
				.setFillPattern(FillPattern.Stroke).build();						
		Shape shape = parent.get(Tags.Shape, null);
		try{
			shape = Rect.from(shape.x()+lvl*margin, shape.y()+lvl*margin,
				          	  shape.width()-lvl*margin*2, shape.height()-lvl*margin*2);
		}catch(java.lang.IllegalArgumentException e){};
		shape.paint(canvas, mark);
		
		if (print)
			System.out.println("Ancestor(" + colorS + "):\n" + w.getRepresentation("\t"));
		
		return markParents(canvas,parent,it,lvl+1,print);
	}	
	
	// ######################
	//	Widget tree renderer
	// ######################
	
	private void colorShadowText(Canvas canvas, Pen pen, Pen shadowPen, double x, double y, String text){
		canvas.text(shadowPen,x+1,y+1,0,text);
		canvas.text(pen,x,y,0,text);
	}
	
	private String briefRepresentation(Widget widget){
		String briefS = "";
		Role role = widget.get(Tags.Role, null);
		if (role != null)
			briefS += "ROLE: " + role.toString();
		String title = widget.get(Tags.Title, null);
		if (title != null)
			briefS += ", TITLE: " + title;
		return briefS;
	}
	
	public int[] drawWidgetTree(SUT system, Canvas canvas, int x, int y, Widget wtWidget, Widget cursorWidget, int printedIDs){
		final int WIDGET_TREE_NODE_WIDTH = 1, WIDGET_TREE_NODE_HEIGHT = 16;
		final int FONT_SIZE = 12; //(int)Pen.PEN_WHITE_TEXT_12px.fontSize().doubleValue(); 
		int nw = WIDGET_TREE_NODE_WIDTH, nh = WIDGET_TREE_NODE_HEIGHT;
		boolean isRoot = (wtWidget.parent() == null),
				isCursor = wtWidget == cursorWidget,
				isAncestor = Util.isAncestorOf(wtWidget, cursorWidget);
		if (isCursor || isAncestor){
			nw = WIDGET_TREE_NODE_HEIGHT / 2;
			printedIDs++;
			int printY = y + nh + 1 + printedIDs * FONT_SIZE;
			canvas.rect((isCursor ? Pen.PEN_BLUE_FILL : (isRoot ? Pen.PEN_RED_FILL : Pen.PEN_GREEN_FILL)), x, y, nw, nh+1);
			if (isRoot)
				colorShadowText(canvas, Pen.PEN_RED_TEXT_12px, Pen.PEN_WHITE_TEXT_12px, x + nh, y, system.getStatus());
			else {
				canvas.line((isCursor ? Pen.PEN_BLUE_1px_ALPHA : Pen.PEN_GREEN_1px_ALPHA), x, y + nh + 1, x, printY);
				canvas.rect((isCursor ? Pen.PEN_BLUE_FILL : Pen.PEN_GREEN_FILL),
							(isCursor ? FONT_SIZE : x - nw/2),
							printY, nw, FONT_SIZE);
				if (isCursor)
					canvas.line(Pen.PEN_BLUE_1px, FONT_SIZE, printY, x, printY);
				colorShadowText(canvas,(isCursor ? Pen.PEN_BLUE_TEXT_12px : Pen.PEN_GREEN_TEXT_12px), Pen.PEN_WHITE_TEXT_12px,
								(isCursor ? FONT_SIZE + nw : x + nw), printY,
								(isCursor ? wtWidget.getRepresentation("") :
									wtWidget.get(Tags.ConcreteID) +
				    		    			" (" + briefRepresentation(wtWidget) + ")"));
			}
		} else
			canvas.line(Pen.PEN_WHITE_1px, x, y, x, y + nh);
		Widget child;
		int[] currentX = null;
		int returnX = x;
		x += nw + 1;
		y += nh;
		Pen treeLinePen = Pen.PEN_GREY_1px;
		if (isCursor)
			treeLinePen = Pen.PEN_BLUE_1px;
		else if (isAncestor)
			treeLinePen = Pen.PEN_GREEN_1px;
		boolean childOverflow = wtWidget.childCount() > 8,
				nothingRendered = true;
		int bf = 0, af = -1;
		for (int i=0; i<wtWidget.childCount(); i++){
			child = wtWidget.child(i);
			if (!childOverflow || Util.isAncestorOf(child, cursorWidget) || child == cursorWidget ||
				(nothingRendered && i == wtWidget.childCount() - 1)){
				af = 0;
				currentX = drawWidgetTree(system,canvas,x,y,child,cursorWidget,printedIDs);
				canvas.line(treeLinePen, returnX + nw, y, currentX[1], y);
				x = currentX[0];
				nothingRendered = false;
			} else{
				if (af == -1) bf++; else af++;
			}
		}
		if (bf > 0 || af > 0){
			canvas.text(Pen.PEN_WHITE_TEXT_6px, currentX[1] + 1, y + 3, 0, "" + bf);
			canvas.text(Pen.PEN_WHITE_TEXT_6px, currentX[1] + 1, y + 9, 0, "" + af);
		}
		int maxf = Math.max(bf, af);
		return new int[]{x + (childOverflow ? (int)(6*Math.log10(maxf)) : 0), returnX + nw - 1};
	}	
	
	// ###############################
	//	Rendering offset calculations
	// ###############################
	
	private double[] calculateOffset(Canvas canvas, Shape shape){
		return new double[]{
			canvas.x() + canvas.width() - (shape.x() + shape.width()),
			canvas.y() + canvas.height() - (shape.y() + shape.height())
		};
	}
	
	private Shape calculateInnerShape(Shape shape, double[] offset){
		if (offset[0] > 0 && offset[1] > 0)
			return shape;
		else{
			double offsetX = offset[0] > 0 ? 0 : offset[0];
			double offsetY = offset[1] > 0 ? 0 : offset[1];
			return Rect.from(shape.x() + offsetX, shape.y() + offsetY,
					 		 shape.width(), shape.height());
		}
	}
	
	public Shape repositionShape(Canvas canvas, Shape shape){
		double[] offset = calculateOffset(canvas,shape); // x,y
		return calculateInnerShape(shape,offset);		
	}
	
	// fix WidgetInfo panel outside screen in some cases
	public Shape calculateWidgetInfoShape(Canvas canvas, Shape cwShape, double widgetInfoW, double widgetInfoH){
		Shape s = Rect.from(cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
		Shape rs = repositionShape(canvas,s);
		if (s == rs)
			return cwShape;
		else
			return rs;
	}	
	
	// #####################
	//  Screenshots helpers
	// #####################
	
	public String getStateshot(State state){
		Shape viewPort = null;
		if (state.childCount() > 0){
			viewPort = state.child(0).get(Tags.Shape, null);
			if (viewPort != null && (viewPort.width() * viewPort.height() < 1))
				viewPort = null;
		}
		if (viewPort == null)
			viewPort = state.get(Tags.Shape, null); // get the SUT process canvas (usually, full monitor screen)
		if (viewPort.width() <= 0 || viewPort.height() <= 0)
			return null;
		AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height()), AWTCanvas.StorageFormat.PNG, 1);
		return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID), scrshot);
	}
	
	public String getActionshot(State state, Action action){
		List<Finder> targets = action.get(Tags.Targets, null);
		if (targets != null){
			Widget w;
			Shape s;
			Rectangle r;
			Rectangle actionArea = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
			for (Finder f : targets){
				w = f.apply(state);
				s = w.get(Tags.Shape);
				r = new Rectangle((int)s.x(), (int)s.y(), (int)s.width(), (int)s.height());
				actionArea = actionArea.union(r);
			}
			if (actionArea.isEmpty())
				return null;
			AWTCanvas scrshot = AWTCanvas.fromScreenshot(Rect.from(actionArea.x, actionArea.y, actionArea.width, actionArea.height),
														 AWTCanvas.StorageFormat.PNG, 1);
			return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID), action.get(Tags.ConcreteID), scrshot);
		}
		return null;
	}	
	
}
