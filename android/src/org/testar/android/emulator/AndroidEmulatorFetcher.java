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

package org.testar.android.emulator;

import java.util.ArrayDeque;
import java.util.Deque;

import org.fruit.alayer.AutomationCache;
import org.fruit.alayer.Rect;
import org.fruit.alayer.SUT;
import org.fruit.alayer.windows.Windows;

/**
 * Call OS TESTAR plugin to obtain information about the Emulator that contains the Android SUT
 * 
 * Currently implemented for Windows 10 OS, with Android Studio Emulator
 */
public class AndroidEmulatorFetcher {

	private SUT system;

	private boolean releaseCachedAutomatinElement;
	private long automationPointer;
	private long cacheRequestPointer;

	private AutomationCache nativeAutomationCache;

	public AndroidEmulatorFetcher(SUT system, long automationPointer, long cacheRequestPointer) {
		this.system = system;
		this.automationPointer = automationPointer;
		this.cacheRequestPointer = cacheRequestPointer;
		setNativeAutomationCache();
	}

	/* lists all visible top level windows in ascending z-order (foreground window last) */
	public Iterable<Long> getVisibleTopLevelWindowHandles(){
		Deque<Long> ret = new ArrayDeque<>();
		long windowHandle = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

		while(windowHandle != 0){
			if(Windows.IsWindowVisible(windowHandle)){
				long exStyle = Windows.GetWindowLong(windowHandle, Windows.GWL_EXSTYLE);
				if((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0){
					ret.addFirst(windowHandle);
				}				
			}
			windowHandle = Windows.GetNextWindow(windowHandle, Windows.GW_HWNDNEXT);
		}

		System.clearProperty("DEBUG_WINDOWS_PROCESS_NAMES");

		return ret;
	}

	/* fire up the cache request */
	private long uiaCacheWindowTree(long windowHandle){
		long aep = Long.MIN_VALUE;
		
		if (nativeAutomationCache != null) {
			aep = nativeAutomationCache.getCachedAutomationElement(windowHandle, automationPointer, cacheRequestPointer);
		}
		releaseCachedAutomatinElement = (aep == Long.MIN_VALUE);
		if (releaseCachedAutomatinElement) { // cache miss
			return Windows.IUIAutomation_ElementFromHandleBuildCache(automationPointer, windowHandle, cacheRequestPointer);
		}
		else {
			return aep;
		}
	}

	/**
	 * This method returns the main Rect of the Emulator
	 * 
	 * @param windowsHandle
	 * @return
	 */
	private Rect windowsEmulatorRect(long windowsHandle) {
		long r[] = Windows.GetWindowRect(windowsHandle);
		if(r[2] - r[0] >= 0 && r[3] - r[1] >= 0) {
			return Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
		}

		return Rect.from(0, 0, 0, 0);
	}

	/**
	 * This method returns the Rect of the internal UIPane
	 * that contains the Canvas of the Android SUT.
	 * 
	 * If cannot be found return windowsEmulatorRect
	 * 
	 * @param windowsHandle
	 * @return
	 */
	public Rect windowsEmulatorInternalPanel(long windowsHandle) {
		Rect panelRect = uiaDescendRect(windowsHandle, uiaCacheWindowTree(windowsHandle));

		if(!panelRect.equals(Rect.from(0, 0, 0, 0))) {
			return panelRect;
		}

		return windowsEmulatorRect(windowsHandle);
	}

	private Rect uiaDescendRect(long hwnd, long uiaCachePointer){
		if(uiaCachePointer == 0) { 
			return Rect.from(0, 0, 0, 0);
		}

		// We are looking for the internal sub panel that contains the Android SUT Canvas
		if(Windows.IUIAutomationElement_get_ControlType(uiaCachePointer, true) == Windows.UIA_PaneControlTypeId
				&& Windows.IUIAutomationElement_get_Name(uiaCachePointer, true).contains("sub")) {

			// sub panel bounding rectangle
			long r[] = Windows.IUIAutomationElement_get_BoundingRectangle(uiaCachePointer, true);
			if(r != null && r[2] - r[0] >= 0 && r[3] - r[1] >= 0) {
				return Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
			}
		}

		// iterate trough child UIAutomation Elements
		long uiaChildrenPointer = Windows.IUIAutomationElement_GetCachedChildren(uiaCachePointer);
		if (releaseCachedAutomatinElement) {
			Windows.IUnknown_Release(uiaCachePointer);
		}

		if(uiaChildrenPointer != 0) {
			long nrOfChildren = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPointer);

			if(nrOfChildren > 0){
				for(int i = 0; i < nrOfChildren; i++){
					long childPointer = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPointer, i);
					if(childPointer != 0){
						Rect rectChild = uiaDescendRect(hwnd, childPointer);
						if(!rectChild.equals(Rect.from(0, 0, 0, 0))) {
							return rectChild;
						}
					}
				}
			}

			Windows.IUnknown_Release(uiaChildrenPointer);
		}

		return Rect.from(0, 0, 0, 0);
	}

	private void setNativeAutomationCache() {
		nativeAutomationCache = new AutomationCache(){
			@Override
			public void nativeReleaseAutomationElement(long elementPtr){
				Windows.IUnknown_Release(elementPtr);
			}
			@Override
			public long nativeGetAutomationElementFromHandle(long automationPtr, long hwndPtr){
				return Windows.IUIAutomation_ElementFromHandle(automationPtr, hwndPtr);
			}
			@Override
			public long[] nativeGetAutomationElementBoundingRectangl(long cachedAutomationElementPtr, boolean fromCache){
				return  Windows.IUIAutomationElement_get_BoundingRectangle(cachedAutomationElementPtr, fromCache);
			}
			@Override
			public long nativeGetAutomationElementFromHandleBuildCache(long automationPtr, long hwndPtr, long cacheRequestPtr){
				return Windows.IUIAutomation_ElementFromHandleBuildCache(automationPtr, hwndPtr, cacheRequestPtr);
			}
		};
	}
}
