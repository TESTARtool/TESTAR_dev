/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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

package org.testar;

import org.testar.settings.dialog.tagsvisualization.TagFilter;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.devices.Mouse;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import static org.testar.monkey.alayer.Tags.Role;
import static org.testar.monkey.alayer.Tags.Visualizer;

public class SutVisualization {

    /**
     * Visualizing filtered actions with grey colored dots
     *
     * @param canvas
     * @param state
     * @param actions
     */
    public static void visualizeFilteredActions(Canvas canvas, State state, Set<Action> actions){
        Pen greyPen = Pen.newPen().setColor(Color.from(128, 128, 128, 96)).setFillPattern(FillPattern.Solid).setStrokeWidth(20).build();
        try {
            for(Action a : actions){
                a.get(Visualizer, Util.NullVisualizer).run(state, canvas, greyPen);
            }
        } catch(IllegalStateException ise) {
            System.out.println("visualizeFilteredActions : canvas visualization not available!");
            if(ise.getMessage()!=null) { System.out.println(ise.getMessage()); }
        }
    }

    /**
     *
     * @param showExtendedWidgetInfo
     * @param markParentWidget
     * @param mouse
     * @param lastPrintParentsOf
     * @param canvas
     * @param state
     */
    public static synchronized void visualizeState(boolean showExtendedWidgetInfo, boolean markParentWidget, Mouse mouse, String lastPrintParentsOf, Canvas canvas, State state){
        Point cursor = mouse.cursor();
        Widget cursorWidget = Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);

        if(cursorWidget != null){
            Widget rootW = cursorWidget;
            while (rootW.parent() != null && rootW.parent() != rootW) {
                rootW = rootW.parent();
            }
            Shape cwShape = cursorWidget.get(Tags.Shape, null);

            if(cwShape != null){
                cwShape.paint(canvas, Pen.PEN_MARK_ALPHA);
                cwShape.paint(canvas, Pen.PEN_MARK_BORDER);
                if (!showExtendedWidgetInfo){
                	// Widget properties we are going to show in Spy mode when the information is not extended
                	String rootText = "StateID: " + rootW.get(Tags.AbstractID, "");
                	String widgetText = "WidgetID: " + cursorWidget.get(Tags.AbstractID, "");
                	String titleText = "Title: " + cursorWidget.get(Tags.Title, "");
                	String roleText = "Role: " + cursorWidget.get(Role, Roles.Widget).toString();
                	String enabledText = "Enabled: " + cursorWidget.get(Tags.Enabled, false);
                	String shapeText = "Shape: " + cursorWidget.get(Tags.Shape, Rect.from(0, 0, 0, 0));
                	String pathText = "Path: " + cursorWidget.get(Tags.Path, "");

                	// Calculate the maximum length of the widget properties strings
                	String[] textArray = {rootText, widgetText, titleText, roleText, enabledText, shapeText, pathText};
                	int maxLength = 0;
                	for (String text : textArray) {
                		maxLength = Math.max(maxLength, text.length());
                	}
                	double miniwidgetInfoW = maxLength * 8.0;
                	if (miniwidgetInfoW < 256) miniwidgetInfoW = 256;
                	double miniwidgetInfoH = 20 * textArray.length; // Each property uses a height of 20

                	// Create the Shape of the visual rectangles we draw in Spy mode
                	Shape minicwShape = Rect.from(cwShape.x() + cwShape.width()/2 + 32,
                			cwShape.y() + cwShape.height()/2 + 32,
                			miniwidgetInfoW, miniwidgetInfoH);
                	Shape repositionShape = ProtocolUtil.calculateWidgetInfoShape(canvas,minicwShape, miniwidgetInfoW, miniwidgetInfoH);
                	if (repositionShape != minicwShape){
                		double x = repositionShape.x() - repositionShape.width() - 32,
                				y = repositionShape.y() - repositionShape.height() - 32;
                		if (x < 0) x = 0; if (y < 0) y = 0;
                		minicwShape = Rect.from(x,y,repositionShape.width(), repositionShape.height());
                	}

                	// Draw the rectangle and widget properties in the screen
                	canvas.rect(Pen.PEN_WHITE_ALPHA, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                	canvas.rect(Pen.PEN_BLACK, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                	canvas.text(Pen.PEN_RED, minicwShape.x(), minicwShape.y(), 0, rootText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 20, 0, widgetText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 40, 0, titleText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 60, 0, roleText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 80, 0, enabledText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 100, 0, shapeText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 120, 0, pathText);
                }

                // TODO: Check if this is useful. If not, just remove it.
                // SHIFT + ALT --> Toggle widget-tree hierarchy display
                if (markParentWidget){
                    String cursorWidgetID = cursorWidget.get(Tags.ConcreteID);
                    boolean print = !cursorWidgetID.equals(lastPrintParentsOf);
                    if (print){
                        lastPrintParentsOf = cursorWidgetID;
                        System.out.println("Parents of: " + cursorWidget.get(Tags.Title));
                    }
                    int lvls = ProtocolUtil.markParents(canvas,cursorWidget,ProtocolUtil.ancestorsMarkingColors.keySet().iterator(),0,print);
                    if (lvls > 0){
                        Shape legendShape = ProtocolUtil.repositionShape(canvas,Rect.from(cursor.x(), cursor.y(), 110, lvls*25));
                        canvas.rect(Pen.PEN_WHITE_ALPHA, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
                        canvas.rect(Pen.PEN_BLACK, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
                        int shadow = 2;
                        String l;
                        Iterator<String> it = ProtocolUtil.ancestorsMarkingColors.keySet().iterator();
                        for (int i=0; i<lvls; i++){
                            l = it.next();
                            Pen lpen = Pen.newPen().setColor(ProtocolUtil.ancestorsMarkingColors.get(l)).build();
                            canvas.text(lpen, legendShape.x() - shadow, legendShape.y() - shadow + i*25, 0, l);
                            canvas.text(lpen, legendShape.x() + shadow, legendShape.y() - shadow + i*25, 0, l);
                            canvas.text(lpen, legendShape.x() + shadow, legendShape.y() + shadow + i*25, 0, l);
                            canvas.text(lpen, legendShape.x() - shadow, legendShape.y() + shadow + i*25, 0, l);
                            canvas.text(Pen.PEN_BLACK, legendShape.x()         , legendShape.y() + i*25         , 0, l);
                        }
                    }
                }

                int MAX_ANCESTORS_PERLINE = 6;
                double widgetInfoW = canvas.width() / 2;
                double widgetInfoH = (1 + calculateNumberOfTagsToShow(cursorWidget) + Util.size(Util.ancestors(cursorWidget)) / MAX_ANCESTORS_PERLINE) * 20;
                cwShape = ProtocolUtil.calculateWidgetInfoShape(canvas, cwShape, widgetInfoW, widgetInfoH);

                if(showExtendedWidgetInfo){
                    //canvas.rect(wpen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
                    //canvas.rect(apen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
                    canvas.rect(Pen.PEN_WHITE_ALPHA, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
                    canvas.rect(Pen.PEN_BLACK, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);

                    //canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y(), 0, "Role: " + cursorWidget.get(Role, Roles.Widget).toString());
                    //canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y() - 20, 0, "Path: " + Util.indexString(cursorWidget));
                    int pos = -20;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ancestors: ");

                    //for(Widget p : Util.ancestors(cursorWidget))
                    //	sb.append("::").append(p.get(Role, Roles.Widget));
                    //canvas.text(apen, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                    // (fix too many ancestors)
                    int i=0;
                    for(Widget p : Util.ancestors(cursorWidget)){
                        sb.append("::").append(p.get(Role, Roles.Widget));
                        i++;
                        if (i >= MAX_ANCESTORS_PERLINE){
                            canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                            i=0;
                            sb = new StringBuilder();
                            sb.append("\t");
                        }
                    }
                    if (i > 0) {
                        canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                    }

                    for (Tag<?> t : cursorWidget.tags()) {
                        // Only add show the tags which have been set in the filter.
                        if (TagFilter.getInstance().visualizeTag(t)) {
                            canvas.text((t.isOneOf(Tags.Role, Tags.Title, Tags.Shape, Tags.Enabled, Tags.Path,
                                    Tags.ConcreteID)) ? Pen.PEN_RED : Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos += 20),
                                    0, t.name() + ":   " + Util.abbreviate(Util.toString(cursorWidget.get(t)), 50, "..."));
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate how many tags we need to show .
     * @param cursorWidget The widget pointed by the cursor.
     * @return The number of tags we need to show.
     */
    public static int calculateNumberOfTagsToShow(Widget cursorWidget) {
        int tagsToShow = 0;
        for (Tag<?> t : cursorWidget.tags()) {
            // Only add show the tags which have been set in the filter.
            if (TagFilter.getInstance().visualizeTag(t)) {
                tagsToShow++;
            }
        }
        return tagsToShow;
    }

    /**
     * Visualizing available actions with colored dots on a canvas on top of SUT
     *
     * @param canvas
     * @param state
     * @param actions
     */
    public static void visualizeActions(Canvas canvas, State state, Set<Action> actions){
        int zindex, minz = Integer.MAX_VALUE, maxz = Integer.MIN_VALUE;
        Map<Action,Integer> zindexes = new HashMap<Action,Integer>();
        for(Action a : actions){
            //a.get(Visualizer, Util.NullVisualizer).run(state, canvas, Pen.PEN_IGNORE);
            zindex = getTargetZindex(state,a);
            zindexes.put(a, Integer.valueOf(zindex));
            if (zindex < minz)
                minz = zindex;
            if (zindex > maxz)
                maxz = zindex;
        }

        try {
            for(Action a : actions){
                zindex = 1; // default
                Pen vp = Pen.PEN_IGNORE;
                a.get(Visualizer, Util.NullVisualizer).run(state, canvas, vp);
            }
        } catch(IllegalStateException ise) {
            System.out.println("visualizeActions : canvas visualization not available!");
            if(ise.getMessage()!=null) { System.out.println(ise.getMessage()); }
        }
    }

    /**
     * Getting the Z index of a widget targeted by the given action
     *
     * used only by visualizeActions()
     *
     * @param state
     * @param a
     * @return
     */
    public static int getTargetZindex(State state, Action a){
        try{
            String targetID = a.get(Tags.TargetID);
            Widget w;
            if (targetID != null){
                w = getWidget(state,targetID);
                if (w != null)
                    return (int)w.get(Tags.ZIndex).doubleValue();
            }
        } catch(NoSuchTagException ex){}
        return 1; // default
    }

    private static Widget getWidget(State state, String concreteID){
        for (Widget w : state){
            if (w.get(Tags.ConcreteID).equals(concreteID)){
                return w;
            }
        }
        return null;
    }


    /**
     * Visualizing the selected action with red colored dot
     *
     * @param canvas
     * @param state
     * @param action
     */
    public static void visualizeSelectedAction(Settings settings, Canvas canvas, State state, Action action){
        Pen redPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).setStrokeWidth(20).build();
        try {
            org.testar.monkey.alayer.Visualizer visualizer = action.get(Visualizer, Util.NullVisualizer);
            double actionDuration = settings.get(ConfigTags.ActionDuration);
            final int BLINK_COUNT = 3;
            final double BLINK_DELAY = actionDuration / BLINK_COUNT;
            for(int i = 0; i < BLINK_COUNT; i++){
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, Pen.PEN_IGNORE);
                canvas.end();
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, redPen);
                canvas.end();
            }
        } catch(IllegalStateException ise) {
            System.out.println("visualizeSelectedAction : canvas visualization not available!");
            if(ise.getMessage()!=null) { System.out.println(ise.getMessage()); }
        }
    }
}
