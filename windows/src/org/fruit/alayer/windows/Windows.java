/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Provides access to the classic native WinApi Functions.
 */ 
public final class Windows{

	private Windows(){}

	/* GDI PixelFormat */
	public static final long PixelFormatIndexed      = 0x00010000; 
	public static final long PixelFormatGDI          = 0x00020000; 
	public static final long PixelFormatAlpha        = 0x00040000; 
	public static final long PixelFormatPAlpha       = 0x00080000; 
	public static final long PixelFormatExtended     = 0x00100000; 
	public static final long PixelFormatCanonical    = 0x00200000; 

	public static final long PixelFormatUndefined = 0;
	public static final long PixelFormatDontCare  = 0;

	public static final long PixelFormat1bppIndexed    =  (1 | ( 1 << 8) | PixelFormatIndexed | PixelFormatGDI);
	public static final long PixelFormat4bppIndexed    =  (2 | ( 4 << 8) | PixelFormatIndexed | PixelFormatGDI);
	public static final long PixelFormat8bppIndexed    =  (3 | ( 8 << 8) | PixelFormatIndexed | PixelFormatGDI);
	public static final long PixelFormat16bppGrayScale =  (4 | (16 << 8) | PixelFormatExtended); 
	public static final long PixelFormat16bppRGB555    =  (5 | (16 << 8) | PixelFormatGDI); 
	public static final long PixelFormat16bppRGB565    =  (6 | (16 << 8) | PixelFormatGDI); 
	public static final long PixelFormat16bppARGB1555  =  (7 | (16 << 8) | PixelFormatAlpha | PixelFormatGDI); 
	public static final long PixelFormat24bppRGB       =  (8 | (24 << 8) | PixelFormatGDI); 
	public static final long PixelFormat32bppRGB       =  (9 | (32 << 8) | PixelFormatGDI); 
	public static final long PixelFormat32bppARGB      =  (10 | (32 << 8) | PixelFormatAlpha | PixelFormatGDI | PixelFormatCanonical); 
	public static final long PixelFormat32bppPARGB     =  (11 | (32 << 8) | PixelFormatAlpha | PixelFormatPAlpha | PixelFormatGDI); 
	public static final long PixelFormat48bppRGB       =  (12 | (48 << 8) | PixelFormatExtended); 
	public static final long PixelFormat64bppARGB      =  (13 | (64 << 8) | PixelFormatAlpha  | PixelFormatCanonical | PixelFormatExtended); 
	public static final long PixelFormat64bppPARGB     =  (14 | (64 << 8) | PixelFormatAlpha  | PixelFormatPAlpha | PixelFormatExtended); 
	public static final long PixelFormatMax            =   15; 


	/* UIA Orientation types */
	public static final long OrientationType_None = 0;
	public static final long OrientationType_Horizontal = 1;
	public static final long OrientationType_Vertical = 2;

	/* UIA window interaction state */	
	public static final long WindowInteractionState_Running = 0;
	public static final long WindowInteractionState_Closing = 1;
	public static final long WindowInteractionState_ReadyForUserInteraction = 2;
	public static final long WindowInteractionState_BlockedByModalWindow = 3;
	public static final long WindowInteractionState_NotResponding = 4;

	/* UIA window visual state */	
	public static final long WindowVisualState_Normal = 0;
	public static final long WindowVisualState_Maximized = 1;
	public static final long WindowVisualState_Minimized = 2;


	/* UIA uiaElement mode */
	public static final long AutomationElementMode_None = 0;
	public static final long AutomationElementMode_Full = 1;


	/* UIA tree scope */	
	public static final long TreeScope_Element = 1;
	public static final long TreeScope_Children = 2;
	public static final long TreeScope_Descendants = 4;
	public static final long TreeScope_Parent = 8;
	public static final long TreeScope_Ancestors = 16;
	public static final long TreeScope_Subtree = 7;

	/* COM concurrency model */
	public static final long COINIT_APARTMENTTHREADED  = 0x2;
	public static final long COINIT_MULTITHREADED      = 0x0;
	public static final long COINIT_DISABLE_OLE1DDE    = 0x4;
	public static final long COINIT_SPEED_OVER_MEMORY  = 0x8;


	/* UIA property ids */
	public static final long UIA_AcceleratorKeyPropertyId = 30006;
	public static final long UIA_AccessKeyPropertyId	= 30007;
	public static final long UIA_AriaPropertiesPropertyId	= 30102;
	public static final long UIA_AriaRolePropertyId	= 30101;
	public static final long UIA_AutomationIdPropertyId	= 30011;
	public static final long UIA_BoundingRectanglePropertyId	= 30001;
	public static final long UIA_ClassNamePropertyId	= 30012;
	public static final long UIA_ClickablePointPropertyId	= 30014;
	public static final long UIA_ControllerForPropertyId	= 30104;
	public static final long UIA_ControlTypePropertyId	= 30003;
	public static final long UIA_CulturePropertyId	= 30015;
	public static final long UIA_DescribedByPropertyId	= 30105;
	public static final long UIA_FlowsFromPropertyId	= 30148;
	public static final long UIA_FlowsToPropertyId	= 30106;
	public static final long UIA_FrameworkIdPropertyId	= 30024;
	public static final long UIA_FullDescriptionPropertyId = 30159;
	public static final long UIA_HasKeyboardFocusPropertyId	= 30008;
	public static final long UIA_HelpTextPropertyId	= 30013;
	public static final long UIA_IsContentElementPropertyId	= 30017;
	public static final long UIA_IsControlElementPropertyId	= 30016;
	public static final long UIA_IsDataValidForFormPropertyId	= 30103;
	public static final long UIA_IsDialogPropertyId = 30174;
	public static final long UIA_IsEnabledPropertyId	= 30010;
	public static final long UIA_IsKeyboardFocusablePropertyId	= 30009;
	public static final long UIA_IsOffscreenPropertyId	= 30022;
	public static final long UIA_IsPasswordPropertyId	= 30019;
	public static final long UIA_IsPeripheralPropertyId = 30150;
	public static final long UIA_IsRequiredForFormPropertyId	= 30025;
	public static final long UIA_ItemStatusPropertyId	= 30026;
	public static final long UIA_ItemTypePropertyId	= 30021;
	public static final long UIA_LabeledByPropertyId	= 30018;
	public static final long UIA_LandmarkTypePropertyId = 30157;
	public static final long UIA_LevelPropertyId = 30154;
	public static final long UIA_LocalizedLandmarkTypePropertyId = 30158;
	public static final long UIA_LiveSettingPropertyId = 30135;
	public static final long UIA_LocalizedControlTypePropertyId = 30004;
	public static final long UIA_NamePropertyId	= 30005;
	public static final long UIA_NativeWindowHandlePropertyId	= 30020;
	public static final long UIA_OrientationPropertyId	= 30023;
	public static final long UIA_PositionInSetPropertyId = 30152;
	public static final long UIA_ProcessIdPropertyId	= 30002;
	public static final long UIA_RuntimeIdPropertyId = 30000;
	public static final long UIA_ProviderDescriptionPropertyId = 30107;
	public static final long UIA_RotationPropertyId = 30166;
	public static final long UIA_SizeOfSetPropertyId = 30153;
	public static final long UIA_VisualEffectsPropertyId = 30163;

	// control pattern property ids
	public static final long UIA_AnnotationAnnotationTypeIdPropertyId	= 30113;
	public static final long UIA_AnnotationAnnotationTypeNamePropertyId	= 30114;
	public static final long UIA_AnnotationAuthorPropertyId	= 30115;
	public static final long UIA_AnnotationDateTimePropertyId	= 30116;
	public static final long UIA_AnnotationTargetPropertyId	= 30117;
	public static final long UIA_DockDockPositionPropertyId	= 30069;
	public static final long UIA_DragDropEffectPropertyId	= 30139;
	public static final long UIA_DragDropEffectsPropertyId	= 30140;
	public static final long UIA_DragIsGrabbedPropertyId	= 30138;
	public static final long UIA_DragGrabbedItemsPropertyId	= 30144;
	public static final long UIA_DropTargetDropTargetEffectPropertyId	= 30142;
	public static final long UIA_DropTargetDropTargetEffectsPropertyId	= 30143;
	public static final long UIA_ExpandCollapseExpandCollapseStatePropertyId	= 30070;
	public static final long UIA_GridColumnCountPropertyId	= 30063;
	public static final long UIA_GridItemColumnPropertyId	= 30065;
	public static final long UIA_GridItemColumnSpanPropertyId	= 30067;
	public static final long UIA_GridItemContainingGridPropertyId	= 30068;
	public static final long UIA_GridItemRowPropertyId	= 30064;
	public static final long UIA_GridItemRowSpanPropertyId	= 30066;
	public static final long UIA_GridRowCountPropertyId	= 30062;
	public static final long UIA_LegacyIAccessibleChildIdPropertyId	= 30091;
	public static final long UIA_LegacyIAccessibleDefaultActionPropertyId	= 30100;
	public static final long UIA_LegacyIAccessibleDescriptionPropertyId	= 30094;
	public static final long UIA_LegacyIAccessibleHelpPropertyId	= 30097;
	public static final long UIA_LegacyIAccessibleKeyboardShortcutPropertyId	= 30098;
	public static final long UIA_LegacyIAccessibleNamePropertyId	= 30092;
	public static final long UIA_LegacyIAccessibleRolePropertyId	= 30095;
	public static final long UIA_LegacyIAccessibleSelectionPropertyId	= 30099;
	public static final long UIA_LegacyIAccessibleStatePropertyId	= 30096;
	public static final long UIA_LegacyIAccessibleValuePropertyId	= 30093;
	public static final long UIA_MultipleViewCurrentViewPropertyId	= 30071;
	public static final long UIA_MultipleViewSupportedViewsPropertyId	= 30072;
	public static final long UIA_RangeValueIsReadOnlyPropertyId	= 30048;
	public static final long UIA_RangeValueLargeChangePropertyId	= 30051;
	public static final long UIA_RangeValueMaximumPropertyId	= 30050;
	public static final long UIA_RangeValueMinimumPropertyId	= 30049;
	public static final long UIA_RangeValueSmallChangePropertyId	= 30052;
	public static final long UIA_RangeValueValuePropertyId	= 30047;
	public static final long UIA_ScrollHorizontallyScrollablePropertyId	= 30057;
	public static final long UIA_ScrollHorizontalScrollPercentPropertyId	= 30053;
	public static final long UIA_ScrollHorizontalViewSizePropertyId	= 30054;
	public static final long UIA_ScrollVerticallyScrollablePropertyId	= 30058;
	public static final long UIA_ScrollVerticalScrollPercentPropertyId	= 30055;
	public static final long UIA_ScrollVerticalViewSizePropertyId	= 30056;
	public static final long UIA_SelectionCanSelectMultiplePropertyId	= 30060;
	public static final long UIA_SelectionIsSelectionRequiredPropertyId	= 30061;
	public static final long UIA_SelectionSelectionPropertyId	= 30059;
	public static final long UIA_SelectionItemIsSelectedPropertyId	= 30079;
	public static final long UIA_SelectionItemSelectionContainerPropertyId	= 30080;
	public static final long UIA_SpreadsheetItemFormulaPropertyId	= 30129;
	public static final long UIA_SpreadsheetItemAnnotationObjectsPropertyId	= 30130;
	public static final long UIA_SpreadsheetItemAnnotationTypesPropertyId	= 30131;
	public static final long UIA_StylesExtendedPropertiesPropertyId	= 30126;
	public static final long UIA_StylesFillColorPropertyId	= 30122;
	public static final long UIA_StylesFillPatternColorPropertyId	= 30125;
	public static final long UIA_StylesFillPatternStylePropertyId	= 30123;
	public static final long UIA_StylesShapePropertyId	= 30124;
	public static final long UIA_StylesStyleIdPropertyId	= 30120;
	public static final long UIA_StylesStyleNamePropertyId	= 30121;
	public static final long UIA_TableColumnHeadersPropertyId	= 30082;
	public static final long UIA_TableItemColumnHeaderItemsPropertyId	= 30085;
	public static final long UIA_TableRowHeadersPropertyId	= 30081;
	public static final long UIA_TableRowOrColumnMajorPropertyId	= 30083;
	public static final long UIA_TableItemRowHeaderItemsPropertyId	= 30084;
	public static final long UIA_ToggleToggleStatePropertyId	= 30086;
	public static final long UIA_TransformCanMovePropertyId	= 30087;
	public static final long UIA_TransformCanResizePropertyId	= 30088;
	public static final long UIA_TransformCanRotatePropertyId	= 30089;
	public static final long UIA_Transform2CanZoomPropertyId	= 30133;
	public static final long UIA_Transform2ZoomLevelPropertyId	= 30145;
	public static final long UIA_Transform2ZoomMaximumPropertyId	= 30147;
	public static final long UIA_Transform2ZoomMinimumPropertyId	= 30146;
	public static final long UIA_ValueIsReadOnlyPropertyId	= 30046;
	public static final long UIA_ValueValuePropertyId	= 30045;
	public static final long UIA_WindowCanMaximizePropertyId	= 30073;
	public static final long UIA_WindowCanMinimizePropertyId	= 30074;
	public static final long UIA_WindowIsModalPropertyId	= 30077;
	public static final long UIA_WindowIsTopmostPropertyId	= 30078;
	public static final long UIA_WindowWindowInteractionStatePropertyId	= 30076;
	public static final long UIA_WindowWindowVisualStatePropertyId	= 30075;

	public static final long UIA_IsAnnotationPatternAvailablePropertyId	= 30118;
	public static final long UIA_IsDockPatternAvailablePropertyId	= 30027;
	public static final long UIA_IsDragPatternAvailablePropertyId	= 30137;
	public static final long UIA_IsDropTargetPatternAvailablePropertyId	= 30141;
	public static final long UIA_IsExpandCollapsePatternAvailablePropertyId	= 30028;
	public static final long UIA_IsGridItemPatternAvailablePropertyId	= 30029;
	public static final long UIA_IsGridPatternAvailablePropertyId	= 30030;
	public static final long UIA_IsInvokePatternAvailablePropertyId	= 30031;
	public static final long UIA_IsItemContainerPatternAvailablePropertyId	= 30108;
	public static final long UIA_IsLegacyIAccessiblePatternAvailablePropertyId	= 30090;
	public static final long UIA_IsMultipleViewPatternAvailablePropertyId	= 30032;
	public static final long UIA_IsObjectModelPatternAvailablePropertyId	= 30112;
	public static final long UIA_IsRangeValuePatternAvailablePropertyId	= 30033;
	public static final long UIA_IsScrollItemPatternAvailablePropertyId	= 30035;
	public static final long UIA_IsScrollPatternAvailablePropertyId	= 30034;
	public static final long UIA_IsSelectionItemPatternAvailablePropertyId	= 30036;
	public static final long UIA_IsSelectionPatternAvailablePropertyId	= 30037;
	public static final long UIA_IsSpreadsheetPatternAvailablePropertyId	= 30128;
	public static final long UIA_IsSpreadsheetItemPatternAvailablePropertyId	= 30132;
	public static final long UIA_IsStylesPatternAvailablePropertyId	= 30127;
	public static final long UIA_IsSynchronizedInputPatternAvailablePropertyId	= 30110;
	public static final long UIA_IsTableItemPatternAvailablePropertyId	= 30039;
	public static final long UIA_IsTablePatternAvailablePropertyId	= 30038;
	public static final long UIA_IsTextChildPatternAvailablePropertyId	= 30136;
	public static final long UIA_IsTextPatternAvailablePropertyId	= 30040;
	public static final long UIA_IsTextPattern2AvailablePropertyId	= 30119;
	public static final long UIA_IsTogglePatternAvailablePropertyId	= 30041;
	public static final long UIA_IsTransformPatternAvailablePropertyId	= 30042;
	public static final long UIA_IsTransformPattern2AvailablePropertyId	= 30134;
	public static final long UIA_IsValuePatternAvailablePropertyId	= 30043;
	public static final long UIA_IsVirtualizedItemPatternAvailablePropertyId	= 30109;
	public static final long UIA_IsWindowPatternAvailablePropertyId	= 30044;



	/* UIA control patterns */
	public static final long UIA_AnnotationPatternId =	10023;
	public static final long UIA_DockPatternId =	10011;
	public static final long UIA_DragPatternId =	10030;
	public static final long UIA_DropTargetPatternId =	10031;
	public static final long UIA_ExpandCollapsePatternId =	10005;
	public static final long UIA_GridItemPatternId =	10007;
	public static final long UIA_GridPatternId =	10006;
	public static final long UIA_InvokePatternId =	10000;
	public static final long UIA_ItemContainerPatternId =	10019;
	public static final long UIA_LegacyIAccessiblePatternId =	10018;
	public static final long UIA_MultipleViewPatternId =	10008;
	public static final long UIA_ObjectModelPatternId =	10022;
	public static final long UIA_RangeValuePatternId =	10003;
	public static final long UIA_ScrollItemPatternId =	10017;
	public static final long UIA_ScrollPatternId =	10004;
	public static final long UIA_SelectionItemPatternId =	10010;
	public static final long UIA_SelectionPatternId =	10001;
	public static final long UIA_SpreadsheetPatternId =	10026;
	public static final long UIA_SpreadsheetItemPatternId =	10027;
	public static final long UIA_StylesPatternId =	10025;
	public static final long UIA_SynchronizedInputPatternId =	10021;
	public static final long UIA_TableItemPatternId =	10013;
	public static final long UIA_TablePatternId =	10012;
	public static final long UIA_TextChildPatternId =	10029;
	public static final long UIA_TextPatternId =	10014;
	public static final long UIA_TextPattern2Id =	10024;
	public static final long UIA_TogglePatternId =	10015;
	public static final long UIA_TransformPatternId =	10016;
	public static final long UIA_TransformPattern2Id =	10028;
	public static final long UIA_ValuePatternId =	10002;
	public static final long UIA_VirtualizedItemPatternId =	10020;
	public static final long UIA_WindowPatternId =	10009;

	/* UIA control type ids */
	public static final long UIA_AppBarControlTypeId =	50040; // since Windows 8.1
	public static final long UIA_ButtonControlTypeId =	50000;
	public static final long UIA_CalendarControlTypeId =	50001;
	public static final long UIA_CheckBoxControlTypeId =	50002;
	public static final long UIA_ComboBoxControlTypeId =	50003;
	public static final long UIA_CustomControlTypeId =	50025;
	public static final long UIA_DataGridControlTypeId =	50028;
	public static final long UIA_DataItemControlTypeId =	50029;
	public static final long UIA_DocumentControlTypeId =	50030;
	public static final long UIA_EditControlTypeId =	50004;
	public static final long UIA_GroupControlTypeId =	50026;
	public static final long UIA_HeaderControlTypeId =	50034;
	public static final long UIA_HeaderItemControlTypeId =	50035;
	public static final long UIA_HyperlinkControlTypeId =	50005;
	public static final long UIA_ImageControlTypeId =	50006;
	public static final long UIA_ListControlTypeId =	50008;
	public static final long UIA_ListItemControlTypeId =	50007;
	public static final long UIA_MenuBarControlTypeId =	50010;
	public static final long UIA_MenuControlTypeId =	50009;
	public static final long UIA_MenuItemControlTypeId =	50011;
	public static final long UIA_PaneControlTypeId =	50033;
	public static final long UIA_ProgressBarControlTypeId =	50012;
	public static final long UIA_RadioButtonControlTypeId =	50013;
	public static final long UIA_ScrollBarControlTypeId =	50014;
	public static final long UIA_SemanticZoomControlTypeId =	50039;
	public static final long UIA_SeparatorControlTypeId =	50038;
	public static final long UIA_SliderControlTypeId =	50015;
	public static final long UIA_SpinnerControlTypeId =	50016;
	public static final long UIA_SplitButtonControlTypeId =	50031;
	public static final long UIA_StatusBarControlTypeId =	50017;
	public static final long UIA_TabControlTypeId =	50018;
	public static final long UIA_TabItemControlTypeId =	50019;
	public static final long UIA_TableControlTypeId =	50036;
	public static final long UIA_TextControlTypeId =	50020;
	public static final long UIA_ThumbControlTypeId =	50027;
	public static final long UIA_TitleBarControlTypeId =	50037;
	public static final long UIA_ToolBarControlTypeId =	50021;
	public static final long UIA_ToolTipControlTypeId =	50022;
	public static final long UIA_TreeControlTypeId =	50023;
	public static final long UIA_TreeItemControlTypeId =	50024;
	public static final long UIA_WindowControlTypeId =	50032;

	/* UIA error codes */
	public static final long UIA_E_ELEMENTNOTAVAILABLE = 0x80040201;
	public static final long UIA_E_ELEMENTNOTENABLED = 0x80040200;
	public static final long UIA_E_INVALIDOPERATION = 0x80131509;
	public static final long UIA_E_NOCLICKABLEPOINT = 0x80040202;
	public static final long UIA_E_NOTSUPPORTED = 0x80040204;
	public static final long UIA_E_PROXYASSEMBLYNOTLOADED = 0x80040203;
	public static final long UIA_E_TIMEOUT = 0;

	/* UIA event identifiers */
	public static final long UIA_AsyncContentLoadedEventId =	20006;
	public static final long UIA_AutomationFocusChangedEventId =	20005;
	public static final long UIA_AutomationPropertyChangedEventId =	20004;
	public static final long UIA_Drag_DragStartEventId =	20026;
	public static final long UIA_Drag_DragCancelEventId =	20027;
	public static final long UIA_Drag_DragCompleteEventId =	20028;
	public static final long UIA_DropTarget_DragEnterEventId =	20029;
	public static final long UIA_DropTarget_DragLeaveEventId =	20030;
	public static final long UIA_DropTarget_DroppedEventId =	20031;
	public static final long UIA_HostedFragmentRootsInvalidatedEventId =	20015;
	public static final long UIA_InputDiscardedEventId =	20022;
	public static final long UIA_InputReachedOtherElementEventId =	20021;
	public static final long UIA_InputReachedTargetEventId =	20020;
	public static final long UIA_Invoke_InvokedEventId =	20009;
	public static final long UIA_LayoutInvalidatedEventId =	20008;
	public static final long UIA_LiveRegionChangedEventId =	20025;
	public static final long UIA_MenuClosedEventId =	20007;
	public static final long UIA_MenuModeEndEventId =	20019;
	public static final long UIA_MenuModeStartEventId =	20018;
	public static final long UIA_MenuOpenedEventId =	20003;
	public static final long UIA_Selection_InvalidatedEventId =	20013;
	public static final long UIA_SelectionItem_ElementAddedToSelectionEventId =	20010;
	public static final long UIA_SelectionItem_ElementRemovedFromSelectionEventId =	20011;
	public static final long UIA_SelectionItem_ElementSelectedEventId =	20012;
	public static final long UIA_StructureChangedEventId =	20002;
	public static final long UIA_SystemAlertEventId =	20024;
	public static final long UIA_Text_TextChangedEventId =	20015;
	public static final long UIA_Text_TextSelectionChangedEventId =	20014;
	public static final long UIA_ToolTipClosedEventId =	20001;
	public static final long UIA_ToolTipOpenedEventId =	20000;
	public static final long UIA_Window_WindowClosedEventId =	20017;
	public static final long UIA_Window_WindowOpenedEventId =	20016;

	/* UIA annotation type ids */
	public static final long AnnotationType_Comment = 60003;
	public static final long AnnotationType_Footer = 60007;
	public static final long AnnotationType_FormulaError = 60004;
	public static final long AnnotationType_GrammarError = 60002;
	public static final long AnnotationType_Header = 60006;
	public static final long AnnotationType_Highlighted = 60008;
	public static final long AnnotationType_SpellingError = 60001;
	public static final long AnnotationType_TrackChanges = 60005;
	public static final long AnnotationType_Unknown = 60000;

	/* UIA style ids */
	public static final long StyleId_Custom = 70000;
	public static final long StyleId_Heading1 = 70001;
	public static final long StyleId_Heading2 = 70002;
	public static final long StyleId_Heading3 = 70003;
	public static final long StyleId_Heading4 = 70004;
	public static final long StyleId_Heading5 = 70005;
	public static final long StyleId_Heading6 = 70006;
	public static final long StyleId_Heading7 = 70007;
	public static final long StyleId_Heading8 = 70008;
	public static final long StyleId_Heading9 = 70009;
	public static final long StyleId_Title = 70010;
	public static final long StyleId_Subtitle = 70011;
	public static final long StyleId_Normal = 70012;
	public static final long StyleId_Emphasis = 70013;
	public static final long StyleId_Quote = 70014;


	public static final int MN_GETHMENU = 0x01E1;
	public static final int OBJID_CLIENT = 0xFFFFFFFC;
	public static final int OBJID_HSCROLL = -6;
	public static final int OBJID_VSCROLL = -5;
	public static final int OBJID_MENU = 0xFFFFFFFD;
	public static final int OBJID_SYSMENU = 0xFFFFFFFF;
	public static final int STATE_SYSTEM_INVISIBLE = 32768;
	public static final int STATE_SYSTEM_OFFSCREEN = 65536;
	public static final int STATE_SYSTEM_PRESSED = 8;
	public static final int STATE_SYSTEM_UNAVAILABLE = 1;
	public static final int GW_HWNDFIRST = 0;
	public static final int GW_HWNDLAST = 1;
	public static final int GW_HWNDNEXT = 2;
	public static final int GW_HWNDPREV = 3;
	public static final int GW_OWNER = 4;
	public static final int GW_CHILD = 5;
	public static final int GW_ENABLEDPOPUP = 6;
	public static final int MF_BYCOMMAND = 0x00000000;
	public static final int MF_BYPOSITION = 0x00000400;
	public static final int LVIR_BOUNDS = 0;
	public static final int LVIR_ICON = 1;
	public static final int LVIR_LABEL = 2;
	public static final int LVIR_SELECTBOUNDS = 3;

	
	public static final long SC_CLOSE = 0xF060;


	/* GetWindowLong */
	public static final long GWL_EXSTYLE = -20;
	public static final long GWL_HINSTANCE = -6;
	public static final long GWL_HWNDPARENT = -8;
	public static final long GWL_ID = -12;
	public static final long GWL_STYLE = -16;
	public static final long GWL_USERDATA = -21;
	public static final long GWL_WNDPROC = -4;


	/* Keycodes */
	public static final int VK_LCONTROL = 0xA2;
	public static final int VK_RCONTROL = 0xA3;
	public static final int VK_ESCAPE = 0x1B;
	public static final int SM_CYHSCROLL = 3;
	public static final int SM_CYVSCROLL = 20;


	/* Window Classes */
	public static final String ANIMATE_CLASS = "SysAnimate32";
	public static final String DATETIMEPICK_CLASS = "SysDateTimePick32";
	public static final String HOTKEY_CLASS = "msctls_hotkey32";
	public static final String MONTHCAL_CLASS = "SysMonthCal32";
	public static final String PROGRESS_CLASS = "msctls_progress32";
	public static final String REBARCLASSNAME = "ReBarWindow32";
	public static final String STATUSCLASSNAME = "msctls_statusbar32";
	public static final String TOOLBARCLASSNAME = "ToolbarWindow32";
	public static final String TOOLTIPS_CLASS = "tooltips_class32";
	public static final String TRACKBAR_CLASS = "msctls_trackbar32";
	public static final String UPDOWN_CLASS = "msctls_updown32";
	public static final String WC_BUTTON = "Button";
	public static final String WC_COMBOBOX = "ComboBox";
	public static final String WC_COMBOBOXEX = "ComboBoxEx32";
	public static final String WC_EDIT = "Edit";
	public static final String WC_HEADER = "SysHeader32";
	public static final String WC_LISTBOX = "ListBox";
	public static final String WC_IPADDRESS = "SysIPAddress32";
	public static final String WC_LINK = "1000439C";
	public static final String WC_LISTVIEW = "SysListView32";
	public static final String WC_NATIVEFONTCTL = "NativeFontCtl";
	public static final String WC_PAGESCROLLER = "SysPager";
	public static final String WC_SCROLLBAR = "ScrollBar";
	public static final String WC_STATIC = "Static";
	public static final String WC_TABCONTROL = "SysTabControl32";
	public static final String WC_TREEVIEW = "SysTreeView32";


	/* Window Messages */
	public static int WM_GETFONT = 0x0031;
	public static int WM_GETTEXT = 0x000D;
	public static int WM_GETTEXTLENGTH = 0x000E;
	public static int TB_GETITEMRECT;


	/* Gdiplus Constants */
	public static final int Gdiplus_FontStyleRegular      = 0;
	public static final int Gdiplus_FontStyleBold         = 1;
	public static final int Gdiplus_FontStyleItalic       = 2;
	public static final int Gdiplus_FontStyleBoldItalic   = 3;
	public static final int Gdiplus_FontStyleUnderline    = 4;
	public static final int Gdiplus_FontStyleStrikeout    = 8; 

	public static final int Gdiplus_UnitWorld        = 0;
	public static final int Gdiplus_UnitDisplay      = 1;
	public static final int Gdiplus_UnitPixel        = 2;
	public static final int Gdiplus_UnitPoint        = 3;
	public static final int Gdiplus_UnitInch         = 4;
	public static final int Gdiplus_UnitDocument     = 5;
	public static final int Gdiplus_UnitMillimeter   = 6;

	public static final int Gdiplus_Ok                          = 0;
	public static final int Gdiplus_GenericError                = 1;
	public static final int Gdiplus_InvalidParameter            = 2;
	public static final int Gdiplus_OutOfMemory                 = 3;
	public static final int Gdiplus_ObjectBusy                  = 4;
	public static final int Gdiplus_InsufficientBuffer          = 5;
	public static final int Gdiplus_NotImplemented              = 6;
	public static final int Gdiplus_Win32Error                  = 7;
	public static final int Gdiplus_WrongState                  = 8;
	public static final int Gdiplus_Aborted                     = 9;
	public static final int Gdiplus_FileNotFound                = 10;
	public static final int Gdiplus_ValueOverflow               = 11;
	public static final int Gdiplus_AccessDenied                = 12;
	public static final int Gdiplus_UnknownImageFormat          = 13;
	public static final int Gdiplus_FontFamilyNotFound          = 14;
	public static final int Gdiplus_FontStyleNotFound           = 15;
	public static final int Gdiplus_NotTrueTypeFont             = 16;
	public static final int Gdiplus_UnsupportedGdiplusVersion   = 17;
	public static final int Gdiplus_GdiplusNotInitialized       = 18;
	public static final int Gdiplus_PropertyNotFound            = 19;
	public static final int Gdiplus_PropertyNotSupported        = 20;
	public static final int Gdiplus_ProfileNotFound             = 21;


	/* Misc Constants */
	public static final long DELETE = 0x00010000L;
	public static final long READ_CONTROL = 0x00020000L;
	public static final long SYNCHRONIZE = 0x00100000L;
	public static final long WRITE_DAC = 0x00040000L;
	public static final long WRITE_OWNER = 0x00080000L;	
	public static final long PROCESS_QUERY_INFORMATION = 0x0400;
	public static final long PROCESS_VM_READ = 0x0010;
	public static final long PROCESS_TERMINATE = 0x0001;
	
	public static final long WAIT_ABANDONED = 0x00000080L;
	public static final long WAIT_OBJECT_0 = 0x00000000L;
	public static final long WAIT_TIMEOUT = 0x00000102L;
	public static final long WAIT_FAILED = 0xFFFFFFFF;

	public static final long WS_OVERLAPPEDWINDOW = 13565952;
	public static final long WS_EX_LAYERED = 0x00080000;
	public static final long WS_EX_TOOLWINDOW = 0x00000080L;
	public static final long WS_EX_TOPMOST = 0x00000008L;
	public static final long WS_EX_TRANSPARENT = 0x00000020L;
	public static final long WS_EX_NOACTIVATE = 0x08000000L;

	public static final long SWP_SHOWWINDOW = 0x0040;
	public static final long AC_SRC_ALPHA = 1;
	public static final long ULW_ALPHA = 2;
	public static final long HWND_TOPMOST = -1;
	public static final long SW_SHOWNOACTIVATE = 4;
	public static final long MONITOR_DEFAULTTOPRIMARY = 1;
	public static final long PM_NOREMOVE = 0x0000;

	public static final long STILL_ACTIVE = 259;



	/* COM Constants */
	public static final long CLSCTX_INPROC_SERVER            = 0x1;
	public static final long CLSCTX_INPROC_HANDLER           = 0x2;
	public static final long CLSCTX_LOCAL_SERVER             = 0x4;
	public static final long CLSCTX_INPROC_SERVER16          = 0x8;
	public static final long CLSCTX_REMOTE_SERVER            = 0x10;
	public static final long CLSCTX_INPROC_HANDLER16         = 0x20;
	public static final long CLSCTX_RESERVED1                = 0x40;
	public static final long CLSCTX_RESERVED2                = 0x80;
	public static final long CLSCTX_RESERVED3                = 0x100;
	public static final long CLSCTX_RESERVED4                = 0x200;
	public static final long CLSCTX_NO_CODE_DOWNLOAD         = 0x400;
	public static final long CLSCTX_RESERVED5                = 0x800;
	public static final long CLSCTX_NO_CUSTOM_MARSHAL        = 0x1000;
	public static final long CLSCTX_ENABLE_CODE_DOWNLOAD     = 0x2000;
	public static final long CLSCTX_NO_FAILURE_LOG           = 0x4000;
	public static final long CLSCTX_DISABLE_AAA              = 0x8000;
	public static final long CLSCTX_ENABLE_AAA               = 0x10000;
	public static final long CLSCTX_FROM_DEFAULT_CONTEXT     = 0x20000;
	public static final long CLSCTX_ACTIVATE_32_BIT_SERVER   = 0x40000;
	public static final long CLSCTX_ACTIVATE_64_BIT_SERVER   = 0x80000;
	public static final long CLSCTX_ENABLE_CLOAKING          = 0x100000;
	public static final long CLSCTX_APPCONTAINER             = 0x400000;
	public static final long CLSCTX_ACTIVATE_AAA_AS_IU       = 0x800000;
	public static final long CLSCTX_PS_DLL                   = 0x80000000;


	/* Functions */
	public static native boolean IsIconic(long hWnd);
	public static native long GetProcessId(long hProcess) throws WinApiException;
	public static native boolean TextOut(int hdc, int x, int y, String text);
	public static native boolean InvalidateRect(int hwnd, int x, int y, int width, int height, boolean bErased);	
	public static native int GetWindowDC(int hwnd);	
	public static native int SetPixel(int hdc, int x, int y, int color);	
	public static native int RGBMacro(int red, int green, int blue);	
	public static native int[] GetClientRect(int hwnd);
	public static native byte[] loadFile(String name);
	public static native boolean SetCursorPos(int x, int y);
	public static native double[] GetCursorPos();
	public static native long GetForegroundWindow();
	public static native boolean SetForegroundWindow(long hwnd);
	public static native int WindowFromPoint(int x, int y);
	public static native int ChildWindowFromPoint(int parentHwnd, int x, int y);
	public static native int GetWindowThreadId(int hwnd);
	public static native long GetWindowProcessId(long hwnd);
	public static native long SendMessage(long hWnd, long Msg, long wParam, long lParam);
	public static native int[] GetMenuItemRect(int hWnd, int hMenu, int uItem);
	public static native String GetMenuString(int hMenu, int uIDItem, int uFlag);
	public static native int GetMenuItemCount(int hMenu);
	public static native int GetSubMenu(int hMenu, int nPos);
	public static native long GetSystemMenu(long hWnd, boolean bRevert);    
	public static native long GetMenu(long hWnd);
	public static native int[] GetMenuBarInfo(int hwnd, int idObject, int idItem);
	public static native long[] EnumWindows();
	public static native long[] EnumWindows(long pid);
	public static native long[] EnumAllWindows();
	public static native long[] EnumAllWindows(long pid);
	public static native long[] EnumChildWindows(long hWndParent);
	public static native boolean IsWindow(long hwnd);
	public static native boolean IsWindowVisible(long hwnd);
	public static native String GetClassName(long hwnd);    
	public static native int[] GetToolBarItemRect(int hwnd, int index, boolean dropdown);
	public static native int GetToolBarButtonCount(int hwnd);
	public static native int[] GetToolButtonInfo(int hwnd, int index);
	public static native String GetToolButtonText(int hwnd, int idCommand);
	public static native int ListView_GetItemCount(int hwnd);
	public static native String ListView_GetItemText(int hwnd, int iItem, int iSubItem);
	public static native int[] ListView_GetItemRect(int hwnd, int iItem, int flags);
	public static native int[] GetScrollBarInfo(int hwnd, int idObject);
	public static native int[] GetComboBoxInfo(int hwnd);
	public static native long[] GetWindowRect(long hwnd);
	public static native int FillRect(int hdc, int left, int top, int right, int bottom, int brush);
	public static native int CreateSolidBrush(int color);
	public static native int ReleaseDC(int hwnd, int hdc);
	public static native long SelectObject(long hdc, long hgdiobj);
	public static native boolean DeleteObject(int hObject);    
	public static native int GetParent(int hwnd);
	public static native long GetWindow(long hwnd, long flags);
	public static native int GetAncestor(int hwnd, int flags);
	public static native int GetWindowTextLength(int hwnd);
	public static native String GetWindowText(long hwnd);
	public static native int GetShellWindow();
	public static native long GetDesktopWindow();
	public static native int GetAsyncKeyState(int vKey);
	public static native boolean Rectangle(int hdc, int nLeftRect, int nTopRect, int nRightRect, int nBottomRect);
	public static native int GetSystemMetrics(int nIndex);
	public static native long GetNextWindow(long hWnd, long wCmd);
	public static native long[] CreateProcess(String applicationName, String commandLine, 
			boolean inheritHandles, long creationFlags, 
			String[] environment, String currentDir, 
			String desktop, String title, long[] startupInfo);
	public static native boolean CloseHandle(long hObject);
	public static native void ExitProcess(long exitCode);
	public static native boolean TerminateProcess(long hProcess, long exitCode);
	public static native boolean SetWindowPos(long hwnd, long hwndInsertAfter, long x, long y, long cx, long cy, long uFlags);
	public static native long CreateWindowEx(long dwExStyle, String lpClassName, String lpWindowName, long dwStyle, 
			long x, long y, long nWidth, long nHeight, long hWndParent, long hMenu, 
			long hInstance, long lpParam);
	public static native void UpdateLayeredWindow(long hwnd, long hdcDst, long pptDstX, long pptDstY, 
			long psizeCX, long psizeCY, long hdcSrc, long pptSrcX, long pptSrcY, long crKey, int pblendOp,
			int pblendFlags, int pblendSCAlpha, int pblendAlphaFormat, long dwFlags);	
	public static native boolean ShowWindow(long hwnd, long nCmdShow);
	public static native long[] PeekMessage(long hwnd, long wMsgFilterMin, long wMsgFilterMax, long wRemoveMsg);	
	public static native long[] GetMessage(long hwnd, long wMsgFilterMin, long wMsgFilterMax);
	public static native boolean TranslateMessage(long[] lpMsg);
	public static native long DispatchMessage(long[] lpmsg);
	public static native long GetModuleHandleEx(long dwFlags, String lpModuleName);	
	public static native long GetCurrentModule();
	public static native long MonitorFromPoint(long x, long y, long dwFlags);
	public static long GetPrimaryMonitorHandle(){ return MonitorFromPoint(0, 0, MONITOR_DEFAULTTOPRIMARY); /* because point {0,0} is always on the primary monitor!! */ }
	public static native long[] GetMonitorInfo(long hMonitor);
	public static native long GetDC(long hWnd);
	public static native long CreateCompatibleDC(long hdc);
	public static native long CreateCompatibleBitmap(long hdc, long nWidth, long nHeight);	
	public static native long[] EnumProcesses() throws WinApiException;
	public static native long[] EnumProcessModules(long hProcess) throws WinApiException;
	public static native String GetModuleBaseName(long hProcess, long hModule) throws WinApiException;
	public static native long GetWindowLong(long hwnd, long nIndex);

	/* Gdiplus */
	public static native long GdiplusStartup() throws GDIException;
	public static native void GdiplusShutdown(long gdiplusToken) throws GDIException;
	public static native long Gdiplus_Graphics_FromHDC(long hDC) throws GDIException;
	public static native long Gdiplus_Graphics_Clear(long pGraphics, int alpha, int red, int green, int blue) throws GDIException;
	public static native long Gdiplus_Graphics_DrawLine(long pGraphics, long pPen, double x1, double y1, double x2, double y2) throws GDIException;
	public static native long Gdiplus_Graphics_DrawRectangle(long pGraphics, long pPen, double x, double y, double width, double height) throws GDIException;
	public static native long Gdiplus_Graphics_FillRectangle(long pGraphics, long pBrush, double x, double y, double width, double height) throws GDIException;
	public static native long Gdiplus_Graphics_DrawEllipse(long pGraphics, long pPen, double x, double y, double width, double height) throws GDIException;
	public static native long Gdiplus_Graphics_FillEllipse(long pGraphics, long pBrush, double x, double y, double width, double height) throws GDIException;
	public static native long Gdiplus_Graphics_DrawString(long pGraphics, String text, long pFont, double x, double y, long pBrush) throws GDIException;	

	public static native void Gdiplus_Graphics_DrawImage(long pGraphics, long pImage, long x, long y) throws GDIException;
	public static native void Gdiplus_Graphics_DrawImage(long pGraphics, long x, long y, long width, long height, long imgWidth, long imgHeight, long pixelFormat, int[] data) throws GDIException;
	public static native long Gdiplus_Bitmap_Create(long width, long height, long stride, long pixelFormat, byte[] data) throws GDIException;
	public static native void Gdiplus_Bitmap_Destroy(long pBitmap) throws GDIException;

	public static native long Gdiplus_Pen_Create(int alpha, int red, int green, int blue, double width) throws GDIException;
	public static native long Gdiplus_Pen_SetColor(long pPen, int alpha, int red, int green, int blue) throws GDIException;
	public static native long Gdiplus_Pen_SetWidth(long pPen, double width) throws GDIException;
	public static native void Gdiplus_Pen_Destroy(long pPen) throws GDIException;
	public static native long Gdiplus_SolidBrush_Create(int alpha, int red, int green, int blue) throws GDIException;
	public static native long Gdiplus_SolidBrush_SetColor(long pBrush, int alpha, int red, int green, int blue) throws GDIException;
	public static native void Gdiplus_SolidBrush_Destroy(long pBrush) throws GDIException;
	public static native long Gdiplus_Font_Create(long pFontFamily, double size, int style, int unit) throws GDIException;
	public static native void Gdiplus_Font_Destroy(long pFont) throws GDIException;
	public static native long Gdiplus_FontFamily_Create(String name) throws GDIException;
	public static native void Gdiplus_FontFamily_Destroy(long pFontFamily) throws GDIException;

	// begin by urueda
	public static native long   GetProcessMemoryInfo(long processID);
	public static native long[] GetProcessTimes(long processID);
	public static native String	GetProcessNameFromHWND(long hwnd);
	// end by urueda

	public static native boolean InitializeAccessBridge(); // by ferpasri & urueda
	// begin by urueda
	public static native long[]   GetAccessibleContext(long hwnd); // vmid x ac
	public static native long 	  GetHWNDFromAccessibleContext(long vmid, long ac);
	//public static native int	  GetVisibleChildrenCount(long vmid, long ac);
	//public static native long[]   GetVisibleChildren(long vmid, long ac);
	public static native long     GetAccessibleChildFromContext(long vmid, long ac, int i);
	public static native Object[] GetAccessibleContextProperties(long vmid, long ac);
	// end by urueda

	public static String Gdiplus_Status2String(int statusCode){
		switch(statusCode){
		case Gdiplus_Ok:   				          return "Ok"; 
		case Gdiplus_GenericError:                return "GenericError";
		case Gdiplus_InvalidParameter:            return "InvalidParameter";
		case Gdiplus_OutOfMemory:                 return "OutOfMemory";
		case Gdiplus_ObjectBusy:                  return "ObjectBusy";
		case Gdiplus_InsufficientBuffer:          return "InsufficientBuffer";
		case Gdiplus_NotImplemented:              return "NotImplemented";
		case Gdiplus_Win32Error:                  return "Win32Error";
		case Gdiplus_WrongState:                  return "WrongState";
		case Gdiplus_Aborted:                     return "Aborted";
		case Gdiplus_FileNotFound:                return "FileNotFound";
		case Gdiplus_ValueOverflow:               return "ValueOverflow";
		case Gdiplus_AccessDenied:                return "AccessDenied";
		case Gdiplus_UnknownImageFormat:          return "UnknownImageFormat";
		case Gdiplus_FontFamilyNotFound:          return "FontFamilyNotFound";
		case Gdiplus_FontStyleNotFound:           return "FontStyleNotFound";
		case Gdiplus_NotTrueTypeFont:             return "NotTrueTypeFont";
		case Gdiplus_UnsupportedGdiplusVersion:   return "UnsupportedGdiplusVersion";
		case Gdiplus_GdiplusNotInitialized:       return "GdiplusNotInitialized";
		case Gdiplus_PropertyNotFound:            return "PropertyNotFound";
		case Gdiplus_PropertyNotSupported:        return "PropertyNotSupported";
		case Gdiplus_ProfileNotFound:             return "ProfileNotFound";
		default:       						      return "Unkown Error";
		}
	}


	public static native long OpenProcess(long dwDesiredAccess, boolean bInheritHandle, long dwProcessId) throws WinApiException;
	public static native long WaitForSingleObject(long hHandle, long dwMilliseconds) throws WinApiException;
	public static native long WaitForInputIdle(long hProcess) throws WinApiException;
	public static native long GetExitCodeProcess(long hProcess) throws WinApiException;

	/* COM */
	public static native long CoInitializeEx(long pvReserved, long dwCoInit) throws WinApiException;
	public static native long CoCreateInstance(long pClsid, long pUnkOuter, long dwClsContext, long pIId);
	public static native long Get_CLSID_CUIAutomation_Ptr();
	public static native long Get_IID_IUIAutomation_Ptr();
	// begin by wcoux
	public static native long Get_CLSID_ApplicationActivationManager_Ptr();
	public static native long Get_IID_IApplicationActivationManager_Ptr();
	// end by wcoux
	public static native long IUnknown_QueryInterface(long pIUnknown, long pIID);
	public static native long IUnknown_AddRef(long pIUnknown);
	public static native long IUnknown_Release(long pIUnknown);
	public static native void CoUninitialize();


	/* ApplicationActivationManager */
	public static native long IApplicationActivationManager_ActivateApplication(long pAppActMngr, String appUserModelId, String arguments, int options) throws UIAException; // by wcoux


	/* UIAutomation */	
	public static native long IUIAutomation_GetRootElement(long pAutomation) throws UIAException;
	public static native long IUIAutomation_CreateCacheRequest(long pAutomation) throws UIAException;
	public static native long IUIAutomation_ElementFromPoint(long pAutomation, long x, long y);
	public static native long IUIAutomation_ElementFromHandle(long pAutomation, long hwnd);
	public static native long IUIAutomation_ElementFromHandleBuildCache(long pAutomation, long hwnd, long pCacheRequest);
	public static native long IUIAutomation_CreateTrueCondition(long pAutomation) throws UIAException;
	public static native long IUIAutomation_CreateAndCondition(long pAutomation, long pCond1, long pCond2) throws UIAException;	
	public static native long IUIAutomation_CreatePropertyCondition(long pAutomation, long propertyId, String value) throws UIAException;
	public static native long IUIAutomation_CreatePropertyCondition(long pAutomation, long propertyId, boolean value) throws UIAException;
	public static native long IUIAutomation_CreatePropertyCondition(long pAutomation, long propertyId, int value) throws UIAException;
	public static native long IUIAutomation_get_ControlViewCondition(long pAutomation) throws UIAException;	
	public static native boolean IUIAutomation_CompareElements(long pAutomation, long pEl1, long pEl2) throws UIAException;


	public static native void IUIAutomationCacheRequest_AddProperty(long pRequest, long propertyId) throws UIAException;
	public static native void IUIAutomationCacheRequest_AddPattern(long pRequest, long patternId) throws UIAException;
	public static native void IUIAutomationCacheRequest_put_TreeFilter(long pRequest, long pFilter) throws UIAException;
	public static native void IUIAutomationCacheRequest_put_TreeScope(long pRequest, long treeScope) throws UIAException;
	public static native void IUIAutomationCacheRequest_put_AutomationElementMode(long pRequest, long mode) throws UIAException;

	public static native String IUIAutomationElement_get_AutomationId(long pElement, boolean fromCache);	
	public static native long[] IUIAutomationElement_GetRuntimeId(long pElement);
	public static native String IUIAutomationElement_get_AcceleratorKey(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_AccessKey(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_HelpText(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_ClassName(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_Name(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_ItemType(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_ItemStatus(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_LocalizedControlType(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_ProviderDescription(long pElement, boolean fromCache);
	public static native String IUIAutomationElement_get_FrameworkId(long pElement, boolean fromCache);
	public static native long IUIAutomationElement_get_ControlType(long pElement, boolean fromCache);
	public static native long IUIAutomationElement_get_Culture(long pElement, boolean fromCache);
	public static native long IUIAutomationElement_get_Orientation(long pElement, boolean fromCache);
	public static native long IUIAutomationElement_get_ProcessId(long pElement, boolean fromCache);
	public static native long IUIAutomationElement_get_NativeWindowHandle(long pElement, boolean fromCache);
	public static native boolean IUIAutomationElement_get_IsContentElement(long pElement, boolean fromCache);
	public static native boolean IUIAutomationElement_get_IsControlElement(long pElement, boolean fromCache);	
	public static native boolean IUIAutomationElement_get_IsEnabled(long pElement, boolean fromCache);
	public static native boolean IUIAutomationElement_get_HasKeyboardFocus(long pElement, boolean fromCache);
	public static native boolean IUIAutomationElement_get_IsKeyboardFocusable(long pElement, boolean fromCache);
	public static native boolean IUIAutomationElement_get_IsOffscreen(long pElement, boolean fromCache);
	public static native long[] IUIAutomationElement_get_BoundingRectangle(long pElement, boolean fromCache);	
	public static native long IUIAutomationElement_GetPattern(long pElement, long patternId, boolean fromCache);
	public static native Object IUIAutomationElement_GetCurrentPropertyValue(long pElement, long propertyId, boolean fromCache); // by urueda	
	public static native Object IUIAutomationElement_GetPropertyValueEx(long pElement, long propertyId, boolean ignoreDefaultValue, boolean fromCache);
	public static native long IUIAutomationElement_GetCachedChildren(long pElement);	
	public static native long IUIAutomationElement_FindAll(long pElement, long treeScope, long pCondition) throws UIAException;
	public static native long IUIAutomationElement_FindAllBuildCache(long pElement, long treeScope, long pCondition, long pCacheRequest) throws UIAException;
	public static native boolean IUIAutomationWindowPattern_get_CanMaximize(long pElement, boolean fromCache);
	public static native boolean IUIAutomationWindowPattern_get_CanMinimize(long pElement, boolean fromCache);
	public static native boolean IUIAutomationWindowPattern_get_IsModal(long pElement, boolean fromCache);
	public static native boolean IUIAutomationWindowPattern_get_IsTopmost(long pElement, boolean fromCache);
	public static native long IUIAutomationWindowPattern_get_WindowInteractionState(long pElement, boolean fromCache);
	public static native long IUIAutomationWindowPattern_get_WindowVisualState(long pElement, boolean fromCache);


	public static native long IUIAutomationElementArray_get_Length(long pArray) throws UIAException;
	public static native long IUIAutomationElementArray_GetElement(long pArray, int idx) throws UIAException;


	public static native void SafeArrayDestroy(long pArray) throws WinApiException;
	public static native long SafeArrayGetIntElement(long pArray, long idx) throws WinApiException;				// This is only for 1-dimensional arrays yet! (UIA only uses 1-dimensional ones!)
	public static native long SafeArrayGetUBound(long pArray, long dim) throws WinApiException;
	public static native String IUIAutomationElement_get_ValuePattern(long pElement, long patternId);

	public static boolean hitTest(long pAutomation, long pExpected, long x, long y){
		long pUnderCursor = 0;

		try{
			pUnderCursor = Windows.IUIAutomation_ElementFromPoint(pAutomation, x, y);
			if(pUnderCursor == 0) return false;

			long[] rid1 = Windows.IUIAutomationElement_GetRuntimeId(pExpected);
			long[] rid2 = Windows.IUIAutomationElement_GetRuntimeId(pUnderCursor);
			if(rid1 == null || rid2 == null || rid1.length != rid2.length)
				return false;

			for(int i = 0; i < rid1.length; i++){
				if(rid1[i] != rid2[i])
					return false;				
			}
			return true;
		}finally{
			if(pUnderCursor != 0) Windows.IUnknown_Release(pUnderCursor);
		}
	}

	static{
		try{
			if(new File("windows.dll").exists())
				loadExternalLib("windows.dll");
			else
				loadExternalLib(System.getenv("DIRNAME")+File.separator+"windows.dll");
		}catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}

	// by urueda
	private static void loadExternalLib(String name) throws IOException {
		File f = new File(name);
		if (f.exists()){
			System.out.println("Loading external lib ... " + name);
			System.load(f.getAbsolutePath());
		} else{
			System.out.println("Loading resource lib ... " + name);
			loadLib(name);
		}
	}
	
	private static void loadLib(String name) throws IOException {
		InputStream in = Windows.class.getResourceAsStream(name);
		File fileOut = File.createTempFile("fruit_windows", ".dll");
		fileOut.deleteOnExit();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));
		copy(in, out);
		in.close();
		out.close();
		System.load(fileOut.getAbsolutePath());
	}

	private static void copy(InputStream in, OutputStream out) throws IOException{
		while(true){
			int data = in.read();
			if(data == -1)
				break;
			out.write(data);
		}
	}
}
