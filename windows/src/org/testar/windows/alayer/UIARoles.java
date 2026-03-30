/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.alayer;

import java.util.Collection;
import java.util.Map;

import org.testar.core.util.Util;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.windows.Windows;

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
