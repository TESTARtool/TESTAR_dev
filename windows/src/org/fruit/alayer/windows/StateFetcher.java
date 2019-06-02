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
 *  @author Urko Rueda (refactor from UIAStateBuilder)
 */
package org.fruit.alayer.windows;

import org.fruit.Util;
import org.fruit.alayer.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class StateFetcher implements Callable<UIAState>{
	
	private final SUT system;

	transient long automationPointer, cacheRequestPointer;
	// begin by urueda
	private boolean releaseCachedAutomatinElement;
	
	private boolean accessBridgeEnabled;
	
	private static Pattern sutProcessesMatcher;
	// end by urueda
	
	public StateFetcher(SUT system, long automationPointer, long cacheRequestPointer,
						boolean accessBridgeEnabled, String SUTProcesses){		
		this.system = system;
		this.automationPointer = automationPointer;
		this.cacheRequestPointer = cacheRequestPointer;
		// begin by urueda
		this.accessBridgeEnabled = accessBridgeEnabled;
		if (SUTProcesses == null || SUTProcesses.isEmpty())
			StateFetcher.sutProcessesMatcher = null;
		else
			StateFetcher.sutProcessesMatcher = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);		
		// end by urueda
	}
	
	// by urueda (refactor)
	public static UIARootElement buildRoot(SUT system){
		UIARootElement uiaRoot = new UIARootElement();	
		uiaRoot.isRunning = system.isRunning();

		long[] coordinates = Windows.GetMonitorInfo(Windows.GetPrimaryMonitorHandle());
		if(coordinates[3] - coordinates[1] >= 0 && coordinates[4] - coordinates[2] >= 0)
			uiaRoot.rect = Rect.fromCoordinates(coordinates[1], coordinates[2], coordinates[3], coordinates[4]);
		uiaRoot.timeStamp = System.currentTimeMillis();
		uiaRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
		uiaRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;		
		
		return uiaRoot;
	}

	public UIAState call() throws Exception {				
		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);		

		// first build the UIAElement skeleton.
		// this means fetching information from the Windows Automation API about all the elements in the Automation Tree
		UIARootElement uiaRoot = buildSkeleton(system);

		// next we use the created Automation tree, with the uiaRoot as its base, to create the Testar widget tree
		UIAState root = createWidgetTree(uiaRoot);
		root.set(Tags.Role, Roles.Process);
		root.set(Tags.NotResponding, false);
		// begin by urueda
		for (Widget w : root)
			w.set(Tags.Path,Util.indexString(w));
		if (system != null && (root == null || root.childCount() == 0) && system.getNativeAutomationCache() != null)
			system.getNativeAutomationCache().releaseCachedAutomationElements(); // prevent SUT UI not ready due to caching
		// end by urueda
		Windows.CoUninitialize();
		
		return root;
	}
	
	/**
	 * Checks whether a window conforms to the SUT.
	 * @param hwnd A window.
	 * @return true if the window conforms to the SUT, false otherwise.
	 * @author urueda
	 */
	private boolean isSUTProcess(long hwnd){
		if (StateFetcher.sutProcessesMatcher == null)
			return false;
		
		String processName = Windows.GetProcessNameFromHWND(hwnd);
		
		if (processName != null && !(processName.isEmpty()) && StateFetcher.sutProcessesMatcher.matcher(processName).matches())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param system
	 * @return
	 */
	private UIARootElement buildSkeleton(SUT system){
		UIARootElement uiaRoot = buildRoot(system);

		if(!uiaRoot.isRunning)
			return uiaRoot;

		uiaRoot.pid = system.get(Tags.PID);
		//uiaRoot.isForeground = WinProcess.isForeground(uiaRoot.pid);
		
		// find all visible top level windows on the desktop
		Iterable<Long> visibleTopLevelWindowHandles = this.getVisibleTopLevelWindowHandles();
		
		UIAElement modalElement = null; // by urueda

		// descend the root windows which belong to our process, using UIAutomation
		uiaRoot.children = new ArrayList<UIAElement>();
		boolean isOwnedWindow;
		long windowProcessId;
		List<Long> ownedWindows = new ArrayList<Long>();
		for(long windowHandle : visibleTopLevelWindowHandles){
			isOwnedWindow = Windows.GetWindow(windowHandle, Windows.GW_OWNER) != 0;
			windowProcessId = Windows.GetWindowProcessId(windowHandle);

			// check if the window process id matches our SUT process id or if it is a sub-process of the SUT process
			if (windowProcessId == uiaRoot.pid || isSUTProcess(windowHandle)){ // by urueda
				uiaRoot.isForeground = uiaRoot.isForeground || WinProcess.isForeground(windowProcessId); // by urueda ( SUT as a set of windows/processes )
				if(!isOwnedWindow){
					//uiaDescend(uiaCacheWindowTree(windowHandle), uiaRoot);
					// by urueda
					modalElement = this.accessBridgeEnabled ? abDescend(windowHandle, uiaRoot, 0, 0) :
															  uiaDescend(windowHandle, uiaCacheWindowTree(windowHandle), uiaRoot);
				} else
					ownedWindows.add(windowHandle);
			}
		}
		
		// if UIAutomation missed an owned window, we'll collect it here
		for(long windowHandle : ownedWindows){
			if(!uiaRoot.windowHandleMap.containsKey(windowHandle)){
				//uiaDescend(uiaCacheWindowTree(windowHandle), uiaRoot);
				UIAElement modalE;
				// begin by urueda
				if ((modalE = this.accessBridgeEnabled ? abDescend(windowHandle, uiaRoot, 0, 0) :
														 uiaDescend(windowHandle, uiaCacheWindowTree(windowHandle), uiaRoot)) != null)
					modalElement = modalE;
				// end by urueda
			}
		}

		// set z-indices for the windows
		int z = 0;
		for(long windowHandle : visibleTopLevelWindowHandles){
			//long exStyle = Windows.GetWindowLong(windowHandle, Windows.GWL_EXSTYLE);
			//if((exStyle & Windows.WS_EX_NOACTIVATE) != 0)
			//	System.out.println(windowHandle  + "   " + Windows.GetWindowText(windowHandle) + "   " + Windows.GetClassName(windowHandle));

			UIAElement window = uiaRoot.windowHandleMap.get(windowHandle);

			// if we didn't encounter the window yet, it will be a foreign window

			// begin by wcoux (Spy mode layering through transparent window)
			// Get the title of the window - if the window is Testar's Spy window, skip it. Otherwise,
			// the HitTest function will not work properly: it doesn't actually hit test, it checks if windows are
			// on top of the SUT and the Java implementation of the Spy mode drawing is an invisible always on top window.
			/*String windowTitle = Windows.GetWindowText(windowHandle);

				if (windowTitle != null && Objects.equals(windowTitle, "Testar - Spy window")) {
					continue;
				}*/
			// end by wcoux
			
			if(window == null){
				window = new UIAElement(uiaRoot);
				uiaRoot.children.add(window);
				window.ignore = true;
				window.windowHandle = windowHandle;
				long r[] = Windows.GetWindowRect(windowHandle);
				if(r[2] - r[0] >= 0 && r[3] - r[1] >= 0)
					window.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
				window.ctrlId = Windows.UIA_WindowControlTypeId;
				uiaRoot.windowHandleMap.put(windowHandle, window);
			}
						
			window.zindex = z++;
						
			if(window.ctrlId == Windows.UIA_MenuControlTypeId || window.ctrlId == Windows.UIA_WindowControlTypeId || window.parent == uiaRoot)
				window.isTopLevelContainer = true;
				
		}
		
		calculateZIndices(uiaRoot);
		buildTLCMap(uiaRoot);
		markBlockedElements(uiaRoot);

		markBlockedElements(uiaRoot,modalElement); // by urueda		

		return uiaRoot;
	}

	/* lists all visible top level windows in ascending z-order (foreground window last) */
	private Iterable<Long> getVisibleTopLevelWindowHandles(){
		Deque<Long> ret = new ArrayDeque<Long>();
		long windowHandle = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

		while(windowHandle != 0){
			if(Windows.IsWindowVisible(windowHandle)){
				long exStyle = Windows.GetWindowLong(windowHandle, Windows.GWL_EXSTYLE);
				if((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0){
					ret.addFirst(windowHandle);
					// begin by urueda
					if (System.getProperty("DEBUG_WINDOWS_PROCESS_NAMES") != null)
						System.out.println("<" + windowHandle + "> window' process name <" + Windows.GetProcessNameFromHWND(windowHandle) + ">");
					// end by urueda					
				}				
			}
			windowHandle = Windows.GetNextWindow(windowHandle, Windows.GW_HWNDNEXT);
		}
		
		System.clearProperty("DEBUG_WINDOWS_PROCESS_NAMES"); // by urueda
		
		return ret;
	}
	
	/* fire up the cache request */
	private long uiaCacheWindowTree(long windowHandle){
		// begin by urueda
		long aep = Long.MIN_VALUE;
		if (system.getNativeAutomationCache() != null)
			aep = system.getNativeAutomationCache().getCachedAutomationElement(windowHandle, automationPointer, cacheRequestPointer);
		releaseCachedAutomatinElement = (aep == Long.MIN_VALUE);
		if (releaseCachedAutomatinElement) // cache miss
			return Windows.IUIAutomation_ElementFromHandleBuildCache(automationPointer, windowHandle, cacheRequestPointer);
		else
			return aep;
		// end by urueda
	}

	private void buildTLCMap(UIARootElement root){
		ElementMap.Builder builder = ElementMap.newBuilder();
		buildTLCMap(builder, root);
		root.elementMap = builder.build();
	}

	private void buildTLCMap(ElementMap.Builder builder, UIAElement el){
		if(el.isTopLevelContainer)
			builder.addElement(el);			

		for(int i = 0; i < el.children.size(); i++)
			buildTLCMap(builder, el.children.get(i));
	}

	private UIAElement uiaDescend(long hwnd, long uiaCachePointer, UIAElement parent){ // by urueda (returns a modal widget if detected)
		if(uiaCachePointer == 0)
			//return;
			return null; // by urueda

		UIAElement modalElement = null; // by urueda

		UIAElement el = new UIAElement(parent);
		parent.children.add(el);

		el.ctrlId = Windows.IUIAutomationElement_get_ControlType(uiaCachePointer, true);
		el.windowHandle = Windows.IUIAutomationElement_get_NativeWindowHandle(uiaCachePointer, true);

		// bounding rectangle
		long r[] = Windows.IUIAutomationElement_get_BoundingRectangle(uiaCachePointer, true);
		if(r != null && r[2] - r[0] >= 0 && r[3] - r[1] >= 0)
			el.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);

		el.enabled = Windows.IUIAutomationElement_get_IsEnabled(uiaCachePointer, true);
		el.name = Windows.IUIAutomationElement_get_Name(uiaCachePointer, true);
		el.helpText = Windows.IUIAutomationElement_get_HelpText(uiaCachePointer, true);
		el.automationId = Windows.IUIAutomationElement_get_AutomationId(uiaCachePointer, true);
		el.className = Windows.IUIAutomationElement_get_ClassName(uiaCachePointer, true);
		el.providerDesc = Windows.IUIAutomationElement_get_ProviderDescription(uiaCachePointer, true);
		el.frameworkId = Windows.IUIAutomationElement_get_FrameworkId(uiaCachePointer, true);
		el.orientation = Windows.IUIAutomationElement_get_Orientation(uiaCachePointer, true);
		el.isContentElement = Windows.IUIAutomationElement_get_IsContentElement(uiaCachePointer, true);
		el.isControlElement = Windows.IUIAutomationElement_get_IsControlElement(uiaCachePointer, true);
		el.hasKeyboardFocus = Windows.IUIAutomationElement_get_HasKeyboardFocus(uiaCachePointer, true);
		el.isKeyboardFocusable = Windows.IUIAutomationElement_get_IsKeyboardFocusable(uiaCachePointer, true);
		el.accessKey = Windows.IUIAutomationElement_get_AccessKey(uiaCachePointer, true);
		el.acceleratorKey = Windows.IUIAutomationElement_get_AcceleratorKey(uiaCachePointer, true);
		el.valuePattern = Windows.IUIAutomationElement_get_ValuePattern(uiaCachePointer, Windows.UIA_ValuePatternId);

		parent.root.windowHandleMap.put(el.windowHandle, el);

		// get extra infos from windows
		if(el.ctrlId == Windows.UIA_WindowControlTypeId){
			//long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true);
			long uiaWindowPointer = Windows.IUIAutomationElement_GetPattern(uiaCachePointer, Windows.UIA_WindowPatternId, true); // by urueda
			if(uiaWindowPointer != 0){
				el.wndInteractionState = Windows.IUIAutomationWindowPattern_get_WindowInteractionState(uiaWindowPointer, true);
				el.blocked = (el.wndInteractionState != Windows.WindowInteractionState_ReadyForUserInteraction);
				el.isTopmostWnd = Windows.IUIAutomationWindowPattern_get_IsTopmost(uiaWindowPointer, true);
				el.isModal = Windows.IUIAutomationWindowPattern_get_IsModal(uiaWindowPointer, true);
				Windows.IUnknown_Release(uiaWindowPointer);
			}
			el.culture = Windows.IUIAutomationElement_get_Culture(uiaCachePointer, true);
		}

		// check if we missed detection of a modal window
		if (!el.isModal && el.automationId != null &&
				(el.automationId.contains("messagebox") || el.automationId.contains("window"))){ // try to detect potential modal window!
			modalElement = markModal(el);
		}

		// get some non-cached property values for elements implementing the scroll pattern
		Object obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsScrollPatternAvailablePropertyId, false); //true);
		el.scrollPattern = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
		if (el.scrollPattern){
			//el.scrollbarInfo = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_CLIENT);
			//el.scrollbarInfoH = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_HSCROLL);
			//el.scrollbarInfoV = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_VSCROLL);
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer,  Windows.UIA_ScrollHorizontallyScrollablePropertyId, false);
			el.hScroll = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer,  Windows.UIA_ScrollVerticallyScrollablePropertyId, false);
			el.vScroll = obj instanceof Boolean ? ((Boolean)obj).booleanValue() : false;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollHorizontalViewSizePropertyId, false);
			el.hScrollViewSize = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollVerticalViewSizePropertyId, false);
			el.vScrollViewSize = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollHorizontalScrollPercentPropertyId, false);
			el.hScrollPercent = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollVerticalScrollPercentPropertyId, false);
			el.vScrollPercent = obj instanceof Double ? ((Double)obj).doubleValue() : -1.0;
		}
		// end by urueda	

		// get the properties for potential child elements
		long uiaChildrenPointer = Windows.IUIAutomationElement_GetCachedChildren(uiaCachePointer);
		if (releaseCachedAutomatinElement) // by urueda
			Windows.IUnknown_Release(uiaCachePointer);

		if(uiaChildrenPointer != 0){
			long nrOfChildren = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPointer);

			if(nrOfChildren > 0){
				el.children = new ArrayList<UIAElement>((int)nrOfChildren);

				for(int i = 0; i < nrOfChildren; i++){
					long childPointer = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPointer, i);
					if(childPointer != 0){
						// begin by urueda
						UIAElement modalE = uiaDescend(hwnd, childPointer, el);
						if (modalE != null && modalElement == null) // parent-modal is preferred to child-modal
							modalElement = modalE;
						// end by urueda							
					}
				}
			}
			Windows.IUnknown_Release(uiaChildrenPointer);
		}
		
		return modalElement; // by urueda
	}
	
	// by urueda (through AccessBridge)
	private UIAElement abDescend(long hwnd, UIAElement parent, long vmid, long ac){
		UIAElement modalElement = null;

		long[] vmidAC;
		if (vmid == 0)
			vmidAC = Windows.GetAccessibleContext(hwnd);
		else
			vmidAC = new long[]{ vmid,ac };
		if (vmidAC != null){			
			Object[] props = Windows.GetAccessibleContextProperties(vmidAC[0],vmidAC[1]);
			if (props != null){
				String role 		 = (String) props[0],
					   name 		 = (String) props[1],
					   description 	 = (String) props[2],
					   x 			 = (String) props[3],
					   y 			 = (String) props[4],
					   width 		 = (String) props[5],
					   height 		 = (String) props[6],
					   indexInParent = (String) props[7],
					   childrenCount = (String) props[8];

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

				el.windowHandle = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);
				if (role.equals(AccessBridgeControlTypes.ACCESSIBLE_DIALOG)){
					el.isTopLevelContainer = true;
					modalElement = el;
				}
				el.ctrlId = AccessBridgeControlTypes.toUIA(role);				
				if (el.ctrlId == Windows.UIA_MenuControlTypeId) // || el.ctrlId == Windows.UIA_WindowControlTypeId)
					el.isTopLevelContainer = true;
				else if (el.ctrlId == Windows.UIA_EditControlTypeId)
					el.isKeyboardFocusable = true;
				el.name = name;				
				el.helpText = description;
				// el.enabled = true;
				parent.root.windowHandleMap.put(el.windowHandle, el);
				
				
				//MenuItems are duplicate with AccessBridge when we open one Menu or combo box
				if(!role.equals("menu") && !role.equals("combo box")
					&& childrenCount != null && !childrenCount.isEmpty() && !childrenCount.equals("null")){
					/*int cc = Windows.GetVisibleChildrenCount(vmidAC[0], vmidAC[1]);					
					if (cc > 0){
						el.children = new ArrayList<UIAElement>(cc);
						long[] children = Windows.GetVisibleChildren(vmidAC[0],vmidAC[1]);
						for (int i=0; i<children.length; i++)
							abDescend(windowHandle,el,vmidAC[0],children[i]);
					}*/
					
						long childAC;
						int c = new Integer(childrenCount).intValue();
						el.children = new ArrayList<UIAElement>(c);
						for (int i=0; i<c; i++){
							childAC =  Windows.GetAccessibleChildFromContext(vmidAC[0],vmidAC[1],i);
							abDescend(hwnd,el,vmidAC[0],childAC);
						}
				}

			}
		}
				
		return modalElement;
		
	}

	// by urueda (mark a proper widget as modal)
	private UIAElement markModal(UIAElement element){
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

	private void markBlockedElements(UIAElement element){
		for(UIAElement c : element.children){
			if(element.blocked && !(c.ctrlId == Windows.UIA_WindowControlTypeId && c.blocked == false))
				c.blocked = true;
			markBlockedElements(c);
		}
	}

	// by urueda
	private void markBlockedElements(UIAElement element, UIAElement modalElement){
		if (modalElement != null){
			for(UIAElement c : element.children){
				if (c != modalElement){
					c.blocked = true;
					markBlockedElements(c,modalElement);
				}
			}				
		}
	}

	private void calculateZIndices(UIAElement el){
		if (el.parent != null){
			if (this.accessBridgeEnabled) // TLC are not exposed as visible desktop controls
				el.zindex = el.parent.zindex + (el.parent.isTopLevelContainer ? 1 : 0);
			else if (!el.isTopLevelContainer)		
				el.zindex = el.parent.zindex;
		}
									
		for(int i = 0; i < el.children.size(); i++)
			calculateZIndices(el.children.get(i));
	}

	private UIAState createWidgetTree(UIARootElement root){
		UIAState state = new UIAState(root);
		root.backRef = state;
		for(UIAElement childElement : root.children){
			if(!childElement.ignore)
				createWidgetTree(state, childElement);
		}
		return state;
	}

	private void createWidgetTree(UIAWidget parent, UIAElement element){
		UIAWidget w = parent.root().addChild(parent, element);
		element.backRef = w;
		for(UIAElement child : element.children)
			createWidgetTree(w, child);
	}
	
}
