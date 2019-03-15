/***************************************************************************************************
 *
 * Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

package es.upv.staq.testar.managers;

import es.upv.staq.testar.CodingManager;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.monkey.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * A management utility for SUT UI actions filtering.
 *
 * @author Urko Rueda Molina (alias: urueda)
 */
public class FilteringManager {

  public static final String XML_UI_FILTER_VERSION = "1.0.20170515";
  //By default it uses the root path (bin), but it's modified to the selected settings
  public static String protocolFilterFile = "protocol_filter.xml";

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

  private class WidgetInfo {
    private String idType; // CodingManager.*ID
    private String role, parentRole;
    private String title, parentTitle;
    private Integer treeDepth, children, filterCode;

    WidgetInfo (String idType, String role, String parentRole, String title, String parentTitle,
                       Integer treeDepth, Integer children, Integer filterCode) {
      this.idType = idType;
      this.role = role;
      this.parentRole = parentRole;
      this.title = new Integer(title.hashCode()).toString();
      this.parentTitle = new Integer(parentTitle.hashCode()).toString();
      this.treeDepth = treeDepth;
      this.children = children;
      this.filterCode = filterCode;
    }
  }

  // key = widget ID; value = filter type
  private HashMap<String, WidgetInfo> widgetsFilterList = new HashMap<String, WidgetInfo>();

  public FilteringManager () {
    try {
      protocolFilterFile = Settings.getSettingsPath() + "/protocol_filter.xml";
      docFactory = DocumentBuilderFactory.newInstance();
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  public void saveFilters () {
    File f = new File(protocolFilterFile);
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
      writer.write("\t<" + XML_TAG_UI_FILTERING_TYPES + ">\n");
      writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.WIDGET_ACTION_TABU_FILTER + "\" desc=\"TABU LIST\"/>\n");
      writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.WIDGET_ACTION_WHITE_FILTER + "\" desc=\"WHITE LIST\"/>\n");
      if (DataManager.DATA_TYPES != null) {
        for (String type: DataManager.DATA_TYPES.keySet()) {
          writer.write("\t\t<" + XML_TAG_UI_FILTERING + " type=\"" + DataManager.DATA_TYPES.get(type).toString() + "\" desc=\"" + type + "\"/>\n");
        }
      }
      writer.write("\t</" + XML_TAG_UI_FILTERING_TYPES + ">\n");
      // filters
      writer.write("\t<" + XML_TAG_UI_FILTERS + ">\n");
      WidgetInfo wi;
      for (String wid: widgetsFilterList.keySet()) {
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
      writer.write("\t</" + XML_TAG_UI_FILTERS + ">\n");
      writer.write("</" + XML_TAG_UI_FILTER_ROOT + ">\n");
      writer.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadFilters () {
    File f = new File(protocolFilterFile);
    if (f.exists()) {
      BufferedInputStream stream = null;
      try {
        WidgetInfo wi;
        String depthS;
        String childrenS;
        String filterCodeS;
        stream = new BufferedInputStream(new FileInputStream(f));
        Document doc = docBuilder.parse(stream);
        Node node;
        Element element;
        NodeList nList = doc.getElementsByTagName(XML_TAG_UI_FILTER);
        widgetsFilterList = new HashMap<String, WidgetInfo>(nList.getLength());
        for (int nidx = 0; nidx < nList.getLength(); nidx++) {
          node = nList.item(nidx);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            element = (Element) node;
            depthS = element.getAttribute(XML_ATTRIBUTE_TREE_DEPTH);
            childrenS = element.getAttribute(XML_ATTRIBUTE_TREE_CHILDREN);
            filterCodeS = element.getAttribute(XML_TAG_UI_FILTERING);
            Integer treeDepth;
            Integer childrenCount;
            if (filterCodeS != null && filterCodeS.length() > 0) {
              if (depthS == null || "".equals(depthS)) {
               treeDepth = new Integer(-1) ;
              } else {
                treeDepth = new Integer(depthS);
              }
              if (childrenS == null || "".equals(childrenS)) {
                childrenCount = new Integer(-1);
              } else {
                childrenCount = new Integer(childrenS);
              }

              wi = new WidgetInfo(
                  element.getAttribute(XML_ATTRIBUTE_CODING_TYPE), // id type
                  element.getAttribute(XML_ATTRIBUTE_ROLE), // role
                  element.getAttribute(XML_ATTRIBUTE_PARENT_ROLE), // parent role
                  element.getAttribute(XML_ATTRIBUTE_TITLE), // title
                  element.getAttribute(XML_ATTRIBUTE_PARENT_TITLE), // parent title
                  treeDepth, childrenCount,
                  new Integer(filterCodeS)); // filter code
              widgetsFilterList.put(element.getAttribute("wid"), wi);
            }
            else {
              System.out.println("FilteringManager: WRONG FILTER");
            }
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
        } catch (IOException e) {
        }
      }
    }
  }

  public void filterLists (Widget w, int widgetFilter, String idType) {
    String parentRole = "null", parentTitle = "null";
    if (w.parent() != null) {
      Role r = w.parent().get(Tags.Role, null);
      if (r != null) {
        parentRole = r.toString();
      }
      parentTitle = w.parent().get(Tags.Title, "null");
    }
    String roleStr;
    if (w.get(Tags.Role) != null) {
      roleStr = w.get(Tags.Role).toString();
    } else {
      roleStr = "null";
    }
    WidgetInfo wi = new WidgetInfo(
        idType,
        roleStr, // role
        parentRole, // parent role
        w.get(Tags.Title, "null"), // title
        parentTitle, // parent title
        new Integer(Util.depth(w)), // tree depth
        new Integer(w.childCount()), // children count
        new Integer(widgetFilter)); // filter code
    String widgetID = null;
    switch (idType) {
      case CodingManager.ABSTRACT_R_ID:
        widgetID = w.get(Tags.Abstract_R_ID);
        break;
      case CodingManager.ABSTRACT_R_T_ID:
        widgetID = w.get(Tags.Abstract_R_T_ID);
        break;
      case CodingManager.ABSTRACT_R_T_P_ID:
        widgetID = w.get(Tags.Abstract_R_T_P_ID);
        break;
      default:
    }
    WidgetInfo winfo = widgetsFilterList.get(widgetID);
    switch (widgetFilter) {
      case DataManager.ANY_DATA_TYPE:
        widgetsFilterList.remove(widgetID);
        break;
      case DataManager.WIDGET_ACTION_TABU_FILTER: // cycle: white -> regular -> tabu
        if (winfo != null && winfo.filterCode.intValue() == DataManager.WIDGET_ACTION_WHITE_FILTER) {
          widgetsFilterList.remove(widgetID);
        }
        else {
          widgetsFilterList.put(widgetID, wi);
        }
        break;
      case DataManager.WIDGET_ACTION_WHITE_FILTER: // cycle: tabu -> regular -> white
        if (winfo != null && winfo.filterCode.intValue() == DataManager.WIDGET_ACTION_TABU_FILTER) {
          widgetsFilterList.remove(widgetID);
        }
        else {
          widgetsFilterList.put(widgetID, wi);
        }
        break;
      default:
        if (winfo != null && winfo.filterCode.intValue() == widgetFilter) {
          widgetsFilterList.remove(widgetID);
        }
        else {
          widgetsFilterList.put(widgetID, wi);
        }
    }
  }

  private Set<Widget> getDragWidgets (State state, double[] filterArea) {
    if (state == null) {
      return null;
    }
    double areaW = Math.abs(filterArea[0] - filterArea[2]);
    double areaH = Math.abs(filterArea[1] - filterArea[3]);
    if (areaW == 0 || areaH == 0 || areaW > 1048576 || areaH > 1048576) {
      return null;
    }
    Double areaX;
    if (filterArea[0] < filterArea[2]) {
      areaX = filterArea[0];
    } else {
      areaX = filterArea[2];
    }
    Double areaY;
    if (filterArea[1] < filterArea[3]) {
      areaY = filterArea[1];
    } else {
      areaY = filterArea[3];
    }
    Rect r = Rect.from(areaX, areaY, areaW, areaH);
    filterArea[0] = Double.MAX_VALUE;
    filterArea[1] = Double.MAX_VALUE;
    filterArea[2] = Double.MIN_VALUE;
    filterArea[3] = Double.MIN_VALUE;
    return Util.widgetsFromArea(state, r);
  }

  public void manageWhiteTabuLists (State state, Mouse mouse, double[] filterArea, boolean whiteTabuMode, boolean preciseCoding) {
    Set<Widget> widgets = getDragWidgets(state, filterArea);
    if (widgets == null || widgets.isEmpty()) {
      Widget cursorWidget = getWidgetUnderCursor(state, mouse);
      if (cursorWidget == null) {
        return;
      }
      manageWhiteTabuLists(cursorWidget, whiteTabuMode, preciseCoding);
    }
    else {
      for (Widget w: widgets) {
        manageWhiteTabuLists(w, whiteTabuMode, preciseCoding);
      }
    }
  }

  private void manageWhiteTabuLists (Widget w, boolean whiteTabuMode, boolean preciseCoding) {
    String abstractIdStr;
    if (preciseCoding) {
      abstractIdStr = CodingManager.ABSTRACT_R_T_P_ID;
    } else {
      abstractIdStr = CodingManager.ABSTRACT_R_T_ID;
    }
    int widgetActionStr;
    if (whiteTabuMode) {
      widgetActionStr = DataManager.WIDGET_ACTION_WHITE_FILTER;
    } else {
      widgetActionStr = DataManager.WIDGET_ACTION_TABU_FILTER;
    }
    filterLists(w,  widgetActionStr, abstractIdStr);
    saveFilters();
  }

  /**
   * Opens a Dialog asking for "Widget input value type" - not used at the moment.
   *
   * @param state the SUT's current state
   * @param mouse the mouse pointer
   * @param preciseCoding identification including PID
   */
  public void setWidgetFilter (State state, Mouse mouse, boolean preciseCoding) {
    Widget cursorWidget = getWidgetUnderCursor(state, mouse);
    if (cursorWidget == null || DataManager.DATA_TYPES == null || DataManager.DATA_TYPES.isEmpty()) {
      return;
    }
    Object[] options = DataManager.DATA_TYPES.keySet().toArray();
    String s = (String) JOptionPane.showInputDialog(new JFrame(), "Input values:", "Widget input value type",
        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    String abstractStr;
    if (preciseCoding) {
      abstractStr = CodingManager.ABSTRACT_R_T_P_ID;
    } else {
      abstractStr = CodingManager.ABSTRACT_R_T_ID;
    }
    if (DataManager.DATA_TYPES.containsKey(s)) {
      filterLists(cursorWidget, DataManager.DATA_TYPES.get(s), abstractStr);
      saveFilters();
    }
  }

  public boolean blackListed (Widget w) {
    WidgetInfo wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_P_ID));
    if (wi == null) {
      wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_ID));
    }
    return (wi != null && wi.filterCode.intValue() == DataManager.WIDGET_ACTION_TABU_FILTER);
  }

  public boolean whiteListed (Widget w) {
    WidgetInfo wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_P_ID));
    if (wi == null) {
      wi = widgetsFilterList.get(w.get(Tags.Abstract_R_T_ID));
    }
    return (wi != null && wi.filterCode.intValue() == DataManager.WIDGET_ACTION_WHITE_FILTER);
  }

  public String getRandomText (Widget w) {
    String wid = w.get(Tags.Abstract_R_T_P_ID);

    if (!widgetsFilterList.containsKey(wid)) {
      wid = w.get(Tags.Abstract_R_T_ID);
    }
    if (widgetsFilterList.containsKey(wid)) {
      int widgetFilter = widgetsFilterList.get(wid).filterCode;
      if (widgetFilter == DataManager.PRIMITIVE_DATA_TYPE_NUMBER) {
        return DataManager.getRandomPrimitiveDataTypeNumber();
      }
      else if (widgetFilter == DataManager.PRIMITIVE_DATA_TYPE_TEXT) {
        return DataManager.getRandomPrimitiveDataTypeText();
      }
      Set<String> dataSamples = DataManager.INPUT_VALUES.get(widgetFilter);
      if (dataSamples != null) {
        return DataManager.getRandom(dataSamples);
      }
    }
    return null;
  }

  public void visualizeActions (Canvas canvas, State state) {
    String wid;
    for (Widget w: state) {
      wid = w.get(Tags.Abstract_R_T_P_ID);
      if (widgetsFilterList.containsKey(wid)) {
        visualizeActions(canvas, state, w, wid, true);
      }
      else {
        wid = w.get(Tags.Abstract_R_T_ID);
        if (widgetsFilterList.containsKey(wid)) {
          visualizeActions(canvas, state, w, wid, false);
        }
      }
    }
  }

  private void visualizeActions (Canvas canvas, State state, Widget widget, String widgetID, boolean preciseCoding) {
    Pen pen = null;
    int widgetFilter = widgetsFilterList.get(widgetID).filterCode;
    switch (widgetFilter) {
      case DataManager.WIDGET_ACTION_WHITE_FILTER:
        if (preciseCoding) {
          pen = PEN_PRECISE_WHITE;
        } else {
          pen = PEN_WHITE;
        }
        break;
      case DataManager.WIDGET_ACTION_TABU_FILTER:
        if (preciseCoding) {
          pen = PEN_PRECISE_BLACK;
        } else {
          pen = PEN_BLACK;
        }
        break;
      default:
        if (preciseCoding) {
          pen = PEN_PRECISE_BLUE;
        } else {
          pen = PEN_BLUE;
        }
    }
    Shape shape = widget.get(Tags.Shape);
    if (preciseCoding) {
      double width = shape.width() - 6, height = shape.height() - 6;
      if (width < 1) {
        width = 1;
      }
      if (height < 1) {
        height = 1;
      }
      shape = Rect.from(shape.x() + 3, shape.y() + 3, width, height);
    }
    new ShapeVisualizer(pen, shape, "", 0.5, 0.5).run(state, canvas, Pen.PEN_IGNORE);
  }

  public static Widget getWidgetUnderCursor (State state, Mouse mouse) {
    if (state == null) {
      return null;
    }
    Point cursor = mouse.cursor();
    Assert.notNull(cursor);
    return Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);
  }
}
