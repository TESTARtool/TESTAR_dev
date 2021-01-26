/***************************************************************************************************
 *
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.protocols;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import nl.ou.testar.DerivedActions;
import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fruit.alayer.Tags.Title;
import static org.fruit.alayer.webdriver.enums.WdTags.*;
import static org.fruit.alayer.webdriver.enums.WdTags.WebScrollVerticalPercent;
import static org.fruit.alayer.webdriver.enums.WdTags.WebScrollVerticalViewSize;

public class GenericUtilsProtocol extends ClickFilterLayerProtocol {


    protected boolean randomScrollUntilWidgetTagContainingFound(Tag<?> tag, String value, State state, SUT system, int maxNumberOfScrolldowns){
        int numberOfScrolldowns = 0;
        while(numberOfScrolldowns<maxNumberOfScrolldowns){
            System.out.println("DEBUG: "+numberOfScrolldowns+ " < "+maxNumberOfScrolldowns +" :: looking for "+tag+"="+value);
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithTagContaining(tag,value,state);
            if(widget!=null && widget.get(WdTags.WebIsFullOnScreen).booleanValue()){
                System.out.println("Matching widget was found on screen, executing left click action");
//                for(Tag t:widget.tags()){
//                    System.out.println(t.name()+" = "+widget.get(t));
//                }
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                System.out.println("DEBUG: executing left click on widget "+widget.get(Tags.Desc));
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                //slide down if possible
                System.out.println("DEBUG: executing slide down");
                executeRandomSlideAction(state,system);
                state = getState(system);
                numberOfScrolldowns++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+" contains " + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    protected boolean randomScrollUntilWidgetWithMatchingTagFound(Tag<?> tag, String value, State state, SUT system, int maxNumberOfScrolldowns){
        int numberOfScrolldowns = 0;
        while(numberOfScrolldowns<maxNumberOfScrolldowns){
            System.out.println("DEBUG: "+numberOfScrolldowns+ " < "+maxNumberOfScrolldowns +" :: looking for "+tag+"="+value);
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithMatchingTag(tag,value,state);
            if(widget!=null && widget.get(WdTags.WebIsFullOnScreen).booleanValue()){
                System.out.println("Matching widget was found on screen, executing left click action");
//                for(Tag t:widget.tags()){
//                    System.out.println(t.name()+" = "+widget.get(t));
//                }
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                System.out.println("DEBUG: executing left click on widget "+widget.get(Tags.Desc));
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                //slide down if possible
                System.out.println("DEBUG: executing slide down");
                executeRandomSlideAction(state,system);
                state = getState(system);
                numberOfScrolldowns++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+" contains " + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /*
    protected Drag[] getRandomWebDriverVerticalScroll(Widget widget, double scrollArrowSize, double scrollThick) {
        boolean hScroll = widget.get(WebHorizontallyScrollable, Boolean.FALSE);
        boolean vScroll = widget.get(WebVerticallyScrollable, Boolean.FALSE);
        if (!hScroll && !vScroll) {
            return null;
        }

        Drag[] vDrags = null;
        if (vScroll) {
            double viewSize = widget.get(WebScrollVerticalViewSize, Double.MIN_VALUE);
            if (viewSize > 0) {
                double scrollPercent = widget.get(WebScrollVerticalPercent, -1.0);
                Shape shape = widget.get(Tags.Shape, null);
                if (shape != null) {
                    //vDrags = getDrags(shape, false, vViewSize, vScrollPercent, scrollArrowSize, scrollThick);
                    // system dependent
                    double scrollableSize = (false ? shape.width() : shape.height()) - scrollArrowSize * 2;
                    double fromX;
                    double fromY;

                    // vertical
                        fromX = shape.x() + shape.width() - scrollThick / 2;
                        fromY = shape.y() + scrollArrowSize +
                                scrollableSize * scrollPercent / 100.0 +
                                (scrollPercent < 50.0 ? scrollThick / 2 : -3 * scrollThick / 2);


                    int dragC = (int) Math.ceil(100.0 / viewSize) - 1;
                    if (dragC < 1) {
                        return null;
                    }
                    double[] emptyDragPoints = calculateScrollDragPoints(dragC,
                            false ? fromX - shape.x() : fromY - shape.y(),
                            scrollableSize / (double) dragC);

                    vDrags = new Drag[dragC];
                    for (int i = 0; i < dragC; i++) {
                        double toX = false ? shape.x() + scrollArrowSize + emptyDragPoints[i] : fromX;
                        double toY = false ? fromY : shape.y() + scrollArrowSize + emptyDragPoints[i];

                        vDrags[i] = new Drag(fromX, fromY, toX, toY);
                    }
                }
            }
        }

        return vDrags;
    }
*/

    protected void executeRandomSlideAction(State state, SUT system){
        Set<Action> scrollActions = new HashSet<>();
        for(Widget widget:state){
            Drag[] drags = null;
            //If there are scroll (drags/drops) actions possible
            if((drags = widget.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICK)) != null) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //For each possible drag, create an action
                for (Drag drag : drags){
                    //Create a slide action with the Action Compiler
                    scrollActions.add(ac.slideFromTo(
                            new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                            new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                            widget));
                }
            }
        }
        //executing randomly one slide action:
        long graphTime = System.currentTimeMillis();
        Random rnd = new Random(graphTime);
        int randomNr = rnd.nextInt(scrollActions.size());
        System.out.println("DEBUG: executing random drag action, "+scrollActions.size()+" scroll actions found, getting index "+randomNr);
        //FIXME does not work as expected, takes one leap down and then clicks arrow up
        executeAction(system,state,new ArrayList<Action>(scrollActions).get(randomNr));
    }

    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected void executeSlideDownAction(State state, SUT system){
        for(Widget widget:state){
            Drag[] drags = null;
            //If there are scroll (drags/drops) actions possible
            if((drags = widget.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICK)) != null) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //For each possible drag, create an action
                for (Drag drag : drags){
                    System.out.println("DEBUG: executing drag action");
                    //Create a slide action with the Action Compiler
                    executeAction(system,state,ac.slideFromTo(
                            new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                            new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                            widget));
                    // executing only 1 drag action:
                    return;
                }
            }
        }
    }


    protected boolean waitAndLeftClickWidgetWithTagContaining(Tag<?> tag, String value, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithTagContaining(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+" contains " + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    protected boolean waitAndLeftClickWidgetWithTagStartingWith(Tag<?> tag, String value, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithTagStartingWith(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+" starts with " + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }


    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitAndLeftClickWidgetWithMatchingTag(Tag<?> tag, String value, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithMatchingTag(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is typed into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param textToType types the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tag<?> tag, String value, String textToType, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithMatchingTag(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                executeAction(system,state,ac.clickTypeInto(widget, textToType, true));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }
    
    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is pasted into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param textToPaste paste the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndPasteIntoWidgetWithMatchingTag(Tag<?> tag, String value, String textToPaste, State state, SUT system, int maxNumberOfRetries, double waitBetween){
    	int numberOfRetries = 0;
    	while(numberOfRetries<maxNumberOfRetries){
    		//looking for a widget with matching tag value:
    		Widget widget = getWidgetWithMatchingTag(tag,value,state);
    		if(widget!=null){
    			StdActionCompiler ac = new AnnotatingActionCompiler();
    			executeAction(system, state, ac.pasteTextInto(widget, textToPaste, true));
    			// is waiting needed after the action has been executed?
    			return true;
    		}
    		else{
    			Util.pause(waitBetween);
    			state = getState(system);
    			numberOfRetries++;
    		}
    	}
    	System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
    	printTagValuesOfWidgets(tag,state);
    	return false;
    }

    /**
     * Iterates the widgets of the state until a widget with matching tag value is found.
     * The value is case sensitive.
     *
     * @param tag
     * @param value
     * @param state
     * @return the matching widget if found, null if not found
     */
    protected Widget getWidgetWithMatchingTag(Tag<?> tag, String value, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else if(widget.get(tag, null).toString().equals(value)){
                return widget;
            }
        }
        return null;
    }


    protected Widget getWidgetWithTagContaining(Tag<?> tag, String value, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else if(widget.get(tag, null).toString().contains(value)){
                return widget;
            }
        }
        return null;
    }

    protected Widget getWidgetWithTagStartingWith(Tag<?> tag, String value, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else if(widget.get(tag, null).toString().startsWith(value)){
                return widget;
            }
        }
        return null;
    }

    /**
     * Prints to system out all the widgets that have some value in the given tag.
     *
     * @param tag
     * @param state
     */
    protected void printTagValuesOfWidgets(Tag<?> tag, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else{
                System.out.println(tag.toString()+"=" + widget.get(tag, null).toString()+ "; Description of the widget="+widget.get(Tags.Desc, ""));
            }
        }
    }


    /**
     * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
     * @param actions
     * @param ac
     * @param scrollArrowSize
     * @param scrollThick
     * @param widget
     */
    @Deprecated
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget widget){
        Drag[] drags = null;
        //If there are scroll (drags/drops) actions possible
        if((drags = widget.scrollDrags(scrollArrowSize,scrollThick)) != null){
            //For each possible drag, create an action and add it to the derived actions
            for (Drag drag : drags){
                //Create a slide action with the Action Compiler, and add it to the set of derived actions
                actions.add(ac.slideFromTo(
                        new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                        new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                        widget
                ));

            }
        }
    }

    @Deprecated
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget widget, State state){
        addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);
    }

    /**
     * Adds sliding actions into available actions of the DerivedActions for the given widget
     * and returns DerivedActions after that
     *
     * @param derived
     * @param ac
     * @param drags
     * @param widget
     * @return DerivedActions with added sliding actions in the available actions
     */
    protected DerivedActions addSlidingActions(DerivedActions derived, StdActionCompiler ac, Drag[] drags, Widget widget){

            //TODO creates multiple drag actions for one widget?
            //For each possible drag, create an action and add it to the derived actions
            for (Drag drag : drags){
                //Create a slide action with the Action Compiler, and add it to the set of derived actions
                derived.addAvailableAction(ac.slideFromTo(
                        new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                        new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                        widget
                ));

            }
        return derived;
    }

    /**
     * Check whether a widget is clickable
     * @param w
     * @return
     */
    protected boolean isClickable(Widget w){
        Role role = w.get(Tags.Role, Roles.Widget);
        if(Role.isOneOf(role, NativeLinker.getNativeClickableRoles()))
            return true;
        return false;
    }

    /**
     * Check whether a widget is typeable
     * @param w
     * @return
     */
    protected boolean isTypeable(Widget w){
        return NativeLinker.isNativeTypeable(w);
    }

    /**
     * Check whether widget w should be filtered based on
     * its title (matching the regular expression of the Dialog --> clickFilterPattern)
     * that is cannot be hit
     * @param w
     * @return
     */
    protected boolean isUnfiltered(Widget w){
        //Check whether the widget can be hit
        // If not, it should be filtered
        if(!Util.hitTest(w, 0.5, 0.5))
            return false;

        //Check whether the widget has an empty title or no title
        //If it has, it is unfiltered
        //Because it cannot match the regular expression of the Action Filter.
        String title = w.get(Title, "");
        if (title == null || title.isEmpty())
            return true;

        //If no clickFilterPattern exists, then create it
        //Get the clickFilterPattern from the regular expression provided by the tester in the Dialog
        if (this.clickFilterPattern == null)
            this.clickFilterPattern = Pattern.compile(settings().get(ConfigTags.ClickFilter), Pattern.UNICODE_CHARACTER_CLASS);

        //Check whether the title matches any of the clickFilterPatterns
        Matcher m = this.clickFilterMatchers.get(title);
        if (m == null){
            m = this.clickFilterPattern.matcher(title);
            this.clickFilterMatchers.put(title, m);
        }
        return !m.matches();
    }

    /**
     * Return a list of widgets that have the maximal Zindex
     * @param state
     * @return
     */
    protected List<Widget> getTopWidgets(State state){
        List<Widget> topWidgets = new ArrayList<>();
        double maxZIndex = state.get(Tags.MaxZIndex);
        for (Widget w : state)
            if (w.get(Tags.ZIndex) == maxZIndex)
                topWidgets.add(w);
        return topWidgets;
    }

    protected boolean isNOP(Action action){
        String as = action.toString();
        if (as != null && as.equals(NOP.NOP_ID))
            return true;
        else
            return false;
    }

    protected boolean isESC(Action action){
        Role r = action.get(Tags.Role, null);
        if (r != null && r.isA(ActionRoles.HitKey)){
            String desc = action.get(Tags.Desc, null);
            if (desc != null && desc.contains("VK_ESCAPE"))
                return true;
        }
        return false;
    }
}
