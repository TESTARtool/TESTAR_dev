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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.fruit.Util;
import org.fruit.alayer.*;
public class StateFetcher implements Callable<UIAState>{

  private static final double NO_SIZE = -1.0;

  private final SUT system;

  transient long pAutomation, pCacheRequest;
  private boolean releaseCachedAutomatinElement;

  private boolean accessBridgeEnabled;

  private static Pattern sutProcessesMatcher;

  public StateFetcher(SUT system, long pAutomation, long pCacheRequest,
            boolean accessBridgeEnabled, String SUTProcesses) {
    this.system = system;
    this.pAutomation = pAutomation;
    this.pCacheRequest = pCacheRequest;
    this.accessBridgeEnabled = accessBridgeEnabled;
    if (SUTProcesses == null || SUTProcesses.isEmpty()) {
      StateFetcher.sutProcessesMatcher = null;
    } else {
      StateFetcher.sutProcessesMatcher = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);
    }
  }

  public static UIARootElement buildRoot(SUT system) {
    UIARootElement uiaRoot = new UIARootElement();
    uiaRoot.setRunning(system.isRunning());

    long[] info = Windows.GetMonitorInfo(Windows.GetPrimaryMonitorHandle());
    if (info[3] - info[1] >= 0 && info[4] - info[2] >= 0) {
      uiaRoot.rect = Rect.fromCoordinates(info[1], info[2], info[3], info[4]);
    }
    uiaRoot.setTimeStamp(System.currentTimeMillis());
    uiaRoot.setHasStandardKeyboard(system.get(Tags.StandardKeyboard, null) != null);
    uiaRoot.setHasStandardMouse(system.get(Tags.StandardMouse, null) != null);

    return uiaRoot;
  }

  public UIAState call() throws Exception {
    Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

    UIARootElement uiaRoot = buildSkeletton(system);

    UIAState root = createWidgetTree(uiaRoot);
    root.set(Tags.Role, Roles.Process);
    root.set(Tags.NotResponding, false);
    for (Widget w: root) {
      w.set(Tags.Path,Util.indexString(w));
    }
    if (system != null && (root == null || root.childCount() == 0) && system.getNativeAutomationCache() != null) {
      system.getNativeAutomationCache().releaseCachedAutomationElements(); // prevent SUT UI not ready due to caching
    }
    Windows.CoUninitialize();

    return root;
  }

  /**
   * Checks whether a window conforms to the SUT.
   * @param hwnd a handle for a window
   * @return true if the window conforms to the SUT, false otherwise
   * @author urueda
   */
  private boolean isSUTProcess(long hwnd) {
    if (StateFetcher.sutProcessesMatcher == null) {
      return false;
    }
    String processName = Windows.GetProcessNameFromHWND(hwnd);

    return (processName != null && !(processName.isEmpty()) && StateFetcher.sutProcessesMatcher.matcher(processName).matches());
  }

  /**
   * Build the skeletton.
   * @param sut the system under testing
   * @return the root skeletton
   */
  private UIARootElement buildSkeletton(SUT sut) {
    UIARootElement uiaRoot = buildRoot(sut);

    if (!uiaRoot.isRunning()) {
      return uiaRoot;
    }
    uiaRoot.setPid(sut.get(Tags.PID));

    // find all visible top level windows on the desktop
    Iterable<Long> visibleTopLevelWindows = this.visibleTopLevelWindows();

    UIAElement modalElement = null; // by urueda

    // descend the root windows which belong to our process, using UIAutomation
    uiaRoot.children = new ArrayList<UIAElement>();
    boolean owned;
    long hwndPID;
    List<Long> ownedWindows = new ArrayList<Long>();
    for (long hwnd: visibleTopLevelWindows) {
      owned = Windows.GetWindow(hwnd, Windows.GW_OWNER) != 0;
      //if (Windows.GetWindowProcessId(hwnd) == uiaRoot.pid) {
      hwndPID = Windows.GetWindowProcessId(hwnd);
      if (hwndPID == uiaRoot.getPid() || isSUTProcess(hwnd)) {
        uiaRoot.setForeground(uiaRoot.isForeground() || WinProcess.isForeground(hwndPID));
        if (!owned) {
          if (this.accessBridgeEnabled) {
            modalElement =  abDescend(hwnd, uiaRoot, 0, 0);
          } else {
            modalElement = uiaDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot);
          }
        } else {
          ownedWindows.add(hwnd);
        }
      }
    }

    // if UIAutomation missed an owned window, we'll collect it here
    for (long hwnd: ownedWindows) {
      if (!uiaRoot.getHwndMap().containsKey(hwnd)) {
        UIAElement modalE;
        if (this.accessBridgeEnabled ) {
          modalE = abDescend(hwnd, uiaRoot, 0, 0);
        } else {
          modalE =  uiaDescend(hwnd, uiaCacheWindowTree(hwnd), uiaRoot);
        }

        if (modalE != null) {
          modalElement = modalE;
        }
      }
    }

    // set z-indices for the windows
    int z = 0;
    for (long hwnd: visibleTopLevelWindows) {
      UIAElement wnd = uiaRoot.getHwndMap().get(hwnd);

      if (wnd == null) {
        wnd = new UIAElement(uiaRoot);
        uiaRoot.children.add(wnd);
        wnd.ignore = true;
        wnd.hwnd = hwnd;
        long[] r = Windows.GetWindowRect(hwnd);
        if (r[2] - r[0] >= 0 && r[3] - r[1] >= 0) {
          wnd.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
        }
        wnd.ctrlId = Windows.UIA_WindowControlTypeId;
        uiaRoot.getHwndMap().put(hwnd, wnd);
      }

      wnd.zindex = z++;

      if (wnd.ctrlId == Windows.UIA_MenuControlTypeId || wnd.ctrlId == Windows.UIA_WindowControlTypeId || wnd.parent == uiaRoot) {
        wnd.isTopLevelContainer = true;
      }

    }

    calculateZIndices(uiaRoot);
    buildTLCMap(uiaRoot);
    markBlockedElements(uiaRoot);

    markBlockedElements(uiaRoot,modalElement);

    return uiaRoot;
  }

  /* lists all visible top level windows in ascending z-order (foreground window last) */
  private Iterable<Long> visibleTopLevelWindows() {
    Deque<Long> ret = new ArrayDeque<Long>();
    long hwnd = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

    while (hwnd != 0) {
      if (Windows.IsWindowVisible(hwnd)) {
        long exStyle = Windows.GetWindowLong(hwnd, Windows.GWL_EXSTYLE);
        if ((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0) {
          ret.addFirst(hwnd);
          if (System.getProperty("DEBUG_WINDOWS_PROCESS_NAMES") != null) {
            System.out.println("<" + hwnd + "> window' process name <" + Windows.GetProcessNameFromHWND(hwnd) + ">");
          }
        }
      }
      hwnd = Windows.GetNextWindow(hwnd, Windows.GW_HWNDNEXT);
    }

    System.clearProperty("DEBUG_WINDOWS_PROCESS_NAMES");

    return ret;
  }

  /* fire up the cache request */
  private long uiaCacheWindowTree(long hwnd) {
    long aep = Long.MIN_VALUE;
    if (system.getNativeAutomationCache() != null) {
      aep = system.getNativeAutomationCache().getCachedAutomationElement(hwnd, pAutomation, pCacheRequest);
    }
    releaseCachedAutomatinElement = (aep == Long.MIN_VALUE);

    if (releaseCachedAutomatinElement) {// cache miss
      return Windows.IUIAutomation_ElementFromHandleBuildCache(pAutomation, hwnd, pCacheRequest);
    } else {
      return aep;
    }
  }

  private void buildTLCMap(UIARootElement root) {
    ElementMap.Builder builder = ElementMap.newBuilder();
    buildTLCMap(builder, root);
    root.setTlc(builder.build());
  }

  private void buildTLCMap(ElementMap.Builder builder, UIAElement el) {
    if (el.isTopLevelContainer) {
      builder.addElement(el);
    }
    for (int i = 0; i < el.children.size(); i++) {
      buildTLCMap(builder, el.children.get(i));
    }
  }

  private UIAElement uiaDescend(long hwnd, long uiaPtr, UIAElement parent) { // by urueda (returns a modal widget if detected)
    if (uiaPtr == 0) {
      return null;
    }

    UIAElement modalElement = null;

    UIAElement el = new UIAElement(parent);
    parent.children.add(el);

    el.ctrlId = Windows.IUIAutomationElement_get_ControlType(uiaPtr, true);
    el.hwnd = Windows.IUIAutomationElement_get_NativeWindowHandle(uiaPtr, true);

    // bounding rectangle
    long[] r = Windows.IUIAutomationElement_get_BoundingRectangle(uiaPtr, true);
    if (r != null && r[2] - r[0] >= 0 && r[3] - r[1] >= 0) {
      el.rect = Rect.fromCoordinates(r[0], r[1], r[2], r[3]);
    }
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

    parent.root.getHwndMap().put(el.hwnd, el);

    // get extra infos from windows
    if (el.ctrlId == Windows.UIA_WindowControlTypeId) {
       long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true); // by urueda
      if (uiaWndPtr != 0) {
        el.wndInteractionState = Windows.IUIAutomationWindowPattern_get_WindowInteractionState(uiaWndPtr, true);
        el.blocked = (el.wndInteractionState != Windows.WindowInteractionState_ReadyForUserInteraction);
        el.isTopmostWnd = Windows.IUIAutomationWindowPattern_get_IsTopmost(uiaWndPtr, true);
        el.isModal = Windows.IUIAutomationWindowPattern_get_IsModal(uiaWndPtr, true);
        Windows.IUnknown_Release(uiaWndPtr);
      }
      el.culture = Windows.IUIAutomationElement_get_Culture(uiaPtr, true);
    }

    if (!el.isModal && el.automationId != null &&
        (el.automationId.contains("messagebox") || el.automationId.contains("window"))) { // try to detect potential modal window!
      modalElement = markModal(el);
    }
    Object obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_IsScrollPatternAvailablePropertyId, false); //true);
    if (el.scrollPattern) {
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr,  Windows.UIA_ScrollHorizontallyScrollablePropertyId, false);
      if (obj instanceof Boolean) {
        el.hScroll = ((Boolean)obj).booleanValue();
      } else {
        el.hScroll = false;
      }
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr,  Windows.UIA_ScrollVerticallyScrollablePropertyId, false);
      if (obj instanceof Boolean) {
        el.vScroll = ((Boolean)obj).booleanValue();
      } else {
        el.vScroll = false;
      }
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollHorizontalViewSizePropertyId, false);
      if (obj instanceof Boolean) {
        el.hScrollViewSize = ((Double)obj).doubleValue();
      } else {
        el.hScrollViewSize = NO_SIZE;
      }
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollVerticalViewSizePropertyId, false);
      if (obj instanceof Boolean) {
        el.vScrollViewSize = ((Double)obj).doubleValue();
      } else {
        el.vScrollViewSize = NO_SIZE;
      }
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollHorizontalScrollPercentPropertyId, false);
      if (obj instanceof Boolean) {
        el.hScrollPercent = ((Double)obj).doubleValue();
      } else {
        el.hScrollPercent = NO_SIZE;
      }
      obj = Windows.IUIAutomationElement_GetCurrentPropertyValue(uiaPtr, Windows.UIA_ScrollVerticalScrollPercentPropertyId, false);
      if (obj instanceof Boolean) {
        el.vScrollPercent = ((Double)obj).doubleValue();
      } else {
        el.vScrollPercent = NO_SIZE;
      }
    }

    // descend children

    long uiaChildrenPtr = Windows.IUIAutomationElement_GetCachedChildren(uiaPtr);
    if (releaseCachedAutomatinElement) {
      Windows.IUnknown_Release(uiaPtr);
    }
    if (uiaChildrenPtr != 0) {
      long count = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPtr);

      if (count > 0) {
        el.children = new ArrayList<UIAElement>((int)count);

        for (int i = 0; i < count; i++) {
          long ptrChild = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPtr, i);
          if (ptrChild != 0) {
            UIAElement modalE = uiaDescend(hwnd, ptrChild, el);
            if (modalE != null && modalElement == null) {
              // parent-modal is preferred to child-modal
              modalElement = modalE;
            }
          }
        }
      }
      Windows.IUnknown_Release(uiaChildrenPtr);
    }

    return modalElement;
  }

  private UIAElement abDescend(long hwnd, UIAElement parent, long vmid, long ac) {
    UIAElement modalElement = null;

    long[] vmidAC;
    if (vmid == 0) {
      vmidAC = Windows.GetAccessibleContext(hwnd);
    } else {
      vmidAC = new long[]{ vmid,ac };
    }
    if (vmidAC != null) {
      Object[] props = Windows.GetAccessibleContextProperties(vmidAC[0],vmidAC[1]);
      if (props != null) {
        String role      = (String) props[0],
             name      = (String) props[1],
             description    = (String) props[2],
             x        = (String) props[3],
             y        = (String) props[4],
             width      = (String) props[5],
             height      = (String) props[6],
             indexInParent = (String) props[7],
             childrenCount = (String) props[8];

        Rect rect = null;
        try {
          rect = Rect.from(new Double(x).doubleValue(), new Double(y).doubleValue(),
                   new Double(width).doubleValue(), new Double(height).doubleValue());
        } catch (Exception e) {
          return null;
        }

        UIAElement el = new UIAElement(parent);
        parent.children.add(el);
        el.rect = rect;

        el.hwnd = Windows.GetHWNDFromAccessibleContext(vmidAC[0],vmidAC[1]);
        if (role.equals(AccessBridgeControlTypes.ACCESSIBLE_DIALOG)) {
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
        parent.root.getHwndMap().put(el.hwnd, el);

        //MenuItems are duplicate with AccessBridge when we open one Menu or combo box
        if (!"menu".equals(role) && !"combo box".equals(role)
          && childrenCount != null && !childrenCount.isEmpty() && !"null".equals(childrenCount)) {

            long childAC;
            int c = new Integer(childrenCount).intValue();
            el.children = new ArrayList<UIAElement>(c);
            for (int i=0; i<c; i++) {
              childAC =  Windows.GetAccessibleChildFromContext(vmidAC[0],vmidAC[1],i);
              abDescend(hwnd,el,vmidAC[0],childAC);
            }
        }
      }
    }

    return modalElement;

  }

  // mark a proper widget as modal
  private UIAElement markModal(UIAElement element) {
    if (element == null) {
      return null; // no proper widget found to mark as modal
    } else if (element.ctrlId != Windows.UIA_WindowControlTypeId && element.ctrlId != Windows.UIA_PaneControlTypeId &&
        element.ctrlId != Windows.UIA_GroupControlTypeId) {
      return markModal(element.parent);
    }
    else {
      element.isModal = true;
      return element;
    }
  }

  private void markBlockedElements(UIAElement element) {
    for (UIAElement c: element.children) {
      if (element.blocked && !(c.ctrlId == Windows.UIA_WindowControlTypeId && !c.blocked)) {
        c.blocked = true;
      }
      markBlockedElements(c);
    }
  }

  private void markBlockedElements(UIAElement element, UIAElement modalElement) {
    if (modalElement != null) {
      for (UIAElement c: element.children) {
        if (c != modalElement) {
          c.blocked = true;
          markBlockedElements(c,modalElement);
        }
      }
    }
  }

  private void calculateZIndices(UIAElement el) {
    if (el.parent != null) {
      if (this.accessBridgeEnabled) {// TLC are not exposed as visible desktop controls
        int toplevelInt;
        if (el.parent.isTopLevelContainer) {
          toplevelInt = 1;
        } else {
          toplevelInt = 0;
        }
        el.zindex = el.parent.zindex + toplevelInt;
      } else if (!el.isTopLevelContainer) {
        el.zindex = el.parent.zindex;
      }
    }

    for (int i = 0; i < el.children.size(); i++) {
      calculateZIndices(el.children.get(i));
    }
  }

  private UIAState createWidgetTree(UIARootElement root) {
    UIAState state = new UIAState(root);
    root.backRef = state;
    for (UIAElement childElement: root.children) {
      if (!childElement.ignore) {
        createWidgetTree(state, childElement);
      }
    }
    return state;
  }

  private void createWidgetTree(UIAWidget parent, UIAElement element) {
    UIAWidget w = parent.root().addChild(parent, element);
    element.backRef = w;
    for (UIAElement child: element.children) {
      createWidgetTree(w, child);
    }
  }
}
