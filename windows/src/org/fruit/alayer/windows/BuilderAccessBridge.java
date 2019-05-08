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

	public static boolean customJavaSwingButtons;

	public static boolean visualizeJavaTable;
	public static int numberOfRowsToVisualizeJavaTable = 1;

	public static boolean updateActionJavaTable;
	public static int childsOfJavaTable = 0;

	public static boolean searchNonVisibleJavaWindows;

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

				el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);
				if (role.equals(AccessBridgeControlTypes.ACCESSIBLE_DIALOG)){
					el.isTopLevelContainer = true;
					modalElement = el;
				}

				el.ctrlId = AccessBridgeControlTypes.toUIA(role);				
				if (el.ctrlId == Windows.UIA_MenuControlTypeId)
					el.isTopLevelContainer = true;
				else if (el.ctrlId == Windows.UIA_EditControlTypeId)
					el.isKeyboardFocusable = true;

				el.name = name;				
				el.helpText = description;
				el.automationId = role;

				el.enabled = accesibleStateSet.contains("enabled");

				el.blocked = !accesibleStateSet.contains("showing");

				if(role.contains("internal frame")){
					if(customJavaSwingButtons) 
						childrenCount+=3; //Minimize,Maximize,Close
					if(!existTopJavaInternalFrame) {
						existTopJavaInternalFrame = true;
						el.isTopJavaInternalFrame = true;
					}
				}

				if(role.contains("spinbox") && customJavaSwingButtons) {
					childrenCount+=2; //Increase,Decrease
				}

				parent.root.hwndMap.put(el.hwnd, el);

				if(childrenCount != -1 && childrenCount != 0){

					//Bug ID 4944762- getVisibleChildren for list-like components needed

					/*int cc = Windows.GetVisibleChildrenCount(vmidAC[0], vmidAC[1]);					
					if (cc > 0){
						el.children = new ArrayList<UIAElement>(cc);
						long[] children = Windows.GetVisibleChildren(vmidAC[0],vmidAC[1]);
						for (int i=0; i<children.length; i++)
							accessBridgeDescend(hwnd,el,vmidAC[0],children[i]);
					}*/

					long childAC;
					int c = childrenCount;
					el.children = new ArrayList<UIAElement>(c);

					//Swing Java Tables needs a custom descend to obtain properly cells values
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

						//Remove internal frame childs, we are going to create manually
						if(role.contains("internal frame") && customJavaSwingButtons) {
							c -= 3; 
							createButtonsForJInternalFrame(el);
						}
						//Remove internal frame childs, we are going to create manually
						else if(role.contains("spinbox") && customJavaSwingButtons) {
							c -= 2;
							createButtonsForSpinBox(el);
						}

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

	/**
	 * Select randomly a cell of the Swing Java Table, to obtain their properties
	 */
	private static UIAElement updateActionJavaTable(long hwnd, UIAElement parent, long vmid, long ac) {

		int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);

		int randomRow = new Random().nextInt(tableRowColumn[0]);
		int randomColumn = new Random().nextInt(tableRowColumn[1]);

		//Select a random cell to obtain the properties and allow to generate an action
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

				//Custom name to allow the subsequent search into deriveActions methods
				el.automationId = "TableCell "+role;

				el.enabled = true;

				el.blocked = false;

				parent.root.hwndMap.put(el.hwnd, el);

			}
		}


		return parent;
	}

	/**
	 * User can choose the number of rows to visualize into settings protocol
	 * Then we are going to use that number to select the rows and obtain properly the widget cell properties
	 */
	private static UIAElement visualizeJavaTableDescend (long hwnd, UIAElement parent, long vmid, long ac) {

		int[] tableRowColumn = Windows.GetNumberOfTableRowColumn(vmid, ac);

		int checkRows = numberOfRowsToVisualizeJavaTable;
		if(tableRowColumn[0]<checkRows)
			checkRows = tableRowColumn[0];

		for (int i=0; i<checkRows; i++){

			//Select one by one the rows of the table to access properly to the cell element properties
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

	/**
	 * Create manually 3 buttons to represent the Minimize, Maximize and Close options of JInternalFrames
	 * TODO: Lazy solution, move or create in other way
	 */
	private static UIAElement createButtonsForJInternalFrame(UIAElement parent){

		double posY = parent.rect.y() + (11);

		double minimizePosX = parent.rect.x() + parent.rect.width() - 67;
		double maximizePosX = parent.rect.x() + parent.rect.width() - 52;
		double closePosX = parent.rect.x() + parent.rect.width() - 22;

		Rect rectMinimize = null;
		Rect rectMaximize = null;
		Rect rectClose = null;
		try {
			rectMinimize = Rect.from(new Double(minimizePosX).doubleValue(), new Double(posY).doubleValue(),
					new Double(10).doubleValue(), new Double(10).doubleValue());

			rectMaximize = Rect.from(new Double(maximizePosX).doubleValue(), new Double(posY).doubleValue(),
					new Double(10).doubleValue(), new Double(10).doubleValue());

			rectClose = Rect.from(new Double(closePosX).doubleValue(), new Double(posY).doubleValue(),
					new Double(10).doubleValue(), new Double(10).doubleValue());
		} catch (Exception e){}

		UIAElement elMinimize = new UIAElement(parent);
		UIAElement elMaximize = new UIAElement(parent);
		UIAElement elClose = new UIAElement(parent);

		parent.children.add(elMinimize);
		parent.children.add(elMaximize);
		parent.children.add(elClose);

		elMinimize.rect = rectMinimize;
		elMaximize.rect = rectMaximize;
		elClose.rect = rectClose;

		elMinimize.hwnd = parent.hwnd;
		elMaximize.hwnd = parent.hwnd;
		elClose.hwnd = parent.hwnd;

		elMinimize.ctrlId = Windows.UIA_ButtonControlTypeId;
		elMaximize.ctrlId = Windows.UIA_ButtonControlTypeId;
		elClose.ctrlId = Windows.UIA_ButtonControlTypeId;

		elMinimize.name = "Minimize";
		elMaximize.name = "Maximize";	
		elClose.name = "Close";	

		elMinimize.automationId ="Button";
		elMaximize.automationId ="Button";
		elClose.automationId ="Button";

		if(parent.enabled){
			elMinimize.enabled = true;
			elMaximize.enabled = true;
			elClose.enabled = true;
		}

		if(!parent.blocked){
			elMinimize.blocked = false;
			elMaximize.blocked = false;
			elClose.blocked = false;
		}

		parent.root.hwndMap.put(elMinimize.hwnd, elMinimize);
		parent.root.hwndMap.put(elMaximize.hwnd, elMaximize);
		parent.root.hwndMap.put(elClose.hwnd, elClose);


		return parent;
	}

	/**
	 * Create manually 2 buttons to represent the Increase and Decrease options of SpinBoxes
	 * TODO: Lazy solution, move or create in other way
	 */
	private static UIAElement createButtonsForSpinBox(UIAElement parent){

		double posX = parent.rect.x() + parent.rect.width() - 13;

		double IncreaseY = parent.rect.y() + parent.rect.height() - 23;
		double decreaseY = parent.rect.y() + parent.rect.height() - 9;

		Rect rectIncrease = null;
		Rect rectDecrease = null;
		try {
			rectIncrease = Rect.from(new Double(posX).doubleValue(), new Double(IncreaseY).doubleValue(),
					new Double(10).doubleValue(), new Double(10).doubleValue());

			rectDecrease = Rect.from(new Double(posX).doubleValue(), new Double(decreaseY).doubleValue(),
					new Double(10).doubleValue(), new Double(10).doubleValue());
		} catch (Exception e){}

		UIAElement elIncrease = new UIAElement(parent);
		UIAElement elDecrease = new UIAElement(parent);

		parent.children.add(elIncrease);
		parent.children.add(elDecrease);

		elIncrease.rect = rectIncrease;
		elDecrease.rect = rectDecrease;

		elIncrease.hwnd = parent.hwnd;
		elDecrease.hwnd = parent.hwnd;

		elIncrease.ctrlId = Windows.UIA_ButtonControlTypeId;
		elDecrease.ctrlId = Windows.UIA_ButtonControlTypeId;

		elIncrease.name = "Increase";
		elDecrease.name = "Maximize";

		elIncrease.automationId ="Button";
		elDecrease.automationId ="Button";

		if(parent.enabled){
			elIncrease.enabled = true;
			elDecrease.enabled = true;
		}

		if(!parent.blocked){
			elIncrease.blocked = false;
			elDecrease.blocked = false;
		}

		parent.root.hwndMap.put(elIncrease.hwnd, elIncrease);
		parent.root.hwndMap.put(elDecrease.hwnd, elDecrease);

		return parent;
	}

}
