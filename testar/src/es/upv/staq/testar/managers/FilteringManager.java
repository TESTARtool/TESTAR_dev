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

package es.upv.staq.testar.managers;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fruit.Util;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.protocols.ProtocolUtil;

/**
 * A management utility for SUT UI actions filtering.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FilteringManager {

	public static final String XML_UI_FILTER_VERSION = "1.0.20170515";
    public static final String PROTOCOL_FILTER_FILE = "protocol_filter.xml";

	private static final String XML_TAG_UI_FILTER_ROOT = "TESTAR_uifilter",
								XML_TAG_UI_FILTERING_TYPES = "filtering_types",
								XML_TAG_UI_FILTERS = "filters",				
								XML_TAG_UI_FILTERING = "filtering",
								XML_TAG_UI_FILTER = "filter",
								XML_ATTRIBUTE_CODING_TYPE = "coding",
								XML_ATTRIBUTE_ROLE = "role",
								XML_ATTRIBUTE_TITLE = "title",
								XML_ATTRIBUTE_PARENT_ROLE = "p.role",
								XML_ATTRIBUTE_PARENT_TITLE = "p.title",
    							XML_ATTRIBUTE_TREE_DEPTH = "depth",
    							XML_ATTRIBUTE_TREE_CHILDREN = "children";
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
    // rendering pens
    private static final int PEN_ALPHA = 128;
    private static final Pen PEN_WHITE = Pen.newPen().setColor(Color.from(128, 255, 128, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							PEN_PRECISE_WHITE = Pen.newPen().setColor(Color.from(0, 255, 0, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							PEN_BLACK = Pen.newPen().setColor(Color.from(255, 128, 128, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							PEN_PRECISE_BLACK = Pen.newPen().setColor(Color.from(255, 0, 0, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							PEN_BLUE = Pen.newPen().setColor(Color.from(128, 128, 255, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
							PEN_PRECISE_BLUE = Pen.newPen().setColor(Color.from(0, 0, 255, PEN_ALPHA)).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();		
	
    private class WidgetInfo{
    	String idType; // CodingManager.*ID  	
    	String role, parentRole;
    	String title, parentTitle;
    	Integer treeDepth, children, filterCode;
    	public WidgetInfo(String idType, String role, String parentRole, String title, String parentTitle,
    			          Integer treeDepth, Integer children, Integer filterCode){
    		this.idType = idType;
    		this.role = role; this.parentRole = parentRole;
    		this.title = new Integer(title.hashCode()).toString(); this.parentTitle = new Integer(parentTitle.hashCode()).toString();
    		this.treeDepth = treeDepth; this.children = children; this.filterCode = filterCode;
    	}
    }
    
    // key = widget ID; value = filter type
    private HashMap<String,WidgetInfo> widgetsFilterList = new HashMap<String,WidgetInfo>();    
    
    public FilteringManager(){
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
		} catch(ParserConfigurationException e){
			e.printStackTrace();
		}    	
    }
    
	public void saveFilters(){
		File f = new File(PROTOCOL_FILTER_FILE);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write("<?xml version=\"1.0\"?>\n");
			writer.write("<" + XML_TAG_UI_FILTER_ROOT + " version=\"" + XML_UI_FILTER_VERSION + "\">\n");
			// coding types
			writer.write("\t<coding_types>\n");
			writer.write("\t\t<coding type=\"" + CodingManager.ABSTRACT_R_ID + "\" desc=\"ROLE property\"/>\n");
			writer.write("\t\t<coding type=\"" + CodingManager.ABSTRACT_R_T_ID + "\" desc=\"ROLE and TITLE properties\"/>\n");
			writer.write("\t\t<coding type=\"" + CodingManager.ABSTRACT_R_T_P_ID + "\" desc=\"ROLE, TITLE and PATH properties\"/>\n");
			writer.write("\t</coding_types>\n");
			// filterING types
			writer.write("\t<" + XML_TAG_UI_FILTERING_TYPES +  ">\n");
			writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.WIDGET_ACTION_TABU_FILTER + "\" desc=\"TABU LIST\"/>\n");
			writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.WIDGET_ACTION_WHITE_FILTER + "\" desc=\"WHITE LIST\"/>\n");
			if (DataManager.DATA_TYPES != null){
				for (String type : DataManager.DATA_TYPES.keySet())
					writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.DATA_TYPES.get(type).toString() + "\" desc=\"" + type + "\"/>\n");
			}
			writer.write("\t</" + XML_TAG_UI_FILTERING_TYPES +  ">\n");
			// filters
			writer.write("\t<" + XML_TAG_UI_FILTERS +  ">\n");
			WidgetInfo wi;
			for (String wid : widgetsFilterList.keySet()){
				wi = widgetsFilterList.get(wid);
				writer.write("\t\t<" + XML_TAG_UI_FILTER + " wid=\"" + wid + "\"");
				writer.write(" " + XML_TAG_UI_FILTERING + "=\"" + wi.filterCode.toString() + "\"");
				writer.write(" " + XML_ATTRIBUTE_CODING_TYPE + "=\"" + wi.idType + "\"");
				writer.write(" " + XML_ATTRIBUTE_ROLE + "=\"" + wi.role + "\"");
				writer.write(" " + XML_ATTRIBUTE_PARENT_ROLE + "=\"" + wi.parentRole + "\"");
				writer.write(" " + XML_ATTRIBUTE_TITLE + "=\"" + wi.title + "\"");
				writer.write(" " + XML_ATTRIBUTE_PARENT_TITLE + "=\"" + wi.parentTitle + "\"");
				writer.write(" " + XML_ATTRIBUTE_TREE_DEPTH + "=\"" + wi.treeDepth + "\"");
				writer.write(" " + XML_ATTRIBUTE_TREE_CHILDREN + "=\"" + wi.children + "\"/>\n");
			}
			writer.write("\t</" + XML_TAG_UI_FILTERS +  ">\n");
			writer.write("</" + XML_TAG_UI_FILTER_ROOT + ">\n");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadFilters(){
		File f = new File(PROTOCOL_FILTER_FILE);
		if (f.exists()){
			BufferedInputStream stream = null;
			try {
				WidgetInfo wi; String depthS, childrenS, filterCodeS;
				stream = new BufferedInputStream(new FileInputStream(f));
				Document doc = docBuilder.parse(stream);					
		        Node node; Element element;
		        NodeList nList = doc.getElementsByTagName(XML_TAG_UI_FILTER);
				widgetsFilterList = new HashMap<String,WidgetInfo>(nList.getLength()); 
		        for (int nidx = 0; nidx < nList.getLength(); nidx++){
		        	node = nList.item(nidx);
		        	if (node.getNodeType() == Node.ELEMENT_NODE){
		        		element = (Element) node;
		        		depthS = element.getAttribute(XML_ATTRIBUTE_TREE_DEPTH);
		        		childrenS = element.getAttribute(XML_ATTRIBUTE_TREE_CHILDREN);
		        		filterCodeS = element.getAttribute(XML_TAG_UI_FILTERING);
		        		if (filterCodeS != null && filterCodeS.length() > 0){
			        		wi = new WidgetInfo(
			        			element.getAttribute(XML_ATTRIBUTE_CODING_TYPE), // id type
			            		element.getAttribute(XML_ATTRIBUTE_ROLE), // role
			            		element.getAttribute(XML_ATTRIBUTE_PARENT_ROLE), // parent role
			            		element.getAttribute(XML_ATTRIBUTE_TITLE), // title 
			            		element.getAttribute(XML_ATTRIBUTE_PARENT_TITLE), // parent title
			            		depthS == null || depthS == "" ? new Integer(-1) : new Integer(depthS), // tree depth
			            		childrenS == null || childrenS == "" ? new Integer(-1) : new Integer(childrenS), // children count
			            		new Integer(filterCodeS)); // filter code
			        		widgetsFilterList.put(element.getAttribute("wid"),wi);
		        		} else
		        			System.out.println("FilteringManager: WRONG FILTER");		        			
		        	}
		        }			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {}
			}
		}
	}
	
    public void filterLists(Widget w, int widgetFilter, String idType){    	
    	String parentRole = "null", parentTitle = "null";
    	if (w.parent() != null){
    		Role r = w.parent().get(Tags.Role, null);
    		if (r != null) parentRole = r.toString();
    		parentTitle = w.parent().get(Tags.Title, "null");
    	}
		WidgetInfo wi = new WidgetInfo(
				idType,
    			w.get(Tags.Role) != null ? w.get(Tags.Role).toString() : "null", // role
    			parentRole, // parent role
    			w.get(Tags.Title,"null"), // title 
    			parentTitle, // parent title
    			new Integer(Util.depth(w)), // tree depth
    			new Integer(w.childCount()), // children count
    			new Integer(widgetFilter)); // filter code
    	String widgetID = null;
    	switch(idType){
    	case CodingManager.ABSTRACT_R_ID:
    		widgetID = w.get(Tags.Abstract_R_ID);
    		break;
    	case CodingManager.ABSTRACT_R_T_ID:
    		widgetID = w.get(Tags.Abstract_R_T_ID);
    		break;
    	case CodingManager.ABSTRACT_R_T_P_ID:
    		widgetID = w.get(Tags.Abstract_R_T_P_ID);
    		break;
    	}
    	WidgetInfo winfo = widgetsFilterList.get(widgetID);
    	switch(widgetFilter){
    	case DataManager.ANY_DATA_TYPE:
    		widgetsFilterList.remove(widgetID);
    		break;
    	case DataManager.WIDGET_ACTION_TABU_FILTER: // cycle: white -> regular -> tabu
        	if (winfo != null && winfo.filterCode.intValue() == DataManager.WIDGET_ACTION_WHITE_FILTER)
        		widgetsFilterList.remove(widgetID);
        	else
        		widgetsFilterList.put(widgetID,wi);
        	break;
    	case DataManager.WIDGET_ACTION_WHITE_FILTER: // cycle: tabu -> regular -> white
        	if (winfo != null && winfo.filterCode.intValue() == DataManager.WIDGET_ACTION_TABU_FILTER)
        		widgetsFilterList.remove(widgetID);
        	else
        		widgetsFilterList.put(widgetID,wi);    	
        	break;
        default:
        	if (winfo != null && winfo.filterCode.intValue() == widgetFilter)
        		widgetsFilterList.remove(widgetID);
        	else
        		widgetsFilterList.put(widgetID,wi);    	
    	}
    }
    
    private Set<Widget> getDragWidgets(State state, double[] filterArea){
    	if (state == null) return null;
    	double areaW = Math.abs(filterArea[0] - filterArea[2]);
    	double areaH = Math.abs(filterArea[1] - filterArea[3]);
    	if (areaW == 0 || areaH == 0 || areaW > 1048576 || areaH > 1048576) return null;
    	Rect r = Rect.from(filterArea[0] < filterArea[2] ? filterArea[0] : filterArea[2],
    					   filterArea[1] < filterArea[3] ? filterArea[1] : filterArea[3],
    					   areaW, areaH);
    	filterArea[0] = filterArea[1] = Double.MAX_VALUE;
    	filterArea[2] = filterArea[3] = Double.MIN_VALUE;
    	return Util.widgetsFromArea(state, r);
    }    
    
    public void manageWhiteTabuLists(State state, Mouse mouse, double[] filterArea, boolean whiteTabuMode, boolean preciseCoding){
    	Set<Widget> widgets = getDragWidgets(state,filterArea);
    	if (widgets == null || widgets.isEmpty()){
    		Widget cursorWidget = ProtocolUtil.getWidgetUnderCursor(state,mouse);
    		if (cursorWidget == null) return;
    		manageWhiteTabuLists(cursorWidget, whiteTabuMode, preciseCoding);
    	} else{
    		for (Widget w : widgets)
        		manageWhiteTabuLists(w,whiteTabuMode,preciseCoding);
    	}
    }
    
    private void manageWhiteTabuLists(Widget w, boolean whiteTabuMode, boolean preciseCoding){
        filterLists(w,whiteTabuMode ? DataManager.WIDGET_ACTION_WHITE_FILTER : DataManager.WIDGET_ACTION_TABU_FILTER,
        		    preciseCoding ? CodingManager.ABSTRACT_R_T_P_ID : CodingManager.ABSTRACT_R_T_ID);
        saveFilters();
    }
    
    public void setWidgetFilter(State state, Mouse mouse, boolean preciseCoding){
    	Widget cursorWidget = ProtocolUtil.getWidgetUnderCursor(state,mouse);
        if (cursorWidget == null || DataManager.DATA_TYPES == null || DataManager.DATA_TYPES.isEmpty()) return;
        Object[] options = DataManager.DATA_TYPES.keySet().toArray();
    	String s = (String) JOptionPane.showInputDialog(new JFrame(),"Input values:","Widget input value type",
    		JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
    	if (DataManager.DATA_TYPES.containsKey(s)){
    		filterLists(cursorWidget,DataManager.DATA_TYPES.get(s),preciseCoding ? CodingManager.ABSTRACT_R_T_P_ID : CodingManager.ABSTRACT_R_T_ID);
    		saveFilters();
    	}
    }
    
    public boolean blackListed(Widget w){
    	WidgetInfo wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_P_ID));
    	if (wi == null)
    		wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_ID));
    	return (wi != null && wi.filterCode.intValue() == DataManager.WIDGET_ACTION_TABU_FILTER);
    }

    public boolean whiteListed(Widget w){
    	WidgetInfo wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_P_ID));
    	if (wi == null)
    		wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_ID));
    	return (wi != null && wi.filterCode.intValue() == DataManager.WIDGET_ACTION_WHITE_FILTER);
    }
    
    public String getRandomText(Widget w){
    	String wid = w.get(Tags.Abstract_R_T_P_ID);;
    	if (!widgetsFilterList.containsKey(wid))
        	wid = w.get(Tags.Abstract_R_T_ID);    		
    	if (widgetsFilterList.containsKey(wid)){
        	int widgetFilter = widgetsFilterList.get(wid).filterCode;
        	if (widgetFilter == DataManager.PRIMITIVE_DATA_TYPE_NUMBER)
        		return DataManager.getRandomPrimitiveDataTypeNumber();
        	else if (widgetFilter == DataManager.PRIMITIVE_DATA_TYPE_TEXT)
        		return DataManager.getRandomPrimitiveDataTypeText();
        	Set<String> dataSamples = DataManager.INPUT_VALUES.get(widgetFilter);
        	if (dataSamples != null)
        		return DataManager.getRandom(dataSamples);
    	}
    	return null;
    }    
 
    public void visualizeActions(Canvas canvas, State state){
		String wid;
		for(Widget w : state){
			wid = w.get(Tags.Abstract_R_T_P_ID);
			if (widgetsFilterList.containsKey(wid))
				visualizeActions(canvas,state,w,wid,true);
			else{
				wid = w.get(Tags.Abstract_R_T_ID);
				if (widgetsFilterList.containsKey(wid))
					visualizeActions(canvas,state,w,wid,false);
			}
		}    	
    }
    
    private void visualizeActions(Canvas canvas, State state, Widget widget, String widgetID, boolean preciseCoding){
		Pen pen = null;
		int widgetFilter = widgetsFilterList.get(widgetID).filterCode;
		switch(widgetFilter){
		case DataManager.WIDGET_ACTION_WHITE_FILTER:
			pen = (preciseCoding ? PEN_PRECISE_WHITE : PEN_WHITE);
			break;
		case DataManager.WIDGET_ACTION_TABU_FILTER:
			pen = (preciseCoding ? PEN_PRECISE_BLACK : PEN_BLACK);
			break;
		default:
			pen = (preciseCoding ? PEN_PRECISE_BLUE : PEN_BLUE);
		}
		Shape shape = widget.get(Tags.Shape);
		if (preciseCoding){
			double width =  shape.width() - 6, height = shape.height() - 6;
			if (width < 1) width = 1;
			if (height < 1) height = 1;
			shape = Rect.from(shape.x() + 3, shape.y() + 3, width, height);
		}
		new ShapeVisualizer(pen, shape, "", 0.5, 0.5).run(state, canvas, Pen.PEN_IGNORE);
    }    
    
}
