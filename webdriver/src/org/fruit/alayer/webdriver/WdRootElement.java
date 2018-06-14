package org.fruit.alayer.webdriver;

import java.util.Map;

public class WdRootElement extends WdElement {
  private static final long serialVersionUID = -6309113639487862284L;

  public long pid;
  public long timeStamp;
  public boolean isRunning;
  public boolean isForeground;
  public boolean hasStandardMouse;
  public boolean hasStandardKeyboard;
  public String documentTitle;

  public WdRootElement(Map<String, Object> packedbody) {
    super(packedbody, null, null);
    root = this;
    parent = this;
    isForeground = (Boolean) packedbody.get("documentHasFocus");
    documentTitle = (String) packedbody.get("documentTitle");
    blocked = false;
  }

  public WdElement at(double x, double y) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void fillScrollValues() {
    hScroll = scrollWidth > clientWidth;
    if (scrollWidth != clientWidth) {
      hScrollPercent = 100.0 * scrollLeft / (scrollWidth - clientWidth);
    }
    hScrollViewSize = 100.0 * clientWidth / scrollWidth;

    vScroll = scrollHeight > clientHeight;
    if (scrollHeight != clientHeight) {
      vScrollPercent = 100.0 * scrollTop / (scrollHeight - clientHeight);
    }
    vScrollViewSize = 100.0 * clientHeight / scrollHeight;

    scrollPattern = vScroll || hScroll;
  }
}
