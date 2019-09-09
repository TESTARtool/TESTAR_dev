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

import org.fruit.alayer.HitTester;

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
