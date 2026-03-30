/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class WdRootElement extends WdElement {
  private static final long serialVersionUID = -6309113639487862284L;

  public long pid;
  public long timeStamp;
  public boolean isRunning;
  public boolean isForeground;
  public boolean hasStandardMouse;
  public boolean hasStandardKeyboard;
  public double largestContentfulPaint;
  public String documentTitle;

  public WdRootElement() {
	  super(null, null);
	  root = this;
	  parent = null; // root element has no parent
	  isForeground = true;
	  documentTitle = WdDriver.getCurrentUrl();
	  blocked = false;
  }

  public WdRootElement(Map<String, Object> packedbody) {
    super(packedbody, null, null);
    root = this;
    parent = null; // root element has no parent
    isForeground = (Boolean) packedbody.get("documentHasFocus");
    documentTitle = (String) packedbody.get("documentTitle");
    blocked = false;

    Object lcpTime = packedbody.get("largestContentfulPaint");
    if (lcpTime instanceof Number) {
    	largestContentfulPaint = ((Number) lcpTime).doubleValue();
    	largestContentfulPaint = BigDecimal.valueOf(largestContentfulPaint)
    			.setScale(3, RoundingMode.HALF_UP)
    			.doubleValue();
    } else {
    	largestContentfulPaint = -1.0;
    }
  }

  public WdElement at(double x, double y) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void fillScrollValues() {

    // Vertical Scroll
    vScroll = scrollHeight > clientHeight;
    if (scrollHeight != clientHeight) {
      vScrollPercent = 100.0 * scrollTop / (scrollHeight - clientHeight);
    }
    vScrollViewSize = 100.0 * clientHeight / scrollHeight;

    // TESTAR script function that compares document body height with browser height
    Object _isPageVerticalScrollable = WdDriver.executeScript("return isPageVerticalScrollable()");
    if (_isPageVerticalScrollable instanceof Boolean) {
      vScroll = (Boolean)_isPageVerticalScrollable;
      vScrollViewSize = 100.0 * rect.height() / clientHeight;
    }

    // Horizontal Scroll
    hScroll = scrollWidth > clientWidth;
    if (scrollWidth != clientWidth) {
      hScrollPercent = 100.0 * scrollLeft / (scrollWidth - clientWidth);
    }
    hScrollViewSize = 100.0 * clientWidth / scrollWidth;

    // TESTAR script function that compares document body width with browser width
    Object _isPageHorizontalScrollable = WdDriver.executeScript("return isPageHorizontalScrollable()");
    if (_isPageHorizontalScrollable instanceof Boolean) {
      hScroll = (Boolean) _isPageHorizontalScrollable;
      hScrollViewSize = 100.0 * rect.width() / clientWidth;
    }

    scrollPattern = vScroll || hScroll;
  }
}
