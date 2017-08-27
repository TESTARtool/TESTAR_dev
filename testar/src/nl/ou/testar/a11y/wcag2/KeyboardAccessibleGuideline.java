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
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class KeyboardAccessibleGuideline extends AbstractGuideline {
	
	private static final int MAX_CACHED_SHORTCUT_KEYS = 50;
	
	private final Deque<Action> shortcutKeysCache = new LinkedList<>();
	
	KeyboardAccessibleGuideline(AbstractPrinciple parent) {
		super(1, "Keyboard Accessible", parent);
		criteria.add(new SuccessCriterion(1, "Keyboard", this, Level.A));
		criteria.add(new SuccessCriterion(2, "No Keyboard Trap", this, Level.A));
	}
	
	@Override
	public Set<Action> deriveActions(State state) {
		Set<Action> actions = new HashSet<>();
		StdActionCompiler compiler = new StdActionCompiler();
		Widget prevHasKeyboardFocus = null;
		deriveStandardActions(actions, compiler);
		for (Widget w : state) {
			// skip irrelevant widgets
			if (!AccessibilityUtil.isRelevant(w))
				continue;
			AccessibilityUtil.printWidgetDebugInfo(w);	
			deriveActionsAll(actions, compiler, w);
			if (AccessibilityUtil.hasKeyboardFocus(w)) {
				// catch inconsistent keyboard focus reporting
				if (prevHasKeyboardFocus != null)
					reportDoubleFocus(prevHasKeyboardFocus, w);
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
		actions.add(AccessibilityUtil.NAVIGATE_NEXT_WIDGET);
		actions.add(AccessibilityUtil.NAVIGATE_PREVIOUS_WIDGET);
		actions.add(AccessibilityUtil.NAVIGATE_NEXT_AREA);
		actions.add(AccessibilityUtil.NAVIGATE_PREVIOUS_AREA);
		actions.add(AccessibilityUtil.CANCEL);
		
		// shortcut keys
		addShortcutKey(AccessibilityUtil.ACTIVATE_CONTEXT_MENU);
	}
		
	private void deriveActionsAll(Set<Action> actions, StdActionCompiler compiler,
			Widget w) {
		// if there are tabs, try to switch between them
		if (AccessibilityUtil.isTabItem(w)) {
			actions.add(AccessibilityUtil.NAVIGATE_NEXT_TAB);
			actions.add(AccessibilityUtil.NAVIGATE_PREVIOUS_TAB);
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
		// many widgets accept arrow keys
		if (AccessibilityUtil.canUseLeftRight(w)) {
			actions.add(AccessibilityUtil.NAVIGATE_LEFT);
			actions.add(AccessibilityUtil.NAVIGATE_RIGHT);
		}
		if (AccessibilityUtil.canUseUpDown(w)) {
			actions.add(AccessibilityUtil.NAVIGATE_UP);
			actions.add(AccessibilityUtil.NAVIGATE_DOWN);
		}
		
		// find the appropriate additional actions for the widget
		if (AccessibilityUtil.canUseEditCommands(w)) {
			// if the widget appears to support editing commands, try to manipulate its text
			//actions.add(compiler.clickTypeInto(w, DataManager.getRandomData()));
			actions.add(AccessibilityUtil.DELETE_FORWARD);
			actions.add(AccessibilityUtil.DELETE_BACKWARD);
		} else {
			// for all other widgets, try to activate the widget
			actions.add(AccessibilityUtil.ACTIVATE_WIDGET);
		}
		
		// if the widget is a combo box, try to expand or collapse it
		if (AccessibilityUtil.isComboBox(w)) {
			// TODO: check expanded/collapsed state
			actions.add(AccessibilityUtil.EXPAND_COMBO_BOX);
			actions.add(AccessibilityUtil.COLLAPSE_COMBO_BOX);
		}
		
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
