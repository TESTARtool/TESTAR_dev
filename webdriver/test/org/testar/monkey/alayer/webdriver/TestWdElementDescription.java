package org.testar.monkey.alayer.webdriver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public class TestWdElementDescription {

  private WdRootElement root;

  @Before
  public void setUp() {
    root = new WdRootElement();
  }

  @Test
  public void test_role_with_semantic_description() {
    WdElement element = newElement();
    element.tagName = "A";
    element.innerText = "Buy This!";

    Assert.assertEquals("a_buy_this", element.getElementDescription());
  }

  @Test
  public void test_semantic_priority() {
    WdElement element = newElement();
    element.tagName = "button";
    element.innerText = "Submit";
    element.textContent = "Secondary";

    Assert.assertEquals("button_submit", element.getElementDescription());
  }

  @Test
  public void test_semantic_aria_label() {
    WdElement element = newElement();
    element.tagName = "input";
    element.ariaLabel = "Search Product";

    Assert.assertEquals("input_search_product", element.getElementDescription());
  }

  @Test
  public void test_semantic_aria_labelledby() {
    WdElement element = newElement();
    element.tagName = "input";
    element.ariaLabelledBy = "Search Field";

    Assert.assertEquals("input_search_field", element.getElementDescription());
  }

  @Test
  public void test_semantic_priority_aria_label_over_aria_labelledby() {
    WdElement element = newElement();
    element.tagName = "input";
    element.ariaLabel = "Primary Label";
    element.ariaLabelledBy = "Secondary Label";

    Assert.assertEquals("input_primary_label", element.getElementDescription());
  }

  @Test
  public void test_fallback_to_id() {
    WdElement element = newElement();
    element.tagName = "div";
    element.id = "Main-Section";

    Assert.assertEquals("div_main-section", element.getElementDescription());
  }

  @Test
  public void test_fallback_to_href() {
    WdElement element = newElement();
    element.tagName = "a";
    element.href = "/page.htm";

    Assert.assertEquals("a_/page.htm", element.getElementDescription());
  }

  @Test
  public void test_css_classes_when_no_descriptions() {
    WdElement element = newElement();
    element.cssClasses = Arrays.asList("btn", "primary");

    Assert.assertEquals("btn_primary", element.getElementDescription());
  }

  @Test
  public void test_normalization_on_semantic_description() {
    WdElement element = newElement();
    element.tagName = "button";
    element.innerText = "  Save   50%  NOW!  ";

    Assert.assertEquals("button_save_50_now", element.getElementDescription());
  }

  private WdElement newElement() {
    WdElement element = new WdElement(root, root);
    element.attributeMap = new HashMap<>();
    return element;
  }
}
