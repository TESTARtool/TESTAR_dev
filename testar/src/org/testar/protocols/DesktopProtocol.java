package org.testar.protocols;

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.Drag;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.StdActionCompiler;

import java.util.Set;

public class DesktopProtocol extends ClickFilterLayerProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness

    /**
     * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
     * @param actions
     * @param ac
     * @param scrollArrowSize
     * @param scrollThick
     * @param w
     */
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget w, State state){
        Drag[] drags = null;
        //If there are scroll (drags/drops) actions possible
        if((drags = w.scrollDrags(scrollArrowSize,scrollThick)) != null){
            //For each possible drag, create an action and add it to the derived actions
            for (Drag drag : drags){
                //Create a slide action with the Action Compiler, and add it to the set of derived actions
                actions.add(ac.slideFromTo(
                        new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                        new AbsolutePosition(Point.from(drag.getToX(),drag.getToY()))
                ));

            }
        }
    }
}
