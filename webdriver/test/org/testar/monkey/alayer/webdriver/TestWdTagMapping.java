package org.testar.monkey.alayer.webdriver;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

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
