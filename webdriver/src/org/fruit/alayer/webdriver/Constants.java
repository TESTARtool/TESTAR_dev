package org.fruit.alayer.webdriver;

import java.util.Arrays;
import java.util.List;

public class Constants {
  public static List<String> ignoredTags = Arrays.asList(
      "script", "noscript", "head", "meta", "style", "link", "svg", "canvas");
  public static List<String> hiddenTags = Arrays.asList("canvas");

  // TODO System and CSS dependent
  // element.offsetWidth - element.clientWidth
  public static double scrollArrowSize = 36;
  public static double scrollThick = 16;
}
