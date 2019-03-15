/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

package nl.ou.testar.a11y.wcag2;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;

import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class KeyboardAccessibleGuideline extends AbstractGuideline {

  private static final long serialVersionUID = 4099763917658781054L;

  private static final int MAX_CACHED_SHORTCUT_KEYS = 50;
  private static final int KEYBOARD_TRAP_WIDGET_THRESHOLD = 5;
  private static final int SHORTCUT_KEYS_TO_WIDGETS_RATIO = 3;

  private final Deque<Action> shortcutKeysCache = new LinkedList<>();
  private String lastConcreteWidgetID = "";
  private int sameWidgetCount = 0;

  KeyboardAccessibleGuideline(AbstractPrinciple parent) {
    super(1, "Keyboard Accessible", parent);
    List<SuccessCriterion> criteria = getCriteria();
    criteria.add(new SuccessCriterion(1, "Keyboard",
        this, Level.A, "keyboard-operation-keyboard-operable"));
    criteria.add(new SuccessCriterion(2, "No Keyboard Trap",
        this, Level.A, "keyboard-operation-trapping"));
    setCriteria(criteria);
  }

  @Override
  public EvaluationResults evaluate(List<Widget> widgets) {
    EvaluationResults results = new EvaluationResults();
    SuccessCriterion scKbd = getSuccessCriterionByName("Keyboard"),
        scTrap = getSuccessCriterionByName("No Keyboard Trap");
    int shortcutKeyCount = 0;
    for (Widget w: widgets) {
      if (AccessibilityUtil.hasKeyboardFocus(w)) {
        w.set(WCAG2Tags.WCAG2KeyboardVisited, true);

        String concreteID = w.get(Tags.ConcreteID, "");
        if (lastConcreteWidgetID.equals(concreteID)) {
          sameWidgetCount++;
          if (sameWidgetCount == KEYBOARD_TRAP_WIDGET_THRESHOLD) {
            results.add(new WCAG2EvaluationResult(scTrap, WCAG2EvaluationResult.Type.WARNING,
                "Possible keyboard trap", w));
          }
          else {
            results.add(evaluationPassed(scTrap));
          }
        }
        else {
          sameWidgetCount = 0;
        }
        lastConcreteWidgetID = concreteID;
      } // hasKeyboardFocus(w)

      String key1 = AccessibilityUtil.getAccessKey(w),
          key2 = AccessibilityUtil.getAcceleratorKey(w);
      if ((key1 != null && !key1.isEmpty())
          || (key2 != null && !key2.isEmpty())) {
        shortcutKeyCount++;
      }
    }
    if (shortcutKeyCount * SHORTCUT_KEYS_TO_WIDGETS_RATIO < widgets.size()) {
      results.add(new WCAG2EvaluationResult(scKbd, WCAG2EvaluationResult.Type.WARNING,
          "Possible widgets missing shortcut keys"));
    }
    else {
      results.add(evaluationPassed(scKbd));
    }
    return results;
  }

  @Override
  public Set<Action> deriveActions(List<Widget> widgets) {
    Set<Action> actions = new HashSet<>();
    StdActionCompiler compiler = new StdActionCompiler();
    Widget prevHasKeyboardFocus = null;
    deriveStandardActions(actions, compiler);
    for (Widget w: widgets) {
      deriveActionsAll(actions, compiler, w);
      if (AccessibilityUtil.hasKeyboardFocus(w)) {
        // catch inconsistent keyboard focus reporting
        if (prevHasKeyboardFocus != null) {
          reportDoubleFocus(prevHasKeyboardFocus, w);
        }
        deriveActionsFocus(actions, compiler, w);
        prevHasKeyboardFocus = w;
      } else { // !hasKeyboardFocus(w)
        deriveActionsNoFocus(actions, compiler, w);
      }
    }
    return actions;
  }

  private void deriveStandardActions(Set<Action> actions, StdActionCompiler compiler) {
    // standard keys
    actions.add(AccessibilityUtil.AC_ACTIVATE_WIDGET);
    actions.add(AccessibilityUtil.AC_NAVIGATE_NEXT_WIDGET);
    actions.add(AccessibilityUtil.AC_NAVIGATE_PREVIOUS_WIDGET);
    actions.add(AccessibilityUtil.AC_NAVIGATE_NEXT_GUI_AREA);
    actions.add(AccessibilityUtil.AC_NAVIGATE_PREVIOUS_GUI_AREA);
    actions.add(AccessibilityUtil.AC_OPEN_CONTEXT_MENU);
    actions.add(AccessibilityUtil.AC_CANCEL);
  }

  private void deriveActionsAll(Set<Action> actions, StdActionCompiler compiler,
      Widget w) {
    // if there is a menu bar, try to focus it
    // NOTE: menu bars are somewhat hard to detect, because not all menus are menu bars
    // and some menu bars (e.g. ribbons) are not menus.
    // However, most menu bars have an access key property, so they don't need to be explicitly detected.

    // if there are tabs, try to switch between them
    if (AccessibilityUtil.isTabItem(w)) {
      actions.add(AccessibilityUtil.AC_NAVIGATE_NEXT_TAB);
      actions.add(AccessibilityUtil.AC_NAVIGATE_PREVIOUS_TAB);
    }

    // find shortcut keys
    String shortcutKey = AccessibilityUtil.getAcceleratorKey(w);
    if (shortcutKey != null && !shortcutKey.isEmpty()) {
      Action a = AccessibilityUtil.parseShortcutKey(shortcutKey);
      if (a != null) {
        addShortcutKey(a);
      }
    }

    // find access keys
    String accessKey = AccessibilityUtil.getAccessKey(w);
    if (accessKey != null && !accessKey.isEmpty()) {
      Action a = AccessibilityUtil.parseShortcutKey(accessKey);
      if (a != null) {
        actions.add(a);
      }
    }
  }

  private void deriveActionsFocus(Set<Action> actions, StdActionCompiler compiler,
      Widget w) {
    // get the applicable keys for this widget
    for (Action a: AccessibilityUtil.getApplicableActions(w)) {
      actions.add(a);
    }

    // if shortcut keys are not blocked, e.g. by a modal window, try to use one
    if (AccessibilityUtil.canUseShortcutKeys(w)) {
      for (Action a: shortcutKeysCache) {
        actions.add(a);
      }
    }
  }

  private void deriveActionsNoFocus(Set<Action> actions, StdActionCompiler compiler,
      Widget w) {
    // nothing yet
  }

  private void addShortcutKey(Action a) {
    if (shortcutKeysCache.contains(a)) {
      return;
    }
    shortcutKeysCache.addLast(Assert.notNull(a));
    while (shortcutKeysCache.size() > MAX_CACHED_SHORTCUT_KEYS) {
      shortcutKeysCache.removeFirst();
    }
  }

  private void reportDoubleFocus(Widget oldW, Widget newW) {
    AccessibilityUtil.logA11y("Widgets <"
        + oldW.get(Tags.Title) + ">@" + oldW.get(Tags.ZIndex)
        + " and <" + newW.get(Tags.Title) + ">@" + newW.get(Tags.ZIndex)
        + " both claim to have keyboard focus");
  }

}
