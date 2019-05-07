/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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

import java.util.ArrayList;
import java.util.Random;

import org.fruit.alayer.Rect;

public class BuilderAccessBridge {

	private static boolean existTopJavaInternalFrame;

	public static boolean visualizeJavaTable;
	public static int numberOfRowsToVisualizeJavaTable = 1;

	public static boolean updateActionJavaTable;
	public static int childsOfJavaTable = 0;

	private BuilderAccessBridge() {}

	/**
	 * From a windows HWND use the Java Access Bridge API to build an UIAElement
	 * that represents the element hierarchy of Java applications
	 */
	public static UIAElement accessBridgeDescend(long hwnd, UIAElement parent, long vmid, long ac){
		UIAElement modalElement = null;

		long[] vmidAC;
		if (vmid == 0)
			vmidAC = Windows.GetAccessibleContext(hwnd);
		else
			vmidAC = new long[]{ vmid,ac };

		if (vmidAC != null){
			Object[] props = Windows.GetAccessibleContextProperties(vmidAC[0],vmidAC[1]);
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
					//if (parent.parent == null)
					//	parent.rect = el.rect; // fix UI actions at root widget
				} catch (Exception e){
					return null;
				}

				UIAElement el = new UIAElement(parent);
				parent.children.add(el);
				el.rect = rect;

				el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);
				if (role.equals(AccessBridgeControlTypes.ACCESSIBLE_DIALOG)){
					el.isTopLevelContainer = true;
					modalElement = el;
				}

				if(role.contains("internal frame") && !existTopJavaInternalFrame) {
					existTopJavaInternalFrame = true;
					el.isTopJavaInternalFrame = true;
				}

				el.ctrlId = AccessBridgeControlTypes.toUIA(role);				
				if (el.ctrlId == Windows.UIA_MenuControlTypeId) // || el.ctrlId == Windows.UIA_WindowControlTypeId)
					el.isTopLevelContainer = true;
				else if (el.ctrlId == Windows.UIA_EditControlTypeId)
					el.isKeyboardFocusable = true;

				el.name = name;				
				el.helpText = description;
				el.automationId = role;

				el.enabled = accesibleStateSet.contains("enabled");

				el.blocked = !accesibleStateSet.contains("showing");

				parent.root.hwndMap.put(el.hwnd, el);

				//Sometimes popup menu are duplicated with AccessBridge when we open one Menu or combo box
				//boolean correctComboBox = (role.equals("popup menu") && parent.automationId.equals("combo box"));
				//if( (!role.equals("popup menu") || correctComboBox)

				if(childrenCount != null && !childrenCount.isEmpty() && !childrenCount.equals("null")){

					//Bug ID 4944762- getVisibleChildren for list-like components needed

					/*int cc = Windows.GetVisibleChildrenCount(vmidAC[0], vmidAC[1]);					
					if (cc > 0){
						el.children = new ArrayList<UIAElement>(cc);
						long[] children = Windows.GetVisibleChildren(vmidAC[0],vmidAC[1]);
						for (int i=0; i<children.length; i++)
							accessBridgeDescend(hwnd,el,vmidAC[0],children[i]);
					}*/

					long childAC;
					int c = new Integer(childrenCount).intValue();
					el.children = new ArrayList<UIAElement>(c);

					if(role.contains("table")) {

						int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);

						childsOfJavaTable = tableRowColumn[0] * tableRowColumn[1];

						if(visualizeJavaTable) {
							visualizeJavaTableDescend(hwnd, el, vmid, ac);
						}

						else if(updateActionJavaTable){
							updateActionJavaTable(hwnd, el, vmid, ac);
						}

					}
					else {

						for (int i=0; i<c; i++){
							childAC =  Windows.GetAccessibleChildFromContext(vmidAC[0],vmidAC[1],i);
							accessBridgeDescend(hwnd,el,vmidAC[0],childAC);
						}

					}
				}

			}

		}

		return modalElement;
	}

	private static UIAElement updateActionJavaTable(long hwnd, UIAElement parent, long vmid, long ac) {

		int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);

		int randomRow = new Random().nextInt(tableRowColumn[0]);
		int randomColumn = new Random().nextInt(tableRowColumn[1]);

		//Select a random cell to generate an action
		Windows.SelectTableCell(vmid, ac, randomRow, randomColumn);

		long[] vmidAC;
		if (vmid == 0)
			vmidAC = Windows.GetAccessibleContext(hwnd);
		else
			vmidAC = new long[]{ vmid,ac };

		if (vmidAC != null){

			Object[] props = Windows.GetTableCellProperties(vmid, ac, randomRow, randomColumn);

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

				el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);

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

				parent.root.hwndMap.put(el.hwnd, el);

			}
		}


		return parent;
	}

	private static UIAElement visualizeJavaTableDescend (long hwnd, UIAElement parent, long vmid, long ac) {

		int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);

		int checkRows = numberOfRowsToVisualizeJavaTable;
		if(tableRowColumn[0]<checkRows)
			checkRows = tableRowColumn[0];

		for (int i=0; i<checkRows; i++){

			//Select one row of the table to access properly to the cell element properties
			Windows.SelectTableRow(vmid, ac, i);

			long[] vmidAC;
			if (vmid == 0)
				vmidAC = Windows.GetAccessibleContext(hwnd);
			else
				vmidAC = new long[]{ vmid,ac };

			if (vmidAC != null){
				for(int j=0; j<tableRowColumn[1]; j++) {
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

						el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);

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

						parent.root.hwndMap.put(el.hwnd, el);

					}
				}
			}
		}

		return parent;

	}


}
