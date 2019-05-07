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

import org.fruit.alayer.Rect;

public class BuilderUIAutomation {
	
	private BuilderUIAutomation() {}

	/**
	 * Return a modal widget if detected
	 * 
	 * @param hwnd
	 * @param uiaPtr
	 * @param parent
	 * @return
	 */
	public static UIAElement UIAutomationDescend(long hwnd, long uiaPtr, UIAElement parent){
		
		if(uiaPtr == 0)
			return null;

		UIAElement modalElement = null;

		UIAElement el = new UIAElement(parent);
		parent.children.add(el);

		el.ctrlId = Windows.IUIAutomationElement_get_ControlType(uiaPtr, true);			
		el.hwnd = Windows.IUIAutomationElement_get_NativeWindowHandle(uiaPtr, true);

		// bounding rectangle
		long r[] = Windows.IUIAutomationElement_get_BoundingRectangle(uiaPtr, true);
		if(r != null && r[2] - r[0] >= 0 && r[3] - r[1] >= 0)
			el.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);

		el.enabled = Windows.IUIAutomationElement_get_IsEnabled(uiaPtr, true);
		el.name = Windows.IUIAutomationElement_get_Name(uiaPtr, true);
		el.helpText = Windows.IUIAutomationElement_get_HelpText(uiaPtr, true); 
		el.automationId = Windows.IUIAutomationElement_get_AutomationId(uiaPtr, true);
		el.className = Windows.IUIAutomationElement_get_ClassName(uiaPtr, true); 
		el.providerDesc = Windows.IUIAutomationElement_get_ProviderDescription(uiaPtr, true); 
		el.frameworkId = Windows.IUIAutomationElement_get_FrameworkId(uiaPtr, true); 
		el.orientation = Windows.IUIAutomationElement_get_Orientation(uiaPtr, true);
		el.isContentElement = Windows.IUIAutomationElement_get_IsContentElement(uiaPtr, true);
		el.isControlElement = Windows.IUIAutomationElement_get_IsControlElement(uiaPtr, true);
		el.hasKeyboardFocus = Windows.IUIAutomationElement_get_HasKeyboardFocus(uiaPtr, true); 
		el.isKeyboardFocusable = Windows.IUIAutomationElement_get_IsKeyboardFocusable(uiaPtr, true);
		el.accessKey = Windows.IUIAutomationElement_get_AccessKey(uiaPtr, true);
		el.acceleratorKey = Windows.IUIAutomationElement_get_AcceleratorKey(uiaPtr, true);
		el.valuePattern = Windows.IUIAutomationElement_get_ValuePattern(uiaPtr, Windows.UIA_ValuePatternId);

		parent.root.hwndMap.put(el.hwnd, el);

		// get extra infos from windows
		if(el.ctrlId == Windows.UIA_WindowControlTypeId){
			//long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true);
			long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true);
			if(uiaWndPtr != 0){
				el.wndInteractionState = Windows.IUIAutomationWindowPattern_get_WindowInteractionState(uiaWndPtr, true);
				el.blocked = (el.wndInteractionState != Windows.WindowInteractionState_ReadyForUserInteraction);
				el.isTopmostWnd = Windows.IUIAutomationWindowPattern_get_IsTopmost(uiaWndPtr, true);
				el.isModal = Windows.IUIAutomationWindowPattern_get_IsModal(uiaWndPtr, true);
				Windows.IUnknown_Release(uiaWndPtr);
			}
			el.culture = Windows.IUIAutomationElement_get_Culture(uiaPtr, true);
		}

		if (!el.isModal && el.automationId != null &&
				(el.automationId.contains("messagebox") || el.automationId.contains("window"))){ // try to detect potential modal window!
			modalElement = markModal(el);
		}
		Object obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_IsScrollPatternAvailablePropertyId, false); //true); 
		el.scrollPattern = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
		if (el.scrollPattern){
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr,  Windows.UIA_ScrollHorizontallyScrollablePropertyId, false);
			el.hScroll = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr,  Windows.UIA_ScrollVerticallyScrollablePropertyId, false);
			el.vScroll = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollHorizontalViewSizePropertyId, false);
			el.hScrollViewSize = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollVerticalViewSizePropertyId, false);
			el.vScrollViewSize = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollHorizontalScrollPercentPropertyId, false);
			el.hScrollPercent = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollVerticalScrollPercentPropertyId, false);
			el.vScrollPercent = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
		}	

		// descend children
		long uiaChildrenPtr = Windows.IUIAutomationElement_GetCachedChildren(uiaPtr);
		if (StateFetcher.releaseCachedAutomatinElement)
			Windows.IUnknown_Release(uiaPtr);

		if(uiaChildrenPtr != 0){
			long count = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPtr);

			if(count > 0){
				el.children = new ArrayList<UIAElement>((int)count);

				for(int i = 0; i < count; i++){
					long ptrChild = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPtr, i);
					if(ptrChild != 0){
						UIAElement modalE = UIAutomationDescend(hwnd, ptrChild, el);
						if (modalE != null && modalElement == null) // parent-modal is preferred to child-modal
							modalElement = modalE;						
					}
				}
			}
			Windows.IUnknown_Release(uiaChildrenPtr);
		}

		return modalElement;
	}

	//(mark a proper widget as modal)
	private static UIAElement markModal(UIAElement element){
		if (element == null)
			return null; // no proper widget found to mark as modal
		else if (element.ctrlId != Windows.UIA_WindowControlTypeId && element.ctrlId != Windows.UIA_PaneControlTypeId &&
				element.ctrlId != Windows.UIA_GroupControlTypeId){
			return markModal(element.parent);
		}
		else {
			element.isModal = true;
			return element;
		}
	}

}
