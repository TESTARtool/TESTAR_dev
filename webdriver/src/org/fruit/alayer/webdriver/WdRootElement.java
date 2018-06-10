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
  public String documentTitle;

  public WdRootElement(Map<String, Object> packedbody) {
    super(packedbody, null, null);
    root = this;
    parent = this;
    windowHandles = new HashSet<>();
    isForeground = (Boolean) packedbody.get("documentHasFocus");
    documentTitle = (String) packedbody.get("documentTitle");
    blocked = false;
  }

  public WdElement at(double x, double y) {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void fillScrollValues () {
    hScroll = scrollWidth > clientWidth;
    vScroll = scrollHeight > clientHeight;
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
