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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.StateBuildException;

public final class UIAStateBuilder implements StateBuilder {

	private static final long serialVersionUID = 796655140981849818L;
	final double timeOut;
	transient ExecutorService executor;
	transient long pAutomation, pCondition, pCacheRequest;

	public UIAStateBuilder(){ this(10);	}

	public UIAStateBuilder(double timeOut){
		Assert.isTrue(timeOut > 0);
		this.timeOut = timeOut;
		initialize();
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

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
		initialize();
		executor = Executors.newFixedThreadPool(1);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}

	private class StateFetcher implements Callable<UIAState>{
		private final SUT system;

		public StateFetcher(SUT system){ 
			this.system = system;
		}

		public UIAState call() throws Exception {
			Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);

			UIARootElement uiaRoot = buildSkeletton(system);
			UIAState root = createWidgetTree(uiaRoot);
			root.set(Tags.Role, Roles.Process);
			root.set(Tags.NotResponding, false);
			// begin by urueda
			for (Widget w : root)
				w.set(Tags.Path,Util.indexString(w));
			// end by urueda
			Windows.CoUninitialize();
			return root;
		}

		UIARootElement buildSkeletton(SUT system){
			UIARootElement uiaRoot = new UIARootElement();	
			uiaRoot.isRunning = system.isRunning();

			long[] info = Windows.GetMonitorInfo(Windows.GetPrimaryMonitorHandle());
			uiaRoot.rect = Rect.fromCoordinates(info[1], info[2], info[3], info[4]);
			uiaRoot.timeStamp = System.currentTimeMillis();
			uiaRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
			uiaRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;

			if(!uiaRoot.isRunning)
				return uiaRoot;

			uiaRoot.pid = system.get(Tags.PID);
			uiaRoot.isForeground = WinProcess.isForeground(uiaRoot.pid);			

			// find all visible top level windows on the desktop
			Iterable<Long> visibleTopLevelWindows = visibleTopLevelWindows();

			UIAElement modalElement = null; // by urueda
			
			// descend the root windows which belong to our process, using UIAutomation
			uiaRoot.children = new ArrayList<UIAElement>();
			List<Long> ownedWindows = new ArrayList<Long>();
			for(long hwnd : visibleTopLevelWindows){				
				boolean owned = Windows.GetWindow(hwnd, Windows.GW_OWNER) != 0;
				boolean include = Windows.GetWindowProcessId(hwnd) == uiaRoot.pid;
				
				if(include){
					if(!owned)
						//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
						modalElement = uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot); // by urueda
					else
						ownedWindows.add(hwnd);
				}
			}

			// if UIAutomation missed an owned window, we'll collect it here
			for(long hwnd : ownedWindows){				
				if(!uiaRoot.hwndMap.containsKey(hwnd)){
					//uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot);
					UIAElement modalE;
					// begin by urueda
					if ((modalE = uiaDescend(uiaCacheWindowTree(hwnd), uiaRoot)) != null)
							modalElement = modalE;
					// end by urueda
				}
			}

			// set z-indices for the windows
			int z = 0;
			for(long hwnd : visibleTopLevelWindows){
				long exStyle = Windows.GetWindowLong(hwnd, Windows.GWL_EXSTYLE);				
				//if((exStyle & Windows.WS_EX_NOACTIVATE) != 0)
				//	System.out.println(hwnd  + "   " + Windows.GetWindowText(hwnd) + "   " + Windows.GetClassName(hwnd));

				UIAElement wnd = uiaRoot.hwndMap.get(hwnd);

				// if we didn't encounter the window yet, it will be a foreign window
				
				// begin by wcoux (Spy mode layering through transparent window)
				// Get the title of the window - if the window is Testar's Spy window, skip it. Otherwise,
				// the HitTest function will not work properly: it doesn't actually hit test, it checks if windows are
				// on top of the SUT and the Java implementation of the Spy mode drawing is an invisible always on top window.
				String windowTitle = Windows.GetWindowText(hwnd);

				if (windowTitle != null && Objects.equals(windowTitle, "Testar - Spy window")) {
					continue;
				}
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

			calculateZIndices(uiaRoot, 0);
			buildTLCMap(uiaRoot);
			markBlockedElements(uiaRoot);

			markBlockedElements(uiaRoot,modalElement); // by urueda		

			return uiaRoot;
		}

		/* lists all visible top level windows in ascending z-order (foreground window last) */
		Iterable<Long> visibleTopLevelWindows(){
			Deque<Long> ret = new ArrayDeque<Long>();
			long hwnd = Windows.GetWindow(Windows.GetDesktopWindow(), Windows.GW_CHILD);

			while(hwnd != 0){
				if(Windows.IsWindowVisible(hwnd)){
					
					long exStyle = Windows.GetWindowLong(hwnd, Windows.GWL_EXSTYLE);				
					if((exStyle & Windows.WS_EX_TRANSPARENT) == 0 && (exStyle & Windows.WS_EX_NOACTIVATE) == 0)
						ret.addFirst(hwnd);
				}
				hwnd = Windows.GetNextWindow(hwnd, Windows.GW_HWNDNEXT);
			}
			return ret;
		}

		/* fire up the cache request */
		long uiaCacheWindowTree(long hwnd){
			return Windows.IUIAutomation_ElementFromHandleBuildCache(pAutomation, hwnd, pCacheRequest);
		}

		void buildTLCMap(UIARootElement root){
			ElementMap.Builder builder = ElementMap.newBuilder();
			buildTLCMap(builder, root);
			root.tlc = builder.build();		
		}

		void buildTLCMap(ElementMap.Builder builder, UIAElement el){
			if(el.isTopLevelContainer)
				builder.addElement(el);
			for(int i = 0; i < el.children.size(); i++)
				buildTLCMap(builder, el.children.get(i));
		}

		//void uiaDescend(long uiaPtr, UIAElement parent){
		UIAElement uiaDescend(long uiaPtr, UIAElement parent){ // by urueda (returns a modal widget if detectsed)
			if(uiaPtr == 0)
				//return;
				return null; // by urueda

			UIAElement modalElement = null; // by urueda
			
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
			el.hasKeyboardFocus = Windows.IUIAutomationElement_get_HasKeyboardFocus(uiaPtr, true); 
			el.isKeyboardFocusable = Windows.IUIAutomationElement_get_IsKeyboardFocusable(uiaPtr, true);

			parent.root.hwndMap.put(el.hwnd, el);

			// get extra infos from windows
			if(el.ctrlId == Windows.UIA_WindowControlTypeId){
				long uiaWndPtr = Windows.IUIAutomationElement_GetPattern(uiaPtr, Windows.UIA_WindowPatternId, true);
				if(uiaWndPtr != 0){
					el.wndInteractionState = Windows.IUIAutomationWindowPattern_get_WindowInteractionState(uiaWndPtr, true);
					el.blocked = (el.wndInteractionState != Windows.WindowInteractionState_ReadyForUserInteraction);
					el.isWndTopMost = Windows.IUIAutomationWindowPattern_get_IsTopmost(uiaWndPtr, true);
					el.isModal = Windows.IUIAutomationWindowPattern_get_IsModal(uiaWndPtr, true);
					Windows.IUnknown_Release(uiaWndPtr);
				}
			}
			
			// begin by urueda
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
			// end by urueda	

			// descend children
			long uiaChildrenPtr = Windows.IUIAutomationElement_GetCachedChildren(uiaPtr);
			Windows.IUnknown_Release(uiaPtr);

			if(uiaChildrenPtr != 0){
				long count = Windows.IUIAutomationElementArray_get_Length(uiaChildrenPtr);

				if(count > 0){
					el.children = new ArrayList<UIAElement>((int)count);

					for(int i = 0; i < count; i++){
						long ptrChild = Windows.IUIAutomationElementArray_GetElement(uiaChildrenPtr, i);
						if(ptrChild != 0){
							//uiaDescend(ptrChild, el);
							// begin by urueda
							UIAElement modalE;
							if ((modalE = uiaDescend(ptrChild, el)) != null && modalElement == null) // parent-modal is preferred to child-modal
								modalElement = modalE;
							// end by urueda							
						}
					}
				}
				Windows.IUnknown_Release(uiaChildrenPtr);
			}
			
			return modalElement; // by urueda
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
		
		private void calculateZIndices(UIAElement el, double wndZ){
			if(!el.isTopLevelContainer && el.parent != null)
				el.zindex = el.parent.zindex;

			for(int i = 0; i < el.children.size(); i++)
				calculateZIndices(el.children.get(i), wndZ);
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

	public UIAState apply(SUT system) throws StateBuildException {
		//default state
		UIARootElement uiaRoot = new UIARootElement();
		uiaRoot.isRunning = system.isRunning();

		long[] info = Windows.GetMonitorInfo(Windows.GetPrimaryMonitorHandle());
		uiaRoot.rect = Rect.fromCoordinates(info[1], info[2], info[3], info[4]);
		uiaRoot.timeStamp = System.currentTimeMillis();
		uiaRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
		uiaRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;
		
		try {
			Future<UIAState> future = executor.submit(new StateFetcher(system));
			return future.get((long)(timeOut * 1000.0), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new StateBuildException(e);
		} catch (ExecutionException e) {
			throw new StateBuildException(e);
		} catch (TimeoutException e) {
			UIAState ret = new UIAState(uiaRoot);
			ret.set(Tags.Role, Roles.Process);
			ret.set(Tags.NotResponding, true);
			return ret;
		}
	}

}