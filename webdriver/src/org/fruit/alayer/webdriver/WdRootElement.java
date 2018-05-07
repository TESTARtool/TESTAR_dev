package org.fruit.alayer.webdriver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WdRootElement extends WdElement {
  private static final long serialVersionUID = -6309113639487862284L;

  // TODO Don't need this?
  // TODO Remember window handle(s)?
  public long pid;
  public long timeStamp;
  public boolean isRunning;
  public boolean isForeground;
  public boolean hasStandardMouse;
  public boolean hasStandardKeyboard;
  public Set<String> windowHandles;
  public ElementMap tlc;

  public WdRootElement(Map<String, Object> packedbody) {
    super(packedbody, null, null);
    root = this;
    parent = this;
    windowHandles = new HashSet<>();
    tlc = ElementMap.newBuilder().build();
    isForeground = false;
  }

  public WdElement at(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public boolean visibleAt(WdElement el, double x, double y) {
    if (el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
      return false;
    }

    WdElement topLevelContainer = tlc.at(x, y);
    return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex) && !obscuredByChildren(el, x, y);
  }

  public boolean visibleAt(WdElement el, double x, double y, boolean obscuredByChildFeature) {
    if (el.rect == null || !el.rect.contains(x, y) || !this.rect.contains(x, y)) {
      return false;
    }

    WdElement topLevelContainer = tlc.at(x, y);
    return (topLevelContainer == null || topLevelContainer.zindex <= el.zindex ||
            !obscuredByChildFeature || !obscuredByChildren(el, x, y));
  }

  private boolean obscuredByChildren(WdElement el, double x, double y) {
    for (int i = 0; i < el.children.size(); i++) {
      WdElement child = el.children.get(i);
      if (child.rect != null && child.rect.contains(x, y) && child.zindex >= el.zindex) {
        return true;
      }
    }
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void fillScrollValues () {
    hScroll = innerWidth > clientWidth;
    vScroll = innerHeight > clientHeight;
    scrollPattern = vScroll || hScroll;
    if (scrollWidth != clientWidth) {
      hScrollPercent = 100.0 * scrollLeft / (scrollWidth - clientWidth);
    }
    if (scrollHeight != clientHeight) {
      vScrollPercent = 100.0 * scrollTop / (scrollHeight - clientHeight);
    }
    hScrollViewSize = 100.0 * clientWidth / scrollWidth;
    vScrollViewSize = 100.0 * clientHeight / scrollHeight;
  }
}
