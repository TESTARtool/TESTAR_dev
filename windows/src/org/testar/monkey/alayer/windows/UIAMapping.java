/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2020 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.windows;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

import java.util.HashMap;
import java.util.Map;

import static org.testar.StateManagementTags.*;

public class UIAMapping {

    // a mapping from the state management tags to windows automation tags
    private static Map<Tag<?>, Tag<?>> stateTagMappingWindows = new HashMap<Tag<?>, Tag<?>>()
    {
        {
            put(WidgetControlType, UIATags.UIAControlType);
            put(WidgetWindowHandle, UIATags.UIANativeWindowHandle);
            put(WidgetIsEnabled, UIATags.UIAIsEnabled);
            put(WidgetTitle, UIATags.UIAName);
            put(WidgetHelpText, UIATags.UIAHelpText);
            put(WidgetAutomationId, UIATags.UIAAutomationId);
            put(WidgetClassName, UIATags.UIAClassName);
            put(WidgetFrameworkId, UIATags.UIAFrameworkId);
            put(WidgetOrientationId, UIATags.UIAOrientation);
            put(WidgetIsContentElement, UIATags.UIAIsContentElement);
            put(WidgetIsControlElement, UIATags.UIAIsControlElement);
            put(WidgetHasKeyboardFocus, UIATags.UIAHasKeyboardFocus);
            put(WidgetIsKeyboardFocusable, UIATags.UIAIsKeyboardFocusable);
            put(WidgetItemType, UIATags.UIAItemType);
            put(WidgetItemStatus, UIATags.UIAItemStatus);
            put(WidgetPath, Tags.Path);
            put(WidgetBoundary, UIATags.UIABoundingRectangle);
            put(WidgetIsOffscreen, UIATags.UIAIsOffscreen);
            put(WidgetAccelatorKey, UIATags.UIAAcceleratorKey);
            put(WidgetAccessKey, UIATags.UIAAccessKey);
            put(WidgetAriaProperties, UIATags.UIAAriaProperties);
            put(WidgetAriaRole, UIATags.UIAAriaRole);
            put(WidgetIsDialog, UIATags.UIAIsDialog);
            put(WidgetIsPassword, UIATags.UIAIsPassword);
            put(WidgetIsPeripheral, UIATags.UIAIsPeripheral);
            put(WidgetIsRequiredForForm, UIATags.UIAIsRequiredForForm);
            put(WidgetLandmarkType, UIATags.UIALandmarkType);
            put(WidgetGroupLevel, UIATags.UIALevel);
            put(WidgetLiveSetting, UIATags.UIALiveSetting);
            put(WidgetSetPosition, UIATags.UIAPositionInSet);
            put(WidgetSetSize, UIATags.UIASizeOfSet);
            put(WidgetRotation, UIATags.UIARotation);

            // patterns
            put(WidgetAnnotationPattern, UIATags.UIAIsAnnotationPatternAvailable);
            put(WidgetDockPattern, UIATags.UIAIsDockPatternAvailable);
            put(WidgetDragPattern, UIATags.UIAIsDragPatternAvailable);
            put(WidgetDropTargetPattern, UIATags.UIAIsDropTargetPatternAvailable);
            put(WidgetExpandCollapsePattern, UIATags.UIAIsExpandCollapsePatternAvailable);
            put(WidgetGridItemPattern, UIATags.UIAIsGridItemPatternAvailable);
            put(WidgetGridPattern, UIATags.UIAIsGridPatternAvailable);
            put(WidgetInvokePattern, UIATags.UIAIsInvokePatternAvailable);
            put(WidgetItemContainerPattern, UIATags.UIAIsItemContainerPatternAvailable);
            put(WidgetLegacyIAccessiblePattern, UIATags.UIAIsLegacyIAccessiblePatternAvailable);
            put(WidgetMultipleViewPattern, UIATags.UIAIsMultipleViewPatternAvailable);
            put(WidgetObjectModelPattern, UIATags.UIAIsObjectModelPatternAvailable);
            put(WidgetRangeValuePattern, UIATags.UIAIsRangeValuePatternAvailable);
            put(WidgetScrollItemPattern, UIATags.UIAIsScrollItemPatternAvailable);
            put(WidgetScrollPattern, UIATags.UIAIsScrollPatternAvailable);
            put(WidgetSelectionItemPattern, UIATags.UIAIsSelectionItemPatternAvailable);
            put(WidgetSelectionPattern, UIATags.UIAIsSelectionPatternAvailable);
            put(WidgetSpreadsheetPattern, UIATags.UIAIsSpreadsheetPatternAvailable);
            put(WidgetSpreadsheetItemPattern, UIATags.UIAIsSpreadsheetItemPatternAvailable);
            put(WidgetStylesPattern, UIATags.UIAIsStylesPatternAvailable);
            put(WidgetSynchronizedInputPattern, UIATags.UIAIsSynchronizedInputPatternAvailable);
            put(WidgetTableItemPattern, UIATags.UIAIsTableItemPatternAvailable);
            put(WidgetTablePattern, UIATags.UIAIsTablePatternAvailable);
            put(WidgetTextChildPattern, UIATags.UIAIsTextChildPatternAvailable);
            put(WidgetTextPattern, UIATags.UIAIsTextPatternAvailable);
            put(WidgetTextPattern2, UIATags.UIAIsTextPattern2Available);
            put(WidgetTogglePattern, UIATags.UIAIsTogglePatternAvailable);
            put(WidgetTransformPattern, UIATags.UIAIsTransformPatternAvailable);
            put(WidgetTransformPattern2, UIATags.UIAIsTransformPattern2Available);
            put(WidgetValuePattern, UIATags.UIAIsValuePatternAvailable);
            put(WidgetVirtualizedItemPattern, UIATags.UIAIsVirtualizedItemPatternAvailable);
            put(WidgetWindowPattern, UIATags.UIAIsWindowPatternAvailable);

            // control pattern properties
            put(WidgetAnnotationAnnotationTypeId, UIATags.UIAAnnotationAnnotationTypeId);
            put(WidgetAnnotationAnnotationTypeName, UIATags.UIAAnnotationAnnotationTypeName);
            put(WidgetAnnotationAuthor, UIATags.UIAAnnotationAuthor);
            put(WidgetAnnotationDateTime, UIATags.UIAAnnotationDateTime);
            put(WidgetAnnotationTarget, UIATags.UIAAnnotationTarget);
            put(WidgetDockDockPosition, UIATags.UIADockDockPosition ); // check
            put(WidgetDragDropEffect, UIATags.UIADragDropEffect);
            put(WidgetDragDropEffects, UIATags.UIADragDropEffects); // array
            put(WidgetDragIsGrabbed, UIATags.UIADragIsGrabbed);
            put(WidgetDragGrabbedItems, UIATags.UIADragGrabbedItems); // array
            put(WidgetDropTargetDropTargetEffect, UIATags.UIADropTargetDropTargetEffect);
            put(WidgetDropTargetDropTargetEffects, UIATags.UIADropTargetDropTargetEffects); // array
            put(WidgetExpandCollapseExpandCollapseState, UIATags.UIAExpandCollapseExpandCollapseState);
            put(WidgetGridColumnCount, UIATags.UIAGridColumnCount);
            put(WidgetGridRowCount, UIATags.UIAGridRowCount);
            put(WidgetGridItemColumn, UIATags.UIAGridItemColumn);
            put(WidgetGridItemColumnSpan, UIATags.UIAGridItemColumnSpan);
            put(WidgetGridItemContainingGrid, UIATags.UIAGridItemContainingGrid);
            put(WidgetGridItemRow, UIATags.UIAGridItemRow);
            put(WidgetGridItemRowSpan, UIATags.UIAGridItemRowSpan);
            put(WidgetLegacyIAccessibleChildId, UIATags.UIALegacyIAccessibleChildId);
            put(WidgetLegacyIAccessibleDefaultAction, UIATags.UIALegacyIAccessibleDefaultAction);
            put(WidgetLegacyIAccessibleDescription, UIATags.UIALegacyIAccessibleDescription);
            put(WidgetLegacyIAccessibleHelp, UIATags.UIALegacyIAccessibleHelp);
            put(WidgetLegacyIAccessibleKeyboardShortcut, UIATags.UIALegacyIAccessibleKeyboardShortcut);
            put(WidgetLegacyIAccessibleName, UIATags.UIALegacyIAccessibleName);
            put(WidgetLegacyIAccessibleRole, UIATags.UIALegacyIAccessibleRole);
            put(WidgetLegacyIAccessibleSelection, UIATags.UIALegacyIAccessibleSelection); // list/array
            put(WidgetLegacyIAccessibleState, UIATags.UIALegacyIAccessibleState);
            put(WidgetLegacyIAccessibleValue, UIATags.UIALegacyIAccessibleValue);
            put(WidgetMultipleViewCurrentView, UIATags.UIAMultipleViewCurrentView);
            put(WidgetMultipleViewSupportedViews, UIATags.UIAMultipleViewSupportedViews); // array
            put(WidgetRangeValueIsReadOnly, UIATags.UIARangeValueIsReadOnly);
            put(WidgetRangeValueLargeChange, UIATags.UIARangeValueLargeChange);
            put(WidgetRangeValueMaximum, UIATags.UIARangeValueMaximum);
            put(WidgetRangeValueMinimum, UIATags.UIARangeValueMinimum);
            put(WidgetRangeValueSmallChange, UIATags.UIARangeValueSmallChange);
            put(WidgetRangeValueValue, UIATags.UIARangeValueValue);
            put(WidgetSelectionCanSelectMultiple, UIATags.UIASelectionCanSelectMultiple);
            put(WidgetSelectionIsSelectionRequired, UIATags.UIASelectionIsSelectionRequired);
            put(WidgetSelectionSelection, UIATags.UIASelectionSelection); // array
            put(WidgetSelectionItemIsSelected, UIATags.UIASelectionItemIsSelected);
            put(WidgetSelectionItemSelectionContainer, UIATags.UIASelectionItemSelectionContainer);
            put(WidgetSpreadsheetItemFormula, UIATags.UIASpreadsheetItemFormula);
            put(WidgetSpreadsheetItemAnnotationObjects, UIATags.UIASpreadsheetItemAnnotationObjects); //array
            put(WidgetSpreadsheetItemAnnotationTypes, UIATags.UIASpreadsheetItemAnnotationTypes); // array
            put(WidgetHorizontallyScrollable, UIATags.UIAHorizontallyScrollable);
            put(WidgetVerticallyScrollable, UIATags.UIAVerticallyScrollable);
            put(WidgetScrollHorizontalViewSize, UIATags.UIAScrollHorizontalViewSize);
            put(WidgetScrollVerticalViewSize, UIATags.UIAScrollVerticalViewSize);
            put(WidgetScrollHorizontalPercent, UIATags.UIAScrollHorizontalPercent);
            put(WidgetScrollVerticalPercent, UIATags.UIAScrollVerticalPercent);
            put(WidgetStylesExtendedProperties, UIATags.UIAStylesExtendedProperties);
            put(WidgetStylesFillColor, UIATags.UIAStylesFillColor);
            put(WidgetStylesFillPatternColor, UIATags.UIAStylesFillPatternColor);
            put(WidgetStylesFillPatternStyle, UIATags.UIAStylesFillPatternStyle);
            put(WidgetStylesShape, UIATags.UIAStylesShape);
            put(WidgetStylesStyleId, UIATags.UIAStylesStyleId);
            put(WidgetStylesStyleName, UIATags.UIAStylesStyleName);
            put(WidgetTableColumnHeaders, UIATags.UIATableColumnHeaders); // array
            put(WidgetTableRowHeaders, UIATags.UIATableRowHeaders); // array
            put(WidgetTableRowOrColumnMajor, UIATags.UIATableRowOrColumnMajor);
            put(WidgetTableItemColumnHeaderItems, UIATags.UIATableItemColumnHeaderItems); // array
            put(WidgetTableItemRowHeaderItems, UIATags.UIATableItemRowHeaderItems); //array
            put(WidgetToggleToggleState, UIATags.UIAToggleToggleState);
            put(WidgetTransformCanMove, UIATags.UIATransformCanMove);
            put(WidgetTransformCanResize, UIATags.UIATransformCanResize);
            put(WidgetTransformCanRotate, UIATags.UIATransformCanRotate);
            put(WidgetTransform2CanZoom, UIATags.UIATransform2CanZoom);
            put(WidgetTransform2ZoomLevel, UIATags.UIATransform2ZoomLevel);
            put(WidgetTransform2ZoomMaximum, UIATags.UIATransform2ZoomMaximum);
            put(WidgetTransform2ZoomMinimum, UIATags.UIATransform2ZoomMinimum);
            put(WidgetValueIsReadOnly, UIATags.UIAValueIsReadOnly);
            put(WidgetValueValue, UIATags.UIAValueValue);
            put(WidgetWindowCanMaximize, UIATags.UIAWindowCanMaximize);
            put(WidgetWindowCanMinimize, UIATags.UIAWindowCanMinimize);
            put(WidgetWindowIsModal, UIATags.UIAWindowIsModal);
            put(WidgetWindowIsTopmost, UIATags.UIAWindowIsTopmost);
            put(WidgetWindowWindowInteractionState, UIATags.UIAWindowWindowInteractionState); // check
            put(WidgetWindowWindowVisualState, UIATags.UIAWindowWindowVisualState); // check


        }
    };

    /**
     * This method will return its equivalent, internal UIA tag, if available.
     * @param mappedTag
     * @return
     */
    @SuppressWarnings("unchecked")
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
