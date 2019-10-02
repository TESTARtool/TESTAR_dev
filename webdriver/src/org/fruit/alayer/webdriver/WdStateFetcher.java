/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.fruit.alayer.webdriver;

import org.fruit.Util;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.StateBuildException;
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
        "return getStateTreeTestar(arguments[0])", Constants.ignoredTags);

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
      return null;
    }

    WdRootElement wdRoot = new WdRootElement(packedBody);
    wdRoot.isRunning = system.isRunning();
    wdRoot.timeStamp = System.currentTimeMillis();
    wdRoot.hasStandardKeyboard = system.get(Tags.StandardKeyboard, null) != null;
    wdRoot.hasStandardMouse = system.get(Tags.StandardMouse, null) != null;
    wdRoot.pid = system.get(Tags.PID);

    return wdRoot;
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

    WdState root = createWidgetTree(rootElement);
    root.set(Tags.Role, Roles.Process);
    root.set(Tags.NotResponding, false);

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
    if (!element.enabled) {
      return;
    }

    WdWidget w = parent.root().addChild(parent, element);
    element.backRef = w;
    
    for (WdElement child : element.children) {
      createWidgetTree(w, child);
    }
  }
}
