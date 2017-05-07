/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import java.util.Collection;
import java.util.Map;

import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;

public final class UIARoles {
	private UIARoles(){}
	
	private final static Map<Long, Role> typeIdToRole = Util.newHashMap();

	public static final Role
	
	UIAWidget = from(-1, "UIAWidget", Roles.Widget),
	UIAButton = from(Windows.UIA_ButtonControlTypeId, "UIAButton", UIAWidget, Roles.Button), 
	UIACalendar = from(Windows.UIA_CalendarControlTypeId, "UIACalendar", UIAWidget, Roles.Control), 
	UIACheckBox = from(Windows.UIA_CheckBoxControlTypeId, "UIACheckBox", UIAWidget, Roles.Control), 
	UIAComboBox = from(Windows.UIA_ComboBoxControlTypeId, "UIAComboBox", UIAWidget, Roles.Control),
	UIACustomControl = from(Windows.UIA_CustomControlTypeId, "UIACustomControl", UIAWidget, Roles.Control),
	UIADataGrid = from(Windows.UIA_DataGridControlTypeId, "UIADataGrid", UIAWidget, Roles.Control),
	UIADataItem = from(Windows.UIA_DataItemControlTypeId, "UIADataItem", UIAWidget, Roles.Control),
	UIADocument = from(Windows.UIA_DocumentControlTypeId, "UIADocument", UIAWidget, Roles.Control),
	UIAEdit = from(Windows.UIA_EditControlTypeId, "UIAEdit", UIAWidget, Roles.Control),
	UIAGroup = from(Windows.UIA_GroupControlTypeId, "UIAGroup", UIAWidget, Roles.Control),
	UIAHeader = from(Windows.UIA_HeaderControlTypeId, "UIAHeader", UIAWidget, Roles.Control),
	UIAHeaderItem = from(Windows.UIA_HeaderItemControlTypeId, "UIAHeaderItem", UIAWidget, Roles.Control),
	UIAHyperlink = from(Windows.UIA_HyperlinkControlTypeId, "UIAHyperLink", UIAWidget, Roles.Control),
	UIAImage = from(Windows.UIA_ImageControlTypeId, "UIAImage", UIAWidget, Roles.Control),
	UIAList = from(Windows.UIA_ListControlTypeId, "UIAList", UIAWidget, Roles.Control),
	UIAListItem = from(Windows.UIA_ListItemControlTypeId, "UIAListItem", UIAWidget, Roles.Control),
	UIAMenuBar = from(Windows.UIA_MenuBarControlTypeId, "UIAMenuBar", UIAWidget, Roles.Control),
	UIAMenu = from(Windows.UIA_MenuControlTypeId, "UIAMenu", UIAWidget, Roles.Control),
	UIAMenuItem = from(Windows.UIA_MenuItemControlTypeId, "UIAMenuItem", UIAWidget, Roles.Control),
	UIAPane = from(Windows.UIA_PaneControlTypeId, "UIAPane", UIAWidget, Roles.Control),
	UIAProgressBar = from(Windows.UIA_ProgressBarControlTypeId, "UIAProgressBar", UIAWidget, Roles.Control),
	UIARadioButton = from(Windows.UIA_RadioButtonControlTypeId, "UIARadioButton", UIAWidget, Roles.Control),
	UIAScrollBar = from(Windows.UIA_ScrollBarControlTypeId, "UIAScrollBar", UIAWidget, Roles.Control),
	UIASeanticZoom = from(Windows.UIA_SemanticZoomControlTypeId, "UIASeanticZoom", UIAWidget, Roles.Control),
	UIASeparator = from(Windows.UIA_SeparatorControlTypeId, "UIASeparator", UIAWidget, Roles.Control),
	UIASlider = from(Windows.UIA_SliderControlTypeId, "UIASlider", UIAWidget, Roles.Control),
	UIASpinner = from(Windows.UIA_SpinnerControlTypeId, "UIASpinner", UIAWidget, Roles.Control),
	UIASplitButton = from(Windows.UIA_SplitButtonControlTypeId, "UIASplitButton", UIAWidget, Roles.Control),
	UIAStatusBar = from(Windows.UIA_StatusBarControlTypeId, "UIAStatusBar", UIAWidget, Roles.Control),
	UIATabControl = from(Windows.UIA_TabControlTypeId, "UIATabControl", UIAWidget, Roles.Control),
	UIATabItem = from(Windows.UIA_TabItemControlTypeId, "UIATabItem", UIAWidget, Roles.Control),
	UIATable = from(Windows.UIA_TableControlTypeId, "UIATable", UIAWidget, Roles.Control),
	UIAText = from(Windows.UIA_TextControlTypeId, "UIAText", UIAWidget, Roles.Control),
	UIAThumb = from(Windows.UIA_ThumbControlTypeId, "UIAThumb", UIAWidget, Roles.Control),
	UIATitleBar = from(Windows.UIA_TitleBarControlTypeId, "UIATitleBar", UIAWidget, Roles.Control),
	UIAToolBar = from(Windows.UIA_ToolBarControlTypeId, "UIAToolBar", UIAWidget, Roles.Control),
	UIAToolTip = from(Windows.UIA_ToolTipControlTypeId, "UIAToolTip", UIAWidget, Roles.Control),
	UIATree = from(Windows.UIA_TreeControlTypeId, "UIATree", UIAWidget, Roles.Control),
	UIATreeItem = from(Windows.UIA_TreeItemControlTypeId, "UIATree", UIAWidget, Roles.Control),
	UIAWindow = from(Windows.UIA_WindowControlTypeId, "UIAWindow", UIAWidget, Roles.Control),
	
	UIAUnknown = from(-2, "UIAWindow", UIAWidget, Roles.Control)
	;

	private static Role from(long id, String name, Role...inheritFrom){
		Role ret = Role.from(name, inheritFrom);
		typeIdToRole.put(id, ret);
		return ret;
	}
		
	public static Role fromTypeId(long typeId){
		Role ret = typeIdToRole.get(typeId);
		return (ret == null) ? UIAUnknown : ret; 
	}
	
	// by urueda
	public static Collection<Role> rolesSet(){
		return typeIdToRole.values();
	}
	
}