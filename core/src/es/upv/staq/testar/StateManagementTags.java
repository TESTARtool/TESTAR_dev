package es.upv.staq.testar;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateManagementTags {

    // a widget's control type
    public static final Tag<String> WidgetControlType = Tag.from("Widget control type", String.class);

    // the internal handle to a widget's window
    public static final Tag<Long> WidgetWindowHandle = Tag.from("Widget window handle", Long.class);

    // is the widget enabled?
    public static final Tag<Boolean> WidgetIsEnabled = Tag.from("Widget is enabled", Boolean.class);

    // the widget's title
    public static final Tag<String> WidgetTitle = Tag.from("Widget title", String.class);

    // a help text string that may be set for the widget
    public static final Tag<String> WidgetHelpText = Tag.from("Widget helptext", String.class);

    // the automation id for a particular version of a widget's application
    public static final Tag<String> WidgetAutomationId = Tag.from("Widget automation id", String.class);

    // the widget's classname
    public static final Tag<String> WidgetClassName = Tag.from("Widget class name", String.class);

    // // identifier for the framework that this widget belongs to (swing, flash, wind32, etc..)
    public static final Tag<String> WidgetFrameworkId = Tag.from("Widget framework id", String.class);

    // identifier for the orientation that the widget may or may not have
    public static final Tag<String> WidgetOrientationId = Tag.from("Widget orientation id", String.class);

    // is the widget a content element?
    public static final Tag<Boolean> WidgetIsContentElement = Tag.from("Widget is a content element", Boolean.class);

    // is the widget a control element?
    public static final Tag<Boolean> WidgetIsControlElement = Tag.from("Widget is a control element", Boolean.class);

    // the widget currently has keyboard focus
    public static final Tag<Boolean> WidgetHasKeyboardFocus = Tag.from("Widget has keyboard focus", Boolean.class);

    // it is possible for the widget to receive keyboard focus
    public static final Tag<Boolean> WidgetIsKeyboardFocusable = Tag.from("Widget can have keyboard focus", Boolean.class);

    // the item type of the widget
    public static final Tag<String> WidgetItemType = Tag.from("Widget item type", String.class);

    // a string describing the item status of the widget
    public static final Tag<String> WidgetItemStatus = Tag.from("Widget item status", String.class);

    // the path in the widget tree that leads to the widget
    public static final Tag<String> WidgetPath = Tag.from("Path to the widget", String.class);

    // the on-screen boundaries for the widget (coordinates)
    public static final Tag<String> WidgetBoundary = Tag.from("Widget on-screen boundaries", String.class);


    // a set containing the tags that are available for state management
    private static Set<Tag<?>> stateManagementTags = new HashSet<Tag<?>>() {
        {
            add(WidgetControlType);
            add(WidgetWindowHandle);
            add(WidgetIsEnabled);
            add(WidgetTitle);
            add(WidgetHelpText);
            add(WidgetAutomationId);
            add(WidgetClassName);
            add(WidgetFrameworkId);
            add(WidgetOrientationId);
            add(WidgetIsContentElement);
            add(WidgetIsControlElement);
            add(WidgetHasKeyboardFocus);
            add(WidgetIsKeyboardFocusable);
            add(WidgetItemType);
            add(WidgetItemStatus);
            add(WidgetPath);
            add(WidgetBoundary);
        }
    };

    private static BiMap<Tag<?>, String> settingsMap = HashBiMap.create(stateManagementTags.size());
    static {
        settingsMap.put(WidgetControlType, "WidgetControlType");
        settingsMap.put(WidgetWindowHandle, "WidgetWindowHandle");
        settingsMap.put(WidgetIsEnabled, "WidgetIsEnabled");
        settingsMap.put(WidgetTitle, "WidgetTitle");
        settingsMap.put(WidgetHelpText, "WidgetHelpText");
        settingsMap.put(WidgetAutomationId, "WidgetAutomationId");
        settingsMap.put(WidgetClassName, "WidgetClassName");
        settingsMap.put(WidgetFrameworkId, "WidgetFrameworkId");
        settingsMap.put(WidgetOrientationId, "WidgetOrientationId");
        settingsMap.put(WidgetIsContentElement, "WidgetIsContentElement");
        settingsMap.put(WidgetIsControlElement, "WidgetIsControlElement");
        settingsMap.put(WidgetHasKeyboardFocus, "WidgetHasKeyboardFocus");
        settingsMap.put(WidgetIsKeyboardFocusable, "WidgetIsKeyboardFocusable");
        settingsMap.put(WidgetItemType, "WidgetItemType");
        settingsMap.put(WidgetItemStatus, "WidgetItemStatus");
        settingsMap.put(WidgetPath, "WidgetPath");
    }

    // a mapping from the state management tags back to the normal systems tags.
    private static Map<Tag<?>, Tag<?>> tagMappingOther = new HashMap<Tag<?>, Tag<?>>(){
        {
            put(WidgetControlType, Tags.Role);
            put(WidgetTitle, Tags.Title);
            put(WidgetIsEnabled, Tags.Enabled);
            put(WidgetPath, Tags.Path);
        }
    };

    /**
     * This method will return all the tags that are available for use in state management.
     * @return
     */
    public static Set<Tag<?>> getAllTags() {
        return stateManagementTags;
    }

    /**
     * This method will return its equivalent, internal systems tag, if available.
     * @param mappedTag
     * @param forWindows
     * @return
     */
    public static <T> Tag<T> getMappedTag(Tag<T> mappedTag, boolean forWindows) {
        {
            return (Tag<T>) tagMappingOther.getOrDefault(mappedTag, null);
        }
    }

    /**
     * This method returns the state management tag belonging to a given settings string.
     * @param settingsString
     * @return
     */
    public static Tag<?> getTagFromSettingsString(String settingsString) {
        return settingsMap.inverse().getOrDefault(settingsString, null);
    }

    /**
     * This method returns the settings string for a given state management tag.
     * @param tag
     * @return
     */
    public static String getSettingsStringFromTag(Tag<?> tag) {
        return settingsMap.getOrDefault(tag, null);
    }


}
