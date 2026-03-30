/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.action;

import org.testar.core.exceptions.ActionFailedException;
import org.testar.webdriver.state.WdDriver;
import org.testar.core.action.*;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;

public class WdCloseTabAction extends TaggableBase implements Action {
  private static final long serialVersionUID = 8984904115521916146L;

  public WdCloseTabAction() {
	  this.set(Tags.Role, WdActionRoles.CloseTabScript);
	  this.set(Tags.Desc, "Execute Webdriver script to close the current tab");
  }

  public WdCloseTabAction(State state) {
	  this();
	  this.set(Tags.OriginWidget, state);
  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    WdDriver.executeScript("window.close();");
  }

  @Override
  public String toShortString() {
    return "Close tab";
  }

  @Override
  public String toParametersString() {
    return toShortString();
  }

  @Override
  public String toString(Role... discardParameters) {
    return toShortString();
  }
}
