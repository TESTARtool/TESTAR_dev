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
   * @param x The x-coordiante of the point.
   * @param y The y-coordinate of the point.
   * @return True if the element can be hit at the supplied point on the screen
   */
  public boolean apply(double x, double y) {
    return element.visibleAt(x, y);
  }

  /**
   * Runs the hit test action for a certain point on the element.
   * @param x The x-coordiante of the point.
   * @param y The y-coordinate of the point.
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
