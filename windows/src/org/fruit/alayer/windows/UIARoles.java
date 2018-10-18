/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
	UIAAppBar = from(Windows.UIA_AppBarControlTypeId, "UIAAppBar", UIAWidget, Roles.Control),
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
	UIASemanticZoom = from(Windows.UIA_SemanticZoomControlTypeId, "UIASemanticZoom", UIAWidget, Roles.Control),
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
	UIATreeItem = from(Windows.UIA_TreeItemControlTypeId, "UIATreeItem", UIAWidget, Roles.Control),
	UIAWindow = from(Windows.UIA_WindowControlTypeId, "UIAWindow", UIAWidget, Roles.Control),
	UIAUnknown = from(-2, "UIAUnknown", UIAWidget, Roles.Control);

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
