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

package org.testar.monkey.alayer.webdriver.enums;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;

/**
 * Creates and holds tags specific to WEB info.
 */
public class WdTags extends TagsBase {

  protected static final Set<Tag<?>> wdTags = new HashSet<>();
	
  private WdTags() {
  }

  /**
   * Type of a widget in localized form (language)
   */
  public static final Tag<String> WebLocalizedControlType = from("WebLocalizedControlType", String.class);

  /**
   * Web Type name of a widget.
   */
  public static final Tag<String> WebItemType = from("WebItemType", String.class);
  public static final Tag<String> WebItemStatus = from("WebItemStatus", String.class);
  public static final Tag<String> Desc = from("Desc", String.class);

  public static final Tag<String> WebTagName = from("WebTagName", String.class);

  /**
   * Web identifier of a widget.
   */
  public static final Tag<String> WebId = from("WebId", String.class);
  
  /**
   * Styled class of a widget.
   */
  public static final Tag<String> WebCssClasses = from("WebCssClasses", String.class);
  
  /**
   * Help text of a widget.
   */
  public static final Tag<String> WebHelpText = from("WebHelpText", String.class);

  /**
   * Web name of a widget.
   */
  public static final Tag<String> WebName = from("WebName", String.class);
  
  /**
   * Web generic representative title of a widget.
   */
  public static final Tag<String> WebGenericTitle = from("WebGenericTitle", String.class);
  
  /**
   * Web title of a widget.
   */
  public static final Tag<String> WebTitle = from("WebTitle", String.class);
  
  /**
   * Web text context of a widget.
   */
  public static final Tag<String> WebTextContent = from("WebTextContent", String.class);

  /**
   * Web inner text of a widget.
   */
  public static final Tag<String> WebInnerText = from("WebInnerText", String.class);

  /**
   * Web Hypertext Reference of a widget.
   */
  public static final Tag<String> WebHref = from("WebHref", String.class);
  
  /**
   * Web value of a widget.
   */
  public static final Tag<String> WebValue = from("WebValue", String.class);
  
  /**
   * Web style of a widget.
   */
  public static final Tag<String> WebStyle = from("WebStyle", String.class);

  /**
   * Web style opacity property of a widget.
   */
  public static final Tag<Double> WebStyleOpacity = from("WebStyleOpacity", Double.class);

  /**
   * Web style position property of a widget.
   */
  public static final Tag<String> WebStylePosition = from("WebStylePosition", String.class);

  /**
   * Web style overflow property of a widget.
   */
  public static final Tag<String> WebStyleOverflow = from("WebStyleOverflow", String.class);

  /**
   * Web style overflow-x property of a widget.
   */
  public static final Tag<String> WebStyleOverflowX = from("WebStyleOverflowX", String.class);

  /**
   * Web style overflow-y property of a widget.
   */
  public static final Tag<String> WebStyleOverflowY = from("WebStyleOverflowY", String.class);

  /**
   * Web target of a widget.
   */
  public static final Tag<String> WebTarget = from("WebTarget", String.class);
  
  /**
   * Web alternative information of a widget.
   */
  public static final Tag<String> WebAlt = from("WebAlt", String.class);
  
  /**
   * Web display style of a widget.
   */
  public static final Tag<String> WebDisplay = from("WebDisplay", String.class);

  /**
   * Web element visibility is hidden or collapse.
   */
  public static final Tag<Boolean> WebIsHidden = from("WebIsHidden", Boolean.class);

  /**
   * Web element XPath.
   */
  public static final Tag<String> WebXPath = from("WebXPath", String.class);

  /**
   * Web element is displayed in the screen or not.
   */
  public static final Tag<Boolean> WebIsDisplayed = from("WebIsDisplayed", Boolean.class);

  /**
   * Web computed font size of a widget.
   */
  public static final Tag<String> WebComputedFontSize = from("WebComputedFontSize", String.class);

  /**
   * Web computed color of a widget.
   */
  public static final Tag<Color> WebComputedColor = from("WebComputedColor", Color.class);

  /**
   * Web computed backgroundColor of a widget.
   */
  public static final Tag<Color> WebComputedBackgroundColor = from("WebComputedBackgroundColor", Color.class);

  /**
   * Web type of input of a widget.
   */
  public static final Tag<String> WebType = from("WebType", String.class);
  
  /**
   * Web src of a widget.
   */
  public static final Tag<String> WebSrc = from("WebSrc", String.class);
  
  /**
   * Web zIndex of a widget
   */
  public static final Tag<Double> WebZIndex = from("WebZIndex", Double.class);

  /**
   * Web naturalWidth of a widget
   */
  public static final Tag<Long> WebNaturalWidth = from("WebNaturalWidth", Long.class);

  /**
   * Web naturalHeight of a widget
   */
  public static final Tag<Long> WebNaturalHeight = from("WebNaturalHeight", Long.class);

  /**
   * Web Width of a widget
   */
  public static final Tag<Long> WebDisplayedWidth = from("WebDisplayedWidth", Long.class);

  /**
   * Web Height of a widget
   */
  public static final Tag<Long> WebDisplayedHeight = from("WebDisplayedHeight", Long.class);

  /**
   * Web Control Type Identifier of a widget.
   */
  public static final Tag<Long> WebControlType = from("WebControlType", Long.class);

  /**
   * Web Culture Identifier of a widget.
   */
  public static final Tag<Long> WebCulture = from("WebCulture", Long.class);

  /**
   * If the widget is a Win32 widget, this will be its native handle.
   */
  public static final Tag<Long> WebNativeWindowHandle = from("WebNativeWindowHandle", Long.class);
  public static final Tag<Long> WebOrientation = from("WebOrientation", Long.class);
  public static final Tag<Boolean> WebScrollPattern = from("WebScrollPattern", Boolean.class);
  public static final Tag<Boolean> WebHorizontallyScrollable = from("WebHorizontallyScrollable", Boolean.class);
  public static final Tag<Boolean> WebVerticallyScrollable = from("WebVerticallyScrollable", Boolean.class);
  public static final Tag<Double> WebScrollHorizontalViewSize = from("WebScrollHorizontalViewSize", Double.class);
  public static final Tag<Double> WebScrollVerticalViewSize = from("WebScrollVerticalViewSize", Double.class);
  public static final Tag<Double> WebScrollHorizontalPercent = from("WebScrollHorizontalPercent", Double.class);
  public static final Tag<Double> WebScrollVerticalPercent = from("WebScrollVerticalPercent", Double.class);

  /**
   * Id of the process that this widget belongs to
   */
  public static final Tag<Long> WebProcessId = from("WebProcessId", Long.class);

  /**
   * Id of the framework that this widget belongs to (e.g. Win32, Swing, Flash, ...)
   */
  public static final Tag<String> WebFrameworkId = from("WebFrameworkId", String.class);
  public static final Tag<String> WebAutomationId = from("WebAutomationId", String.class);
  public static final Tag<long[]> WebRuntimeId = from("WebRuntimeId", long[].class);

  /**
   * Whether a widget is a content element.
   */
  public static final Tag<Boolean> WebIsContentElement = from("WebIsContentElement", Boolean.class);

  /**
   * Whether a widget is a control element.
   */
  public static final Tag<Boolean> WebIsControlElement = from("WebIsControlElement", Boolean.class);

  /**
   * If the widget is a window, this tells whether it is the top-most window.
   */
  public static final Tag<Boolean> WebIsTopmostWindow = from("WebIsTopmostWindow", Boolean.class);

  /**
   * If the widget is a window, this tells it is modal (such as a message box).
   */
  public static final Tag<Boolean> WebIsWindowModal = from("WebIsWindowModal", Boolean.class);
  public static final Tag<Long> WebWindowInteractionState = from("WebWindowInteractionState", Long.class);
  public static final Tag<Long> WebWindowVisualState = from("WebWindowVisualState", Long.class);

  /**
   * Bounding rectangle of a widget.
   */
  public static final Tag<Rect> WebBoundingRectangle = from("WebBoundingRectangle", Rect.class);

  /**
   * Whether this widget is currently enabled or disabled (e.g. "greyed out")
   */
  public static final Tag<Boolean> WebIsEnabled = from("WebIsEnabled", Boolean.class);

  /**
   * Whether this widget is disabled (web property "disabled")
   */
  public static final Tag<Boolean> WebIsDisabled = from("WebIsDisabled", Boolean.class);

  /**
   * Whether this widget is currently blocked.
   */
  public static final Tag<Boolean> WebIsBlocked = from("WebIsBlocked", Boolean.class);
  
  /**
   * Whether this widget is currently clickable.
   */
  public static final Tag<Boolean> WebIsClickable = from("WebIsClickable", Boolean.class);
  
  /**
   * Whether this widget comes from Shadow Tree.
   */
  public static final Tag<Boolean> WebIsShadow = from("WebIsShadow", Boolean.class);
  
  /**
   * Whether this widget has keyboard focus (i.e. will receive keyboard input)
   */
  public static final Tag<Boolean> WebHasKeyboardFocus = from("WebHasKeyboardFocus", Boolean.class);

  /**
   * Whether this widget is currently visible on the screen.
   */
  public static final Tag<Boolean> WebIsFullOnScreen = from("WebIsFullOnScreen", Boolean.class);
  public static final Tag<Boolean> WebIsOffScreen = from("WebIsOffScreen", Boolean.class);

  /**
   * Whether this widget can be focused, such that it will receive keyboard input.
   */
  public static final Tag<Boolean> WebIsKeyboardFocusable = from("WebIsKeyboardFocusable", Boolean.class);

  /**
   * Accelerator key of a widget.
   */
  public static final Tag<String> WebAcceleratorKey = from("WebAcceleratorKey", String.class);

  /**
   * Access key of a widget.
   */
  public static final Tag<String> WebAccessKey = from("WebAccessKey", String.class);

  /**
   * Max length of a text widget.
   */
  public static final Tag<Integer> WebMaxLength = from("WebMaxLength", Integer.class);

  /**
   * The length of a web widget, such as a select or form.
   */
  public static final Tag<Integer> WebLength = from("WebLength", Integer.class);

  /**
   * Represents the inner HTML content of a web widget-element as a string.
   */
  public static final Tag<String> WebInnerHTML = from("WebInnerHTML", String.class);

  /**
   * Represents the entire HTML markup of a web widget-element, including its opening and closing tags, as a string.
   */
  public static final Tag<String> WebOuterHTML = from("WebOuterHTML", String.class);

  /**
   * Represents the placeholder text in an <input> or <textarea> element.
   */
  public static final Tag<String> WebPlaceholder = from("WebPlaceholder", String.class);

  /**
   * Represents if an option is selected in a <select> element.
   */
  public static final Tag<Boolean> WebIsSelected = from("WebIsSelected", Boolean.class);

  /**
   * Represents if an <input> element is checked.
   */
  public static final Tag<Boolean> WebIsChecked = from("WebIsChecked", Boolean.class);

  /**
   * Represents if an element is disabled.
   */
  public static final Tag<Boolean> WebIsDisabled = from("WebIsDisabled", Boolean.class);

  /**
   * Represents if an element contains the multiple attribute.
   */
  public static final Tag<Boolean> WebIsMultiple = from("WebIsMultiple", Boolean.class);

  /**
   * Represents the accessible name for an element, used by assistive technologies 
   * such as screen readers. Maps to the HTML 'aria-label' attribute.
   * This value should provide a concise, human-readable label for the element 
   * when a visible label is not present.
   */
  public static final Tag<String> WebAriaLabel = from("WebAriaLabel", String.class);

  /**
   * Identifies the element(s) that label the current element, used by assistive 
   * technologies to provide contextual naming. Maps to the HTML 'aria-labelledby' attribute.
   * This should reference the ID(s) of one or more elements that serve as the accessible name.
   */
  public static final Tag<String> WebAriaLabelledBy = from("WebAriaLabelledBy", String.class);

  /**
   * Represents the remote web element object obtained with Selenium framework
   */
  public static final Tag<WebElement> WebElementSelenium = from("WebElementSelenium", WebElement.class);
  
  /**
   * The attribute map we obtain through the JavaScript plugin.
   */
  @SuppressWarnings("unchecked")
  public static final Tag<Map<String, String>> WebAttributeMap = from("WebAttributeMap", (Class<Map<String, String>>) (Class<?>) Map.class);

  public static Set<Tag<?>> getAllTags() {
	  return tagSet;
  }

  protected static <T> Tag<T> from(String name, Class<T> valueType) {
    Tag<T> tag = TagsBase.from(name, valueType);
    wdTags.add(tag);
    return tag;
  }

  public static Set<Tag<?>> getWdTags() {
    return Collections.unmodifiableSet(wdTags);
  }
}
