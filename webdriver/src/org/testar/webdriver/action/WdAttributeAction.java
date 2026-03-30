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

public class WdAttributeAction extends TaggableBase implements Action {
  private static final long serialVersionUID = 6812147314265385744L;

  private String elementId;
  private String key;
  private String value;

  public WdAttributeAction(String elementId, String key, String value) {
    this.elementId = elementId;
    this.key = key;
    this.value = value;
    this.set(Tags.Role, WdActionRoles.SetAttributeScript);
    this.set(Tags.Desc, "Execute Webdriver script to set into " + elementId + " " + key + " " + value);
  }

  @Override
  public void run(SUT system, State state, double duration) throws ActionFailedException {
      String setAttribute = String.format("setAttribute('%s','%s')", key, value);
      // TODO: Selecting by ID is not enough... Give possibility to define the needed element.
      WdDriver.executeScript(String.format(
              "var s = document.getElementById('%s'); if (s !=null) { document.getElementById('%s').%s; } else { document.getElementsByName('%s').value = '%s'; }",
              elementId, elementId, setAttribute, elementId, value));
  }

  @Override
  public String toShortString() {
    return "Set attribute on id '" + elementId +
           "' for '" + key +
           "' to '" + value + "'";
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
