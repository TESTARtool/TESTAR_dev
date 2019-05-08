/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2019 Universitat Politecnica de Valencia - www.upv.es
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

	public static boolean releaseCachedAutomatinElement;

	private boolean accessBridgeEnabled;

	private static Pattern sutProcessesMatcher;

	public StateFetcher(SUT system, long pAutomation, long pCacheRequest,
			boolean accessBridgeEnabled, String SUTProcesses){		
		
		this.system = system;
		this.pAutomation = pAutomation;
		this.pCacheRequest = pCacheRequest;
		this.accessBridgeEnabled = accessBridgeEnabled;
		
		if (SUTProcesses == null || SUTProcesses.isEmpty())
			StateFetcher.sutProcessesMatcher = null;
		else
			StateFetcher.sutProcessesMatcher = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);
	}

	public static UIARootElement buildRoot(SUT system){
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
		UIARootElement uiaRoot = buildRoot(system);

		if(!uiaRoot.isRunning)
			return uiaRoot;

		uiaRoot.pid = system.get(Tags.PID);

		// find all visible top level windows on the desktop
		Iterable<Long> visibleTopLevelWindows = this.visibleTopLevelWindows();

		UIAElement modalElement = null;

		// descend the root windows which belong to our process, using UIAutomation
		uiaRoot.children = new ArrayList<UIAElement>();
		
		boolean owned;
		long hwndPID;
		List<Long> ownedWindows = new ArrayList<>();
		
		for(long hwnd : visibleTopLevelWindows){
			
			owned = Windows.GetWindow(hwnd, Windows.GW_OWNER) != 0;
			hwndPID = Windows.GetWindowProcessId(hwnd);
			if (hwndPID == uiaRoot.pid || isSUTProcess(hwnd)){
				uiaRoot.isForeground = uiaRoot.isForeground || WinProcess.isForeground(hwndPID); //( SUT as a set of windows/processes )				
				if(!owned){
					//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
					modalElement = this.accessBridgeEnabled ? BuilderAccessBridge.accessBridgeDescend(hwnd, uiaRoot, 0, 0) :
						BuilderUIAutomation.UIAutomationDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot);
				} else
					ownedWindows.add(hwnd);
			}
		}

		// if UIAutomation missed an owned window, we'll collect it here
		for(long hwnd : ownedWindows){				
			if(!uiaRoot.hwndMap.containsKey(hwnd)){
				//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
				UIAElement modalE;

				if ((modalE = this.accessBridgeEnabled ? BuilderAccessBridge.accessBridgeDescend(hwnd, uiaRoot, 0, 0) :
					BuilderUIAutomation.UIAutomationDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot)) != null)
					modalElement = modalE;
			}
		}

		// Associate multiple Java windows hwnd into the current uiaRoot Element
		if(this.accessBridgeEnabled && BuilderAccessBridge.searchNonVisibleJavaWindows) {
			Iterable<Long> allJavaWindows = this.allJavaWindows();
			
			for(long hwnd : allJavaWindows){
				if(!uiaRoot.hwndMap.containsKey(hwnd)){

					UIAElement modalE;

					if ((modalE = this.accessBridgeEnabled ? BuilderAccessBridge.accessBridgeDescend(hwnd, uiaRoot, 0, 0) :
						BuilderUIAutomation.UIAutomationDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot)) != null)
						modalElement = modalE;

				}
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

			// begin by wcoux (Spy mode layering through transparent window)
			// Get the title of the window - if the window is Testar's Spy window, skip it. Otherwise,
			// the HitTest function will not work properly: it doesn't actually hit test, it checks if windows are
			// on top of the SUT and the Java implementation of the Spy mode drawing is an invisible always on top window.
			/*String windowTitle = Windows.GetWindowText(hwnd);

				if (windowTitle != null && Objects.equals(windowTitle, "Testar - Spy window")) {
					continue;
				}*/
			// end by wcoux

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
	private Iterable<Long> visibleTopLevelWindows(){
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

	/* list all existing Java windows of running JVMs */
	private Iterable<Long> allJavaWindows(){
		Deque<Long> ret = new ArrayDeque<Long>();
		long hwnd = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

		while(hwnd != 0){

			if(Windows.isJavaWindow(hwnd))
				ret.addFirst(hwnd);

			hwnd = Windows.GetNextWindow(hwnd, Windows.GW_HWNDNEXT);
		}

		return ret;
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
				el.zindex = el.parent.zindex + (el.parent.isTopLevelContainer ? 1 : 0) + (el.isTopJavaInternalFrame ? 1 : 0);
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
