package org.testar.webdriver.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.core.alayer.Rect;
import org.testar.webdriver.state.WdElement;
import org.testar.webdriver.state.WdRootElement;
import org.testar.webdriver.state.WdState;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;

import java.util.Arrays;
import java.util.HashMap;

public class TestWdTagMapping {

  private WdRootElement rootWdElement;
  private WdState rootWdState;
  private WdElement childWdElement;
  private WdWidget childWdWidget;

  @Before
  public void prepareWdElements() {
    rootWdElement = new WdRootElement();
    rootWdState = new WdState(rootWdElement);

    childWdElement = new WdElement(rootWdElement, rootWdElement);
    childWdWidget = new WdWidget(rootWdState, rootWdState, childWdElement);
  }

  @Test
  public void test_WebAriaLabel_mapping() {
    childWdElement.ariaLabel = "Search";
    assertEquals("Search", childWdWidget.get(WdTags.WebAriaLabel, ""));
  }

  @Test
  public void test_WebId_mapping() {
    childWdElement.id = "id-123";
    assertEquals("id-123", childWdWidget.get(WdTags.WebId, ""));
  }

  @Test
  public void test_WebHref_mapping() {
    childWdElement.href = "https://testar.org";
    assertEquals("https://testar.org", childWdWidget.get(WdTags.WebHref, ""));
  }

  @Test
  public void test_WebPlaceholder_mapping() {
    childWdElement.placeholder = "Type here";
    assertEquals("Type here", childWdWidget.get(WdTags.WebPlaceholder, ""));
  }

  @Test
  public void test_WebIsDisabled_mapping() {
    childWdElement.disabled = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsDisabled, null));
  }

  @Test
  public void test_WebName_mapping() {
    childWdElement.name = "search-input";
    assertEquals("search-input", childWdWidget.get(WdTags.WebName, ""));
  }

  @Test
  public void test_WebTagName_mapping() {
    childWdElement.tagName = "input";
    assertEquals("input", childWdWidget.get(WdTags.WebTagName, ""));
  }

  @Test
  public void test_WebTextContent_mapping() {
    childWdElement.textContent = "Search text";
    assertEquals("Search text", childWdWidget.get(WdTags.WebTextContent, ""));
  }

  @Test
  public void test_WebInnerText_mapping() {
    childWdElement.innerText = "Visible text";
    assertEquals("Visible text", childWdWidget.get(WdTags.WebInnerText, ""));
  }

  @Test
  public void test_WebTitle_mapping() {
    childWdElement.title = "Search";
    assertEquals("Search", childWdWidget.get(WdTags.WebTitle, ""));
  }

  @Test
  public void test_WebValue_mapping() {
    childWdElement.value = "hello";
    assertEquals("hello", childWdWidget.get(WdTags.WebValue, ""));
  }

  @Test
  public void test_WebType_mapping() {
    childWdElement.type = "text";
    assertEquals("text", childWdWidget.get(WdTags.WebType, ""));
  }

  @Test
  public void test_WebSrc_mapping() {
    childWdElement.src = "https://testar.org/image.png";
    assertEquals("https://testar.org/image.png", childWdWidget.get(WdTags.WebSrc, ""));
  }

  @Test
  public void test_WebXPath_mapping() {
    childWdElement.xpath = "/html/body/div[1]";
    assertEquals("/html/body/div[1]", childWdWidget.get(WdTags.WebXPath, ""));
  }

  @Test
  public void test_WebCssClasses_mapping() {
    childWdElement.cssClasses = Arrays.asList("btn", "primary");
    assertEquals("[btn, primary]", childWdWidget.get(WdTags.WebCssClasses, ""));
  }

  @Test
  public void test_WebIsEnabled_mapping() {
    childWdElement.enabled = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsEnabled, null));
  }

  @Test
  public void test_WebIsBlocked_mapping() {
    childWdElement.blocked = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsBlocked, null));
  }

  @Test
  public void test_WebIsClickable_mapping() {
    childWdElement.isClickable = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsClickable, null));
  }

  @Test
  public void test_WebIsShadow_mapping() {
    childWdElement.isShadow = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsShadow, null));
  }

  @Test
  public void test_WebHasKeyboardFocus_mapping() {
    childWdElement.hasKeyboardFocus = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebHasKeyboardFocus, null));
  }

  @Test
  public void test_WebIsKeyboardFocusable_mapping() {
    childWdElement.isKeyboardFocusable = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsKeyboardFocusable, null));
  }

  @Test
  public void test_WebIsContentElement_mapping() {
    childWdElement.isContentElement = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsContentElement, null));
  }

  @Test
  public void test_WebIsControlElement_mapping() {
    childWdElement.isControlElement = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsControlElement, null));
  }

  @Test
  public void test_WebStyle_mapping() {
    childWdElement.style = "color:red;";
    assertEquals("color:red;", childWdWidget.get(WdTags.WebStyle, ""));
  }

  @Test
  public void test_WebStyleOpacity_mapping() {
    childWdElement.styleOpacity = 0.7;
    assertEquals(Double.valueOf(0.7), childWdWidget.get(WdTags.WebStyleOpacity, null));
  }

  @Test
  public void test_WebStyleOverflow_mapping() {
    childWdElement.styleOverflow = "auto";
    assertEquals("auto", childWdWidget.get(WdTags.WebStyleOverflow, ""));
  }

  @Test
  public void test_WebStyleOverflowX_mapping() {
    childWdElement.styleOverflowX = "scroll";
    assertEquals("scroll", childWdWidget.get(WdTags.WebStyleOverflowX, ""));
  }

  @Test
  public void test_WebStyleOverflowY_mapping() {
    childWdElement.styleOverflowY = "hidden";
    assertEquals("hidden", childWdWidget.get(WdTags.WebStyleOverflowY, ""));
  }

  @Test
  public void test_WebStylePosition_mapping() {
    childWdElement.stylePosition = "relative";
    assertEquals("relative", childWdWidget.get(WdTags.WebStylePosition, ""));
  }

  @Test
  public void test_WebZIndex_mapping() {
    childWdElement.zindex = 10.0;
    assertEquals(Double.valueOf(10.0), childWdWidget.get(WdTags.WebZIndex, null));
  }

  @Test
  public void test_WebBoundingRectangle_mapping() {
    Rect rect = Rect.from(1, 2, 30, 40);
    childWdElement.rect = rect;
    assertEquals(rect, childWdWidget.get(WdTags.WebBoundingRectangle, null));
  }

  @Test
  public void test_WebNaturalWidth_mapping() {
    childWdElement.naturalWidth = 300;
    assertEquals(Long.valueOf(300), childWdWidget.get(WdTags.WebNaturalWidth, null));
  }

  @Test
  public void test_WebNaturalHeight_mapping() {
    childWdElement.naturalHeight = 200;
    assertEquals(Long.valueOf(200), childWdWidget.get(WdTags.WebNaturalHeight, null));
  }

  @Test
  public void test_WebDisplayedWidth_mapping() {
    childWdElement.displayedWidth = 120;
    assertEquals(Long.valueOf(120), childWdWidget.get(WdTags.WebDisplayedWidth, null));
  }

  @Test
  public void test_WebDisplayedHeight_mapping() {
    childWdElement.displayedHeight = 80;
    assertEquals(Long.valueOf(80), childWdWidget.get(WdTags.WebDisplayedHeight, null));
  }

  @Test
  public void test_WebMaxLength_mapping() {
    childWdElement.maxLength = 255;
    assertEquals(Integer.valueOf(255), childWdWidget.get(WdTags.WebMaxLength, null));
  }

  @Test
  public void test_WebInnerHTML_mapping() {
    childWdElement.innerHTML = "<span>hello</span>";
    assertEquals("<span>hello</span>", childWdWidget.get(WdTags.WebInnerHTML, ""));
  }

  @Test
  public void test_WebOuterHTML_mapping() {
    childWdElement.outerHTML = "<div><span>hello</span></div>";
    assertEquals("<div><span>hello</span></div>", childWdWidget.get(WdTags.WebOuterHTML, ""));
  }

  @Test
  public void test_WebComputedFontSize_mapping() {
    childWdElement.computedFontSize = "16px";
    assertEquals("16px", childWdWidget.get(WdTags.WebComputedFontSize, ""));
  }

  @Test
  public void test_WebScrollPattern_mapping() {
    childWdElement.scrollPattern = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebScrollPattern, null));
  }

  @Test
  public void test_WebHorizontallyScrollable_mapping() {
    childWdElement.hScroll = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebHorizontallyScrollable, null));
  }

  @Test
  public void test_WebVerticallyScrollable_mapping() {
    childWdElement.vScroll = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebVerticallyScrollable, null));
  }

  @Test
  public void test_WebScrollHorizontalViewSize_mapping() {
    childWdElement.hScrollViewSize = 33.3;
    assertEquals(Double.valueOf(33.3), childWdWidget.get(WdTags.WebScrollHorizontalViewSize, null));
  }

  @Test
  public void test_WebScrollVerticalViewSize_mapping() {
    childWdElement.vScrollViewSize = 44.4;
    assertEquals(Double.valueOf(44.4), childWdWidget.get(WdTags.WebScrollVerticalViewSize, null));
  }

  @Test
  public void test_WebScrollHorizontalPercent_mapping() {
    childWdElement.hScrollPercent = 55.5;
    assertEquals(Double.valueOf(55.5), childWdWidget.get(WdTags.WebScrollHorizontalPercent, null));
  }

  @Test
  public void test_WebScrollVerticalPercent_mapping() {
    childWdElement.vScrollPercent = 66.6;
    assertEquals(Double.valueOf(66.6), childWdWidget.get(WdTags.WebScrollVerticalPercent, null));
  }

  @Test
  public void test_WebIsOffScreen_mapping() {
    childWdElement.isFullVisibleOnScreen = false;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsOffScreen, null));
  }

  @Test
  public void test_WebIsHidden_mapping() {
    childWdElement.visibility = "hidden";
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsHidden, null));
  }

  @Test
  public void test_WebAriaLabelledBy_mapping() {
    childWdElement.ariaLabelledBy = "search-label";
    assertEquals("search-label", childWdWidget.get(WdTags.WebAriaLabelledBy, ""));
  }

  @Test
  public void test_WebAriaDescribedBy_mapping() {
    childWdElement.ariaDescribedBy = "search-help";
    assertEquals("search-help", childWdWidget.get(WdTags.WebAriaDescribedBy, ""));
  }

  @Test
  public void test_WebAriaRole_mapping() {
    childWdElement.ariaRole = "button";
    assertEquals("button", childWdWidget.get(WdTags.WebAriaRole, ""));
  }

  @Test
  public void test_WebAriaChecked_mapping() {
    childWdElement.ariaChecked = "mixed";
    assertEquals("mixed", childWdWidget.get(WdTags.WebAriaChecked, ""));
  }

  @Test
  public void test_WebAriaInvalid_mapping() {
    childWdElement.ariaInvalid = "spelling";
    assertEquals("spelling", childWdWidget.get(WdTags.WebAriaInvalid, ""));
  }

  @Test
  public void test_WebAriaCurrent_mapping() {
    childWdElement.ariaCurrent = "page";
    assertEquals("page", childWdWidget.get(WdTags.WebAriaCurrent, ""));
  }

  @Test
  public void test_WebAriaHasPopup_mapping() {
    childWdElement.ariaHasPopup = "menu";
    assertEquals("menu", childWdWidget.get(WdTags.WebAriaHasPopup, ""));
  }

  @Test
  public void test_WebAriaControls_mapping() {
    childWdElement.ariaControls = "panel-1";
    assertEquals("panel-1", childWdWidget.get(WdTags.WebAriaControls, ""));
  }

  @Test
  public void test_WebAriaLive_mapping() {
    childWdElement.ariaLive = "polite";
    assertEquals("polite", childWdWidget.get(WdTags.WebAriaLive, ""));
  }

  @Test
  public void test_WebAriaValueNow_mapping() {
    childWdElement.ariaValueNow = "50";
    assertEquals("50", childWdWidget.get(WdTags.WebAriaValueNow, ""));
  }

  @Test
  public void test_WebAriaValueMin_mapping() {
    childWdElement.ariaValueMin = "0";
    assertEquals("0", childWdWidget.get(WdTags.WebAriaValueMin, ""));
  }

  @Test
  public void test_WebAriaValueMax_mapping() {
    childWdElement.ariaValueMax = "100";
    assertEquals("100", childWdWidget.get(WdTags.WebAriaValueMax, ""));
  }

  @Test
  public void test_WebAriaValueText_mapping() {
    childWdElement.ariaValueText = "half";
    assertEquals("half", childWdWidget.get(WdTags.WebAriaValueText, ""));
  }

  @Test
  public void test_WebAriaDisabled_mapping() {
    childWdElement.ariaDisabled = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaDisabled, null));
  }

  @Test
  public void test_WebAriaHidden_mapping() {
    childWdElement.ariaHidden = false;
    assertEquals(Boolean.FALSE, childWdWidget.get(WdTags.WebAriaHidden, null));
  }

  @Test
  public void test_WebAriaExpanded_mapping() {
    childWdElement.ariaExpanded = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaExpanded, null));
  }

  @Test
  public void test_WebAriaPressed_mapping() {
    childWdElement.ariaPressed = false;
    assertEquals(Boolean.FALSE, childWdWidget.get(WdTags.WebAriaPressed, null));
  }

  @Test
  public void test_WebAriaSelected_mapping() {
    childWdElement.ariaSelected = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaSelected, null));
  }

  @Test
  public void test_WebAriaRequired_mapping() {
    childWdElement.ariaRequired = false;
    assertEquals(Boolean.FALSE, childWdWidget.get(WdTags.WebAriaRequired, null));
  }

  @Test
  public void test_WebAriaReadOnly_mapping() {
    childWdElement.ariaReadOnly = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaReadOnly, null));
  }

  @Test
  public void test_WebAriaBusy_mapping() {
    childWdElement.ariaBusy = false;
    assertEquals(Boolean.FALSE, childWdWidget.get(WdTags.WebAriaBusy, null));
  }

  @Test
  public void test_WebAriaModal_mapping() {
    childWdElement.ariaModal = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaModal, null));
  }

  @Test
  public void test_WebTarget_mapping() {
    childWdElement.target = "_blank";
    assertEquals("_blank", childWdWidget.get(WdTags.WebTarget, ""));
  }

  @Test
  public void test_WebAlt_mapping() {
    childWdElement.alt = "banner-image";
    assertEquals("banner-image", childWdWidget.get(WdTags.WebAlt, ""));
  }

  @Test
  public void test_WebAccessKey_mapping() {
    childWdElement.accessKey = "k";
    assertEquals("k", childWdWidget.get(WdTags.WebAccessKey, ""));
  }

  @Test
  public void test_WebAcceleratorKey_mapping() {
    childWdElement.acceleratorKey = "Ctrl+K";
    assertEquals("Ctrl+K", childWdWidget.get(WdTags.WebAcceleratorKey, ""));
  }

  @Test
  public void test_WebElementSelenium_mapping() {
    RemoteWebElement remoteWebElement = new RemoteWebElement();
    childWdElement.remoteWebElement = remoteWebElement;
    assertSame(remoteWebElement, childWdWidget.get(WdTags.WebElementSelenium, null));
  }

  @Test
  public void test_WebAttributeMap_mapping() {
    childWdElement.attributeMap = new HashMap<>();
    childWdElement.attributeMap.put("aria-label", "Search");
    assertEquals(childWdElement.attributeMap, childWdWidget.get(WdTags.WebAttributeMap, null));
  }

  @Test
  public void test_WebIsFullOnScreen_mapping() {
    childWdElement.isFullVisibleOnScreen = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsFullOnScreen, null));
  }

  @Test
  public void test_WebGenericTitle_mapping() {
    childWdElement.genericTitle = "generic-title";
    assertEquals("generic-title", childWdWidget.get(WdTags.WebGenericTitle, ""));
  }

  @Test
  public void test_WebIsWindowModal_mapping() {
    childWdElement.isModal = true;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebIsWindowModal, null));
  }

  @Test
  public void test_NullDefaultValue_string() {
    childWdElement.ariaLabel = null;
    assertEquals("default-label", childWdWidget.get(WdTags.WebAriaLabel, "default-label"));
  }

  @Test
  public void test_NullDefaultValue_boolean() {
    childWdElement.ariaDisabled = null;
    assertEquals(Boolean.TRUE, childWdWidget.get(WdTags.WebAriaDisabled, true));
    assertEquals(Boolean.FALSE, childWdWidget.get(WdTags.WebAriaDisabled, false));
  }
}
