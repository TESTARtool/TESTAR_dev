/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.alayer;

import org.testar.core.alayer.HitTester;
import org.testar.webdriver.state.WdElement;

import java.io.Serializable;

public class WdHitTester implements HitTester, Serializable {
  private static final long serialVersionUID = 2627030651264566538L;

  private final WdElement element;

  public WdHitTester(WdElement element) {
    this.element = element;
  }

  /**
   * Runs the hit test action for a certain point on the element.
   *
   * @param x The x-coordiante of the point.
   * @param y The y-coordinate of the point.
   * @return True if the element can be hit at the supplied point on the screen
   */
  public boolean apply(double x, double y) {
    return element.visibleAt(x, y);
  }

  /**
   * Runs the hit test action for a certain point on the element.
   *
   * @param x                      The x-coordiante of the point.
   * @param y                      The y-coordinate of the point.
   * @param obscuredByChildFeature The element is obscured by a child??
   * @return True if the element can be hit at the supplied point on the screen
   */
  public boolean apply(double x, double y, boolean obscuredByChildFeature) {
    return element.visibleAt(x, y, obscuredByChildFeature);
  }

  public String toString() {
    return "WdHitTester";
  }
}
