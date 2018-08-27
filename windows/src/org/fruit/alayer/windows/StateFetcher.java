/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
	transient long pAutomation, pCacheRequest;
	private boolean releaseCachedAutomatinElement;
	private boolean accessBridgeEnabled;
	private static Pattern sutProcessesMatcher;
	
	public StateFetcher(SUT system, long pAutomation, long pCacheRequest,
						boolean accessBridgeEnabled, String SUTProcesses){		
		this.system = system;
		this.pAutomation = pAutomation;
		this.pCacheRequest = pCacheRequest;
		this.accessBridgeEnabled = accessBridgeEnabled;
		if (SUTProcesses == null || SUTProcesses.isEmpty()) {
			System.out.println("StateFetcher: sutProcessMatcher = null");
			StateFetcher.sutProcessesMatcher = null;
		}else {
			System.out.println("StateFetcher: sutProcessMatcher - SUTProcesses is not empty in settings");
			StateFetcher.sutProcessesMatcher = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);
		}
	}

	public static UIARootElement buildRoot(SUT system){
		System.out.println("DEBUG: StateFetcher.buildRoot()");
		UIARootElement uiaRoot = new UIARootElement();	
		uiaRoot.isRunning = system.isRunning();

		long[] info = Windows.GetMonitorInfo(Windows.GetPrimaryMonitorHandle());
		if(info[3] - info[1] >= 0 && info[4] - info[2] >= 0)
			uiaRoot.rect = Rect.fromCoordinates(info[1], info[2], info[3], info[4]);
		uiaRoot.timeStamp = System.currentTimeMillis();
		uiaRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
		uiaRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;		
		
		return uiaRoot;
	}

	public UIAState call() throws Exception {				
		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);		
		System.out.println("DEBUG: StateFetcher.call()");
		UIARootElement uiaRoot = buildSkeletton(system);
				
		UIAState root = createWidgetTree(uiaRoot);
		root.set(Tags.Role, Roles.Process);
		root.set(Tags.NotResponding, false);
		for (Widget w : root)
			w.set(Tags.Path,Util.indexString(w));
		if (system != null && (root == null || root.childCount() == 0) && system.getNativeAutomationCache() != null)
			system.getNativeAutomationCache().releaseCachedAutomationElements(); // prevent SUT UI not ready due to caching
		Windows.CoUninitialize();
		
		return root;
	}
	
	/**
	 * Checks whether a window conforms to the SUT.
	 * @param hwnd A window.
	 * @return true if the window conforms to the SUT, false otherwise.
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
	private UIARootElement buildSkeletton(SUT system){
		System.out.println("DEBUG: StateFetcher.buildSkeletton()");
		UIARootElement uiaRoot = buildRoot(system);

		if(!uiaRoot.isRunning)
			return uiaRoot;

		uiaRoot.pid = system.get(Tags.PID);
		//uiaRoot.isForeground = WinProcess.isForeground(uiaRoot.pid);
		
		// find all visible top level windows on the desktop
		Iterable<Long> visibleTopLevelWindows = this.visibleTopLevelWindows();		
		
		UIAElement modalElement = null;

		// descend the root windows which belong to our process, using UIAutomation
		uiaRoot.children = new ArrayList<UIAElement>();
		boolean owned;
		long hwndPID;
		List<Long> ownedWindows = new ArrayList<Long>();
		for(long hwnd : visibleTopLevelWindows){
			owned = Windows.GetWindow(hwnd, Windows.GW_OWNER) != 0;
			//if (Windows.GetWindowProcessId(hwnd) == uiaRoot.pid){
			hwndPID = Windows.GetWindowProcessId(hwnd);
			System.out.println("DEBUG: visibleTopLevelWindow ["+hwnd+"], PID="+hwndPID+", SUT PID="+uiaRoot.pid);
			if (hwndPID == uiaRoot.pid || isSUTProcess(hwnd)){
				uiaRoot.isForeground = uiaRoot.isForeground || WinProcess.isForeground(hwndPID); // ( SUT as a set of windows/processes )
				if(!owned){
					//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
					modalElement = this.accessBridgeEnabled ? abDescend(hwnd, uiaRoot, 0, 0) :
															  uiaDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot);
				} else {
					System.out.println("DEBUG: adding owned window");
					ownedWindows.add(hwnd);
				}
			}else{
				System.out.println("DEBUG: not SUT window");
			}
		}
		
		// if UIAutomation missed an owned window, we'll collect it here
		for(long hwnd : ownedWindows){				 // TODO is this a bug? should we iterate through all windows, not ownedWindows?
			if(!uiaRoot.hwndMap.containsKey(hwnd)){
				//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
				UIAElement modalE;
				if ((modalE = this.accessBridgeEnabled ? abDescend(hwnd, uiaRoot, 0, 0) :
														 uiaDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot)) != null)
					modalElement = modalE;
			}
		}

		// set z-indices for the windows
		int z = 0;
		for(long hwnd : visibleTopLevelWindows){
			//long exStyle = Windows.GetWindowLong(hwnd, Windows.GWL_EXSTYLE);				
			//if((exStyle & Windows.WS_EX_NOACTIVATE) != 0)
			//	System.out.println(hwnd  + "   " + Windows.GetWindowText(hwnd) + "   " + Windows.GetClassName(hwnd));

			UIAElement wnd = uiaRoot.hwndMap.get(hwnd);

			// if we didn't encounter the window yet, it will be a foreign window

			// (Spy mode layering through transparent window)
			// Get the title of the window - if the window is Testar's Spy window, skip it. Otherwise,
			// the HitTest function will not work properly: it doesn't actually hit test, it checks if windows are
			// on top of the SUT and the Java implementation of the Spy mode drawing is an invisible always on top window.
			/*String windowTitle = Windows.GetWindowText(hwnd);

				if (windowTitle != null && Objects.equals(windowTitle, "Testar - Spy window")) {
					continue;
				}*/
			//
			
			if(wnd == null){
				wnd = new UIAElement(uiaRoot);
				uiaRoot.children.add(wnd);
				wnd.ignore = true;
				wnd.hwnd = hwnd;
				long r[] = Windows.GetWindowRect(hwnd);
				if(r[2] - r[0] >= 0 && r[3] - r[1] >= 0)
					wnd.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
				wnd.ctrlId = Windows.UIA_WindowControlTypeId;
				uiaRoot.hwndMap.put(hwnd, wnd);
			}
						
			wnd.zindex = z++;
						
			if(wnd.ctrlId == Windows.UIA_MenuControlTypeId || wnd.ctrlId == Windows.UIA_WindowControlTypeId || wnd.parent == uiaRoot)
				wnd.isTopLevelContainer = true;				
				
		}
		
		calculateZIndices(uiaRoot);
		buildTLCMap(uiaRoot);
		markBlockedElements(uiaRoot);

		markBlockedElements(uiaRoot,modalElement);

		return uiaRoot;
	}

	/* lists all visible top level windows in ascending z-order (foreground window last) */
	public static Iterable<Long> visibleTopLevelWindows(){
		Deque<Long> ret = new ArrayDeque<Long>();
		long hwnd = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

		while(hwnd != 0){
			if(Windows.IsWindowVisible(hwnd)){
				long exStyle = Windows.GetWindowLong(hwnd, Windows.GWL_EXSTYLE);				
				if((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0){
					ret.addFirst(hwnd);
					if (System.getProperty("DEBUG_WINDOWS_PROCESS_NAMES") != null)
						System.out.println("<" + hwnd + "> window' process name <" + Windows.GetProcessNameFromHWND(hwnd) + ">");

				}				
			}
			hwnd = Windows.GetNextWindow(hwnd, Windows.GW_HWNDNEXT);
		}
		
		System.clearProperty("DEBUG_WINDOWS_PROCESS_NAMES");
		
		return ret;
	}

	public static List<Long> getNewWindows(Iterable<Long> oldVisibleTopLevelWindows){
		List<Long> newWindows = new ArrayList<Long>();
		Iterable<Long> currentVisibleWindows = visibleTopLevelWindows();
		for(Long currentHwnd:currentVisibleWindows){
			boolean existedBefore = false;
			for(Long oldHwnd:oldVisibleTopLevelWindows){
				if(currentHwnd.longValue()==oldHwnd.longValue()){
//					System.out.println("DEBUG: window existed before "+oldHwnd+"=="+currentHwnd);
					existedBefore = true;
				}
//				else{
//					System.out.println("DEBUG: not SUT window "+oldHwnd+"!="+currentHwnd);
//				}
			}
			if(!existedBefore){
				System.out.println("SUT window: ["+currentHwnd+"]");
				newWindows.add(currentHwnd);
			}
		}
		return newWindows;
	}

	public static void printVisibleWindows(Iterable<Long> _visibleTopLevelWindows){
		for(Long hwnd:_visibleTopLevelWindows){
			System.out.println("Visible window ["+hwnd+"]");
		}
	}

	
	/* fire up the cache request */
	private long uiaCacheWindowTree(long hwnd){
		//return Windows.IUIAutomation_ElementFromHandleBuildCache(pAutomation, hwnd, pCacheRequest);
		long aep = Long.MIN_VALUE;
		if (system.getNativeAutomationCache() != null)
			aep = system.getNativeAutomationCache().getCachedAutomationElement(hwnd, pAutomation, pCacheRequest);
		releaseCachedAutomatinElement = (aep == Long.MIN_VALUE);
		if (releaseCachedAutomatinElement) // cache miss
			return Windows.IUIAutomation_ElementFromHandleBuildCache(pAutomation, hwnd, pCacheRequest);
		else
			return aep;
	}

	private void buildTLCMap(UIARootElement root){
		ElementMap.Builder builder = ElementMap.newBuilder();
		buildTLCMap(builder, root);
		root.tlc = builder.build();		
	}

	private void buildTLCMap(ElementMap.Builder builder, UIAElement el){
		if(el.isTopLevelContainer)
			builder.addElement(el);			

		for(int i = 0; i < el.children.size(); i++)
			buildTLCMap(builder, el.children.get(i));
	}

	private UIAElement uiaDescend(long hwnd, long uiaPtr, UIAElement parent){ // (returns a modal widget if detected)
		if(uiaPtr == 0)
			//return;
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
			//el.scrollbarInfo = Windows.GetScrollBarInfo((int)el.hwnd,Windows.OBJID_CLIENT);
			//el.scrollbarInfoH = Windows.GetScrollBarInfo((int)el.hwnd,Windows.OBJID_HSCROLL);
			//el.scrollbarInfoV = Windows.GetScrollBarInfo((int)el.hwnd,Windows.OBJID_VSCROLL);
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
		if (releaseCachedAutomatinElement)
			Windows.IUnknown_Release(uiaPtr);

		if(uiaChildrenPtr != 0){
			long count = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPtr);

			if(count > 0){
				el.children = new ArrayList<UIAElement>((int)count);

				for(int i = 0; i < count; i++){
					long ptrChild = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPtr, i);
					if(ptrChild != 0){
						UIAElement modalE = uiaDescend(hwnd, ptrChild, el);
						if (modalE != null && modalElement == null) // parent-modal is preferred to child-modal
							modalElement = modalE;
					}
				}
			}
			Windows.IUnknown_Release(uiaChildrenPtr);
		}
		
		return modalElement;
	}
	
	// (through AccessBridge)
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

				el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);
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
				parent.root.hwndMap.put(el.hwnd, el);
				
				
				//MenuItems are duplicate with AccessBridge when we open one Menu or combo box
				if(!role.equals("menu") && !role.equals("combo box")
					&& childrenCount != null && !childrenCount.isEmpty() && !childrenCount.equals("null")){
					/*int cc = Windows.GetVisibleChildrenCount(vmidAC[0], vmidAC[1]);					
					if (cc > 0){
						el.children = new ArrayList<UIAElement>(cc);
						long[] children = Windows.GetVisibleChildren(vmidAC[0],vmidAC[1]);
						for (int i=0; i<children.length; i++)
							abDescend(hwnd,el,vmidAC[0],children[i]);
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

	// (mark a proper widget as modal)
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
		System.out.println("DEBUG: StateFetcher.createWidgetTree()");
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
