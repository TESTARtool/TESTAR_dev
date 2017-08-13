/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

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

	public UIAStateBuilder(){ this(10/*seconds*/,false,"");	}

	public UIAStateBuilder(double timeOut, boolean accessBridgeEnabled, String SUTProcesses){ // seconds
		Assert.isTrue(timeOut > 0);
		this.timeOut = timeOut;
		initialize();
		// begin by urueda
		this.accessBridgeEnabled = accessBridgeEnabled;
		this.SUTProcesses = SUTProcesses;
		if (accessBridgeEnabled)
			new Thread(){ public void run(){ Windows.InitializeAccessBridge(); } }.start(); // based on ferpasri
		// end by urueda
		executor = Executors.newFixedThreadPool(1);
	}

	private void initialize(){

		Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);
		//System.out.println(Windows.Get_CLSID_CUIAutomation_Ptr());
		pAutomation = Windows.CoCreateInstance(Windows.Get_CLSID_CUIAutomation_Ptr(), 0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IUIAutomation_Ptr());

		// scope and filter settings
		//long pFirstCondition = Windows.IUIAutomation_CreateTrueCondition(pAutomation);
		long pFirstCondition = Windows.IUIAutomation_get_ControlViewCondition(pAutomation);	


		//pCondition = Windows.IUIAutomation_CreateTrueCondition(pAutomation);
		pCondition = Windows.IUIAutomation_CreateAndCondition(pAutomation, Windows.IUIAutomation_CreatePropertyCondition(pAutomation, Windows.UIA_IsOffscreenPropertyId, false), pFirstCondition);
		//Windows.IUnknown_Release(pFirstCondition);

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
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ClickablePointPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ControllerForPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ControlTypePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_CulturePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_DescribedByPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_FlowsToPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_FrameworkIdPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_HasKeyboardFocusPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_HelpTextPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsContentElementPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsControlElementPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsDataValidForFormPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsEnabledPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsKeyboardFocusablePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsOffscreenPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsPasswordPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsRequiredForFormPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ItemStatusPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ItemTypePropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_LabeledByPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_LocalizedControlTypePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_NativeWindowHandlePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ProviderDescriptionPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_OrientationPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_RuntimeIdPropertyId);
		//Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsWindowPatternAvailablePropertyId);

		// begin by urueda
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_IsScrollPatternAvailablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontallyScrollablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticallyScrollablePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontalViewSizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticalViewSizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollHorizontalScrollPercentPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_ScrollVerticalScrollPercentPropertyId);
		// end by urueda

		// window role properties
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowIsTopmostPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowCanMaximizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowCanMinimizePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowIsModalPropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowWindowInteractionStatePropertyId);
		Windows.IUIAutomationCacheRequest_AddProperty(pCacheRequest, Windows.UIA_WindowWindowVisualStatePropertyId);

	}

	public void release(){
		if(pAutomation != 0){
			Windows.IUnknown_Release(pCondition);
			Windows.IUnknown_Release(pCacheRequest);
			Windows.IUnknown_Release(pAutomation);
			Windows.CoUninitialize();
			pAutomation = 0;
			executor.shutdown();
		}
	}

	public void finalize(){ release(); }

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
			//UIAState ret = new UIAState(uiaRoot);
			UIAState ret = new UIAState(StateFetcher.buildRoot(system)); // by urueda
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