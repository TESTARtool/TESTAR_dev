package org.fruit.alayer.windows;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.HashMap;
import java.util.Map;

import static es.upv.staq.testar.StateManagementTags.*;
import static org.fruit.alayer.windows.UIATags.*;

public class UIAMapping {

    // a mapping from the state management tags to windows automation tags
    private static Map<Tag<?>, Tag<?>> stateTagMappingWindows = new HashMap<Tag<?>, Tag<?>>()
    {
        {
            put(WidgetControlType, UIAControlType);
            put(WidgetWindowHandle, UIANativeWindowHandle);
            put(WidgetIsEnabled, UIAIsEnabled);
            put(WidgetTitle, UIAName);
            put(WidgetHelpText, UIAHelpText);
            put(WidgetAutomationId, UIAAutomationId);
            put(WidgetClassName, UIAClassName);
            put(WidgetFrameworkId, UIAFrameworkId);
            put(WidgetOrientationId, UIAOrientation);
            put(WidgetIsContentElement, UIAIsContentElement);
            put(WidgetIsControlElement, UIAIsControlElement);
            put(WidgetHasKeyboardFocus, UIAHasKeyboardFocus);
            put(WidgetIsKeyboardFocusable, UIAIsKeyboardFocusable);
            put(WidgetItemType, UIAItemType);
            put(WidgetItemStatus, UIAItemStatus);
            put(WidgetPath, Tags.Path);
            put(WidgetBoundary, UIABoundingRectangle);
            put(WidgetIsOffscreen, UIAIsOffscreen);
            put(WidgetAccelatorKey, UIAAcceleratorKey);
            put(WidgetAccessKey, UIAAccessKey);
            put(WidgetAriaProperties, UIAAriaProperties);
            put(WidgetAriaRole, UIAAriaRole);
            put(WidgetIsDialog, UIAIsDialog);
            put(WidgetIsPassword, UIAIsPassword);
            put(WidgetIsPeripheral, UIAIsPeripheral);
            put(WidgetIsRequiredForForm, UIAIsRequiredForForm);
            put(WidgetLandmarkType, UIALandmarkType);
            put(WidgetGroupLevel, UIALevel);
            put(WidgetLiveSetting, UIALiveSetting);
            put(WidgetSetPosition, UIAPositionInSet);
            put(WidgetSetSize, UIASizeOfSet);
            put(WidgetRotation, UIARotation);

            // patterns
            put(WidgetAnnotationPattern, UIAIsAnnotationPatternAvailable);
            put(WidgetDockPattern, UIAIsDockPatternAvailable);
            put(WidgetDragPattern, UIAIsDragPatternAvailable);
            put(WidgetDropTargetPattern, UIAIsDropTargetPatternAvailable);
            put(WidgetExpandCollapsePattern, UIAIsExpandCollapsePatternAvailable);
            put(WidgetGridItemPattern, UIAIsGridItemPatternAvailable);
            put(WidgetGridPattern, UIAIsGridPatternAvailable);
            put(WidgetInvokePattern, UIAIsInvokePatternAvailable);
            put(WidgetItemContainerPattern, UIAIsItemContainerPatternAvailable);
            put(WidgetLegacyIAccessiblePattern, UIAIsLegacyIAccessiblePatternAvailable);
            put(WidgetMultipleViewPattern, UIAIsMultipleViewPatternAvailable);
            put(WidgetObjectModelPattern, UIAIsObjectModelPatternAvailable);
            put(WidgetRangeValuePattern, UIAIsRangeValuePatternAvailable);
            put(WidgetScrollItemPattern, UIAIsScrollItemPatternAvailable);
            put(WidgetScrollPattern, UIAIsScrollPatternAvailable);
            put(WidgetSelectionItemPattern, UIAIsSelectionItemPatternAvailable);
            put(WidgetSelectionPattern, UIAIsSelectionPatternAvailable);
            put(WidgetSpreadsheetPattern, UIAIsSpreadsheetPatternAvailable);
            put(WidgetSpreadsheetItemPattern, UIAIsSpreadsheetItemPatternAvailable);
            put(WidgetStylesPattern, UIAIsStylesPatternAvailable);
            put(WidgetSynchronizedInputPattern, UIAIsSynchronizedInputPatternAvailable);
            put(WidgetTableItemPattern, UIAIsTableItemPatternAvailable);
            put(WidgetTablePattern, UIAIsTablePatternAvailable);
            put(WidgetTextChildPattern, UIAIsTextChildPatternAvailable);
            put(WidgetTextPattern, UIAIsTextPatternAvailable);
            put(WidgetTextPattern2, UIAIsTextPattern2Available);
            put(WidgetTogglePattern, UIAIsTogglePatternAvailable);
            put(WidgetTransformPattern, UIAIsTransformPatternAvailable);
            put(WidgetTransformPattern2, UIAIsTransformPattern2Available);
            put(WidgetValuePattern, UIAIsValuePatternAvailable);
            put(WidgetVirtualizedItemPattern, UIAIsVirtualizedItemPatternAvailable);
            put(WidgetWindowPattern, UIAIsWindowPatternAvailable);

            // control pattern properties
            put(WidgetAnnotationAnnotationTypeId, UIAAnnotationAnnotationTypeId);
            put(WidgetAnnotationAnnotationTypeName, UIAAnnotationAnnotationTypeName);
            put(WidgetAnnotationAuthor, UIAAnnotationAuthor);
            put(WidgetAnnotationDateTime, UIAAnnotationDateTime);
            put(WidgetAnnotationTarget, UIAAnnotationTarget);
            put(WidgetDockDockPosition, UIADockDockPosition ); // check
            put(WidgetDragDropEffect, UIADragDropEffect);
            put(WidgetDragDropEffects, UIADragDropEffects); // array
            put(WidgetDragIsGrabbed, UIADragIsGrabbed);
            put(WidgetDragGrabbedItems, UIADragGrabbedItems); // array
            put(WidgetDropTargetDropTargetEffect, UIADropTargetDropTargetEffect);
            put(WidgetDropTargetDropTargetEffects, UIADropTargetDropTargetEffects); // array
            put(WidgetExpandCollapseExpandCollapseState, UIAExpandCollapseExpandCollapseState);
            put(WidgetGridColumnCount, UIAGridColumnCount);
            put(WidgetGridRowCount, UIAGridRowCount);
            put(WidgetGridItemColumn, UIAGridItemColumn);
            put(WidgetGridItemColumnSpan, UIAGridItemColumnSpan);
            put(WidgetGridItemContainingGrid, UIAGridItemContainingGrid);
            put(WidgetGridItemRow, UIAGridItemRow);
            put(WidgetGridItemRowSpan, UIAGridItemRowSpan);
            put(WidgetLegacyIAccessibleChildId, UIALegacyIAccessibleChildId);
            put(WidgetLegacyIAccessibleDefaultAction, UIALegacyIAccessibleDefaultAction);
            put(WidgetLegacyIAccessibleDescription, UIALegacyIAccessibleDescription);
            put(WidgetLegacyIAccessibleHelp, UIALegacyIAccessibleHelp);
            put(WidgetLegacyIAccessibleKeyboardShortcut, UIALegacyIAccessibleKeyboardShortcut);
            put(WidgetLegacyIAccessibleName, UIALegacyIAccessibleName);
            put(WidgetLegacyIAccessibleRole, UIALegacyIAccessibleRole);
            put(WidgetLegacyIAccessibleSelection, UIALegacyIAccessibleSelection); // list/array
            put(WidgetLegacyIAccessibleState, UIALegacyIAccessibleState);
            put(WidgetLegacyIAccessibleValue, UIALegacyIAccessibleValue);
            put(WidgetMultipleViewCurrentView, UIAMultipleViewCurrentView);
            put(WidgetMultipleViewSupportedViews, UIAMultipleViewSupportedViews); // array
            put(WidgetRangeValueIsReadOnly, UIARangeValueIsReadOnly);
            put(WidgetRangeValueLargeChange, UIARangeValueLargeChange);
            put(WidgetRangeValueMaximum, UIARangeValueMaximum);
            put(WidgetRangeValueMinimum, UIARangeValueMinimum);
            put(WidgetRangeValueSmallChange, UIARangeValueSmallChange);
            put(WidgetRangeValueValue, UIARangeValueValue);
            put(WidgetSelectionCanSelectMultiple, UIASelectionCanSelectMultiple);
            put(WidgetSelectionIsSelectionRequired, UIASelectionIsSelectionRequired);
            put(WidgetSelectionSelection, UIASelectionSelection); // array
            put(WidgetSelectionItemIsSelected, UIASelectionItemIsSelected);
            put(WidgetSelectionItemSelectionContainer, UIASelectionItemSelectionContainer);
            put(WidgetSpreadsheetItemFormula, UIASpreadsheetItemFormula);
            put(WidgetSpreadsheetItemAnnotationObjects, UIASpreadsheetItemAnnotationObjects); //array
            put(WidgetSpreadsheetItemAnnotationTypes, UIASpreadsheetItemAnnotationTypes); // array
            put(WidgetHorizontallyScrollable, UIAHorizontallyScrollable);
            put(WidgetVerticallyScrollable, UIAVerticallyScrollable);
            put(WidgetScrollHorizontalViewSize, UIAScrollHorizontalViewSize);
            put(WidgetScrollVerticalViewSize, UIAScrollVerticalViewSize);
            put(WidgetScrollHorizontalPercent, UIAScrollHorizontalPercent);
            put(WidgetScrollVerticalPercent, UIAScrollVerticalPercent);
            put(WidgetStylesExtendedProperties, UIAStylesExtendedProperties);
            put(WidgetStylesFillColor, UIAStylesFillColor);
            put(WidgetStylesFillPatternColor, UIAStylesFillPatternColor);
            put(WidgetStylesFillPatternStyle, UIAStylesFillPatternStyle);
            put(WidgetStylesShape, UIAStylesShape);
            put(WidgetStylesStyleId, UIAStylesStyleId);
            put(WidgetStylesStyleName, UIAStylesStyleName);
            put(WidgetTableColumnHeaders, UIATableColumnHeaders); // array
            put(WidgetTableRowHeaders, UIATableRowHeaders); // array
            put(WidgetTableRowOrColumnMajor, UIATableRowOrColumnMajor);
            put(WidgetTableItemColumnHeaderItems, UIATableItemColumnHeaderItems); // array
            put(WidgetTableItemRowHeaderItems, UIATableItemRowHeaderItems); //array
            put(WidgetToggleToggleState, UIAToggleToggleState);
            put(WidgetTransformCanMove, UIATransformCanMove);
            put(WidgetTransformCanResize, UIATransformCanResize);
            put(WidgetTransformCanRotate, UIATransformCanRotate);
            put(WidgetTransform2CanZoom, UIATransform2CanZoom);
            put(WidgetTransform2ZoomLevel, UIATransform2ZoomLevel);
            put(WidgetTransform2ZoomMaximum, UIATransform2ZoomMaximum);
            put(WidgetTransform2ZoomMinimum, UIATransform2ZoomMinimum);
            put(WidgetValueIsReadOnly, UIAValueIsReadOnly);
            put(WidgetValueValue, UIAValueValue);
            put(WidgetWindowCanMaximize, UIAWindowCanMaximize);
            put(WidgetWindowCanMinimize, UIAWindowCanMinimize);
            put(WidgetWindowIsModal, UIAWindowIsModal);
            put(WidgetWindowIsTopmost, UIAWindowIsTopmost);
            put(WidgetWindowWindowInteractionState, UIAWindowWindowInteractionState); // check
            put(WidgetWindowWindowVisualState, UIAWindowWindowVisualState); // check


        }
    };

    /**
     * This method will return its equivalent, internal UIA tag, if available.
     * @param mappedTag
     * @return
     */
    public static <T> Tag<T> getMappedStateTag(Tag<T> mappedTag) {
        return (Tag<T>) stateTagMappingWindows.getOrDefault(mappedTag, null);
    }

    // a mapping from tags to their UIA id and vice versa
    private static BiMap<Long, Tag<?>> patternPropertyMapping = HashBiMap.create();

    static {
        patternPropertyMapping.put(Windows.UIA_IsAnnotationPatternAvailablePropertyId, UIATags.UIAIsAnnotationPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsDockPatternAvailablePropertyId, UIATags.UIAIsDockPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsDragPatternAvailablePropertyId, UIATags.UIAIsDragPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsDropTargetPatternAvailablePropertyId, UIATags.UIAIsDropTargetPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsExpandCollapsePatternAvailablePropertyId, UIATags.UIAIsExpandCollapsePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsGridItemPatternAvailablePropertyId, UIATags.UIAIsGridItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsGridPatternAvailablePropertyId, UIATags.UIAIsGridPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsInvokePatternAvailablePropertyId, UIATags.UIAIsInvokePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsItemContainerPatternAvailablePropertyId, UIATags.UIAIsItemContainerPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsLegacyIAccessiblePatternAvailablePropertyId, UIATags.UIAIsLegacyIAccessiblePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsMultipleViewPatternAvailablePropertyId, UIATags.UIAIsMultipleViewPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsObjectModelPatternAvailablePropertyId, UIATags.UIAIsObjectModelPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsRangeValuePatternAvailablePropertyId, UIATags.UIAIsRangeValuePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsScrollItemPatternAvailablePropertyId, UIATags.UIAIsScrollItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsScrollPatternAvailablePropertyId, UIATags.UIAIsScrollPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsSelectionItemPatternAvailablePropertyId, UIATags.UIAIsSelectionItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsSelectionPatternAvailablePropertyId, UIATags.UIAIsSelectionPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsSpreadsheetPatternAvailablePropertyId, UIATags.UIAIsSpreadsheetPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsSpreadsheetItemPatternAvailablePropertyId, UIATags.UIAIsSpreadsheetItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsStylesPatternAvailablePropertyId, UIATags.UIAIsStylesPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsSynchronizedInputPatternAvailablePropertyId, UIATags.UIAIsSynchronizedInputPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTableItemPatternAvailablePropertyId, UIATags.UIAIsTableItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTablePatternAvailablePropertyId, UIATags.UIAIsTablePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTextChildPatternAvailablePropertyId, UIATags.UIAIsTextChildPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTextPatternAvailablePropertyId, UIATags.UIAIsTextPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTextPattern2AvailablePropertyId, UIATags.UIAIsTextPattern2Available);
        patternPropertyMapping.put(Windows.UIA_IsTogglePatternAvailablePropertyId, UIATags.UIAIsTogglePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTransformPatternAvailablePropertyId, UIATags.UIAIsTransformPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsTransformPattern2AvailablePropertyId, UIATags.UIAIsTransformPattern2Available);
        patternPropertyMapping.put(Windows.UIA_IsValuePatternAvailablePropertyId, UIATags.UIAIsValuePatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsVirtualizedItemPatternAvailablePropertyId, UIATags.UIAIsVirtualizedItemPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_IsWindowPatternAvailablePropertyId, UIATags.UIAIsWindowPatternAvailable);
        patternPropertyMapping.put(Windows.UIA_AnnotationAnnotationTypeIdPropertyId, UIATags.UIAAnnotationAnnotationTypeId);
        patternPropertyMapping.put(Windows.UIA_AnnotationAnnotationTypeNamePropertyId, UIATags.UIAAnnotationAnnotationTypeName);
        patternPropertyMapping.put(Windows.UIA_AnnotationAuthorPropertyId, UIATags.UIAAnnotationAuthor);
        patternPropertyMapping.put(Windows.UIA_AnnotationDateTimePropertyId, UIATags.UIAAnnotationDateTime);
        patternPropertyMapping.put(Windows.UIA_AnnotationTargetPropertyId, UIATags.UIAAnnotationTarget);
        patternPropertyMapping.put(Windows.UIA_DockDockPositionPropertyId, UIATags.UIADockDockPosition);
        patternPropertyMapping.put(Windows.UIA_DragDropEffectPropertyId, UIATags.UIADragDropEffect);
        patternPropertyMapping.put(Windows.UIA_DragDropEffectsPropertyId, UIATags.UIADragDropEffects);
        patternPropertyMapping.put(Windows.UIA_DragIsGrabbedPropertyId, UIATags.UIADragIsGrabbed);
        patternPropertyMapping.put(Windows.UIA_DragGrabbedItemsPropertyId, UIATags.UIADragGrabbedItems);
        patternPropertyMapping.put(Windows.UIA_DropTargetDropTargetEffectPropertyId, UIATags.UIADropTargetDropTargetEffect);
        patternPropertyMapping.put(Windows.UIA_DropTargetDropTargetEffectsPropertyId, UIATags.UIADropTargetDropTargetEffects);
        patternPropertyMapping.put(Windows.UIA_ExpandCollapseExpandCollapseStatePropertyId, UIATags.UIAExpandCollapseExpandCollapseState);
        patternPropertyMapping.put(Windows.UIA_GridColumnCountPropertyId, UIATags.UIAGridColumnCount);
        patternPropertyMapping.put(Windows.UIA_GridItemColumnPropertyId, UIATags.UIAGridItemColumn);
        patternPropertyMapping.put(Windows.UIA_GridItemColumnSpanPropertyId, UIATags.UIAGridItemColumnSpan);
        patternPropertyMapping.put(Windows.UIA_GridItemContainingGridPropertyId, UIATags.UIAGridItemContainingGrid);
        patternPropertyMapping.put(Windows.UIA_GridItemRowPropertyId, UIATags.UIAGridItemRow);
        patternPropertyMapping.put(Windows.UIA_GridItemRowSpanPropertyId, UIATags.UIAGridItemRowSpan);
        patternPropertyMapping.put(Windows.UIA_GridRowCountPropertyId, UIATags.UIAGridRowCount);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleChildIdPropertyId, UIATags.UIALegacyIAccessibleChildId);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleDefaultActionPropertyId, UIATags.UIALegacyIAccessibleDefaultAction);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleDescriptionPropertyId, UIATags.UIALegacyIAccessibleDescription);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleHelpPropertyId, UIATags.UIALegacyIAccessibleHelp);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleKeyboardShortcutPropertyId, UIATags.UIALegacyIAccessibleKeyboardShortcut);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleNamePropertyId, UIATags.UIALegacyIAccessibleName);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleRolePropertyId, UIATags.UIALegacyIAccessibleRole);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleSelectionPropertyId, UIATags.UIALegacyIAccessibleSelection);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleStatePropertyId, UIATags.UIALegacyIAccessibleState);
        patternPropertyMapping.put(Windows.UIA_LegacyIAccessibleValuePropertyId, UIATags.UIALegacyIAccessibleValue);
        patternPropertyMapping.put(Windows.UIA_MultipleViewCurrentViewPropertyId, UIATags.UIAMultipleViewCurrentView);
        patternPropertyMapping.put(Windows.UIA_MultipleViewSupportedViewsPropertyId, UIATags.UIAMultipleViewSupportedViews);
        patternPropertyMapping.put(Windows.UIA_RangeValueIsReadOnlyPropertyId, UIATags.UIARangeValueIsReadOnly);
        patternPropertyMapping.put(Windows.UIA_RangeValueLargeChangePropertyId, UIATags.UIARangeValueLargeChange);
        patternPropertyMapping.put(Windows.UIA_RangeValueMaximumPropertyId, UIATags.UIARangeValueMaximum);
        patternPropertyMapping.put(Windows.UIA_RangeValueMinimumPropertyId, UIATags.UIARangeValueMinimum);
        patternPropertyMapping.put(Windows.UIA_RangeValueSmallChangePropertyId, UIATags.UIARangeValueSmallChange);
        patternPropertyMapping.put(Windows.UIA_RangeValueValuePropertyId, UIATags.UIARangeValueValue);
        patternPropertyMapping.put(Windows.UIA_ScrollHorizontallyScrollablePropertyId, UIATags.UIAHorizontallyScrollable);
        patternPropertyMapping.put(Windows.UIA_ScrollHorizontalScrollPercentPropertyId, UIATags.UIAScrollHorizontalPercent);
        patternPropertyMapping.put(Windows.UIA_ScrollHorizontalViewSizePropertyId, UIATags.UIAScrollHorizontalViewSize);
        patternPropertyMapping.put(Windows.UIA_ScrollVerticallyScrollablePropertyId, UIATags.UIAVerticallyScrollable);
        patternPropertyMapping.put(Windows.UIA_ScrollVerticalScrollPercentPropertyId, UIATags.UIAScrollVerticalPercent);
        patternPropertyMapping.put(Windows.UIA_ScrollVerticalViewSizePropertyId, UIATags.UIAScrollVerticalViewSize);
        patternPropertyMapping.put(Windows.UIA_SelectionCanSelectMultiplePropertyId, UIATags.UIASelectionCanSelectMultiple);
        patternPropertyMapping.put(Windows.UIA_SelectionIsSelectionRequiredPropertyId, UIATags.UIASelectionIsSelectionRequired);
        patternPropertyMapping.put(Windows.UIA_SelectionSelectionPropertyId, UIATags.UIASelectionSelection);
        patternPropertyMapping.put(Windows.UIA_SelectionItemIsSelectedPropertyId, UIATags.UIASelectionItemIsSelected);
        patternPropertyMapping.put(Windows.UIA_SelectionItemSelectionContainerPropertyId, UIATags.UIASelectionItemSelectionContainer);
        patternPropertyMapping.put(Windows.UIA_SpreadsheetItemFormulaPropertyId, UIATags.UIASpreadsheetItemFormula);
        patternPropertyMapping.put(Windows.UIA_SpreadsheetItemAnnotationObjectsPropertyId, UIATags.UIASpreadsheetItemAnnotationObjects);
        patternPropertyMapping.put(Windows.UIA_SpreadsheetItemAnnotationTypesPropertyId, UIATags.UIASpreadsheetItemAnnotationTypes);
        patternPropertyMapping.put(Windows.UIA_StylesExtendedPropertiesPropertyId, UIATags.UIAStylesExtendedProperties);
        patternPropertyMapping.put(Windows.UIA_StylesFillColorPropertyId, UIATags.UIAStylesFillColor);
        patternPropertyMapping.put(Windows.UIA_StylesFillPatternColorPropertyId, UIATags.UIAStylesFillPatternColor);
        patternPropertyMapping.put(Windows.UIA_StylesFillPatternStylePropertyId, UIATags.UIAStylesFillPatternStyle);
        patternPropertyMapping.put(Windows.UIA_StylesShapePropertyId, UIATags.UIAStylesShape);
        patternPropertyMapping.put(Windows.UIA_StylesStyleIdPropertyId, UIATags.UIAStylesStyleId);
        patternPropertyMapping.put(Windows.UIA_StylesStyleNamePropertyId, UIATags.UIAStylesStyleName);
        patternPropertyMapping.put(Windows.UIA_TableColumnHeadersPropertyId, UIATags.UIATableColumnHeaders);
        patternPropertyMapping.put(Windows.UIA_TableItemColumnHeaderItemsPropertyId, UIATags.UIATableItemColumnHeaderItems);
        patternPropertyMapping.put(Windows.UIA_TableRowHeadersPropertyId, UIATags.UIATableRowHeaders);
        patternPropertyMapping.put(Windows.UIA_TableRowOrColumnMajorPropertyId, UIATags.UIATableRowOrColumnMajor);
        patternPropertyMapping.put(Windows.UIA_TableItemRowHeaderItemsPropertyId, UIATags.UIATableItemRowHeaderItems);
        patternPropertyMapping.put(Windows.UIA_ToggleToggleStatePropertyId, UIATags.UIAToggleToggleState);
        patternPropertyMapping.put(Windows.UIA_TransformCanMovePropertyId, UIATags.UIATransformCanMove);
        patternPropertyMapping.put(Windows.UIA_TransformCanResizePropertyId, UIATags.UIATransformCanResize);
        patternPropertyMapping.put(Windows.UIA_TransformCanRotatePropertyId, UIATags.UIATransformCanRotate);
        patternPropertyMapping.put(Windows.UIA_Transform2CanZoomPropertyId, UIATags.UIATransform2CanZoom);
        patternPropertyMapping.put(Windows.UIA_Transform2ZoomLevelPropertyId, UIATags.UIATransform2ZoomLevel);
        patternPropertyMapping.put(Windows.UIA_Transform2ZoomMaximumPropertyId, UIATags.UIATransform2ZoomMaximum);
        patternPropertyMapping.put(Windows.UIA_Transform2ZoomMinimumPropertyId, UIATags.UIATransform2ZoomMinimum);
        patternPropertyMapping.put(Windows.UIA_ValueIsReadOnlyPropertyId, UIATags.UIAValueIsReadOnly);
        patternPropertyMapping.put(Windows.UIA_ValueValuePropertyId, UIATags.UIAValueValue);
        patternPropertyMapping.put(Windows.UIA_WindowCanMaximizePropertyId, UIATags.UIAWindowCanMaximize);
        patternPropertyMapping.put(Windows.UIA_WindowCanMinimizePropertyId, UIATags.UIAWindowCanMinimize);
        patternPropertyMapping.put(Windows.UIA_WindowIsModalPropertyId, UIATags.UIAWindowIsModal);
        patternPropertyMapping.put(Windows.UIA_WindowIsTopmostPropertyId, UIATags.UIAWindowIsTopmost);
        patternPropertyMapping.put(Windows.UIA_WindowWindowInteractionStatePropertyId, UIATags.UIAWindowWindowInteractionState);
        patternPropertyMapping.put(Windows.UIA_WindowWindowVisualStatePropertyId, UIATags.UIAWindowWindowVisualState);
    }

    public static long getPatternPropertyIdentifier(Tag<?> patternPropertyTag) {
        return patternPropertyMapping.inverse().getOrDefault(patternPropertyTag, null);
    }

}
