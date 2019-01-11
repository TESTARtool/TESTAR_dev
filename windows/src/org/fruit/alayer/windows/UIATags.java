/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
*******************************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */

package org.fruit.alayer.windows;

import org.fruit.alayer.Rect;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;

public final class UIATags extends TagsBase {

  private UIATags() {}

  /**
   * Type of a widget in localized form (language)
   */
  public static final Tag<String> UIALocalizedControlType = from("UIALocalizedControlType", String.class);

  /**
   * UIA Type name of a widget.
   */
  public static final Tag<String> UIAItemType = from("UIAItemType", String.class);
  public static final Tag<String> UIAItemStatus = from("UIAItemStatus", String.class);
  public static final Tag<String> UIAProviderDescription = from("UIAProviderDescription", String.class);

  /**
   * Win32 API ClassName of a widget
   */
  public static final Tag<String> UIAClassName = from("UIAClassName", String.class);

  /**
   * Help text of a widget
   */
  public static final Tag<String> UIAHelpText = from("UIAHelpText", String.class);

  /**
   * Title or name of a widget.
   */
  public static final Tag<String> UIAName = from("UIAName", String.class);

  /**
   * UIA Control Type Identifier of a widget
   */
  public static final Tag<Long> UIAControlType = from("UIAControlType", Long.class);

  /**
   * UIA Culture Identifier of a widget
   */
  public static final Tag<Long> UIACulture = from("UIACulture", Long.class);

  /**
   * If the widget is a Win32 widget, this will be its native handle
   */
  public static final Tag<Long> UIANativeWindowHandle = from("UIANativeWindowHandle", Long.class);
  public static final Tag<Long> UIAOrientation = from("UIAOrientation", Long.class);

  // by urueda
  public static final Tag<Boolean> UIAScrollPattern = from("UIAScrollPattern", Boolean.class);
  //public static final Tag<int[]> UIAScrollbarInfo = from("UIAScrollbarInfo", int[].class);
  //public static final Tag<int[]> UIAScrollbarInfoH = from("UIAScrollbarInfoH", int[].class);
  //public static final Tag<int[]> UIAScrollbarInfoV = from("UIAScrollbarInfoV", int[].class);
  public static final Tag<Boolean> UIAHorizontallyScrollable = from("UIAHorizontallyScrollable", Boolean.class);
  public static final Tag<Boolean> UIAVerticallyScrollable = from("UIAVerticallyScrollable", Boolean.class);
  public static final Tag<Double> UIAScrollHorizontalViewSize = from("UIAScrollHorizontalViewSize", Double.class);
  public static final Tag<Double> UIAScrollVerticalViewSize = from("UIAScrollVerticalViewSize", Double.class);
  public static final Tag<Double> UIAScrollHorizontalPercent = from("UIAScrollHorizontalPercent", Double.class);
  public static final Tag<Double> UIAScrollVerticalPercent = from("UIAScrollVerticalPercent", Double.class);

  /**
   * Id of the process that this widget belongs to
   */
  public static final Tag<Long> UIAProcessId = from("UIAProcessId", Long.class);

  /**
   * Id of the framework that this widget belongs to (e.g. Win32, Swing, Flash, ...)
   */
  public static final Tag<String> UIAFrameworkId = from("UIAFrameworkId", String.class);
  public static final Tag<String> UIAAutomationId = from("UIAAutomationId", String.class);
  public static final Tag<long[]> UIARuntimeId = from("UIARuntimeId", long[].class);

  /**
   * Whether a widget is a content element.
   */
  public static final Tag<Boolean> UIAIsContentElement = from("UIAIsContentElement", Boolean.class);

  /**
   * Whether a widget is a control element.
   */
  public static final Tag<Boolean> UIAIsControlElement = from("UIAIsControlElement", Boolean.class);

  /**
   * If the widget is a window, this tells whether it is the top-most window
   */
  public static final Tag<Boolean> UIAIsTopmostWindow = from("UIAIsTopmostWindow", Boolean.class);

  /**
   * If the widget is a window, this tells it is modal (such as a message box)
   */
  public static final Tag<Boolean> UIAIsWindowModal = from("UIAIsWindowModal", Boolean.class);
  public static final Tag<Long> UIAWindowInteractionState = from("UIAWindowInteractionState", Long.class);
  public static final Tag<Long> UIAWindowVisualState = from("UIAWindowVisualState", Long.class);

  /**
   * Bounding rectangle of a widget
   */
  public static final Tag<Rect> UIABoundingRectangle = from("UIABoundingRectangle", Rect.class);

  /**
   * Whether this widget is currently enabled or disabled (e.g. "greyed out")
   */
  public static final Tag<Boolean> UIAIsEnabled = from("UIAIsEnabled", Boolean.class);

  /**
   * Whether this widget has keyboard focus (i.e. will receive keyboard input)
   */
  public static final Tag<Boolean> UIAHasKeyboardFocus = from("UIAHasKeyboardFocus", Boolean.class);

  /**
   * Whether this widget is currently visible on the screen.
   */
  public static final Tag<Boolean> UIAIsOffscreen = from("UIAIsOffscreen", Boolean.class);

  /**
   * Whether this widget can be focused, such that it will receive keyboard input
   */
  public static final Tag<Boolean> UIAIsKeyboardFocusable = from("UIAIsKeyboardFocusable", Boolean.class);

  /**
   * Accelerator key of a widget.
   */
  public static final Tag<String> UIAAcceleratorKey = from("UIAAcceleratorKey", String.class);

  /**
   * Access key of a widget.
   */
  public static final Tag<String> UIAAccessKey = from("UIAAccessKey", String.class);

}
