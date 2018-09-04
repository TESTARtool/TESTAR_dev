package org.fruit.alayer.webdriver;

import java.util.Arrays;
import java.util.List;

public class Constants {
  // List of HTML tags that getStateTreeTestar should ignore
  // no widgets can be found here
  public static List<String> ignoredTags = Arrays.asList(
      "script", "noscript", "head", "meta", "style", "link", "svg", "canvas");
  // Disable the state-canvas
  public static List<String> hiddenTags = Arrays.asList("canvas");

  // element.offsetWidth - element.clientWidth
  public static double scrollArrowSize = 36;
  public static double scrollThick = 15;
}
