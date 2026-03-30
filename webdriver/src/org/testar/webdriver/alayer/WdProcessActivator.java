/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.alayer;

import org.testar.webdriver.state.WdDriver;

public final class WdProcessActivator implements Runnable {

  public void run() {
    WdDriver.activate();
  }
}
