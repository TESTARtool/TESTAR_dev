package org.fruit.alayer.windows;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.fruit.alayer.Tag;

import java.util.Set;

public class UIAMapping {

    private static BiMap<Long, Tag<Boolean>> patternAvailabilityMapping = HashBiMap.create();

    static {
            patternAvailabilityMapping.put(Windows.UIA_IsAnnotationPatternAvailablePropertyId, UIATags.UIAIsAnnotationPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsDockPatternAvailablePropertyId, UIATags.UIAIsDockPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsDragPatternAvailablePropertyId, UIATags.UIAIsDragPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsDropTargetPatternAvailablePropertyId, UIATags.UIAIsDropTargetPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsExpandCollapsePatternAvailablePropertyId, UIATags.UIAIsExpandCollapsePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsGridItemPatternAvailablePropertyId, UIATags.UIAIsGridItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsGridPatternAvailablePropertyId, UIATags.UIAIsGridPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsInvokePatternAvailablePropertyId, UIATags.UIAIsInvokePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsItemContainerPatternAvailablePropertyId, UIATags.UIAIsItemContainerPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsLegacyIAccessiblePatternAvailablePropertyId, UIATags.UIAIsLegacyIAccessiblePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsMultipleViewPatternAvailablePropertyId, UIATags.UIAIsMultipleViewPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsObjectModelPatternAvailablePropertyId, UIATags.UIAIsObjectModelPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsRangeValuePatternAvailablePropertyId, UIATags.UIAIsRangeValuePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsScrollItemPatternAvailablePropertyId, UIATags.UIAIsScrollItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsScrollPatternAvailablePropertyId, UIATags.UIAIsScrollPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsSelectionItemPatternAvailablePropertyId, UIATags.UIAIsSelectionItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsSelectionPatternAvailablePropertyId, UIATags.UIAIsSelectionPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsSpreadsheetPatternAvailablePropertyId, UIATags.UIAIsSpreadsheetPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsSpreadsheetItemPatternAvailablePropertyId, UIATags.UIAIsSpreadsheetItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsStylesPatternAvailablePropertyId, UIATags.UIAIsStylesPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsSynchronizedInputPatternAvailablePropertyId, UIATags.UIAIsSynchronizedInputPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTableItemPatternAvailablePropertyId, UIATags.UIAIsTableItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTablePatternAvailablePropertyId, UIATags.UIAIsTablePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTextChildPatternAvailablePropertyId, UIATags.UIAIsTextChildPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTextPatternAvailablePropertyId, UIATags.UIAIsTextPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTextPattern2AvailablePropertyId, UIATags.UIAIsTextPattern2Available);
            patternAvailabilityMapping.put(Windows.UIA_IsTogglePatternAvailablePropertyId, UIATags.UIAIsTogglePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTransformPatternAvailablePropertyId, UIATags.UIAIsTransformPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsTransformPattern2AvailablePropertyId, UIATags.UIAIsTransformPattern2Available);
            patternAvailabilityMapping.put(Windows.UIA_IsValuePatternAvailablePropertyId, UIATags.UIAIsValuePatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsVirtualizedItemPatternAvailablePropertyId, UIATags.UIAIsVirtualizedItemPatternAvailable);
            patternAvailabilityMapping.put(Windows.UIA_IsWindowPatternAvailablePropertyId, UIATags.UIAIsWindowPatternAvailable);
    }

    public static Tag<Boolean> getMappedPatternAvailabilityTag(long propertyId) {
        return patternAvailabilityMapping.getOrDefault(propertyId, null);
    }

    public static long getPropertyIdForAvailabityTag(Tag<Boolean> tag) {
        return patternAvailabilityMapping.inverse().getOrDefault(tag, null);
    }

    public static Set<Long> getPatternPropertyIds() {
        return patternAvailabilityMapping.keySet();
    }

    public static Set<Tag<Boolean>> getPatternAvailabilityTags() {
        return patternAvailabilityMapping.inverse().keySet();
    }

    private static BiMap<Long, Tag<?>> patternPropertyMapping = HashBiMap.create();

    static {
        patternPropertyMapping.put(Windows.UIA_AnnotationAnnotationTypeIdPropertyId, UIATags.UIAAnnotationAnnotationTypeId);
        patternPropertyMapping.put(Windows.UIA_AnnotationAnnotationTypeNamePropertyId, UIATags.UIAAnnotationAnnotationTypeName);
        patternPropertyMapping.put(Windows.UIA_AnnotationAuthorPropertyId, UIATags.UIAAnnotationAuthor);
        patternPropertyMapping.put(Windows.UIA_AnnotationDateTimePropertyId, UIATags.UIAAnnotationDateTime);
        patternPropertyMapping.put(Windows.UIA_AnnotationTargetPropertyId, UIATags.UIAnnotationTarget);
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
