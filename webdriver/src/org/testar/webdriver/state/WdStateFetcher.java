/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.testar.core.util.Util;
import org.testar.core.alayer.Roles;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.core.exceptions.StateBuildException;
import org.testar.webdriver.tag.WdTags;
import org.testar.webdriver.util.WdConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WdStateFetcher implements Callable<WdState> {
  private final SUT system;

  public WdStateFetcher(SUT system) {
    this.system = system;
  }

  @SuppressWarnings("unchecked")
  public static WdRootElement buildRoot(SUT system) throws StateBuildException {
    Object result = WdDriver.executeScript(
        "return getStateTreeTestar(arguments[0], arguments[1])", 
        WdConstants.getIgnoredTags(), WdConstants.getIgnoredAttributes());

    // TODO As Edge limits its recursion to 20, we need to flatten the tree in JS
    // And unflatten the list here into a nested Map (as produced by Chrome / FF)
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/18531786/
    Map<String, Object> packedBody;
    if (result instanceof List) {
      packedBody = unflattenTree((List<Map<String, Object>>) result);
    }
    else if (result instanceof Map) {
      packedBody = (Map<String, Object>) result;
    }
    else {
    	return emptyRootState(system);
    }

    WdRootElement wdRoot = new WdRootElement(packedBody);
    wdRoot.isRunning = system.isRunning();
    wdRoot.timeStamp = System.currentTimeMillis();
    wdRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
    wdRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;
    wdRoot.pid = system.get(Tags.PID);

    return wdRoot;
  }

  /**
   * Create and return an empty state
   */
  private static WdRootElement emptyRootState(SUT system) {
	  WdRootElement emptyRootState = new WdRootElement();
	  emptyRootState.isRunning = system.isRunning();
	  emptyRootState.timeStamp = System.currentTimeMillis();
	  emptyRootState.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
	  emptyRootState.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;
	  emptyRootState.pid = system.get(Tags.PID);
	  return emptyRootState;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> unflattenTree(List<Map<String, Object>> flatTree) {
    for (int idx = flatTree.size() - 1; idx > 0; idx--) {
      Map<String, Object> node = flatTree.remove(idx);

      Long parentId = (Long) node.get("parentId");
      Map<String, Object> parent = getParent(parentId.intValue(), flatTree);
      List<Map<String, Object>> wrappedChildren = (List<Map<String, Object>>) parent.get("wrappedChildren");
      wrappedChildren.add(node);
    }

    return flatTree.get(0);
  }

  private static Map<String, Object> getParent(int parentId,
                                               List<Map<String, Object>> flatTree) {
    Map<String, Object> parent = flatTree.remove(parentId);
    Map<String, Object> newParent = new HashMap<>();
    for (String key : parent.keySet()) {
      newParent.put(key, parent.get(key));
    }
    flatTree.add(parentId, newParent);
    return newParent;
  }

  public WdState call() {
    WdRootElement rootElement = buildRoot(system);

    if (rootElement == null) {
      system.set(Tags.Desc, " ");
      return new WdState(null);
    }

    system.set(Tags.Desc, rootElement.documentTitle);
    system.set(Tags.StateRenderTime, rootElement.largestContentfulPaint);

    WdState root = createWidgetTree(rootElement);
    root.set(Tags.Role, Roles.Process);
    root.set(Tags.NotResponding, false);
    root.set(WdTags.WebHref, WdDriver.getCurrentUrl());
    root.set(WdTags.WebTitle, WdDriver.getTitle());
    root.set(Tags.StateRenderTime, rootElement.largestContentfulPaint);

    for (Widget w : root) {
      w.set(Tags.Path, Util.indexString(w));
    }

    return root;
  }

  private WdState createWidgetTree(WdRootElement root) {
    WdState state = new WdState(root);
    root.backRef = state;
    for (WdElement childElement : root.children) {
      if (!childElement.ignore) {
        createWidgetTree(state, childElement);
      }
    }
    return state;
  }

  private void createWidgetTree(WdWidget parent, WdElement element) {
    WdWidget w = parent.root().addChild(parent, element);
    element.backRef = w;
    
    for (WdElement child : element.children) {
      createWidgetTree(w, child);
    }
  }
}
