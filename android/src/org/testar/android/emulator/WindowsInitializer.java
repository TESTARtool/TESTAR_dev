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

import java.util.stream.Stream;

import org.fruit.alayer.windows.UIAMapping;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.windows.Windows;

public class WindowsInitializer {

	private long automationPointer;
	private long treeFilterConditionPointer;
	private long cacheRequestPointer;

	public long getAutomationPointer() {
		return automationPointer;
	}

	public long getTreeFilterConditionPointer() {
		return treeFilterConditionPointer;
	}

	public long getCacheRequestPointer() {
		return cacheRequestPointer;
	}

	public WindowsInitializer(){

		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

		// create an automation cache
		automationPointer = Windows.CoCreateInstance(Windows.Get_CLSID_CUIAutomation_Ptr(), 0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IUIAutomation_Ptr());
		cacheRequestPointer = Windows.IUIAutomation_CreateCacheRequest(automationPointer);

		// scope and filter settings
		// only retrieve the control view
		long firstConditionPointer = Windows.IUIAutomation_get_ControlViewCondition(automationPointer);
		// next we only want the elements that are on screen
		treeFilterConditionPointer = Windows.IUIAutomation_CreateAndCondition(automationPointer, Windows.IUIAutomation_CreatePropertyCondition(automationPointer, Windows.UIA_IsOffscreenPropertyId, false), firstConditionPointer);

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
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ControlTypePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_CulturePropertyId);
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
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_ProcessIdPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AriaPropertiesPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(cacheRequestPointer, Windows.UIA_AriaRolePropertyId);
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
		}
	}

	public void finalize(){ release(); }

}
