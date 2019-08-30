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

import es.upv.staq.testar.StateManagementTags;
import org.fruit.Util;
import org.fruit.alayer.*;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateFetcher implements Callable<UIAState>{
	
	private final SUT system;

	transient long automationPointer, cacheRequestPointer;

	private boolean releaseCachedAutomatinElement;
	
	private boolean accessBridgeEnabled;
	
	private static Pattern sutProcessesMatcher;

	private List<Map<String , String>> mappedValues;

	
	public StateFetcher(SUT system, long automationPointer, long cacheRequestPointer,
						boolean accessBridgeEnabled, String SUTProcesses){		
		this.system = system;
		this.automationPointer = automationPointer;
		this.cacheRequestPointer = cacheRequestPointer;
		this.accessBridgeEnabled = accessBridgeEnabled;
		if (SUTProcesses == null || SUTProcesses.isEmpty())
			StateFetcher.sutProcessesMatcher = null;
		else
			StateFetcher.sutProcessesMatcher = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);

		mappedValues = new ArrayList<>();
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
//		writeToCSV(mappedValues);

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

		UIAElement uiaElement = new UIAElement(parent);
		parent.children.add(uiaElement);

		////////////////////  START BACKWARDS COMPATIBLE STUFF /////////////////////////////////////
		// fetch windows automation properties and store them on the element as tags
		uiaElement.ctrlId = Windows.IUIAutomationElement_get_ControlType(uiaCachePointer, true);
		uiaElement.windowHandle = Windows.IUIAutomationElement_get_NativeWindowHandle(uiaCachePointer, true);

		// bounding rectangle
		long r[] = Windows.IUIAutomationElement_get_BoundingRectangle(uiaCachePointer, true);
		if(r != null && r[2] - r[0] >= 0 && r[3] - r[1] >= 0)
			uiaElement.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);

		uiaElement.enabled = Windows.IUIAutomationElement_get_IsEnabled(uiaCachePointer, true);
		uiaElement.name = Windows.IUIAutomationElement_get_Name(uiaCachePointer, true);
		uiaElement.helpText = Windows.IUIAutomationElement_get_HelpText(uiaCachePointer, true);
		uiaElement.automationId = Windows.IUIAutomationElement_get_AutomationId(uiaCachePointer, true);
		uiaElement.className = Windows.IUIAutomationElement_get_ClassName(uiaCachePointer, true);
		uiaElement.providerDesc = Windows.IUIAutomationElement_get_ProviderDescription(uiaCachePointer, true);
		uiaElement.frameworkId = Windows.IUIAutomationElement_get_FrameworkId(uiaCachePointer, true);
		uiaElement.orientation = Windows.IUIAutomationElement_get_Orientation(uiaCachePointer, true);
		uiaElement.isContentElement = Windows.IUIAutomationElement_get_IsContentElement(uiaCachePointer, true);
		uiaElement.isControlElement = Windows.IUIAutomationElement_get_IsControlElement(uiaCachePointer, true);
		uiaElement.hasKeyboardFocus = Windows.IUIAutomationElement_get_HasKeyboardFocus(uiaCachePointer, true);
		uiaElement.isKeyboardFocusable = Windows.IUIAutomationElement_get_IsKeyboardFocusable(uiaCachePointer, true);
		uiaElement.accessKey = Windows.IUIAutomationElement_get_AccessKey(uiaCachePointer, true);
		uiaElement.acceleratorKey = Windows.IUIAutomationElement_get_AcceleratorKey(uiaCachePointer, true);
		uiaElement.valuePattern = Windows.IUIAutomationElement_get_ValuePattern(uiaCachePointer, Windows.UIA_ValuePatternId);

		parent.root.windowHandleMap.put(uiaElement.windowHandle, uiaElement);

		// get extra infos from windows
		if(uiaElement.ctrlId == Windows.UIA_WindowControlTypeId){
			//long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true);
			long uiaWindowPointer = Windows.IUIAutomationElement_GetPattern(uiaCachePointer, Windows.UIA_WindowPatternId, true); // by urueda
			if(uiaWindowPointer != 0){
				uiaElement.wndInteractionState = Windows.IUIAutomationWindowPattern_get_WindowInteractionState(uiaWindowPointer, true);
				uiaElement.blocked = (uiaElement.wndInteractionState != Windows.WindowInteractionState_ReadyForUserInteraction);
				uiaElement.isTopmostWnd = Windows.IUIAutomationWindowPattern_get_IsTopmost(uiaWindowPointer, true);
				uiaElement.isModal = Windows.IUIAutomationWindowPattern_get_IsModal(uiaWindowPointer, true);

				// also set the tags in the uiaelement
				uiaElement.set(UIATags.UIAIsTopmostWindow, uiaElement.isTopmostWnd);
				uiaElement.set(UIATags.UIAIsWindowModal, uiaElement.isModal);
				uiaElement.set(UIATags.UIAWindowInteractionState, uiaElement.wndInteractionState);

				Windows.IUnknown_Release(uiaWindowPointer);
			}
			uiaElement.culture = Windows.IUIAutomationElement_get_Culture(uiaCachePointer, true);
		}

		// check if we missed detection of a modal window
		if (!uiaElement.isModal && uiaElement.automationId != null &&
				(uiaElement.automationId.contains("messagebox") || uiaElement.automationId.contains("window"))){ // try to detect potential modal window!
			modalElement = markModal(uiaElement);
		}


		//////////////////////////   END BACKWARDS COMPATIBLE STUFF  ////////////////////////////////

		// get the active pattern availability properties (these specify if certain control patterns are available in the uia element
		UIATags.getPatternAvailabilityTags().stream().filter(UIATags::tagIsActive).forEach(availabilityTag -> {
			Object object = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, UIAMapping.getPatternPropertyIdentifier(availabilityTag), true);
			uiaElement.set(availabilityTag, object instanceof Boolean && (Boolean)object);

			// if a pattern is present, we also want to store the properties that are specific to that pattern
			if (uiaElement.get(availabilityTag)) {
				UIATags.getChildTags(availabilityTag).stream().filter(UIATags::tagIsActive).forEach(patternPropertyTag -> {
					if (patternPropertyTag.equals(UIATags.UIAValueValue)) {
						// this property for some reason cannot be retrieved using the getCurrentPropertyValue method
						// that is why we use the value that was directly received
						uiaElement.set(UIATags.UIAValueValue, uiaElement.valuePattern);
					}
					Object propertyObject = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, UIAMapping.getPatternPropertyIdentifier(patternPropertyTag), true);
					if (propertyObject != null) {
						try {
							setConvertedObjectValue(patternPropertyTag, propertyObject, uiaElement);
						} catch (Exception e) {
							System.out.println("Exception while setting tag " + patternPropertyTag.name());
							System.out.println(e.getMessage());
						}
					}
				});
			}
		});


		//////////////// SOME MORE BACKWARDS COMPATIBLE STUFF /////////////////////
		// get some non-cached property values for elements implementing the scroll pattern
		Object obj;
		uiaElement.scrollPattern = uiaElement.get(UIATags.UIAIsScrollPatternAvailable);
		if (uiaElement.scrollPattern){
			//el.scrollbarInfo = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_CLIENT);
			//el.scrollbarInfoH = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_HSCROLL);
			//el.scrollbarInfoV = Windows.GetScrollBarInfo((int)el.windowHandle,Windows.OBJID_VSCROLL);
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer,  Windows.UIA_ScrollHorizontallyScrollablePropertyId, true);
			uiaElement.hScroll = obj instanceof Boolean && ((Boolean) obj);
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer,  Windows.UIA_ScrollVerticallyScrollablePropertyId, true);
			uiaElement.vScroll = obj instanceof Boolean && ((Boolean) obj);
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollHorizontalViewSizePropertyId, true);
			uiaElement.hScrollViewSize = obj instanceof Double ? ((Double)obj) : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollVerticalViewSizePropertyId, true);
			uiaElement.vScrollViewSize = obj instanceof Double ? ((Double)obj) : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollHorizontalScrollPercentPropertyId, true);
			uiaElement.hScrollPercent = obj instanceof Double ? ((Double)obj) : -1.0;
			obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_ScrollVerticalScrollPercentPropertyId, true);
			uiaElement.vScrollPercent = obj instanceof Double ? ((Double)obj) : -1.0;
		}

		//////////////// END SOME MORE BACKWARDS COMPATIBLE STUFF   //////////////////

		// now add the properties as tags
		// the reason why we're doing this, is because the tags make for a more flexible way of adding properties
		// as opposed to hard-coded attributes.
		// it also makes for a uniform interface when talking about the UIA attributes
		// In time, these hard-coded attributes should be refactored away.
		uiaElement.set(UIATags.UIAControlType, uiaElement.ctrlId);
		uiaElement.set(UIATags.UIANativeWindowHandle, uiaElement.windowHandle);
		uiaElement.set(UIATags.UIAIsEnabled, uiaElement.enabled);
		uiaElement.set(UIATags.UIAName, uiaElement.name);
		uiaElement.set(UIATags.UIAHelpText, uiaElement.helpText);
		uiaElement.set(UIATags.UIAAutomationId, uiaElement.automationId);
		uiaElement.set(UIATags.UIAClassName, uiaElement.className);
		uiaElement.set(UIATags.UIAProviderDescription, uiaElement.providerDesc);
		uiaElement.set(UIATags.UIAFrameworkId, uiaElement.frameworkId);
		uiaElement.set(UIATags.UIAOrientation, uiaElement.orientation);
		uiaElement.set(UIATags.UIAIsContentElement, uiaElement.isContentElement);
		uiaElement.set(UIATags.UIAIsControlElement, uiaElement.isControlElement);
		uiaElement.set(UIATags.UIAHasKeyboardFocus, uiaElement.hasKeyboardFocus);
		uiaElement.set(UIATags.UIAIsKeyboardFocusable, uiaElement.isKeyboardFocusable);
		uiaElement.set(UIATags.UIAAccessKey, uiaElement.accessKey);
		uiaElement.set(UIATags.UIAAcceleratorKey, uiaElement.acceleratorKey);
		uiaElement.set(UIATags.UIABoundingRectangle, uiaElement.rect);

		// new properties, not in attributes yet
		uiaElement.set(UIATags.UIALocalizedControlType, Windows.IUIAutomationElement_get_LocalizedControlType(uiaCachePointer, true));
//		System.out.println("Control type: " + uiaElement.get(UIATags.UIALocalizedControlType));
		uiaElement.set(UIATags.UIAItemType, Windows.IUIAutomationElement_get_ItemType(uiaCachePointer, true));
//		System.out.println("Item type: " + uiaElement.get(UIATags.UIAItemType));
		uiaElement.set(UIATags.UIAItemStatus, Windows.IUIAutomationElement_get_ItemStatus(uiaCachePointer, true));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_FullDescriptionPropertyId, true);
		uiaElement.set(UIATags.UIAFullDescription, obj instanceof String ? (String)obj : "");
//		System.out.println(uiaElement.get(UIATags.UIAFullDescription));
		uiaElement.set(UIATags.UIACulture, Windows.IUIAutomationElement_get_Culture(uiaCachePointer, true));
		uiaElement.set(UIATags.UIAProcessId, Windows.IUIAutomationElement_get_ProcessId(uiaCachePointer, true));
		uiaElement.set(UIATags.UIAIsOffscreen, Windows.IUIAutomationElement_get_IsOffscreen(uiaCachePointer, true));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_AriaPropertiesPropertyId, true);
		uiaElement.set(UIATags.UIAAriaProperties, obj instanceof String ? (String)obj : "");
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_AriaRolePropertyId, true);
		uiaElement.set(UIATags.UIAAriaRole, obj instanceof String ? (String) obj : "");
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsDataValidForFormPropertyId, true);
		uiaElement.set(UIATags.UIAIsDataValidForForm, obj instanceof Boolean && ((Boolean) obj));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsDialogPropertyId, true);
		uiaElement.set(UIATags.UIAIsDialog, obj instanceof Boolean && ((Boolean) obj));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsPasswordPropertyId, true);
		uiaElement.set(UIATags.UIAIsPassword, obj instanceof Boolean && ((Boolean) obj));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsPeripheralPropertyId, true);
		uiaElement.set(UIATags.UIAIsPeripheral, obj instanceof Boolean && ((Boolean) obj));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_IsRequiredForFormPropertyId, true);
		uiaElement.set(UIATags.UIAIsRequiredForForm, obj instanceof Boolean && ((Boolean) obj));
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_LabeledByPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIALabeledBy, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_LandmarkTypePropertyId, true);
		setObjectValueIfNotNull(UIATags.UIALandmarkType, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_LocalizedLandmarkTypePropertyId, true);
		setObjectValueIfNotNull(UIATags.UIALocalizedLandmarkType, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_LevelPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIALevel, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_LiveSettingPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIALiveSetting, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_PositionInSetPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIAPositionInSet, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_SizeOfSetPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIASizeOfSet, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_RotationPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIARotation, obj, uiaElement);
		obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaCachePointer, Windows.UIA_VisualEffectsPropertyId, true);
		setObjectValueIfNotNull(UIATags.UIAVisualEffects, obj, uiaElement);





		// get the properties for potential child elements
		long uiaChildrenPointer = Windows.IUIAutomationElement_GetCachedChildren(uiaCachePointer);
		if (releaseCachedAutomatinElement) // by urueda
			Windows.IUnknown_Release(uiaCachePointer);

		if(uiaChildrenPointer != 0){
			long nrOfChildren = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPointer);

			if(nrOfChildren > 0){
				uiaElement.children = new ArrayList<UIAElement>((int)nrOfChildren);

				for(int i = 0; i < nrOfChildren; i++){
					long childPointer = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPointer, i);
					if(childPointer != 0){
						// begin by urueda
						UIAElement modalE = uiaDescend(hwnd, childPointer, uiaElement);
						if (modalE != null && modalElement == null) // parent-modal is preferred to child-modal
							modalElement = modalE;
						// end by urueda							
					}
				}
			}
			Windows.IUnknown_Release(uiaChildrenPointer);
		}

		// add to csv for analysis purposed
//		mappedValues.add(extractTagsForCsv(uiaElement));
		
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

	private <T> void setObjectValueIfNotNull(Tag<T> tag, Object object, UIAElement uiaElement) {
		if (object != null) {
			uiaElement.set(tag, (T) object);
		}
	}

	private <T> void setConvertedObjectValue(Tag<T> tag, Object object, UIAElement uiaElement) {
		Stream<Tag<?>> tagsToWatch = Stream.of(
			UIATags.UIADropTargetDropTargetEffects,
			UIATags.UIALegacyIAccessibleSelection,
			UIATags.UIAMultipleViewSupportedViews,
			UIATags.UIASelectionSelection,
			UIATags.UIASpreadsheetItemAnnotationObjects,
			UIATags.UIASpreadsheetItemAnnotationTypes,
			UIATags.UIATableColumnHeaders,
			UIATags.UIATableRowHeaders,
			UIATags.UIATableItemColumnHeaderItems,
			UIATags.UIATableItemRowHeaderItems,
			UIATags.UIADragDropEffects,
			UIATags.UIADragGrabbedItems);

		tagsToWatch.forEach(tagToWatch -> {
			if (tagToWatch.equals(tag)) {
				System.out.println("Encountered " + tagToWatch.name() + ": " + object.toString());
			}
		});

		// there are some values that can be returned, which need special processing, because they
		// can be arrays, etc.
		// not a fan of multiple if/else statements, so will have to see about refactoring this at one point in time..
		if (tag.equals(UIATags.UIADragDropEffects)) {
			// array of strings...convert to a single string
			if (object instanceof String[]) {
				uiaElement.set(tag, (T)String.join(", ", (String[])object));
			}
			else if (object instanceof String) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIADragGrabbedItems)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIADropTargetDropTargetEffects)) {
			// array of strings...convert to a single string
			if (object instanceof String[]) {
				uiaElement.set(tag, (T)String.join(", ", (String[])object));
			}
			else if (object instanceof String) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIALegacyIAccessibleSelection)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIAMultipleViewSupportedViews)) {
			if (object instanceof Long[]) {
				uiaElement.set(tag, (T) Arrays.stream((Long[])object).map(Object::toString).reduce("", (base, string) -> base.equals("") ? string : base + ", " + string));
			}
		}
		else if (tag.equals(UIATags.UIASelectionSelection)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIASpreadsheetItemAnnotationObjects)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIASpreadsheetItemAnnotationTypes)) {
			if (object instanceof Long[]) {
				uiaElement.set(tag, (T) Arrays.stream((Long[])object).map(Object::toString).reduce("", (base, string) -> base.equals("") ? string : base + ", " + string));
			}
		}
		else if (tag.equals(UIATags.UIATableColumnHeaders)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIATableRowHeaders)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIATableItemColumnHeaderItems)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else if (tag.equals(UIATags.UIATableItemRowHeaderItems)) {
			// not sure what vt_unknown will translate into, so we just leave it as object for now
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
		else {
			if (object != null) {
				uiaElement.set(tag, (T) object);
			}
		}
	}

	private Map<String, String> extractTagsForCsv(UIAElement uiaElement) {
		List<Tag<?>> stateTags = StateManagementTags.getAllTags().stream().map(UIAMapping::getMappedStateTag).collect(Collectors.toList());
		return stateTags.stream().collect(Collectors.toMap(Tag::name, tag -> uiaElement.get(tag, null) != null ? uiaElement.get(tag, null).toString() : "null"));
	}

	public void writeToCSV(List<Map<String, String>> valuesToExport) {
		List<String> linesToExport = new ArrayList<>();

		// title row:
		List<Tag<?>> stateTags = StateManagementTags.getAllTags().stream().map(UIAMapping::getMappedStateTag).sorted(Comparator.comparing(Tag::name)).collect(Collectors.toList());
		String titleRowString = convertToCSV(stateTags.stream().map(Tag::name).toArray(String[]::new));
		linesToExport.add(titleRowString);

		for(Map<String, String> valueMapping : valuesToExport) {
			// follow the stateTags list order
			String[] line = stateTags.stream().map(tag -> valueMapping.getOrDefault(tag.name(),null) == null ? "null" : valueMapping.get(tag.name())).toArray(String[]::new);
			linesToExport.add(convertToCSV(line));
		}

		File csvOutputFile = new File("widgetUIAOutput.csv");
		try {
			PrintWriter printWriter = new PrintWriter(csvOutputFile);
			linesToExport.stream().forEach(printWriter::println);
			printWriter.flush();
			printWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String convertToCSV(String[] data) {
		return Stream.of(data)
				.map(this::escapeSpecialCharacters)
				.collect(Collectors.joining(";"));
	}

	public String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		escapedData = escapedData.replaceAll(";", "^");
		return escapedData;
	}
}
