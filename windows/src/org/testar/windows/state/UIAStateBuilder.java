/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.testar.core.Assert;
import org.testar.core.alayer.Roles;
import org.testar.core.state.SUT;
import org.testar.core.state.StateBuilder;
import org.testar.core.tag.Tags;
import org.testar.core.exceptions.StateBuildException;
import org.testar.windows.Windows;
import org.testar.windows.tag.UIAMapping;
import org.testar.windows.tag.UIATags;

public final class UIAStateBuilder implements StateBuilder {

    private static final long serialVersionUID = 796655140981849818L;
    final double timeOut; // seconds
    transient ExecutorService executor;
    transient long automationPointer, treeFilterConditionPointer, cacheRequestPointer;
    boolean accessBridgeEnabled;
    String SUTProcesses; // regex

    public UIAStateBuilder(){ this(10/*seconds*/,false,"");    }

    public UIAStateBuilder(double timeOut, boolean accessBridgeEnabled, String SUTProcesses){ // seconds
        Assert.isTrue(timeOut > 0);
        this.timeOut = timeOut;
        initialize();
        this.accessBridgeEnabled = accessBridgeEnabled;
        this.SUTProcesses = SUTProcesses;
        if (accessBridgeEnabled)
            new Thread(){ public void run(){ Windows.InitializeAccessBridge(); } }.start();
        executor = Executors.newFixedThreadPool(1);
    }

    private void initialize(){

        Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

        // create an automation cache
        automationPointer = Windows.CoCreateInstance(Windows.Get_CLSID_CUIAutomation_Ptr(), 0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IUIAutomation_Ptr());
        cacheRequestPointer = Windows.IUIAutomation_CreateCacheRequest(automationPointer);

        // scope and filter settings
        // only retrieve the control view
        long firstConditionPointer = Windows.IUIAutomation_get_ControlViewCondition(automationPointer);
        // next we only want the elements that are on screen
        treeFilterConditionPointer = Windows.IUIAutomation_CreateAndCondition(automationPointer, Windows.IUIAutomation_CreatePropertyCondition(automationPointer, Windows.UIA_IsOffscreenPropertyId, false), firstConditionPointer);
        //Windows.IUnknown_Release(pFirstCondition);

        // add the filter and treescope to the cache. For the scope we want the uiaElement and all of its descendants.
        Windows.IUIAutomationCacheRequest_put_TreeFilter(cacheRequestPointer, treeFilterConditionPointer);
        Windows.IUIAutomationCacheRequest_put_TreeScope(cacheRequestPointer, Windows.TreeScope_Subtree);
        Windows.IUIAutomationCacheRequest_put_AutomationElementMode(cacheRequestPointer, Windows.AutomationElementMode_Full);

        // cache patterns
        Windows.IUIAutomationCacheRequest_AddPattern(cacheRequestPointer, Windows.UIA_WindowPatternId);
        Windows.IUIAutomationCacheRequest_AddPattern(cacheRequestPointer, Windows.UIA_ValuePatternId);

        // cache properties
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_NamePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AcceleratorKeyPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AccessKeyPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AutomationIdPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_BoundingRectanglePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ClassNamePropertyId);
        //Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ClickablePointPropertyId);
        //Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ControllerForPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ControlTypePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_CulturePropertyId);
        //Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_DescribedByPropertyId);
        //Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FlowsToPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FrameworkIdPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_FullDescriptionPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_HasKeyboardFocusPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_HelpTextPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsContentElementPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsControlElementPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDataValidForFormPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsEnabledPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsKeyboardFocusablePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsOffscreenPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsPasswordPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsRequiredForFormPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ItemStatusPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ItemTypePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LabeledByPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LocalizedControlTypePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_NativeWindowHandlePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ProviderDescriptionPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_OrientationPropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_RuntimeIdPropertyId);
        //Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsWindowPatternAvailablePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ProcessIdPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AriaPropertiesPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AriaRolePropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsDialogPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_IsPeripheralPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LandmarkTypePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LocalizedLandmarkTypePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LevelPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_LiveSettingPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_PositionInSetPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_SizeOfSetPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_RotationPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_VisualEffectsPropertyId);

        // cache all active pattern availability and pattern properties
        UIATags.getPatternAvailabilityTags().stream().flatMap(tag -> Stream.concat(Stream.of(tag), UIATags.getChildTags(tag).stream()))
                .filter(UIATags::tagIsActive).forEach(tag ->
                    Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, UIAMapping.getPatternPropertyIdentifier(tag))
        );


        // the following properties are left in for backwards compatibility
        // scroll control pattern properties
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontallyScrollablePropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticallyScrollablePropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontalViewSizePropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticalViewSizePropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollHorizontalScrollPercentPropertyId);
//        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ScrollVerticalScrollPercentPropertyId);

        // window control pattern properties
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowIsTopmostPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowCanMaximizePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowCanMinimizePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowIsModalPropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowWindowInteractionStatePropertyId);
        Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_WindowWindowVisualStatePropertyId);

    }

    public void release(){
        if(automationPointer != 0){
            Windows.IUnknown_Release(treeFilterConditionPointer);
            Windows.IUnknown_Release(cacheRequestPointer);
            Windows.IUnknown_Release(automationPointer);
            Windows.CoUninitialize();
            automationPointer = 0;
            executor.shutdown();
        }
    }

    public void finalize(){ release(); }

    public UIAState apply(SUT system) throws StateBuildException {
        try {
            Future<UIAState> future = executor.submit(new UIAStateFetcher(system, automationPointer, cacheRequestPointer, this.accessBridgeEnabled, this.SUTProcesses));
            return future.get((long)(timeOut * 1000.0), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new StateBuildException(e);
        } catch (ExecutionException e) {
            e.printStackTrace(); // make the exception traceable
            throw new StateBuildException(e);
        } catch (TimeoutException e) {
            //UIAState ret = new UIAState(uiaRoot);
            UIAState ret = new UIAState(UIAStateFetcher.buildRoot(system));
            ret.set(Tags.Role, Roles.Process);
            ret.set(Tags.NotResponding, true);
            return ret;
        }
    }

    /*private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
        initialize();
        executor = Executors.newFixedThreadPool(1);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();
    }*/

}
