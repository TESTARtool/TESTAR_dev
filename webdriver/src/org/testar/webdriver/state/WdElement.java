/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.TaggableBase;
import org.testar.webdriver.alayer.WdCanvasDimensions;
import org.testar.webdriver.util.WdConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WdElement extends TaggableBase implements Serializable {
  private static final long serialVersionUID = 2695983969893321255L;
  private static final int TEXT_DESC_MAX_LENGTH = 30;

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
  public boolean isModal = false; // i.c.w. access key

  public String id, name, genericTitle, tagName, textContent, helpText, title, placeholder, innerText;
  public String xpath = "";
  public List<String> cssClasses = new ArrayList<>();
  public String display, type;
  public String innerHTML, outerHTML;
  public int maxLength;

  public boolean enabled, ignore, disabled;
  public boolean isClickable;
  public boolean isShadow;
  public boolean isContentElement, isControlElement;
  public boolean hasKeyboardFocus, isKeyboardFocusable;
  public String acceleratorKey, accessKey;
  public String valuePattern, href, style, styleOverflow, styleOverflowX, styleOverflowY, stylePosition, target, alt, src, visibility;
  public String value;
  
  public double zindex;
  public double styleOpacity;
  public Rect rect;
  public boolean scrollPattern, hScroll, vScroll;
  public double hScrollViewSize, vScrollViewSize, hScrollPercent, vScrollPercent;
  public boolean isFullVisibleOnScreen;

  public boolean checked, selected;

  // ComputedStyle properties
  public String computedFontSize;

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

  // Web aria properties
  public String ariaLabel, ariaLabelledBy, ariaDescribedBy, ariaRole, ariaChecked, ariaInvalid;
  public String ariaCurrent, ariaHasPopup, ariaControls, ariaLive;
  public String ariaValueNow, ariaValueMin, ariaValueMax, ariaValueText;
  public Boolean ariaDisabled, ariaHidden, ariaExpanded, ariaPressed, ariaSelected;
  public Boolean ariaRequired, ariaReadOnly, ariaBusy, ariaModal;

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

    ariaLabel = getAttribute("aria-label");
    ariaLabelledBy = getAttribute("aria-labelledby");
    ariaDescribedBy = getAttribute("aria-describedby");
    ariaRole = getAttribute("role");
    ariaDisabled = parseBoolean(getAttribute("aria-disabled"));
    ariaHidden = parseBoolean(getAttribute("aria-hidden"));
    ariaExpanded = parseBoolean(getAttribute("aria-expanded"));
    ariaPressed = parseBoolean(getAttribute("aria-pressed"));
    ariaSelected = parseBoolean(getAttribute("aria-selected"));
    ariaChecked = getAttribute("aria-checked");
    ariaRequired = parseBoolean(getAttribute("aria-required"));
    ariaInvalid = getAttribute("aria-invalid");
    ariaReadOnly = parseBoolean(getAttribute("aria-readonly"));
    ariaCurrent = getAttribute("aria-current");
    ariaHasPopup = getAttribute("aria-haspopup");
    ariaControls = getAttribute("aria-controls");
    ariaLive = getAttribute("aria-live");
    ariaBusy = parseBoolean(getAttribute("aria-busy"));
    ariaModal = parseBoolean(getAttribute("aria-modal"));
    ariaValueNow = getAttribute("aria-valuenow");
    ariaValueMin = getAttribute("aria-valuemin");
    ariaValueMax = getAttribute("aria-valuemax");
    ariaValueText = getAttribute("aria-valuetext");

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

    enabled = !WdConstants.getHiddenTags().contains(tagName) && !disabled;
    if (display != null && display.toLowerCase().equals("none")) {
      enabled = false;
    }

    List<Map<String, Object>> wrappedChildren =
        (List<Map<String, Object>>) packedElement.get("wrappedChildren");
    for (Map<String, Object> wrappedChild : wrappedChildren) {
      WdElement child = new WdElement(wrappedChild, root, this);
      if (!WdConstants.getHiddenTags().contains(child.tagName) &&
          !WdConstants.getIgnoredTags().contains(child.tagName)) {
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
    // Role/tag context: "button", "input", "a", etc.
    String roleDescription = normalizeDescription(tagName);

    // Visible/accessible semantic labels
    String semanticDescription = "";
    
    if (hasText(ariaLabel)) {
      semanticDescription = normalizeDescription(ariaLabel);
    }
    else if (hasText(ariaLabelledBy)) {
      semanticDescription = normalizeDescription(ariaLabelledBy);
    }
    else if (hasText(placeholder)) {
      semanticDescription = normalizeDescription(placeholder);
    }
    else if (hasText(innerText)) {
      semanticDescription = normalizeAndTruncateDescription(innerText, TEXT_DESC_MAX_LENGTH);
    }
    else if (hasText(textContent)) {
      semanticDescription = normalizeAndTruncateDescription(textContent, TEXT_DESC_MAX_LENGTH);
    }
    else if (hasText(title)) {
      semanticDescription = normalizeDescription(title);
    }
    else if (hasText(alt)) {
      semanticDescription = normalizeDescription(alt);
    }
    else if (hasText(value)) {
      semanticDescription = normalizeDescription(value);
    }
    else if (hasText(name)) {
      semanticDescription = normalizeDescription(name);
    }

    // Use technical properties if semantic descriptions are empty
    if (semanticDescription.isEmpty() && hasText(id)) {
      semanticDescription = normalizeDescription(id);
    }
    else if (semanticDescription.isEmpty() && hasText(href)) {
      semanticDescription = normalizeDescription(href);
    }

    // If these combined description still empty, return css classes
    if (roleDescription.isEmpty() && semanticDescription.isEmpty()) {
      return String.join("_", cssClasses);
    }
    // Else return the combined role_semantic description
    return roleDescription + "_" + semanticDescription;
  }

  private boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }

  private String normalizeDescription(String value) {
    String description = value == null ? "" : value.trim().toLowerCase();
    description = description.replaceAll("\\s+", "_");
    description = description.replaceAll("[^a-z0-9_./-]", "");
    description = description.replaceAll("_+", "_");
    return description;
  }

  private String normalizeAndTruncateDescription(String value, int maxLen) {
    String normalized = normalizeDescription(value);
    if (normalized.length() <= maxLen) {
      return normalized;
    }
    return normalized.substring(0, maxLen);
  }

  private String getAttribute(String key) {
    return (attributeMap == null) ? "" : attributeMap.getOrDefault(key, "");
  }

  private Boolean parseBoolean(String value) {
    if (value == null) return null;
    String normalized = value.trim().toLowerCase();
    if (normalized.isEmpty()) return null;
    if (normalized.equals("true")) return true;
    if (normalized.equals("false")) return false;
    return null;
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

	  boolean isVisibleAtCanvas = rect.x() >= 0 && rect.x() + rect.width() <= WdCanvasDimensions.getCanvasWidth() 
			  && rect.y() >= 0 && rect.y() + rect.height() <= WdCanvasDimensions.getInnerHeight();

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
      // If this element (or any ancestor) is hidden or has display:none, 
      // consider is not displayed
      for (WdElement n = this; n != null; n = n.parent) {
          if (n.isHidden()) return false;

          String d = n.display;
          if (d != null && d.trim().equalsIgnoreCase("none")) {
              return false;
          }
      }

      return true;
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
