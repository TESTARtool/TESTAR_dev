/***************************************************************************************************
 *
 * Copyright (c) 2017, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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

package org.fruit.alayer.windows;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps the AccessBridge Accessible Roles from AccessBridgePackages.h
 * to UIA Automation control types.
 */
public class AccessBridgeControlTypes {

	private AccessBridgeControlTypes() {}

	private static final int MISSING_UIA = -1;

	/**
	 * Object is used to alert the user about something.
	 */
	static final String ACCESSIBLE_ALERT  = "alert";

	/**
	 * The header for a column of data.
	 */
	static final String ACCESSIBLE_COLUMN_HEADER  = "column header";

	/** 
	 * Object that can be drawn into and is used to trap
	 * events.
	 * see ACCESSIBLE_FRAME
	 * see ACCESSIBLE_GLASS_PANE
	 * see ACCESSIBLE_LAYERED_PANE
	 */     
	static final String ACCESSIBLE_CANVAS  = "canvas";

	/** 
	 * A list of choices the user can select from.  Also optionally 
	 * allows the user to enter a choice of their own.
	 */     
	static final String ACCESSIBLE_COMBO_BOX  = "combo box";

	/** 
	 * An iconified internal frame in a DESKTOP_PANE.
	 * see ACCESSIBLE_DESKTOP_PANE
	 * see ACCESSIBLE_INTERNAL_FRAME
	 */
	static final String ACCESSIBLE_DESKTOP_ICON  = "desktop icon";

	/** 
	 * A frame-like object that is clipped by a desktop pane.  The
	 * desktop pane, internal frame, and desktop icon objects are 
	 * often used to create multiple document interfaces within an
	 * application.
	 * see ACCESSIBLE_DESKTOP_ICON
	 * see ACCESSIBLE_DESKTOP_PANE
	 * see ACCESSIBLE_FRAME
	 */
	static final String ACCESSIBLE_INTERNAL_FRAME  = "internal frame";

	/**
	 * A pane that supports internal frames and 
	 * iconified versions of those internal frames.
	 * see ACCESSIBLE_DESKTOP_ICON
	 * see ACCESSIBLE_INTERNAL_FRAME
	 */
	static final String ACCESSIBLE_DESKTOP_PANE  = "desktop pane";

	/** 
	 * A specialized pane whose primary use is inside a DIALOG
	 * see ACCESSIBLE_DIALOG
	 */
	static final String ACCESSIBLE_OPTION_PANE  = "option pane";

	/** 
	 * A top level window with no title or border.
	 * see ACCESSIBLE_FRAME
	 * see ACCESSIBLE_DIALOG
	 */
	static final String ACCESSIBLE_WINDOW  = "window";

	/**
	 * A top level window with a title bar, border, menu bar, etc.  It is
	 * often used as the primary window for an application.
	 * see ACCESSIBLE_DIALOG
	 * see ACCESSIBLE_CANVAS
	 * see ACCESSIBLE_WINDOW
	 */
	static final String ACCESSIBLE_FRAME  = "frame";

	/** 
	 * A top level window with title bar and a border.  A dialog is similar 
	 * to a frame, but it has fewer properties and is often used as a 
	 * secondary window for an application.
	 * see ACCESSIBLE_FRAME
	 * see ACCESSIBLE_WINDOW
	 */
	static final String ACCESSIBLE_DIALOG  = "dialog";

	/**
	 * A specialized dialog that lets the user choose a color.
	 */
	static final String ACCESSIBLE_COLOR_CHOOSER  = "color chooser";


	/**
	 * A pane that allows the user to navigate through 
	 * and select the contents of a directory.  May be used
	 * by a file chooser.
	 * see ACCESSIBLE_FILE_CHOOSER
	 */
	static final String ACCESSIBLE_DIRECTORY_PANE  = "directory pane";

	/**
	 * A specialized dialog that displays the files in the directory
	 * and lets the user select a file, browse a different directory,
	 * or specify a filename.  May use the directory pane to show the
	 * contents of a directory.
	 * see ACCESSIBLE_DIRECTORY_PANE
	 */
	static final String ACCESSIBLE_FILE_CHOOSER  = "file chooser";

	/** 
	 * An object that fills up space in a user interface.  It is often
	 * used in interfaces to tweak the spacing between components,
	 * but serves no other purpose.
	 */
	static final String ACCESSIBLE_FILLER  = "filler";

	/**
	 * A hypertext anchor
	 */
	static final String ACCESSIBLE_HYPERLINK  = "hyperlink";

	/**
	 * A small fixed size picture, typically used to decorate components.
	 */
	static final String ACCESSIBLE_ICON  = "icon";

	/** 
	 * An object used to present an icon or short string in an interface.
	 */
	static final String ACCESSIBLE_LABEL  = "label";

	/**
	 * A specialized pane that has a glass pane and a layered pane as its
	 * children.
	 * see ACCESSIBLE_GLASS_PANE
	 * see ACCESSIBLE_LAYERED_PANE
	 */
	static final String ACCESSIBLE_ROOT_PANE  = "root pane";

	/**
	 * A pane that is guaranteed to be painted on top
	 * of all panes beneath it.
	 * see ACCESSIBLE_ROOT_PANE
	 * see ACCESSIBLE_CANVAS
	 */
	static final String ACCESSIBLE_GLASS_PANE  = "glass pane";

	/** 
	 * A specialized pane that allows its children to be drawn in layers,
	 * providing a form of stacking order.  This is usually the pane that
	 * holds the menu bar as well as the pane that contains most of the
	 * visual components in a window.
	 * see ACCESSIBLE_GLASS_PANE
	 * see ACCESSIBLE_ROOT_PANE
	 */
	static final String ACCESSIBLE_LAYERED_PANE  = "layered pane";

	/**
	 * An object that presents a list of objects to the user and allows the
	 * user to select one or more of them.  A list is usually contained
	 * within a scroll pane.
	 * see ACCESSIBLE_SCROLL_PANE
	 * see ACCESSIBLE_LIST_ITEM
	 */
	static final String ACCESSIBLE_LIST  = "list";

	/**
	 * An object that presents an element in a list.  A list is usually
	 * contained within a scroll pane.
	 * see ACCESSIBLE_SCROLL_PANE
	 * see ACCESSIBLE_LIST
	 */
	static final String ACCESSIBLE_LIST_ITEM  = "list item";

	/**
	 * An object usually drawn at the top of the primary dialog box of
	 * an application that contains a list of menus the user can choose
	 * from.  For example, a menu bar might contain menus for "File,"
	 * "Edit," and "Help."
	 * see ACCESSIBLE_MENU
	 * see ACCESSIBLE_POPUP_MENU
	 * see ACCESSIBLE_LAYERED_PANE
	 */
	static final String ACCESSIBLE_MENU_BAR  = "menu bar";

	/** 
	 * A temporary window that is usually used to offer the user a 
	 * list of choices, and then hides when the user selects one of
	 * those choices.
	 * see ACCESSIBLE_MENU
	 * see ACCESSIBLE_MENU_ITEM
	 */
	static final String ACCESSIBLE_POPUP_MENU  = "popup menu";

	/** 
	 * An object usually found inside a menu bar that contains a list
	 * of actions the user can choose from.  A menu can have any object
	 * as its children, but most often they are menu items, other menus,
	 * or rudimentary objects such as radio buttons, check boxes, or
	 * separators.  For example, an application may have an "Edit" menu 
	 * that contains menu items for "Cut" and "Paste."
	 * see ACCESSIBLE_MENU_BAR
	 * see ACCESSIBLE_MENU_ITEM
	 * see ACCESSIBLE_SEPARATOR
	 * see ACCESSIBLE_RADIO_BUTTON
	 * see ACCESSIBLE_CHECK_BOX
	 * see ACCESSIBLE_POPUP_MENU
	 */    
	static final String ACCESSIBLE_MENU  = "menu";

	/**
	 * An object usually contained in a menu that presents an action 
	 * the user can choose.  For example, the "Cut" menu item in an
	 * "Edit" menu would be an action the user can select to cut the
	 * selected area of text in a document.
	 * see ACCESSIBLE_MENU_BAR
	 * see ACCESSIBLE_SEPARATOR
	 * see ACCESSIBLE_POPUP_MENU
	 */
	static final String ACCESSIBLE_MENU_ITEM  = "menu item";

	/**
	 * An object usually contained in a menu to provide a visual
	 * and logical separation of the contents in a menu.  For example,
	 * the "File" menu of an application might contain menu items for
	 * "Open," "Close," and "Exit," and will place a separator between
	 * "Close" and "Exit" menu items.
	 * see ACCESSIBLE_MENU
	 * see ACCESSIBLE_MENU_ITEM
	 */
	static final String ACCESSIBLE_SEPARATOR  = "separator";

	/**
	 * An object that presents a series of panels (or page tabs), one at a 
	 * time, through some mechanism provided by the object.  The most common 
	 * mechanism is a list of tabs at the top of the panel.  The children of
	 * a page tab list are all page tabs.
	 * see ACCESSIBLE_PAGE_TAB
	 */
	static final String ACCESSIBLE_PAGE_TAB_LIST  = "page tab list";

	/**
	 * An object that is a child of a page tab list.  Its sole child is
	 * the panel that is to be presented to the user when the user 
	 * selects the page tab from the list of tabs in the page tab list.
	 * see ACCESSIBLE_PAGE_TAB_LIST
	 */
	static final String ACCESSIBLE_PAGE_TAB  = "page tab";

	/**
	 * A generic container that is often used to group objects.
	 */
	static final String ACCESSIBLE_PANEL  = "panel";

	/**
	 * An object used to indicate how much of a task has been completed.
	 */
	static final String ACCESSIBLE_PROGRESS_BAR  = "progress bar";

	/**
	 * A text object used for passwords, or other places where the 
	 * text contents is not shown visibly to the user
	 */
	static final String ACCESSIBLE_PASSWORD_TEXT  = "password text";

	/**
	 * An object the user can manipulate to tell the application to do
	 * something.
	 * see ACCESSIBLE_CHECK_BOX
	 * see ACCESSIBLE_TOGGLE_BUTTON
	 * see ACCESSIBLE_RADIO_BUTTON
	 */
	static final String ACCESSIBLE_PUSH_BUTTON  = "push button";

	/**
	 * A specialized push button that can be checked or unchecked, but
	 * does not provide a separate indicator for the current state.
	 * see ACCESSIBLE_PUSH_BUTTON
	 * see ACCESSIBLE_CHECK_BOX
	 * see ACCESSIBLE_RADIO_BUTTON
	 */
	static final String ACCESSIBLE_TOGGLE_BUTTON  = "toggle button";

	/**
	 * A choice that can be checked or unchecked and provides a 
	 * separate indicator for the current state.
	 * see ACCESSIBLE_PUSH_BUTTON
	 * see ACCESSIBLE_TOGGLE_BUTTON
	 * see ACCESSIBLE_RADIO_BUTTON
	 */
	static final String ACCESSIBLE_CHECK_BOX  = "check box";

	/**
	 * A specialized check box that will cause other radio buttons in the
	 * same group to become unchecked when this one is checked.  
	 * see ACCESSIBLE_PUSH_BUTTON
	 * see ACCESSIBLE_TOGGLE_BUTTON
	 * see ACCESSIBLE_CHECK_BOX
	 */
	static final String ACCESSIBLE_RADIO_BUTTON  = "radio button";

	/**
	 * The header for a row of data.
	 */
	static final String ACCESSIBLE_ROW_HEADER  = "row header";

	/**
	 * An object that allows a user to incrementally view a large amount
	 * of information.  Its children can include scroll bars and a viewport.
	 * see ACCESSIBLE_SCROLL_BAR
	 * see ACCESSIBLE_VIEWPORT
	 */
	static final String ACCESSIBLE_SCROLL_PANE  = "scroll pane";

	/** 
	 * An object usually used to allow a user to incrementally view a
	 * large amount of data.  Usually used only by a scroll pane.
	 * see ACCESSIBLE_SCROLL_PANE
	 */
	static final String ACCESSIBLE_SCROLL_BAR  = "scroll bar";

	/**
	 * An object usually used in a scroll pane.  It represents the portion 
	 * of the entire data that the user can see.  As the user manipulates 
	 * the scroll bars, the contents of the viewport can change.
	 * see ACCESSIBLE_SCROLL_PANE
	 */
	static final String ACCESSIBLE_VIEWPORT  = "viewport";

	/**
	 * An object that allows the user to select from a bounded range.  For
	 * example, a slider might be used to select a number between 0 and 100.
	 */    
	static final String ACCESSIBLE_SLIDER  = "slider";

	/**
	 * A specialized panel that presents two other panels at the same time.
	 * Between the two panels is a divider the user can manipulate to make
	 * one panel larger and the other panel smaller.
	 */
	static final String ACCESSIBLE_SPLIT_PANE  = "split pane";

	/**
	 * An object used to present information in terms of rows and columns.
	 * An example might include a spreadsheet application.
	 */
	static final String ACCESSIBLE_TABLE  = "table";

	/**
	 * An object that presents text to the user.  The text is usually
	 * editable by the user as opposed to a label.
	 * see ACCESSIBLE_LABEL
	 */
	static final String ACCESSIBLE_TEXT  = "text";

	/**
	 * An object used to present hierarchical information to the user.
	 * The individual nodes in the tree can be collapsed and expanded
	 * to provide selective disclosure of the tree's contents.
	 */
	static final String ACCESSIBLE_TREE  = "tree";

	/**
	 * A bar or palette usually composed of push buttons or toggle buttons.
	 * It is often used to provide the most frequently used functions for an
	 * application.
	 */
	static final String ACCESSIBLE_TOOL_BAR  = "tool bar";

	/** 
	 * An object that provides information about another object.  The 
	 * accessibleDescription property of the tool tip is often displayed 
	 * to the user in a small  = "help bubble" when the user causes the 
	 * mouse to hover over the object associated with the tool tip.
	 */
	static final String ACCESSIBLE_TOOL_TIP  = "tool tip";

	/**
	 * An AWT component, but nothing else is known about it.
	 * see ACCESSIBLE_SWING_COMPONENT
	 * see ACCESSIBLE_UNKNOWN
	 */
	static final String ACCESSIBLE_AWT_COMPONENT  = "awt component";

	/**
	 * A Swing component, but nothing else is known about it.
	 * see ACCESSIBLE_AWT_COMPONENT
	 * see ACCESSIBLE_UNKNOWN
	 */
	static final String ACCESSIBLE_SWING_COMPONENT  = "swing component";

	/**
	 * The object contains some Accessible information, but its role is
	 * not known.
	 * see ACCESSIBLE_AWT_COMPONENT
	 * see ACCESSIBLE_SWING_COMPONENT
	 */
	static final String ACCESSIBLE_UNKNOWN  = "unknown";

	/**
	 * A STATUS_BAR is an simple component that can contain
	 * multiple labels of status information to the user.
	 */
	static final String ACCESSIBLE_STATUS_BAR  = "status bar";

	/**
	 * A DATE_EDITOR is a component that allows users to edit
	 * java.util.Date and java.util.Time objects
	 */
	static final String ACCESSIBLE_DATE_EDITOR  = "date editor";

	/**
	 * A SPIN_BOX is a simple spinner component and its main use
	 * is for simple numbers.
	 */
	static final String ACCESSIBLE_SPIN_BOX  = "spinbox";

	/**
	 * A FONT_CHOOSER is a component that lets the user pick various
	 * attributes for fonts.
	 */
	static final String ACCESSIBLE_FONT_CHOOSER  = "font chooser";

	/**
	 * A GROUP_BOX is a simple container that contains a border
	 * around it and contains components inside it.
	 */
	static final String ACCESSIBLE_GROUP_BOX  = "group box";

	/**
	 * A text header
	 */
	static final String ACCESSIBLE_HEADER  = "header";

	/**
	 * A text footer
	 */
	static final String ACCESSIBLE_FOOTER  = "footer";

	/**
	 * A text paragraph
	 */
	static final String ACCESSIBLE_PARAGRAPH  = "paragraph";

	/**
	 * A ruler is an object used to measure distance
	 */
	static final String ACCESSIBLE_RULER  = "ruler";

	/**
	 * A role indicating the object acts as a formula for
	 * calculating a value.  An example is a formula in
	 * a spreadsheet cell.
	 */
	static final String ACCESSIBLE_EDITBAR  = "editbar";

	/**
	 * A role indicating the object monitors the progress 
	 * of some operation.
	 */
	static final String PROGRESS_MONITOR  = "progress monitor";

	private static final long serialVersionUID = 617334006202177665L;
	
	private static final Map<String,Long> MAP_2_UIA = new HashMap<>();

	static {
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_ALERT, Windows.UIA_ControlTypePropertyId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_AWT_COMPONENT, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_CANVAS, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_CHECK_BOX, Windows.UIA_CheckBoxControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_COLOR_CHOOSER, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_COLUMN_HEADER, Windows.UIA_HeaderControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_COMBO_BOX, Windows.UIA_ComboBoxControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_DATE_EDITOR, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_DESKTOP_ICON, Windows.UIA_ImageControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_DESKTOP_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_DIALOG, Windows.UIA_WindowControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_DIRECTORY_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_EDITBAR, Windows.UIA_ToolBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_FILE_CHOOSER, Windows.UIA_WindowControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_FILLER, Windows.UIA_SeparatorControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_FONT_CHOOSER, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_FOOTER, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_FRAME, Windows.UIA_WindowControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_GLASS_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_GROUP_BOX, Windows.UIA_GroupControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_HEADER, Windows.UIA_HeaderControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_HYPERLINK, Windows.UIA_HyperlinkControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_ICON, Windows.UIA_ImageControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_INTERNAL_FRAME, Windows.UIA_WindowControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_LABEL, Windows.UIA_TextControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_LAYERED_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_LIST, Windows.UIA_ListControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_LIST_ITEM, Windows.UIA_ListItemControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_MENU, Windows.UIA_MenuControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_MENU_BAR, Windows.UIA_MenuBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_MENU_ITEM, Windows.UIA_MenuItemControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_OPTION_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PAGE_TAB, Windows.UIA_TabItemControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PAGE_TAB_LIST, Windows.UIA_TabControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PANEL, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PARAGRAPH, Windows.UIA_TextControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PASSWORD_TEXT, Windows.UIA_EditControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_POPUP_MENU, Windows.UIA_MenuControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PROGRESS_BAR, Windows.UIA_ProgressBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_PUSH_BUTTON, Windows.UIA_ButtonControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_RADIO_BUTTON, Windows.UIA_RadioButtonControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_ROOT_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_ROW_HEADER, Windows.UIA_HeaderControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_RULER, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SCROLL_BAR, Windows.UIA_ScrollBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SCROLL_PANE, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SEPARATOR, Windows.UIA_SeparatorControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SLIDER, Windows.UIA_SliderControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SPIN_BOX, Windows.UIA_SpinnerControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SPLIT_PANE, Windows.UIA_SplitButtonControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_STATUS_BAR, Windows.UIA_StatusBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_SWING_COMPONENT, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TABLE, Windows.UIA_TableControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TEXT, Windows.UIA_EditControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TOGGLE_BUTTON, Windows.UIA_ButtonControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TOOL_BAR, Windows.UIA_ToolBarControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TOOL_TIP, Windows.UIA_ToolTipControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_TREE, Windows.UIA_TreeControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_UNKNOWN, Windows.UIA_CustomControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_VIEWPORT, Windows.UIA_PaneControlTypeId);
		MAP_2_UIA.put(AccessBridgeControlTypes.ACCESSIBLE_WINDOW, Windows.UIA_WindowControlTypeId);
	}

	/**
	 * Retrieves the corresponding UIA automation control type.
	 * @param accessibleRole An AccessBridge accessible role.
	 * @return The UIA automation control type, or MISSING_UIA if not equivalent found.
	 */
	public static long toUIA(String accessibleRole){
		if (accessibleRole == null || accessibleRole.isEmpty()){
			System.out.println("WARNING - null/empty accessible role <" + accessibleRole + ">");
			return MISSING_UIA;
		}

		Long uia = MAP_2_UIA.get(accessibleRole);
		if (uia != null)
			return uia.longValue();

		System.out.println("WARNING - missed accessible role <" + accessibleRole + ">");
		return MISSING_UIA;		
	}

}
