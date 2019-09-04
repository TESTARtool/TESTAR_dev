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

    public enum Group {General, ControlPattern}


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

    // is the widget off-screen?
    public static final Tag<Boolean> WidgetIsOffscreen = Tag.from("Widget is off-screen", Boolean.class);

    // accelator key combinations for the widget
    public static final Tag<String> WidgetAccelatorKey = Tag.from("Widget accelator key", String.class);

    // access key that will trigger the widget
    public static final Tag<String> WidgetAccessKey = Tag.from("Widget access key", String. class);

    // Aria properties of a uia element
    public static final Tag<String> WidgetAriaProperties = Tag.from("Widget aria properties", String.class);

    // Aria role of a UIA element
    public static final Tag<String> WidgetAriaRole = Tag.from("Widget aria role", String.class);

    // is the widget a dialog window?
    public static final Tag<Boolean> WidgetIsDialog = Tag.from("Widget is a dialog windows", Boolean.class);

    // does the widget contain password info?
    public static final Tag<Boolean> WidgetIsPassword = Tag.from("Widget contains password info", Boolean.class);

    // Indicated whether the Widget/UIA element represents peripheral UI.
    public static final Tag<Boolean> WidgetIsPeripheral = Tag.from("Widget represents peripheral UI", Boolean.class);

    // is the widget required input for a form?
    public static final Tag<Boolean> WidgetIsRequiredForForm = Tag.from("Widget is required input for a form", Boolean.class);

    // Is the widget/uiaelement part of a landmark/group?
    public static final Tag<Long> WidgetLandmarkType = Tag.from("Widget element grouping", Long.class);

    // the level in a hierarchical group
    public static final Tag<Long>  WidgetGroupLevel = Tag.from("Widget's level in hierarchy", Long.class);

    // widget's live setting
    public static final Tag<Long> WidgetLiveSetting = Tag.from("Widget's live setting", Long.class);

    // widget's position compared to its siblings
    public static final Tag<Long> WidgetSetPosition = Tag.from("Widget's position in sibling set", Long.class);

    // the size of the set of the element and its siblings
    public static final Tag<Long> WidgetSetSize = Tag.from("Widget's sibling set size (inclusive)", Long.class);

    // the angle of the widget's rotation
    public static final Tag<Long> WidgetRotation = Tag.from("Widget's rotation (degrees)", Long.class);

    // widget pattern tags
    public static final Tag<Boolean> WidgetAnnotationPattern = Tag.from("Widget Annotation Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetDockPattern = Tag.from("Widget Dock Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetDragPattern = Tag.from("Widget Drag Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetDropTargetPattern = Tag.from("Widget DropTarget Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetExpandCollapsePattern = Tag.from("Widget ExpandCollapse Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetGridItemPattern = Tag.from("Widget GridItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetGridPattern = Tag.from("Widget Grid Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetInvokePattern = Tag.from("Widget Invoke Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetItemContainerPattern = Tag.from("Widget ItemContainer Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetLegacyIAccessiblePattern = Tag.from("Widget LegacyIAccessible Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetMultipleViewPattern = Tag.from("Widget MultipleView Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetObjectModelPattern = Tag.from("Widget ObjectModel Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetRangeValuePattern = Tag.from("Widget RangeValue Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetScrollItemPattern = Tag.from("Widget ScrollItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetScrollPattern = Tag.from("Widget Scroll Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetSelectionItemPattern = Tag.from("Widget SelectionItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetSelectionPattern = Tag.from("Widget Selection Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetSpreadsheetPattern = Tag.from("Widget Spreadsheet Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetSpreadsheetItemPattern = Tag.from("Widget SpreadsheetItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetStylesPattern = Tag.from("Widget Styles Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetSynchronizedInputPattern = Tag.from("Widget SynchronizedInput Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTableItemPattern = Tag.from("Widget TableItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTablePattern = Tag.from("Widget Table Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTextChildPattern = Tag.from("Widget TextChild Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTextPattern = Tag.from("Widget Text Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTextPattern2 = Tag.from("Widget Text Pattern2", Boolean.class);
    public static final Tag<Boolean> WidgetTogglePattern = Tag.from("Widget Toggle Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTransformPattern = Tag.from("Widget Transform Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetTransformPattern2 = Tag.from("Widget Transform Pattern2", Boolean.class);
    public static final Tag<Boolean> WidgetValuePattern = Tag.from("Widget Value Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetVirtualizedItemPattern = Tag.from("Widget VirtualizedItem Pattern", Boolean.class);
    public static final Tag<Boolean> WidgetWindowPattern = Tag.from("Widget Window Pattern", Boolean.class);

    // widget pattern property tags

    // annotation pattern
    public static final Tag<Long> WidgetAnnotationAnnotationTypeId = Tag.from("WidgetAnnotationAnnotationTypeId", Long.class);
    public static final Tag<String>	WidgetAnnotationAnnotationTypeName = Tag.from("WidgetAnnotationAnnotationTypeName", String.class);
    public static final Tag<String>	WidgetAnnotationAuthor = Tag.from("WidgetAnnotationAuthor", String.class);
    public static final Tag<String> WidgetAnnotationDateTime = Tag.from("WidgetAnnotationDateTime", String.class);
    public static final Tag<Long> WidgetAnnotationTarget = Tag.from("WidgetAnnotationTarget", Long.class);

    // dock pattern
    public static final Tag<Long> WidgetDockDockPosition = Tag.from("WidgetDockDockPosition ", Long.class); // check

    // drag control pattern
    public static final Tag<String> WidgetDragDropEffect = Tag.from("WidgetDragDropEffect", String.class);
    public static final Tag<String> WidgetDragDropEffects = Tag.from("WidgetDragDropEffects", String.class); // array
    public static final Tag<Boolean> WidgetDragIsGrabbed = Tag.from("WidgetDragIsGrabbed", Boolean.class);
    public static final Tag<Object> WidgetDragGrabbedItems = Tag.from("WidgetDragGrabbedItems", Object.class); // array

    // drop target control pattern
    public static final Tag<String> WidgetDropTargetDropTargetEffect = Tag.from("WidgetDropTargetDropTargetEffect", String.class);
    public static final Tag<Long> WidgetDropTargetDropTargetEffects = Tag.from("WidgetDropTargetDropTargetEffects", Long.class); // array

    // expande/collapse pattern
    public static final Tag<Long> WidgetExpandCollapseExpandCollapseState = Tag.from("WidgetExpandCollapseExpandCollapseState", Long.class);

    // grid control pattern
    public static final Tag<Long> WidgetGridColumnCount = Tag.from ("WidgetGridColumnCount", Long.class);
    public static final Tag<Long> WidgetGridRowCount = Tag.from("WidgetGridRowCount", Long.class);

    // grid item control pattern
    public static final Tag<Long> WidgetGridItemColumn = Tag.from("WidgetGridItemColumn", Long.class);
    public static final Tag<Long> WidgetGridItemColumnSpan = Tag.from("WidgetGridItemColumnSpan", Long.class);
    public static final Tag<Long> WidgetGridItemContainingGrid = Tag.from("WidgetGridItemContainingGrid", Long.class);
    public static final Tag<Long> WidgetGridItemRow = Tag.from("WidgetGridItemRow", Long.class);
    public static final Tag<Long> WidgetGridItemRowSpan = Tag.from("WidgetGridItemRowSpan", Long.class);

    // LegacyIAccessible control pattern
    public static final Tag<Long> WidgetLegacyIAccessibleChildId = Tag.from ("WidgetLegacyIAccessibleChildId", Long.class);
    public static final Tag<String> WidgetLegacyIAccessibleDefaultAction = Tag.from("WidgetLegacyIAccessibleDefaultAction", String.class);
    public static final Tag<String> WidgetLegacyIAccessibleDescription = Tag.from("WidgetLegacyIAccessibleDescription", String.class);
    public static final Tag<String> WidgetLegacyIAccessibleHelp = Tag.from("WidgetLegacyIAccessibleHelp", String.class);
    public static final Tag<String> WidgetLegacyIAccessibleKeyboardShortcut = Tag.from("WidgetLegacyIAccessibleKeyboardShortcut", String.class);
    public static final Tag<String> WidgetLegacyIAccessibleName = Tag.from("WidgetLegacyIAccessibleName", String.class);
    public static final Tag<Long> WidgetLegacyIAccessibleRole = Tag.from("WidgetLegacyIAccessibleRole", Long.class);
    public static final Tag<Object> WidgetLegacyIAccessibleSelection = Tag.from("WidgetLegacyIAccessibleSelection", Object.class); // list/array
    public static final Tag<Long> WidgetLegacyIAccessibleState = Tag.from("WidgetLegacyIAccessibleState", Long.class);
    public static final Tag<String> WidgetLegacyIAccessibleValue = Tag.from("WidgetLegacyIAccessibleValue", String. class);

    // MultipleView control pattern
    public static final Tag<Long> WidgetMultipleViewCurrentView = Tag.from("WidgetMultipleViewCurrentView", Long.class);
    public static final Tag<String> WidgetMultipleViewSupportedViews = Tag.from("WidgetMultipleViewSupportedViews", String.class); // array

    // range value control pattern
    public static final Tag<Boolean> WidgetRangeValueIsReadOnly = Tag.from("WidgetRangeValueIsReadOnly", Boolean.class);
    public static final Tag<Long> WidgetRangeValueLargeChange = Tag.from("WidgetRangeValueLargeChange", Long.class);
    public static final Tag<Long> WidgetRangeValueMaximum = Tag.from("WidgetRangeValueMaximum", Long.class);
    public static final Tag<Long> WidgetRangeValueMinimum = Tag.from("WidgetRangeValueMinimum", Long.class);
    public static final Tag<Long> WidgetRangeValueSmallChange = Tag.from("WidgetRangeValueSmallChange", Long.class);
    public static final Tag<Long> WidgetRangeValueValue = Tag.from("WidgetRangeValueValue", Long.class);

    // selection control pattern
    public static final Tag<Boolean> WidgetSelectionCanSelectMultiple = Tag.from("WidgetSelectionCanSelectMultiple", Boolean.class);
    public static final Tag<Boolean> WidgetSelectionIsSelectionRequired = Tag.from("WidgetSelectionIsSelectionRequired", Boolean.class);
    public static final Tag<Object> WidgetSelectionSelection = Tag.from("WidgetSelectionSelection", Object.class); // array

    // selection item control pattern
    public static final Tag<Boolean> WidgetSelectionItemIsSelected = Tag.from("WidgetSelectionItemIsSelected", Boolean.class);
    public static final Tag<Long> WidgetSelectionItemSelectionContainer = Tag.from("WidgetSelectionItemSelectionContainer", Long.class);

    // spreadsheet item control panel
    public static final Tag<String> WidgetSpreadsheetItemFormula = Tag.from("WidgetSpreadsheetItemFormula", String.class);
    public static final Tag<Object> WidgetSpreadsheetItemAnnotationObjects = Tag.from("WidgetSpreadsheetItemAnnotationObjects", Object.class); //array
    public static final Tag<String> WidgetSpreadsheetItemAnnotationTypes = Tag.from("WidgetSpreadsheetItemAnnotationTypes", String.class); // array

    // scroll pattern
    public static final Tag<Boolean> WidgetHorizontallyScrollable = Tag.from("WidgetHorizontallyScrollable", Boolean.class);
    public static final Tag<Boolean> WidgetVerticallyScrollable = Tag.from("WidgetVerticallyScrollable", Boolean.class);
    public static final Tag<Double> WidgetScrollHorizontalViewSize = Tag.from("WidgetScrollHorizontalViewSize", Double.class);
    public static final Tag<Double> WidgetScrollVerticalViewSize = Tag.from("WidgetScrollVerticalViewSize", Double.class);
    public static final Tag<Double> WidgetScrollHorizontalPercent = Tag.from("WidgetScrollHorizontalPercent", Double.class);
    public static final Tag<Double> WidgetScrollVerticalPercent = Tag.from("WidgetScrollVerticalPercent", Double.class);

    // styles control pattern
    public static final Tag<String> WidgetStylesExtendedProperties = Tag.from("WidgetStylesExtendedProperties", String.class);
    public static final Tag<Long> WidgetStylesFillColor = Tag.from("WidgetStylesFillColor", Long.class);
    public static final Tag<Long> WidgetStylesFillPatternColor = Tag.from("WidgetStylesFillPatternColor", Long.class);
    public static final Tag<String> WidgetStylesFillPatternStyle = Tag.from("WidgetStylesFillPatternStyle", String.class);
    public static final Tag<String> WidgetStylesShape = Tag.from("WidgetStylesShape", String.class);
    public static final Tag<Long> WidgetStylesStyleId = Tag.from("WidgetStylesStyleId", Long.class);
    public static final Tag<String> WidgetStylesStyleName = Tag.from("WidgetStylesStyleName", String.class);

    // table control pattern
    public static final Tag<Long> WidgetTableColumnHeaders = Tag.from("WidgetTableColumnHeaders", Long.class); // array
    public static final Tag<Long> WidgetTableRowHeaders = Tag.from("WidgetTableRowHeaders", Long.class); // array
    public static final Tag<Long> WidgetTableRowOrColumnMajor = Tag.from("WidgetTableRowOrColumnMajor", Long.class);

    // table item control panel
    public static final Tag<Object> WidgetTableItemColumnHeaderItems = Tag.from("WidgetTableItemColumnHeaderItems", Object.class); // array
    public static final Tag<Object> WidgetTableItemRowHeaderItems = Tag.from("WidgetTableItemRowHeaderItems", Object.class); //array

    // toggle control pattern
    public static final Tag<Long> WidgetToggleToggleState = Tag.from("WidgetToggleToggleState", Long.class);

    // transform pattern
    public static final Tag<Boolean> WidgetTransformCanMove = Tag.from("WidgetTransformCanMove", Boolean.class);
    public static final Tag<Boolean> WidgetTransformCanResize = Tag.from("WidgetTransformCanResize", Boolean.class);
    public static final Tag<Boolean> WidgetTransformCanRotate = Tag.from("WidgetTransformCanRotate", Boolean.class);

    //transform 2 pattern
    public static final Tag<Boolean> WidgetTransform2CanZoom = Tag.from("WidgetTransform2CanZoom", Boolean.class);
    public static final Tag<Long> WidgetTransform2ZoomLevel = Tag.from("WidgetTransform2ZoomLevel", Long.class);
    public static final Tag<Long> WidgetTransform2ZoomMaximum = Tag.from("WidgetTransform2ZoomMaximum", Long.class);
    public static final Tag<Long> WidgetTransform2ZoomMinimum = Tag.from("WidgetTransform2ZoomMinimum", Long.class);

    // Value control pattern
    public static final Tag<Boolean> WidgetValueIsReadOnly = Tag.from("WidgetValueIsReadOnly", Boolean.class);
    public static final Tag<String> WidgetValueValue = Tag.from("WidgetValueValue", String.class);

    // window control pattern
    public static final Tag<Boolean> WidgetWindowCanMaximize = Tag.from("WidgetWindowCanMaximize", Boolean.class);
    public static final Tag<Boolean> WidgetWindowCanMinimize = Tag.from("WidgetWindowCanMinimize", Boolean.class);
    public static final Tag<Boolean> WidgetWindowIsModal = Tag.from("WidgetWindowIsModal", Boolean.class);
    public static final Tag<Boolean> WidgetWindowIsTopmost = Tag.from("WidgetWindowIsTopmost", Boolean.class);
    public static final Tag<Long> WidgetWindowWindowInteractionState = Tag.from("WidgetWindowWindowInteractionState", Long.class); // check
    public static final Tag<Long> WidgetWindowWindowVisualState = Tag.from("WidgetWindowWindowVisualState", Long.class); // check

    // a set containing the tags that are available for state management
    private static Set<Tag<?>> stateManagementTags = new HashSet<Tag<?>>() {
        {
            add(WidgetControlType);
//            add(WidgetWindowHandle); // this property changes between different executions of the sut
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
            // new
            add(WidgetAccelatorKey);
            add(WidgetAccessKey);
            add(WidgetAriaProperties);
            add(WidgetAriaRole);
//            add(WidgetIsDialog); (deactived for now, because the UIA API does not seem to recognize it)
            add(WidgetIsPassword);
            add(WidgetIsPeripheral);
            add(WidgetIsRequiredForForm);
            add(WidgetLandmarkType);
            add(WidgetGroupLevel);
            add(WidgetLiveSetting);
            add(WidgetSetPosition);
            add(WidgetSetSize);
            add(WidgetRotation);

            // patterns
            add(WidgetAnnotationPattern);
            add(WidgetDockPattern);
            add(WidgetDragPattern);
            add(WidgetDropTargetPattern);
            add(WidgetExpandCollapsePattern);
            add(WidgetGridItemPattern);
            add(WidgetGridPattern);
            add(WidgetInvokePattern);
            add(WidgetItemContainerPattern);
            add(WidgetLegacyIAccessiblePattern);
            add(WidgetMultipleViewPattern);
            add(WidgetObjectModelPattern);
            add(WidgetRangeValuePattern);
            add(WidgetScrollItemPattern);
            add(WidgetScrollPattern);
            add(WidgetSelectionItemPattern);
            add(WidgetSelectionPattern);
            add(WidgetSpreadsheetPattern);
            add(WidgetSpreadsheetItemPattern);
            add(WidgetStylesPattern);
            add(WidgetSynchronizedInputPattern);
            add(WidgetTableItemPattern);
            add(WidgetTablePattern);
            add(WidgetTextChildPattern);
            add(WidgetTextPattern);
            add(WidgetTextPattern2);
            add(WidgetTogglePattern);
            add(WidgetTransformPattern);
            add(WidgetTransformPattern2);
            add(WidgetValuePattern);
            add(WidgetVirtualizedItemPattern);
            add(WidgetWindowPattern);
        }
    };

    /**
     * Method will return true if a given tag is an available state management tag.
     * @param tag
     * @return
     */
    public static boolean isStateManagementTag(Tag<?> tag) {
        return stateManagementTags.contains(tag);
    }

    // a bi-directional mapping from the state management tags to a string equivalent for use in the settings file
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
        settingsMap.put(WidgetAccelatorKey, "WidgetAccelatorKey");
        settingsMap.put(WidgetAccessKey, "WidgetAccessKey");
        settingsMap.put(WidgetAriaProperties, "WidgetAriaProperties");
        settingsMap.put(WidgetAriaRole, "WidgetAriaRole");
        settingsMap.put(WidgetIsDialog, "WidgetIsDialog");
        settingsMap.put(WidgetIsPassword, "WidgetIsPassword");
        settingsMap.put(WidgetIsPeripheral, "WidgetIsPeripheral");
        settingsMap.put(WidgetIsRequiredForForm, "WidgetIsRequiredForForm");
        settingsMap.put(WidgetLandmarkType, "WidgetLandmarkType");
        settingsMap.put(WidgetGroupLevel, "WidgetGroupLevel");
        settingsMap.put(WidgetLiveSetting, "WidgetLiveSetting");
        settingsMap.put(WidgetSetPosition, "WidgetSetPosition");
        settingsMap.put(WidgetSetSize, "WidgetSetSize");
        settingsMap.put(WidgetRotation, "WidgetRotation");
        settingsMap.put(WidgetAnnotationPattern, "WidgetAnnotationPattern");
        settingsMap.put(WidgetDockPattern, "WidgetDockPattern");
        settingsMap.put(WidgetDragPattern, "WidgetDragPattern");
        settingsMap.put(WidgetDropTargetPattern, "WidgetDropTargetPattern");
        settingsMap.put(WidgetExpandCollapsePattern, "WidgetExpandCollapsePattern");
        settingsMap.put(WidgetGridItemPattern, "WidgetGridItemPattern");
        settingsMap.put(WidgetGridPattern, "WidgetGridPattern");
        settingsMap.put(WidgetInvokePattern, "WidgetInvokePattern");
        settingsMap.put(WidgetItemContainerPattern, "WidgetItemContainerPattern");
        settingsMap.put(WidgetLegacyIAccessiblePattern, "WidgetLegacyIAccessiblePattern");
        settingsMap.put(WidgetMultipleViewPattern, "WidgetMultipleViewPattern");
        settingsMap.put(WidgetObjectModelPattern, "WidgetObjectModelPattern");
        settingsMap.put(WidgetRangeValuePattern, "WidgetRangeValuePattern");
        settingsMap.put(WidgetScrollItemPattern, "WidgetScrollItemPattern");
        settingsMap.put(WidgetScrollPattern, "WidgetScrollPattern");
        settingsMap.put(WidgetSelectionItemPattern, "WidgetSelectionItemPattern");
        settingsMap.put(WidgetSelectionPattern, "WidgetSelectionPattern");
        settingsMap.put(WidgetSpreadsheetPattern, "WidgetSpreadsheetPattern");
        settingsMap.put(WidgetSpreadsheetItemPattern, "WidgetSpreadsheetItemPattern");
        settingsMap.put(WidgetStylesPattern, "WidgetStylesPattern");
        settingsMap.put(WidgetSynchronizedInputPattern, "WidgetSynchronizedInputPattern");
        settingsMap.put(WidgetTableItemPattern, "WidgetTableItemPattern");
        settingsMap.put(WidgetTablePattern, "WidgetTablePattern");
        settingsMap.put(WidgetTextChildPattern, "WidgetTextChildPattern");
        settingsMap.put(WidgetTextPattern, "WidgetTextPattern");
        settingsMap.put(WidgetTextPattern2, "WidgetTextPattern2");
        settingsMap.put(WidgetTogglePattern, "WidgetTogglePattern");
        settingsMap.put(WidgetTransformPattern, "WidgetTransformPattern");
        settingsMap.put(WidgetTransformPattern2, "WidgetTransformPattern2");
        settingsMap.put(WidgetValuePattern, "WidgetValuePattern");
        settingsMap.put(WidgetVirtualizedItemPattern, "WidgetVirtualizedItemPattern");
        settingsMap.put(WidgetWindowPattern, "WidgetWindowPattern");
    }

    // a mapping of a tag to its group
    private static Map<Tag<?>, Group> tagGroupMap = new HashMap<Tag<?>, Group>() {
        {
            put(WidgetControlType, Group.General);
            put(WidgetWindowHandle, Group.General);
            put(WidgetIsEnabled, Group.General);
            put(WidgetTitle, Group.General);
            put(WidgetHelpText, Group.General);
            put(WidgetAutomationId, Group.General);
            put(WidgetClassName, Group.General);
            put(WidgetFrameworkId, Group.General);
            put(WidgetOrientationId, Group.General);
            put(WidgetIsContentElement, Group.General);
            put(WidgetIsControlElement, Group.General);
            put(WidgetHasKeyboardFocus, Group.General);
            put(WidgetIsKeyboardFocusable, Group.General);
            put(WidgetItemType, Group.General);
            put(WidgetItemStatus, Group.General);
            put(WidgetPath, Group.General);
            put(WidgetAccelatorKey, Group.General);
            put(WidgetAccessKey, Group.General);
            put(WidgetAriaProperties, Group.General);
            put(WidgetAriaRole, Group.General);
            put(WidgetIsDialog, Group.General);
            put(WidgetIsPassword, Group.General);
            put(WidgetIsPeripheral, Group.General);
            put(WidgetIsRequiredForForm, Group.General);
            put(WidgetLandmarkType, Group.General);
            put(WidgetGroupLevel, Group.General);
            put(WidgetLiveSetting, Group.General);
            put(WidgetSetPosition, Group.General);
            put(WidgetSetSize, Group.General);
            put(WidgetRotation, Group.General);
            put(WidgetAnnotationPattern, Group.ControlPattern);
            put(WidgetDockPattern, Group.ControlPattern);
            put(WidgetDragPattern, Group.ControlPattern);
            put(WidgetDropTargetPattern, Group.ControlPattern);
            put(WidgetExpandCollapsePattern, Group.ControlPattern);
            put(WidgetGridItemPattern, Group.ControlPattern);
            put(WidgetGridPattern, Group.ControlPattern);
            put(WidgetInvokePattern, Group.ControlPattern);
            put(WidgetItemContainerPattern, Group.ControlPattern);
            put(WidgetLegacyIAccessiblePattern, Group.ControlPattern);
            put(WidgetMultipleViewPattern, Group.ControlPattern);
            put(WidgetObjectModelPattern, Group.ControlPattern);
            put(WidgetRangeValuePattern, Group.ControlPattern);
            put(WidgetScrollItemPattern, Group.ControlPattern);
            put(WidgetScrollPattern, Group.ControlPattern);
            put(WidgetSelectionItemPattern, Group.ControlPattern);
            put(WidgetSelectionPattern, Group.ControlPattern);
            put(WidgetSpreadsheetPattern, Group.ControlPattern);
            put(WidgetSpreadsheetItemPattern, Group.ControlPattern);
            put(WidgetStylesPattern, Group.ControlPattern);
            put(WidgetSynchronizedInputPattern, Group.ControlPattern);
            put(WidgetTableItemPattern, Group.ControlPattern);
            put(WidgetTablePattern, Group.ControlPattern);
            put(WidgetTextChildPattern, Group.ControlPattern);
            put(WidgetTextPattern, Group.ControlPattern);
            put(WidgetTextPattern2, Group.ControlPattern);
            put(WidgetTogglePattern, Group.ControlPattern);
            put(WidgetTransformPattern, Group.ControlPattern);
            put(WidgetTransformPattern2, Group.ControlPattern);
            put(WidgetValuePattern, Group.ControlPattern);
            put(WidgetVirtualizedItemPattern, Group.ControlPattern);
            put(WidgetWindowPattern, Group.ControlPattern);
        }
    };

    /**
     * This method will return the tag group for a given state management tag
     * @param tag state management tag
     * @return
     */
    public static Group getTagGroup(Tag<?> tag) {
        return tagGroupMap.getOrDefault(tag, Group.General);
    }

    // we need a mapping from a control pattern availability tag to its children
    private static Map<Tag<?>, Set<Tag<?>>> controlPatternChildMapping = new HashMap<Tag<?>, Set<Tag<?>>>() {
        {
            ////////// PATTERN AVAILABILITY PROPERTIES ////////////
            put(WidgetAnnotationPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetAnnotationAnnotationTypeId);
                    add(WidgetAnnotationAnnotationTypeName);
                    add(WidgetAnnotationAuthor);
                    add(WidgetAnnotationDateTime);
                    add(WidgetAnnotationTarget);
                }
            });
            put(WidgetDockPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetDockDockPosition);
                }
            });
            put(WidgetDragPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetDragDropEffect);
                    add(WidgetDragDropEffects);
                    add(WidgetDragIsGrabbed);
                    add(WidgetDragGrabbedItems);
                }
            });
            put(WidgetDropTargetPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetDropTargetDropTargetEffect);
                    add(WidgetDropTargetDropTargetEffects);
                }
            });
            put(WidgetExpandCollapsePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetExpandCollapseExpandCollapseState);
                }
            });
            put(WidgetGridItemPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetGridItemColumn);
                    add(WidgetGridItemColumnSpan);
                    add(WidgetGridItemContainingGrid);
                    add(WidgetGridItemRow);
                    add(WidgetGridItemRowSpan);
                }
            });
            put(WidgetGridPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetGridColumnCount);
                    add(WidgetGridRowCount);
                }
            });
            put(WidgetInvokePattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetItemContainerPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetLegacyIAccessiblePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetLegacyIAccessibleChildId);
                    add(WidgetLegacyIAccessibleDefaultAction);
                    add(WidgetLegacyIAccessibleDescription);
                    add(WidgetLegacyIAccessibleHelp);
                    add(WidgetLegacyIAccessibleKeyboardShortcut);
                    add(WidgetLegacyIAccessibleName);
                    add(WidgetLegacyIAccessibleRole);
                    add(WidgetLegacyIAccessibleSelection);
                    add(WidgetLegacyIAccessibleState);
                    add(WidgetLegacyIAccessibleValue);
                }
            });
            put(WidgetMultipleViewPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetMultipleViewCurrentView);
                    add(WidgetMultipleViewSupportedViews );
                }
            });
            put(WidgetObjectModelPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetRangeValuePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetRangeValueIsReadOnly);
                    add(WidgetRangeValueLargeChange);
                    add(WidgetRangeValueMaximum);
                    add(WidgetRangeValueMinimum);
                    add(WidgetRangeValueSmallChange);
                    add(WidgetRangeValueValue);
                }
            });
            put(WidgetScrollItemPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetScrollPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetHorizontallyScrollable);
                    add(WidgetVerticallyScrollable);
                    add(WidgetScrollHorizontalViewSize);
                    add(WidgetScrollVerticalViewSize);
                    add(WidgetScrollHorizontalPercent);
                    add(WidgetScrollVerticalPercent);
                }
            });
            put(WidgetSelectionItemPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetSelectionItemIsSelected);
                    add(WidgetSelectionItemSelectionContainer);
                }
            });
            put(WidgetSelectionPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetSelectionCanSelectMultiple);
                    add(WidgetSelectionIsSelectionRequired);
                    add(WidgetSelectionSelection);
                }
            });
            put(WidgetSpreadsheetPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetSpreadsheetItemPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetSpreadsheetItemFormula);
                    add(WidgetSpreadsheetItemAnnotationObjects);
                    add(WidgetSpreadsheetItemAnnotationTypes);
                }
            });
            put(WidgetStylesPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetStylesExtendedProperties);
                    add(WidgetStylesFillColor);
                    add(WidgetStylesFillPatternColor);
                    add(WidgetStylesFillPatternStyle);
                    add(WidgetStylesShape);
                    add(WidgetStylesStyleId);
                    add(WidgetStylesStyleName);
                }
            });
            put(WidgetSynchronizedInputPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetTableItemPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetTableItemColumnHeaderItems);
                    add(WidgetTableItemRowHeaderItems);
                }
            });
            put(WidgetTablePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetTableColumnHeaders);
                    add(WidgetTableRowHeaders);
                    add(WidgetTableRowOrColumnMajor);
                }
            });
            put(WidgetTextChildPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetTextPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetTextPattern2, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetTogglePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetToggleToggleState);
                }
            });
            put(WidgetTransformPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetTransformCanMove);
                    add(WidgetTransformCanResize);
                    add(WidgetTransformCanRotate);
                }
            });
            put(WidgetTransformPattern2, new HashSet<Tag<?>>() {
                {
                    add(WidgetTransform2CanZoom);
                    add(WidgetTransform2ZoomLevel);
                    add(WidgetTransform2ZoomMaximum);
                    add(WidgetTransform2ZoomMinimum);
                }
            });
            put(WidgetValuePattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetValueIsReadOnly);
                    add(WidgetValueValue);
                }
            });
            put(WidgetVirtualizedItemPattern, new HashSet<Tag<?>>() {
                {

                }
            });
            put(WidgetWindowPattern, new HashSet<Tag<?>>() {
                {
                    add(WidgetWindowCanMaximize);
                    add(WidgetWindowCanMinimize);
                    add(WidgetWindowIsModal);
                    add(WidgetWindowIsTopmost);
                    add(WidgetWindowWindowInteractionState);
                    add(WidgetWindowWindowVisualState);
                }
            });
        }
    };

    /**
     * This method returns the child tags for a given pattern tag
     * @param patternTag
     * @return
     */
    public static Set<Tag<?>> getChildTags(Tag<?> patternTag) {
        return controlPatternChildMapping.getOrDefault(patternTag, new HashSet<>());
    }

    // a mapping from the state management tags back to the normal systems tags.
    private static Map<Tag<?>, Tag<?>> tagMappingOther = new HashMap<Tag<?>, Tag<?>>() {
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
