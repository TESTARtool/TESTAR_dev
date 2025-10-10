/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.webdriver;

import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.TaggableBase;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WdElement extends TaggableBase implements Serializable {
  private static final long serialVersionUID = 2695983969893321255L;

  private static final List<String> scrollOn = Arrays.asList("auto", "scroll");
  private static final List<String> scrollOff = Arrays.asList("visible", "hidden", "clip");
  private static final List<String> scrollableChildren = Arrays.asList(
      "block", "run-in", "flow", "flow-root", "table", "flex", "grid",
      "list-item", "table-row", "table-cell", "table-caption", "inline-block",
      "inline-table", "inline-flex", "inline-grid");
  private static final List<String> focusableTags = Arrays.asList(
      "input", "select", "textarea", "a", "button", "area");

  List<WdElement> children = new ArrayList<>();
  WdElement parent;
  WdRootElement root;
  WdWidget backRef;

  public boolean blocked;
  //long culture = 0L;
  boolean isModal = false; // i.c.w. access key

  public String id, name, genericTitle, tagName, textContent, helpText, title, placeholder, innerText;
  public String xpath = "";
  public List<String> cssClasses = new ArrayList<>();
  public String display, type;
  public String innerHTML, outerHTML;
  public int maxLength;

  boolean enabled, ignore, disabled;
  public boolean isClickable;
  public boolean isShadow;
  boolean isContentElement, isControlElement;
  boolean hasKeyboardFocus, isKeyboardFocusable;
  String acceleratorKey, accessKey;
  String valuePattern, href, style, styleOverflow, styleOverflowX, styleOverflowY, stylePosition, target, alt, src, visibility;
  String value;
  
  double zindex;
  double styleOpacity;
  public Rect rect;
  boolean scrollPattern, hScroll, vScroll;
  public double hScrollViewSize, vScrollViewSize, hScrollPercent, vScrollPercent;
  boolean isFullVisibleOnScreen;

  boolean checked, selected;

  // ComputedStyle properties
  String computedFontSize;

  String cssSelector;

  // Keep these here for fillScrollValues
  protected String overflowX, overflowY;
  protected String scrollSnapType;
  protected long clientWidth, clientHeight;
  private long offsetWidth, offsetHeight;
  public long scrollWidth, scrollHeight;
  public long scrollLeft, scrollTop;
  private long borderWidth, borderHeight;
  public long naturalWidth, naturalHeight; 
  public long displayedWidth, displayedHeight;  

  public transient RemoteWebElement remoteWebElement; // Reference to the remote Web Element

  public transient Map<String, String> attributeMap;

  public WdElement(WdRootElement root, WdElement parent) {
	  this.root = root;
	  this.parent = parent;
  }

  @SuppressWarnings("unchecked")
  public WdElement(Map<String, Object> packedElement, WdRootElement root, WdElement parent) {
    this.root = root;
    this.parent = parent;

    try {
    	attributeMap = (Map<String, String>) packedElement.get("attributeMap");
    }catch(ClassCastException e) {
    	System.out.println("-------------------------------------------------------------------------------------");
    	System.out.println("- POSSIBLE KNOWN ERROR: We cannot access the current URL through Selenium WebDriver");
    	System.out.println("- URL: " + WdDriver.getCurrentUrl());
    	System.out.println("- INFO: https://github.com/TESTARtool/TESTAR_dev/issues/203");
    	System.out.println("-------------------------------------------------------------------------------------");
    	throw e;
    }

    id = attributeMap.getOrDefault("id", "");
    name = attributeMap.getOrDefault("name", "");
    genericTitle = (String) packedElement.getOrDefault("name", "");
    tagName = (String) packedElement.get("tagName");
    textContent = ((String) packedElement.get("textContent")).replaceAll("\\s+", " ").trim();
    innerText = (packedElement.get("innerText") == null) ? "" : ((String) packedElement.get("innerText")).replaceAll("\\s+", " ").trim();
    title = attributeMap.getOrDefault("title","");
    href = attributeMap.getOrDefault("href", "");
    value = (packedElement.get("value") instanceof String) ? (String) packedElement.get("value") : "";
    style = attributeMap.getOrDefault("style", "");
    styleOverflow = (packedElement.get("styleOverflow") == null) ? "" : (String) packedElement.get("styleOverflow");
    styleOverflowX = (packedElement.get("styleOverflowX") == null) ? "" : (String) packedElement.get("styleOverflowX");
    styleOverflowY = (packedElement.get("styleOverflowY") == null) ? "" : (String) packedElement.get("styleOverflowY");
    stylePosition = (packedElement.get("stylePosition") == null) ? "" : (String) packedElement.get("stylePosition");
    target = attributeMap.getOrDefault("target", "");
    alt = attributeMap.getOrDefault("alt", "");
    type = attributeMap.getOrDefault("type", "");
    src = attributeMap.getOrDefault("src", "");
    placeholder = attributeMap.getOrDefault("placeholder", "");
    naturalWidth = (packedElement.get("naturalWidth") == null) ? 0 : castDimensionsToLong(packedElement.get("naturalWidth"));
    naturalHeight = (packedElement.get("naturalHeight") == null) ? 0 : castDimensionsToLong(packedElement.get("naturalHeight"));
    displayedWidth = (packedElement.get("displayedWidth") == null) ? 0 : castDimensionsToLong(packedElement.get("displayedWidth"));
    displayedHeight = (packedElement.get("displayedHeight") == null) ? 0 : castDimensionsToLong(packedElement.get("displayedHeight"));
    disabled = attributeMap.containsKey("disabled");
    visibility = (packedElement.get("visibility") == null) ? "" : (String) packedElement.get("visibility");
    xpath = (packedElement.get("xpath") == null) ? "" : (String) packedElement.get("xpath");

    try {
    	maxLength = Integer.valueOf(attributeMap.getOrDefault("maxlength", "-1"));
    } catch (NumberFormatException e) {
    	// This can happen if maxLength is defined without value or with incorrect content "2 2"
    	// Maybe this is a test oracle :)
    	maxLength = -1;
    }

    innerHTML = (String) packedElement.getOrDefault("innerHTML", "");
    outerHTML = (String) packedElement.getOrDefault("outerHTML", "");

    remoteWebElement = (RemoteWebElement)packedElement.get("element");
    checked = asBool(packedElement.getOrDefault("checked", false));
    selected = asBool(packedElement.getOrDefault("selected", false));

    String classesString = attributeMap.getOrDefault("class", "");
    if (classesString != null) {
      cssClasses = Arrays.asList(classesString.split(" "));
    }
    display = (String) packedElement.get("display");
    computedFontSize = (String) packedElement.getOrDefault("computedFontSize", "");

    styleOpacity = castObjectToDouble(packedElement.get("styleOpacity"),1.0);

    zindex = (double) (long) packedElement.get("zIndex");
    fillRect(packedElement);
    fillDimensions(packedElement);
    
    isFullVisibleOnScreen = isFullVisibleAtCanvasBrowser();

    blocked = (Boolean) packedElement.get("isBlocked");
    isClickable = (Boolean) packedElement.get("isClickable");
    isShadow = (parent != null && parent.isShadow) || (Boolean) packedElement.get("isShadowElement");
    isKeyboardFocusable = getIsFocusable();
    hasKeyboardFocus = (Boolean) packedElement.get("hasKeyboardFocus");

    enabled = !Constants.getHiddenTags().contains(tagName) && !disabled;
    if (display != null && display.toLowerCase().equals("none")) {
      enabled = false;
    }

    cssSelector = (String) packedElement.get("cssSelector");

    List<Map<String, Object>> wrappedChildren =
        (List<Map<String, Object>>) packedElement.get("wrappedChildren");
    for (Map<String, Object> wrappedChild : wrappedChildren) {
      WdElement child = new WdElement(wrappedChild, root, this);
      if (!Constants.getHiddenTags().contains(child.tagName) &&
          !Constants.getIgnoredTags().contains(child.tagName)) {
        children.add(child);
      }
    }

    setName();
    fillScrollValues();
    
    // Empty string ?
    //textContent = ((String) packedElement.get("textContent")).replaceAll("\\s+", " ").trim();
    //helpText = attributeMap.get("title");
    //value = String.valueOf(packedElement.getOrDefault("value", ""));
    /*valuePattern = attributeMap.getOrDefault("href", "");
    if (valuePattern == null || valuePattern.equals("")) {
      valuePattern = String.valueOf(packedElement.getOrDefault("value", ""));
    }*/
  }

  private boolean asBool(Object o) {
    if (o == null) return false;
    else return (Boolean)o;
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
  }
  
  /**
   * Check web element parameters and try to find an appropriate one to act as description
   */
  public String getElementDescription() {
	  if(name != null && !name.isEmpty()) {
		  return name;
	  }
	  else if(textContent != null && !textContent.isEmpty()) {
		  return textContent;
	  }
	  else if(id != null && !id.isEmpty()) {
		  return id;
	  }
	  else if(placeholder != null && !placeholder.isEmpty()) {
		  return placeholder;
	  }
	  else if(value != null && !value.isEmpty()) {
		  return value;
	  }
	  else if(type != null && !type.isEmpty()) {
		  return type;
	  }
	  else if(tagName != null && !tagName.isEmpty()) {
		  return tagName;
	  }
	  else if(title != null && !title.isEmpty()) {
		  return title;
	  }
	  else if(href != null && !href.isEmpty()) {
		  return href;
	  }

	  return String.join(",", cssClasses);
  }

  private void setName() {
    if (name == null || name.equals("null") || name.isEmpty()) {
      name = textContent;
    }
  }

  public boolean getIsFocusable() {
    // It's much more complex than this
    // https://allyjs.io/data-tables/focusable.html#document-elements
    // https://allyjs.io/tests/focusable/test.html
    // https://gist.github.com/jamiewilson/c3043f8c818b6b0ccffd
    // https://www.w3.org/TR/html5/editing.html#focus

    return focusableTags.contains(tagName);
  }

  protected void fillScrollValues() {
    // https://stackoverflow.com/a/29956778

    boolean hasHorizontalScrollbar = offsetHeight > (clientHeight + borderHeight);
    if(scrollOff.contains(overflowX) || !scrollSnapType.contains("none")) {
      hScroll = false;
    } else {
      hScroll = scrollWidth > clientWidth && (scrollOn.contains(overflowX) || hasHorizontalScrollbar);
    }
    if (scrollWidth != clientWidth) {
      hScrollPercent = 100.0 * scrollLeft / (scrollWidth - clientWidth);
    }
    hScrollViewSize = 100.0 * clientWidth / scrollWidth;

    boolean hasVerticalScrollbar = offsetWidth > (clientWidth + borderWidth);
    if(scrollOff.contains(overflowY) || !scrollSnapType.contains("none")) {
      vScroll = false;
    } else {
      vScroll = scrollHeight > clientHeight && (scrollOn.contains(overflowY) || hasVerticalScrollbar);
    }
    if (scrollHeight != clientHeight) {
      vScrollPercent = 100.0 * scrollTop / (scrollHeight - clientHeight);
    }
    vScrollViewSize = 100.0 * clientHeight / scrollHeight;

    scrollPattern = hScroll || vScroll;
  }

  public boolean visibleAt(double x, double y) {
    int scrollLeft = (root == null) ? 0 : (int) root.scrollLeft;
    int scrollHeight = (root == null) ? 0 : (int) root.scrollHeight;
    return rect != null && rect.contains(x - scrollLeft, y - scrollHeight);
  }

  public boolean visibleAt(double x, double y, boolean obscuredByChildFeature) {
    return visibleAt(x, y);
  }
  
  private boolean isFullVisibleAtCanvasBrowser() {
	  if (rect == null) return false;

	  boolean isVisibleAtCanvas = rect.x() >= 0 && rect.x() + rect.width() <= CanvasDimensions.getCanvasWidth() 
			  && rect.y() >= 0 && rect.y() + rect.height() <= CanvasDimensions.getInnerHeight();

	  // If the web element is a <select><option>, check the selected option visibility
	  if (tagName != null && tagName.equalsIgnoreCase("option") && outerHTML != null) {
		  if (outerHTML.contains("<option") && (outerHTML.contains("selected>") || outerHTML.contains("selected="))) {
			  return isVisibleAtCanvas;
		  } else {
			  return false;
		  }
	  }

	  // For other web elements, only check if fully visible in the canvas
	  return isVisibleAtCanvas;
  }

  public boolean isHidden() {
      if (visibility == null) return false;
      String vis = visibility.trim().toLowerCase();
      return vis.equals("hidden") || vis.equals("collapse");
  }

  public boolean isDisplayed() {
      if (display == null) return true;
      String disp = display.trim().toLowerCase();
      return !disp.contains("none") && !isHidden();
  }

  @SuppressWarnings("unchecked")
  /*
   * This gets the position relative to the viewport
   */
  private void fillRect(Map<String, Object> packedElement) {
    List<Long> rect = (List<Long>) packedElement.get("rect");
    this.rect = Rect.from(rect.get(0), rect.get(1), rect.get(2), rect.get(3));
  }

  @SuppressWarnings("unchecked")
  private void fillDimensions(Map<String, Object> packedElement) {
    Map<String, Object> dims = (Map<String, Object>) packedElement.get("dimensions");
    overflowX = String.valueOf(dims.get("overflowX"));
    overflowY = String.valueOf(dims.get("overflowY"));
    scrollSnapType = String.valueOf(dims.get("scrollSnapType"));
    clientWidth = castDimensionsToLong(dims.get("clientWidth"));
    clientHeight = castDimensionsToLong(dims.get("clientHeight"));
    offsetWidth = castDimensionsToLong(dims.get("offsetWidth"));
    offsetHeight = castDimensionsToLong(dims.get("offsetHeight"));
    scrollWidth = castDimensionsToLong(dims.get("scrollWidth"));
    scrollHeight = castDimensionsToLong(dims.get("scrollHeight"));
    scrollLeft = castDimensionsToLong(dims.get("scrollLeft"));
    scrollTop = castDimensionsToLong(dims.get("scrollTop"));
    borderWidth = castDimensionsToLong(dims.get("borderWidth"));
    borderHeight = castDimensionsToLong(dims.get("borderHeight"));
  }
  
  private long castDimensionsToLong(Object o) {
	  if(o instanceof Double)
		  return ((Double) o).longValue();
	  else if(o instanceof Long)
		  return ((Long) o).longValue();
	  
	  return (long)o;
  }

  private Double castObjectToDouble(Object o, double defaultValue) {
	  Double val = defaultValue;
	  if (o instanceof Number) {
		  val = ((Number) o).doubleValue();
	  }
	  return val;
  }
}
