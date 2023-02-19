/***************************************************************************************************
*
* Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2020 Open Universiteit - www.ou.nl
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

import java.util.ArrayList;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;

public class AccessBridgeFetcher {
	
	public static boolean swingJavaTableDescend = false;
	
	private AccessBridgeFetcher() {}
	
	/** 
	 * Recursively extract Java Swing Elements information through Access Bridge.
	 * windows/native_src/main_w10.cpp contains the OS level calls
	 */
	public static UIAElement accessBridgeDescend(long hwnd, UIAElement parent, long vmid, long ac){
		UIAElement modalElement = null;
		parent.set(Tags.HWND, hwnd);

		long[] vmidAC;
		if (vmid == 0) {
			vmidAC = Windows.GetAccessibleContext(hwnd);
		} else {
			vmidAC = new long[]{ vmid,ac };
		}
		if (vmidAC != null){			
			Object[] props = Windows.GetAccessibleContextProperties(vmidAC[0],vmidAC[1]);
			if (props != null){
				String name = (String) props[0];
				String description = (String) props[1];
				String role = (String) props[2];
				String accesibleStateSet = (String) props[3];
				String indexInParent = (String) props[4];
				int childrenCount = Integer.parseInt((String) props[5]);
				String x = (String) props[6];
				String y = (String) props[7];
				String width = (String) props[8];
				String height = (String) props[9];
				String accessibleComponent = (String) props[10];
				String accessibleAction = (String) props[11];
				String accessibleSelection = (String) props[12];
				String accessibleText = (String) props[13];
				String accessibleInterfaces = (String) props[14];

				Rect rect = null;
				try {
					rect = Rect.from(new Double(x).doubleValue(), new Double(y).doubleValue(),
							new Double(width).doubleValue(), new Double(height).doubleValue());
				} catch (Exception e){
					return null;
				}

				UIAElement el = new UIAElement(parent);
				parent.children.add(el);
				el.rect = rect;
				el.windowHandle = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);

				if(isJavaSwingTopLevelContainer(role, el)) {
					el.isTopLevelContainer = true;
					modalElement = el;
				}

				el.ctrlId = AccessBridgeControlTypes.toUIA(role);				
				if (el.ctrlId == Windows.UIA_MenuControlTypeId) {
					el.isTopLevelContainer = true;
				} else if (el.ctrlId == Windows.UIA_EditControlTypeId) {
					el.isKeyboardFocusable = true;
				}

				el.name = name;				
				el.helpText = description;
				el.automationId = role;
				el.enabled = accesibleStateSet.contains("enabled");
				el.blocked = !accesibleStateSet.contains("showing");

				parent.root.windowHandleMap.put(el.windowHandle, el);
				
				// Detect duplicated menu item and combo box panels to ignore them
				if(isNonDesiredMenuItem(role, el) || isNonDesiredComboBox(role, el)) {
				    parent.parent.ignore = true;
				}

				/*int cc = Windows.GetVisibleChildrenCount(vmidAC[0], vmidAC[1]);					
					if (cc > 0){
						el.children = new ArrayList<UIAElement>(cc);
						long[] children = Windows.GetVisibleChildren(vmidAC[0],vmidAC[1]);
						for (int i=0; i<children.length; i++)
							accessBridgeDescend(windowHandle,el,vmidAC[0],children[i]);
					}*/

				long childAC;
				el.children = new ArrayList<UIAElement>(childrenCount);

				//Swing Java Tables needs a custom descend to obtain properly cells values
				if(swingJavaTableDescend && role.contains("table")) {
				    // Tables have a viewport parent
				    Rect visibleTable = parent.rect;
				    Rect sizeTable = el.rect;
				    int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);
				    int numberOfTotalRows = tableRowColumn[0];
				    int numberOfTotalColumns = tableRowColumn[1];
				    double rowSize = sizeTable.height() / numberOfTotalRows;
				    int numberOfVisibleRows = (int) (visibleTable.height() / rowSize);

				    if(sizeTable.height() > visibleTable.height()) {
				        swingJavaTableDescend(hwnd, el, vmid, ac, numberOfVisibleRows, numberOfTotalColumns);
				    } else {
				        swingJavaTableDescend(hwnd, el, vmid, ac, numberOfTotalRows, numberOfTotalColumns);
				    }
				}

				for (int i=0; i<childrenCount; i++){
				    childAC =  Windows.GetAccessibleChildFromContext(vmidAC[0],vmidAC[1],i);
				    accessBridgeDescend(hwnd,el,vmidAC[0],childAC);
				}
			}
		}

		return modalElement;
	}
	
	/**
	 * Check the role of the Java Swing element to determine if it is a top level container
	 */
	private static boolean isJavaSwingTopLevelContainer(String role, UIAElement el) {
		// JDialog are by default top level containers
		if (role.equals(AccessBridgeControlTypes.ACCESSIBLE_DIALOG)){
			return true;
		}
		// Usually the JFrame element that descend directly from the root process
		// are also top level containers
		if(role.equals(AccessBridgeControlTypes.ACCESSIBLE_FRAME) 
				&& el.parent != null && (el.parent instanceof UIARootElement)) {
			return true;
		}
		return false;
	}
	
	private static boolean isNonDesiredMenuItem(String role, UIAElement el) {
	    UIAElement parent = el.parent;
	    return (role.equals("menu item") || role.equals("radio button") || role.equals("check box"))
                && parent != null && parent.parent != null 
                && parent.automationId.equals("popup menu") && parent.parent.automationId.equals("panel");
	}

	private static boolean isNonDesiredComboBox(String role, UIAElement el) {
	    UIAElement parent = el.parent;
	    boolean nonDesired = false;

	    /*GOOD combobox: combo box - popup menu - scroll pane - viewport - list - label*/
	    try {
	        nonDesired = (role.equals("label")
	                && parent.automationId.equals("list") 
	                && parent.parent.automationId.equals("viewport")
	                && parent.parent.parent.automationId.equals("scroll pane") 
	                && parent.parent.parent.parent.automationId.equals("popup menu")
	                && !parent.parent.parent.parent.parent.automationId.equals("combo box"));
	    } catch(Exception e) {
	        return false;
	    }

	    return nonDesired;
	}
	
	/**
	 * Use number of visible rows to obtain widget table cell properties
	 */
	private static UIAElement swingJavaTableDescend (long hwnd, UIAElement parent, long vmid, long ac, 
			int numberOfVisibleRows, int numberOfColumns) {

		for (int i=0; i<numberOfVisibleRows; i++){

			//Select one by one the rows of the table to access properly to the cell element properties
			Windows.SelectTableRow(vmid, ac, i);

			long[] vmidAC;
			if (vmid == 0)
				vmidAC = Windows.GetAccessibleContext(hwnd);
			else
				vmidAC = new long[]{ vmid,ac };

			if (vmidAC != null){
				for(int j=0; j<numberOfColumns; j++) {
					Object[] props = Windows.GetTableCellProperties(vmid, ac, 0, j);

					if (props != null){
						String name = (String) props[0];
						String description = (String) props[1];
						String role = (String) props[2];
						String accesibleStateSet = (String) props[3];
						String indexInParent = (String) props[4];
						String childrenCount = (String) props[5];
						String x = (String) props[6];
						String y = (String) props[7];
						String width = (String) props[8];
						String height = (String) props[9];
						String accessibleComponent = (String) props[10];
						String accessibleAction = (String) props[11];
						String accessibleSelection = (String) props[12];
						String accessibleText = (String) props[13];
						String accessibleInterfaces = (String) props[14];

						Rect rect = null;
						try {
							rect = Rect.from(new Double(x).doubleValue(), new Double(y).doubleValue(),
									new Double(width).doubleValue(), new Double(height).doubleValue());
						} catch (Exception e){}

						UIAElement el = new UIAElement(parent);
						parent.children.add(el);
						el.rect = rect;
						el.windowHandle = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);

						el.ctrlId = AccessBridgeControlTypes.toUIA(role);				
						if (el.ctrlId == Windows.UIA_MenuControlTypeId)
							el.isTopLevelContainer = true;
						else if (el.ctrlId == Windows.UIA_EditControlTypeId)
							el.isKeyboardFocusable = true;

						el.name = name;				
						el.helpText = description;
						el.automationId = "TableCell "+role;
						el.enabled = accesibleStateSet.contains("enabled");
						el.blocked = !accesibleStateSet.contains("showing");

						parent.root.windowHandleMap.put(el.windowHandle, el);

					}
				}
			}
		}

		return parent;
	}
}
