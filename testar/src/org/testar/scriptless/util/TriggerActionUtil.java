/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.util;

import java.util.Collections;
import java.util.Map;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.action.StdActionCompiler;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

public final class TriggerActionUtil {

    private TriggerActionUtil() {
    }

    /**
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param state
     * @param system
     * @param runtime
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    public static boolean waitAndLeftClickWidgetWithMatchingTag(String tagName,
                                                                String value,
                                                                State state,
                                                                SUT system,
                                                                TriggerActionRuntime runtime,
                                                                int maxNumberOfRetries,
                                                                double waitBetween) {
        Assert.notNull(tagName, value);
        Assert.notNull(state, system, runtime);

        // If the state has no children return false
        // This may happen because the state has no GUI elements, for example a XML page
        if(state.childCount() == 0) {
            return false;
        }

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            if(!tagName.startsWith("Web")) {
                tagName = "Web" + tagName;
            }
        }

        for(Tag<?> tag : state.child(0).tags()) {
            if(tag.name().equalsIgnoreCase(tagName)) {
                return waitAndLeftClickWidgetWithMatchingTag(
                        tag,
                        value,
                        state,
                        system,
                        runtime,
                        maxNumberOfRetries,
                        waitBetween
                );
            }
        }

        System.out.println("Matching widget was not found, " + tagName + "=" + value);
        return false;
    }

    /**
     * This method waits until a widget with multiple matching tag values is found or the retry limit is reached.
     * If a widget that matches all specified tag values is found, the left mouse button is clicked on it and the
     * return value is true. Else returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     */
    public static boolean waitAndLeftClickWidgetWithMatchingTags(Map<String,String> tagValues,
                                                                 State state,
                                                                 SUT system,
                                                                 TriggerActionRuntime runtime,
                                                                 int maxNumberOfRetries,
                                                                 double waitBetween) {
        Assert.notNull(tagValues);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while (numberOfRetries < maxNumberOfRetries) {
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTags(tagValues, state);
            if (widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredClickAction(state, widget);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
     * @param value
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    public static boolean waitAndLeftClickWidgetWithMatchingTag(Tag<?> tag,
                                                                String value,
                                                                State state,
                                                                SUT system,
                                                                TriggerActionRuntime runtime,
                                                                int maxNumberOfRetries,
                                                                double waitBetween) {
        Assert.notNull(tag, value);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while(numberOfRetries < maxNumberOfRetries) {
            //looking for a widget with matching tag value:
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTag(tag, value, state);
            if(widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredClickAction(state, widget);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /**
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param textToType
     * @param state
     * @param system
     * @param runtime
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    public static boolean waitLeftClickAndTypeIntoWidgetWithMatchingTag(String tagName,
                                                                        String value,
                                                                        String textToType,
                                                                        State state,
                                                                        SUT system,
                                                                        TriggerActionRuntime runtime,
                                                                        int maxNumberOfRetries,
                                                                        double waitBetween) {
        Assert.notNull(tagName, value, textToType);
        Assert.notNull(state, system, runtime);

        // If the state has no children return false
        // This may happen because the state has no GUI elements, for example a XML page
        if(state.childCount() == 0) {
            return false;
        }

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            if(!tagName.startsWith("Web")) {
                tagName = "Web" + tagName;
            }
        }

        for(Tag<?> tag : state.child(0).tags()) {
            if(tag.name().equalsIgnoreCase(tagName)) {
                return waitLeftClickAndTypeIntoWidgetWithMatchingTag(
                        tag,
                        value,
                        textToType,
                        state,
                        system,
                        runtime,
                        maxNumberOfRetries,
                        waitBetween
                );
            }
        }

        System.out.println("Matching widget was not found, " + tagName + "=" + value);
        return false;
    }

    /**
     * This method waits until a widget matching multiple tag values is found or the retry limit is reached.
     * If a matching widget is found, the left mouse button is clicked on it, the given text is typed into it, and the return value is true.
     * Else it returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param textToType types the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    public static boolean waitLeftClickAndTypeIntoWidgetWithMatchingTags(Map<String,String> tagValues,
                                                                         String textToType,
                                                                         State state,
                                                                         SUT system,
                                                                         TriggerActionRuntime runtime,
                                                                         int maxNumberOfRetries,
                                                                         double waitBetween) {
        Assert.notNull(tagValues, textToType);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while(numberOfRetries < maxNumberOfRetries) {
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTags(tagValues, state);
            if(widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredTypeAction(state, widget, textToType, true);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is typed into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
     * @param value
     * @param textToType types the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    public static boolean waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tag<?> tag,
                                                                        String value,
                                                                        String textToType,
                                                                        State state,
                                                                        SUT system,
                                                                        TriggerActionRuntime runtime,
                                                                        int maxNumberOfRetries,
                                                                        double waitBetween) {
        Assert.notNull(tag, value, textToType);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while(numberOfRetries < maxNumberOfRetries) {
            //looking for a widget with matching tag value:
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTag(tag, value, state);
            if(widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredTypeAction(state, widget, textToType, true);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        System.out.println("Matching widget was not found, " + tag.toString() + "=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /**
     *
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param textToPaste
     * @param state
     * @param system
     * @param runtime
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    public static boolean waitLeftClickAndPasteIntoWidgetWithMatchingTag(String tagName,
                                                                         String value,
                                                                         String textToPaste,
                                                                         State state,
                                                                         SUT system,
                                                                         TriggerActionRuntime runtime,
                                                                         int maxNumberOfRetries,
                                                                         double waitBetween) {
        Assert.notNull(tagName, value, textToPaste);
        Assert.notNull(state, system, runtime);

        // If the state has no children return false
        // This may happen because the state has no GUI elements, for example a XML page
        if(state.childCount() == 0) {
            return false;
        }

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            if(!tagName.startsWith("Web")) {
                tagName = "Web" + tagName;
            }
        }
        for(Tag<?> tag : state.child(0).tags()) {
            if(tag.name().equalsIgnoreCase(tagName)) {
                return waitLeftClickAndPasteIntoWidgetWithMatchingTag(
                        tag,
                        value,
                        textToPaste,
                        state,
                        system,
                        runtime,
                        maxNumberOfRetries,
                        waitBetween
                );
            }
        }

        System.out.println("Matching widget was not found, " + tagName + "=" + value);
        return false;
    }

   /**
     * This method waits until a widget matching multiple tag values is found or the retry limit is reached.
     * If a matching widget is found, the left mouse button is clicked on it, the given text is pasted into it, and the return value is true.
     * Else it returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param textToPaste pastes the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    public static boolean waitLeftClickAndPasteIntoWidgetWithMatchingTags(Map<String,String> tagValues,
                                                                          String textToPaste,
                                                                          State state,
                                                                          SUT system,
                                                                          TriggerActionRuntime runtime,
                                                                          int maxNumberOfRetries,
                                                                          double waitBetween) {
        Assert.notNull(tagValues, textToPaste);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while(numberOfRetries < maxNumberOfRetries) {
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTags(tagValues, state);
            if(widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredPasteAction(state, widget, textToPaste, true);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is pasted into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
     * @param value
     * @param textToPaste paste the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param runtime
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    public static boolean waitLeftClickAndPasteIntoWidgetWithMatchingTag(Tag<?> tag,
                                                                         String value,
                                                                         String textToPaste,
                                                                         State state,
                                                                         SUT system,
                                                                         TriggerActionRuntime runtime,
                                                                         int maxNumberOfRetries,
                                                                         double waitBetween) {
        Assert.notNull(tag, value, textToPaste);
        Assert.notNull(state, system, runtime);

        int numberOfRetries = 0;
        while(numberOfRetries < maxNumberOfRetries) {
            //looking for a widget with matching tag value:
            Widget widget = WidgetMatchingUtil.getWidgetWithMatchingTag(tag, value, state);
            if(widget != null) {
                // When the desired widget to interact with is found,
                // Create the triggered action, build the identifier, and execute it.
                Action triggeredAction = triggeredPasteAction(state, widget, textToPaste, true);
                runtime.refreshActions(state, Collections.singleton(triggeredAction));
                runtime.executeTriggerAction(system, state, triggeredAction);
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = runtime.refreshState(system);
                numberOfRetries++;
            }
        }

        System.out.println("Matching widget was not found, " + tag.toString() + "=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /**
     * Prints to system out all the widgets that have some value in the given tag.
     *
     * @param tag
     * @param state
     */
    private static void printTagValuesOfWidgets(Tag<?> tag, State state) {
        for(Widget widget : state) {
            if(widget.get(tag, null) == null) {
                // this widget did not have a value for the given tag
            }
            else{
                System.out.println(tag.toString() + "=" + widget.get(tag, null).toString()+ "; Description of the widget = " + widget.get(Tags.Desc, ""));
            }
        }
    }

    /**
     * By default, trigger click widget using LeftClickAt (Windows level). 
     */
    public static Action triggeredClickAction(State state, Widget widget) {
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	return ac.leftClickAt(widget);
    }

    /**
     * By default, trigger click and type text using ClickTypeInto (Windows level). 
     */
    public static Action triggeredTypeAction(State state, Widget widget, String textToType, boolean replaceText) {
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	return ac.clickTypeInto(widget, textToType, replaceText);
    }

    /**
     * By default, trigger click and paste text using ClickPasteInto (Windows level). 
     */
    public static Action triggeredPasteAction(State state, Widget widget, String textToPaste, boolean replaceText) {
    	StdActionCompiler ac = new AnnotatingActionCompiler();
    	return ac.pasteTextInto(widget, textToPaste, replaceText);
    }
}
