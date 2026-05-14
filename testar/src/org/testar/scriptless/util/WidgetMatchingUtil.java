/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

public final class WidgetMatchingUtil {

    private WidgetMatchingUtil() {
    }

    /**
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param state
     * @return
     */
    public static Widget getWidgetWithMatchingTag(String tagName, String value, State state) {
        // If the state has no children return null
        // This may happen because the state has no GUI elements, for example a XML page
        if(state.childCount() == 0) {
            return null;
        }

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            if(!tagName.startsWith("Web")) {
                tagName = "Web"+tagName;
            }
        }

        for(Tag<?> tag : state.child(0).tags()) {
            if(tag.name().equalsIgnoreCase(tagName)) {
                return getWidgetWithMatchingTag(tag, value, state);
            }
        }

        return null;
    }

     /**
     * Finds a widget that matches all specified tag values
     *
     * @param tagValues A map of tags. The keys are the tag names and the values are the values these tags should have
     * @param state
     * @return
     */
    public static Widget getWidgetWithMatchingTags(Map<String,String> tagValues, State state) {
        // If the state has no children return null
        // This may happen because the state has no GUI elements, for example a XML page
        if(state.childCount() == 0) {
            return null;
        }

        // First make a lookup table to find Tags for each tag name
        Map<String,Tag<?>> tagLookup = new HashMap<String,Tag<?>>();
        for (String tagName : tagValues.keySet()) {

            if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER) && !tagName.startsWith("Web")) {
                tagName = "Web" + tagName;
            }

            boolean tagFound = false;

            for (Tag<?> tag : state.child(0).tags()) {
                if (tag.name().equalsIgnoreCase(tagName) ) {
                    tagLookup.put(tagName,tag);
                    tagFound = true;
                    break;
                }
            }
            if (!tagFound) {
                System.out.println("Error: could not find tag for tag name " + tagName);
                return null;
            }
        }

        // Then check the tags of each widget to see if they match the tag values we are
        // looking for.
        for (Widget widget : state) {
            Vector<String> tagsFound = new Vector<String>();

            HashMap<String, String> webTagValues = new HashMap<>();
            webTagValues.putAll(tagValues);

            for (String tagName : tagValues.keySet()) {

                if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER) &&
                        !tagName.startsWith("Web") ) {
                    String value = tagValues.get(tagName);
                    webTagValues.remove(tagName);
                    tagName = "Web" + tagName;
                    webTagValues.put(tagName, value);
                }

                Tag<?> tag = tagLookup.get(tagName);
                String value = webTagValues.get(tagName);
                if (widget.get(tag, null) != null &&
                        widget.get(tag, null).toString().equals(value) )  {
                    tagsFound.add(tagName);
                }
            }

            if (tagsFound.size() == webTagValues.keySet().size() ) {
                return widget;
            }
        }

        return null;
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
    public static Widget getWidgetWithMatchingTag(Tag<?> tag, String value, State state) {
        for(Widget widget:state) {
            if(widget.get(tag, null)==null) {
                // this widget did not have a value for the given tag
            }
            else if(widget.get(tag, null).toString().equals(value)) {
                return widget;
            }
            else if(widget.get(tag, null).toString().contains(value)) {
                return widget;
            }
        }
        return null;
    }
}
