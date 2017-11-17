/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

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
	private static final int MIN_SAME_WIDGET_COUNT_BEFORE_KEYBOARD_TRAP = 7;
	
	private final Deque<Action> shortcutKeysCache = new LinkedList<>();
	private String lastConcreteID = "";
	private int sameWidgetCount = 0;
	
	KeyboardAccessibleGuideline(AbstractPrinciple parent) {
		super(1, "Keyboard Accessible", parent);
		criteria.add(new SuccessCriterion(1, "Keyboard",
				this, Level.A, "keyboard-operation-keyboard-operable"));
		criteria.add(new SuccessCriterion(2, "No Keyboard Trap",
				this, Level.A, "keyboard-operation-trapping"));
	}
	
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		SuccessCriterion sc = getSuccessCriterionByName("No Keyboard Trap");
		for (Widget w : widgets) {
			if (AccessibilityUtil.hasKeyboardFocus(w)) {
				w.set(WCAG2Tags.WCAG2KeyboardVisited, true);
				
				String concreteID = w.get(Tags.ConcreteID, "");
				if (lastConcreteID.equals(concreteID)) {
					sameWidgetCount++;
					if (sameWidgetCount == MIN_SAME_WIDGET_COUNT_BEFORE_KEYBOARD_TRAP)
						results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.WARNING,
								"Possible keyboard trap", w));
					else
						results.add(evaluationPassed(sc));
				}
				else {
					sameWidgetCount = 0;
				}
				lastConcreteID = concreteID;
			} // hasKeyboardFocus(w)
		}
		return results;
	}
	
	@Override
	public Set<Action> deriveActions(List<Widget> widgets) {
		Set<Action> actions = new HashSet<>();
		StdActionCompiler compiler = new StdActionCompiler();
		Widget prevHasKeyboardFocus = null;
		deriveStandardActions(actions, compiler);
		for (Widget w : widgets) {
			AccessibilityUtil.printWidgetDebugInfo(w);	
			deriveActionsAll(actions, compiler, w);
			if (AccessibilityUtil.hasKeyboardFocus(w)) {
				// catch inconsistent keyboard focus reporting
				if (prevHasKeyboardFocus != null)
					reportDoubleFocus(prevHasKeyboardFocus, w);
				deriveActionsFocus(actions, compiler, w);
				prevHasKeyboardFocus = w;
			}
			else { // !hasKeyboardFocus(w)
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
		String shortcutKey = AccessibilityUtil.getShortcutKey(w);
		if (shortcutKey != null && !shortcutKey.isEmpty()) {
			Action a = AccessibilityUtil.parseShortcutKey(shortcutKey);
			if (a != null)
				addShortcutKey(a);
		}
		
		// find access keys
		String accessKey = AccessibilityUtil.getAccessKey(w);
		if (accessKey != null && !accessKey.isEmpty()) {
			Action a = AccessibilityUtil.parseShortcutKey(accessKey);
			if (a != null)
				actions.add(a);
		}
	}
		
	private void deriveActionsFocus(Set<Action> actions, StdActionCompiler compiler,
			Widget w) {
		// get the applicable keys for this widget
		for (Action a : AccessibilityUtil.getApplicableActions(w))
			actions.add(a);
		
		// if shortcut keys are not blocked, e.g. by a modal window, try to use one
		if (AccessibilityUtil.canUseShortcutKeys(w))
			for (Action a : shortcutKeysCache)
				actions.add(a);
	}
	
	private void deriveActionsNoFocus(Set<Action> actions, StdActionCompiler compiler,
			Widget w) {
		// nothing yet
	}
	
	private void addShortcutKey(Action a) {
		if (shortcutKeysCache.contains(a))
			return;
		shortcutKeysCache.addLast(Assert.notNull(a));
		while (shortcutKeysCache.size() > MAX_CACHED_SHORTCUT_KEYS)
			shortcutKeysCache.removeFirst();
	}
	
	private void reportDoubleFocus(Widget oldW, Widget newW) {
		AccessibilityUtil.logA11y("Widgets <"
				+ oldW.get(Tags.Title) + ">@" + oldW.get(Tags.ZIndex)
				+ " and <" + newW.get(Tags.Title) + ">@" + newW.get(Tags.ZIndex)
				+ " both claim to have keyboard focus");
	}
	
}
