package org.fruit.alayer.webdriver.enums;

import org.fruit.alayer.Rect;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;


// TODO Check which are needed, update comments

/**
 * Creates and holds tags specific to WEB info.
 */
public class WdTags extends TagsBase {

  private WdTags() {}

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
   * Help text of a widget
   */
  public static final Tag<String> WebHelpText = from("WebHelpText", String.class);

  /**
   * Title or name of a widget.
   */
  public static final Tag<String> WebName = from("WebName", String.class);

  /**
   * Web Control Type Identifier of a widget
   */
  public static final Tag<Long> WebControlType = from("WebControlType", Long.class);

  /**
   * Web Culture Identifier of a widget
   */
  public static final Tag<Long> WebCulture = from("WebCulture", Long.class);

  /**
   * If the widget is a Win32 widget, this will be its native handle
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
   * If the widget is a window, this tells whether it is the top-most window
   */
  public static final Tag<Boolean> WebIsTopmostWindow = from("WebIsTopmostWindow", Boolean.class);

  /**
   * If the widget is a window, this tells it is modal (such as a message box)
   */
  public static final Tag<Boolean> WebIsWindowModal = from("WebIsWindowModal", Boolean.class);
  public static final Tag<Long> WebWindowInteractionState = from("WebWindowInteractionState", Long.class);
  public static final Tag<Long> WebWindowVisualState = from("WebWindowVisualState", Long.class);

  /**
   * Bounding rectangle of a widget
   */
  public static final Tag<Rect> WebBoundingRectangle = from("WebBoundingRectangle", Rect.class);

  /**
   * Whether this widget is currently enabled or disabled (e.g. "greyed out")
   */
  public static final Tag<Boolean> WebIsEnabled = from("WebIsEnabled", Boolean.class);

  /**
   * Whether this widget has keyboard focus (i.e. will receive keyboard input)
   */
  public static final Tag<Boolean> WebHasKeyboardFocus = from("WebHasKeyboardFocus", Boolean.class);

  /**
   * Whether this widget is currently visible on the screen.
   */
  public static final Tag<Boolean> WebIsOffscreen = from("WebIsOffscreen", Boolean.class);

  /**
   * Whether this widget can be focused, such that it will receive keyboard input
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

}
