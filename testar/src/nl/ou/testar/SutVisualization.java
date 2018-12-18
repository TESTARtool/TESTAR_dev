/***************************************************************************************************
 *
 * Copyright (c) 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
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

package nl.ou.testar;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.protocols.ProtocolUtil;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.monkey.AbstractProtocol;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.fruit.alayer.Tags.Role;
import static org.fruit.alayer.Tags.Visualizer;

public class SutVisualization {

    /**
     *
     * @param canvas
     * @param state
     * @param system
     */
    public static synchronized void visualizeState(AbstractProtocol.Modes mode, Settings settings, boolean markParentWidget, Mouse mouse, ProtocolUtil protocolUtil,String lastPrintParentsOf, double delay, Canvas canvas, State state, SUT system) {
        if ((mode == AbstractProtocol.Modes.Spy
                || mode == AbstractProtocol.Modes.GenerateManual
                || mode == AbstractProtocol.Modes.ReplayDebug) && settings.get(ConfigTags.DrawWidgetUnderCursor)) {
            Point cursor = mouse.cursor();
            Widget cursorWidget = Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);

            if (cursorWidget != null) {
                Widget rootW = cursorWidget;
                while (rootW.parent() != null && rootW.parent() != rootW)
                    rootW = rootW.parent();
                Shape cwShape = cursorWidget.get(Tags.Shape, null);

                if (cwShape != null) {
                    cwShape.paint(canvas, Pen.PEN_MARK_ALPHA);
                    cwShape.paint(canvas, Pen.PEN_MARK_BORDER);
                    if (!settings.get(ConfigTags.DrawWidgetInfo) && !settings.get(ConfigTags.DrawWidgetTree) && !markParentWidget) {
                        String rootText = "State: " + rootW.get(Tags.ConcreteID),
                                widConcreteText = CodingManager.CONCRETE_ID + ": " + cursorWidget.get(Tags.ConcreteID),
                                roleText = "Role: " + cursorWidget.get(Role, Roles.Widget).toString(),
                                idxText = "Path: " + cursorWidget.get(Tags.Path);

                        double miniwidgetInfoW = Math.max(Math.max(Math.max(rootText.length(), widConcreteText.length()), roleText.length()),idxText.length()) * 8; if (miniwidgetInfoW < 256) miniwidgetInfoW = 256;
                        double miniwidgetInfoH = 80; // 20 * 4
                        Shape minicwShape = Rect.from(cwShape.x() + cwShape.width()/2 + 32,
                                cwShape.y() + cwShape.height()/2 + 32,
                                miniwidgetInfoW, miniwidgetInfoH);
                        Shape repositionShape = protocolUtil.calculateWidgetInfoShape(canvas,minicwShape, miniwidgetInfoW, miniwidgetInfoH);
                        if (repositionShape != minicwShape) {
                            double x = repositionShape.x() - repositionShape.width() - 32,
                                    y = repositionShape.y() - repositionShape.height() - 32;
                            if (x < 0) x = 0; if (y < 0) y = 0;
                            minicwShape = Rect.from(x,y,repositionShape.width(), repositionShape.height());
                        }
                        canvas.rect(Pen.PEN_WHITE_ALPHA, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                        canvas.rect(Pen.PEN_BLACK, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                        canvas.text(Pen.PEN_RED, minicwShape.x(), minicwShape.y(), 0, rootText);
                        canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 20, 0, idxText);
                        canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 40, 0, roleText);
                        canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 60, 0, widConcreteText);
                    }

                    if (markParentWidget) {
                        String cursorWidgetID = cursorWidget.get(Tags.ConcreteID);
                        boolean print = !cursorWidgetID.equals(lastPrintParentsOf);
                        if (print) {
                            lastPrintParentsOf = cursorWidgetID;
                            System.out.println("Parents of: " + cursorWidget.get(Tags.Title));
                        }
                        int lvls = protocolUtil.markParents(canvas,cursorWidget,protocolUtil.ancestorsMarkingColors.keySet().iterator(),0,print);
                        if (lvls > 0) {
                            Shape legendShape = protocolUtil.repositionShape(canvas,Rect.from(cursor.x(), cursor.y(), 110, lvls*25));
                            canvas.rect(Pen.PEN_WHITE_ALPHA, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
                            canvas.rect(Pen.PEN_BLACK, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
                            int shadow = 2;
                            String l;
                            Iterator<String> it = protocolUtil.ancestorsMarkingColors.keySet().iterator();
                            for (int i=0; i<lvls; i++) {
                                l = it.next();
                                Pen lpen = Pen.newPen().setColor(protocolUtil.ancestorsMarkingColors.get(l)).build();
                                canvas.text(lpen, legendShape.x() - shadow, legendShape.y() - shadow + i*25, 0, l);
                                canvas.text(lpen, legendShape.x() + shadow, legendShape.y() - shadow + i*25, 0, l);
                                canvas.text(lpen, legendShape.x() + shadow, legendShape.y() + shadow + i*25, 0, l);
                                canvas.text(lpen, legendShape.x() - shadow, legendShape.y() + shadow + i*25, 0, l);
                                canvas.text(Pen.PEN_BLACK, legendShape.x()         , legendShape.y() + i*25         , 0, l);
                            }
                        }
                    }
                    int MAX_ANCESTORS_PERLINE = 6;
                    double widgetInfoW = canvas.width()/2; //550;
                    double widgetInfoH = (1 + Util.size(cursorWidget.tags()) +
                            Util.size(Util.ancestors(cursorWidget)) / MAX_ANCESTORS_PERLINE)
                            * 20;
                    cwShape = protocolUtil.calculateWidgetInfoShape(canvas,cwShape, widgetInfoW, widgetInfoH);

                    if (settings.get(ConfigTags.DrawWidgetInfo)) {
                        //canvas.rect(wpen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
                        //canvas.rect(apen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
                        canvas.rect(Pen.PEN_WHITE_ALPHA, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
                        canvas.rect(Pen.PEN_BLACK, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);

                        //canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y(), 0, "Role: " + cursorWidget.get(Role, Roles.Widget).toString());
                        //canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y() - 20, 0, "Path: " + Util.indexString(cursorWidget));
                        int pos = -20;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Ancestors: ");

                        //for (Widget p: Util.ancestors(cursorWidget))
                        //  sb.append("::").append(p.get(Role, Roles.Widget));
                        //canvas.text(apen, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                        // (fix too many ancestors)
                        int i=0;
                        for (Widget p: Util.ancestors(cursorWidget)) {
                            sb.append("::").append(p.get(Role, Roles.Widget));
                            i++;
                            if (i >= MAX_ANCESTORS_PERLINE) {
                                canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                                i=0;
                                sb = new StringBuilder();
                                sb.append("\t");
                            }
                        }
                        if (i > 0)
                            canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());

                        for (Tag<?> t: cursorWidget.tags()) {
                            canvas.text((t.isOneOf(Tags.Role,Tags.Title,Tags.Shape,Tags.Enabled,Tags.Path,Tags.ConcreteID)) ? Pen.PEN_RED: Pen.PEN_BLACK,
                                    cwShape.x(), cwShape.y() + (pos+=20), 0, t.name() + ":   " + Util.abbreviate(Util.toString(cursorWidget.get(t)), 50, "..."));
                            // (multi-line display without abbreviation)
              /*final int MAX_TEXT = 50;
              String text = Util.abbreviate(Util.toString(cursorWidget.get(t)), Integer.MAX_VALUE, "NO_SENSE");
              int fragment = 0, limit;
              while (fragment < text.length()) {
                limit = fragment + MAX_TEXT > text.length() ? text.length(): fragment + MAX_TEXT;
                canvas.text((t.equals(Tags.Title) || t.equals(Tags.Role)) ? rpen: apen, cwShape.x(), cwShape.y() + (pos+=20), 0, t.name() + ":   " +
                  text.substring(fragment,limit));
                fragment = limit;
              }*/
                        }
                    }

                    if (settings.get(ConfigTags.DrawWidgetTree)) {
                        canvas.rect(Pen.PEN_BLACK_ALPHA, 0, 0, canvas.width(), canvas.height());
                        protocolUtil.drawWidgetTree(system,canvas,12,12,rootW,cursorWidget,16);
                    }
                    if (settings.get(ConfigTags.GraphsActivated) && delay != Double.MIN_VALUE) { // slow motion?
                        canvas.rect(Pen.PEN_BLACK_ALPHA, 0, 0, canvas.width(), canvas.height());
                        IEnvironment env = Grapher.getEnvironment();
                        IGraphState gs = env.get(state);
                        String wid = cursorWidget.get(Tags.ConcreteID);
                        String graphDebug = "Widget <" + wid + "> count = " + gs.getStateWidgetsExecCount().get(wid);
                        canvas.text(Pen.PEN_WHITE_TEXT_12px, 10, 10, 0, graphDebug);
                    }
                }
            }
        }
    }

    /**
     * Visualizing available actions with colored dots on a canvas on top of SUT
     *
     * @param canvas
     * @param state
     * @param actions
     */
    public static void visualizeActions(AbstractProtocol.Modes mode,Settings settings,  Canvas canvas, State state, Set<Action> actions) {
        if ((mode == AbstractProtocol.Modes.Spy ||
                mode == AbstractProtocol.Modes.GenerateManual ||
                mode == AbstractProtocol.Modes.GenerateDebug) && settings.get(ConfigTags.VisualizeActions)) {
            IEnvironment env = Grapher.getEnvironment();
            int zindex, minz = Integer.MAX_VALUE, maxz = Integer.MIN_VALUE;
            Map<Action,Integer> zindexes = new HashMap<Action,Integer>();
            for (Action a: actions) {
                //a.get(Visualizer, Util.NullVisualizer).run(state, canvas, Pen.PEN_IGNORE);
                zindex = getTargetZindex(state,a);
                zindexes.put(a, new Integer(zindex));
                if (zindex < minz)
                    minz = zindex;
                if (zindex > maxz)
                    maxz = zindex;
            }
            int alfa;
            for (Action a: actions) {
                zindex = 1; // default
                Pen vp = Pen.PEN_IGNORE;
                if (env != null) { // graphs enabled
                    Integer widgetExeCount = env.get(state).getStateWidgetsExecCount().get(env.get(a).getTargetWidgetID());
                    if (widgetExeCount != null && widgetExeCount.intValue() > 0)
                        vp = Pen.newPen().setColor(Pen.darken(Color.from(0,0,255,255),1.0/(1 + (widgetExeCount.intValue()/10)))).build(); // mark executed widgets with a different color
                    else {
                        zindex = zindexes.get(a).intValue();
                        if (minz == maxz || zindex == maxz)
                            alfa = 255;
                        else if (zindex == minz)
                            alfa = 64;
                        else
                            alfa = 128;
                        vp = Pen.newPen().setColor(Pen.darken(Color.from(0,255,0,alfa),1.0)).build(); // color depends on widgets zindex
                    }
                }
                a.get(Visualizer, Util.NullVisualizer).run(state, canvas, vp);
            }
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
    public static int getTargetZindex(State state, Action a) {
        try{
            String targetID = a.get(Tags.TargetID);
            Widget w;
            if (targetID != null) {
                w = getWidget(state,targetID);
                if (w != null)
                    return (int)w.get(Tags.ZIndex).doubleValue();
            }
        } catch(NoSuchTagException ex) {}
        return 1; // default
    }


    private static Widget getWidget(State state, String concreteID) {
        for (Widget w: state) {
            if (w.get(Tags.ConcreteID).equals(concreteID)) {
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
    public static void visualizeSelectedAction(AbstractProtocol.Modes mode,Settings settings, Canvas canvas, State state, Action action) {
        if (mode == AbstractProtocol.Modes.GenerateDebug || mode == AbstractProtocol.Modes.ReplayDebug) {
            Pen redPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).setStrokeWidth(20).build();
            Visualizer visualizer = action.get(Visualizer, Util.NullVisualizer);
            //final int BLINK_COUNT = 3;
            //final double BLINK_DELAY = 0.5;
            double actionDuration = settings.get(ConfigTags.ActionDuration);
            final int BLINK_COUNT = 3;
            final double BLINK_DELAY = actionDuration / BLINK_COUNT;
            for (int i = 0; i < BLINK_COUNT; i++) {
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, Pen.PEN_IGNORE);
                canvas.end();
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, redPen);
                canvas.end();
            }
        }
    }
}
