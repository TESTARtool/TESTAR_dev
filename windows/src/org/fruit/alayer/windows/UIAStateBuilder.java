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
 */
package org.fruit.alayer.windows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fruit.Assert;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.StateBuildException;

public final class UIAStateBuilder implements StateBuilder {

  private static final long serialVersionUID = 796655140981849818L;
  final double timeOut; // seconds
  transient ExecutorService executor;
  transient long pAutomation, pCondition, pCacheRequest;
  // begin by urueda
  boolean accessBridgeEnabled;
  String SUTProcesses; // regex
  // end by urueda

  public UIAStateBuilder() {
    this(10/*seconds*/,false,"");
  }

  public UIAStateBuilder(double timeOut, boolean accessBridgeEnabled, String SUTProcesses) { // seconds
    Assert.isTrue(timeOut > 0);
    this.timeOut = timeOut;
    initialize();
    // begin by urueda
    this.accessBridgeEnabled = accessBridgeEnabled;
    this.SUTProcesses = SUTProcesses;
    if (accessBridgeEnabled) {
      new Thread() {
        public void run() {
          Windows.InitializeAccessBridge();
        }
      }.start();
    }
    executor = Executors.newFixedThreadPool(1);
  }

  private void initialize() {

    Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);
    pAutomation = Windows.CoCreateInstance(Windows.Get_CLSID_CUIAutomation_Ptr(), 0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IUIAutomation_Ptr());

    // scope and filter settings
    long pFirstCondition = Windows.IUIAutomation_get_ControlViewCondition(pAutomation);
    pCondition = Windows.IUIAutomation_CreateAndCondition(pAutomation, Windows.IUIAutomation_CreatePropertyCondition(pAutomation, Windows.UIA_IsOffscreenPropertyId, false), pFirstCondition);
    pCacheRequest = Windows.IUIAutomation_CreateCacheRequest(pAutomation);
    Windows.IUIAutomationCacheRequest_put_TreeFilter(pCacheRequest, pCondition);
    Windows.IUIAutomationCacheRequest_put_TreeScope(pCacheRequest, Windows.TreeScope_Subtree);
    Windows.IUIAutomationCacheRequest_put_AutomationElementMode(pCacheRequest, Windows.AutomationElementMode_Full);

    // cache patterns
    Windows.IUIAutomationCacheRequest_AddPattern(pCacheRequest, Windows.UIA_WindowPatternId);
    Windows.IUIAutomationCacheRequest_AddPattern(pCacheRequest, Windows.UIA_ValuePatternId);

    // cache properties
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_NamePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_AcceleratorKeyPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_AccessKeyPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_AutomationIdPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_BoundingRectanglePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ClassNamePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ControlTypePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_CulturePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_FrameworkIdPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_HasKeyboardFocusPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_HelpTextPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsContentElementPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsControlElementPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsEnabledPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsKeyboardFocusablePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_NativeWindowHandlePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ProviderDescriptionPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_OrientationPropertyId);

    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsScrollPatternAvailablePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontallyScrollablePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticallyScrollablePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontalViewSizePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticalViewSizePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontalScrollPercentPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticalScrollPercentPropertyId);

    // window role properties
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowIsTopmostPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowCanMaximizePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowCanMinimizePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowIsModalPropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowWindowInteractionStatePropertyId);
    Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowWindowVisualStatePropertyId);

  }

  public void release() {
    if (pAutomation != 0) {
      Windows.IUnknown_Release(pCondition);
      Windows.IUnknown_Release(pCacheRequest);
      Windows.IUnknown_Release(pAutomation);
      Windows.CoUninitialize();
      pAutomation = 0;
      executor.shutdown();
    }
  }

  public void finalize() {
    release();
  }

  public UIAState apply(SUT system) throws StateBuildException {
    try {
      Future<UIAState> future = executor.submit(new StateFetcher(system,pAutomation,pCacheRequest,
                                     this.accessBridgeEnabled, this.SUTProcesses));
      return future.get((long)(timeOut * 1000.0), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new StateBuildException(e);
    } catch (ExecutionException e) {
      e.printStackTrace(); // make the exception traceable
      throw new StateBuildException(e);
    } catch (TimeoutException e) {
      UIAState ret = new UIAState(StateFetcher.buildRoot(system)); // by urueda
      ret.set(Tags.Role, Roles.Process);
      ret.set(Tags.NotResponding, true);
      return ret;
    }
  }
}
