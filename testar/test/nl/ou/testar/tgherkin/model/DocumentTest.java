package nl.ou.testar.tgherkin.model;

import static org.fruit.alayer.windows.UIARoles.UIAButton;
import static org.fruit.alayer.windows.UIARoles.UIAText;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.windows.UIATags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.protocol.DocumentProtocol;


/**
 * JUnit test for the Document class. 
 * This class tests execution of the document model:
 * - verification of the number of generated sequences and actions
 * - verification of the number of derived actions and verdict severity
 *
 */
public class DocumentTest {

	private List<Widget> setUpGeneralWidgets() {
		List<Widget> generalWidgets = new ArrayList<Widget>();
		Widget widget = new StdWidget();
		widget.set(Tags.ConcreteID, "ConcreteID1");
		widget.set(Tags.Role, UIAButton);
		widget.set(Tags.Desc, "Desc1");
		widget.set(Tags.Title, "Title1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID1");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID1");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID1");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();
		widget.set(Tags.ConcreteID, "ConcreteID2");
		widget.set(Tags.Role, UIAButton);		
		widget.set(Tags.Desc, "Desc2");
		widget.set(Tags.Title, "Title2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID2");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID2");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID2");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDclearButton");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "clearButton");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID3");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID3");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID3");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();	
		widget.set(Tags.ConcreteID, "ConcreteIDplusButton");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "plusButton");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID4");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID4");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID4");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDequalButton");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "equalButton");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID5");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID5");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID5");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum0Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num0Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID6");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID6");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID6");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum1Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num1Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID7");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID7");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID7");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum2Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num2Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID8");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID8");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID8");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum3Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num3Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID9");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID9");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID9");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum4Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num4Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID10");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID10");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID10");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum5Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num5Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID11");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID11");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID11");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum6Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num6Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID12");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID12");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID12");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum7Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num7Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID13");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID13");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID13");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum8Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num8Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID14");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID14");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID14");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDnum9Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "num9Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID15");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID15");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID15");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDxpower2Button");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "xpower2Button");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID16");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID16");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID16");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDsquareRootButton");
		widget.set(Tags.Role, UIAButton);
		widget.set(UIATags.UIAAutomationId, "squareRootButton");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID17");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID17");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID17");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteIDText");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "Text");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID18");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID18");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID18");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		return generalWidgets;
	}
	
	private State setUpGeneralState() {
		List<Widget> stateWidgets = setUpGeneralWidgets();
		State state = new TestState(stateWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		return state;
	}

	private List<State> setUpGeneralstates(int numberOfStates) {
		List<State> states = new ArrayList<State>();
		for (int index = 1;index <= numberOfStates;index++){
			states.add(setUpGeneralState());	
		}
		return states;
	}
	
	private List<Item> setUpItems(){
		List<Item> itemList = new ArrayList<Item>();
		itemList.add(setupItem1());
		itemList.add(setupItem2());
		itemList.add(setupItem3());
		itemList.add(setupItem4());
		itemList.add(setupItem5());
		itemList.add(setupItem6());
		itemList.add(setupItem7());
		itemList.add(setupItem8());
		itemList.add(setupItem9());
		itemList.add(setupItem10());
		itemList.add(setupItem11());
		itemList.add(setupItem12());
		itemList.add(setupItem13());
		itemList.add(setupItem14());
		itemList.add(setupItem15());
		itemList.add(setupItem16());
		itemList.add(setupItem17());
		itemList.add(setupItem18());
		itemList.add(setupItem19());
		return itemList; 
	}
	
	private Item setupItem1(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"	Selection: click()\r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Step 1 \r\n" + 
				"		When  $Title=\"Title1\" click()\r\n" + 
				"    Step: Step 2 \r\n" + 
				"		When  $Title=\"Title1\" or $Title=\"Title2\" or $Title=\"Title3\" click()";
		// states
		List<State> states = setUpGeneralstates(3);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		actionSetSizes.add(2);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteID1",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteID1",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteID2",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (int index = 1;index <= 2;index++){
			verdicts.add(Verdict.OK);	
		}
		return new Item(settings, proxy, expression, 1, 2, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem2(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"\r\n" + 
				"	Selection: click()\r\n" + 
				"\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"    Step: Step 1 \r\n" + 
				"		When  $Title=\"Title1\" click()\r\n" + 
				"    Step: Step 2 \r\n" + 
				"		Range 10 10\r\n" + 
				"		When  $Title=\"Title1\" or $Title=\"Title2\" or $Title=\"Title3\" click()";
		// states
		List<State> states = setUpGeneralstates(12);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		for (int index = 1;index <= 10;index++){
			actionSetSizes.add(2);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteID1",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteID1",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteID2",ActionRoles.LeftClickAt));
		for (int index = 1;index <= 10; index++) {
			actionSetList.add(actionSet);
		}
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (int index = 1;index <= 11;index++){
			verdicts.add(Verdict.OK);	
		}
		return new Item(settings, proxy, expression, 1, 11, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem3(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Background: Clear.\r\n" + 
				"    Step: Select Clear\r\n" + 
				"		When  $UIAAutomationId=\"clearButton\" click()\r\n" + 
				"		\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select 3\r\n" + 
				"		When  $UIAAutomationId=\"num3Button\" click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"\r\n" + 
				"  Scenario Outline: Add two numbers\r\n" + 
				"    Step: Select number 1\r\n" + 
				"		When  $UIAAutomationId=<number1> click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select number 2\r\n" + 
				"		When  $UIAAutomationId=<number2> click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"  Examples: To be added numbers\r\n" + 
				"    | number1 | number2 |\r\n" + 
				"    |  num9Button  |  num4Button |\r\n" + 
				"    |  num7Button  |  num6Button  |  \r\n" + 
				"	\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 8\r\n" + 
				"		When  $UIAAutomationId=\"num8Button\" click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n";
		// states
		List<State> states = setUpGeneralstates(21);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 20;index++){
			actionSetSizes.add(1);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum3Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum9Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum4Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum7Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum6Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum8Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (int index = 1;index <= 20;index++){
			verdicts.add(Verdict.OK);	
		}
		return new Item(settings, proxy, expression, 4, 20, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem4(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Background: Clear.\r\n" + 
				"    Step: Select Clear\r\n" + 
				"		When  $UIAAutomationId=\"clearButton\" click()\r\n" + 
				"		\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select 3\r\n" + 
				"		When  $UIAAutomationId=\"num3Button\" click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 8\r\n" + 
				"		When  $UIAAutomationId=\"num8Button\" click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"		\r\n" + 
				"Feature: Perform calculations with windows calculator 		\r\n" + 
				"  Scenario Outline: Add two numbers\r\n" + 
				"    Step: Select number 1\r\n" + 
				"		When  $UIAAutomationId=<number1> click()\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"    Step: Select number 2\r\n" + 
				"		When  $UIAAutomationId=<number2> click()\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"  Examples: To be added numbers\r\n" + 
				"    | number1 | number2 |\r\n" + 
				"    |  num9Button  |  num4Button |\r\n" + 
				"    |  num7Button  |  num6Button  |  \r\n" + 
				"    |  num1Button  |  num2Button  |  	";
		// states
		List<State> states = setUpGeneralstates(23);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 22;index++){
			actionSetSizes.add(1);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum3Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum8Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum9Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum4Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum7Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum6Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (int index = 1;index <= 22;index++){
			verdicts.add(Verdict.OK);	
		}
		return new Item(settings, proxy, expression, 5, 22, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem5(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*2$\")\r\n" + 
				"    Step: Select +\r\n" + 
				"		When  $UIAAutomationId=\"plusButton\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorExpression\" and matches($Title,\"^.*2\\s\\+\\s$\")\r\n" + 
				"    Step: Select 3\r\n" + 
				"		When  $UIAAutomationId=\"num3Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*3$\")\r\n" + 
				"    Step: Select = \r\n" + 
				"		When  $UIAAutomationId=\"equalButton\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*5$\")";
		// states
		List<State> states = new ArrayList<State>();
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget> stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 2
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID102");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorExpression");
		widget.set(Tags.Title, "Weergave is 2 + ");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID102");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID102");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID102");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 3
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID103");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 3");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID103");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID103");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID103");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 4
		stepWidgets = setUpGeneralWidgets();		
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID104");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 5");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID104");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID104");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID104");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);	
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 4;index++){
			actionSetSizes.add(1);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum3Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);		
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (int index = 1;index <= 4;index++){
			verdicts.add(Verdict.OK);	
		}
		return new Item(settings, proxy, expression, 1, 4, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem6(){
		// test case 6: step oracle failure 
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*9$\")\r\n";  
		// states
		List<State> states = new ArrayList<State>();
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget> stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin step oracle failure!"));	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem7(){
		// test case 7: same as test case 6, but ApplyDefaultOnMismatch set to true
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, true);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*9$\")\r\n";  
		// states
		List<State> states = new ArrayList<State>();
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget> stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Verdict.SEVERITY_MIN, "Default applied for Tgherkin step mismatch"));			
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem8(){
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"  Selection: click()\r\n" +
				"  Scenario: Take power 2 of a two digit number\r\n" + 
				"    Step: Select 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*1$\")\r\n" + 
				"    Step: Select 0\r\n" + 
				"		When  $UIAAutomationId=\"num0Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*10$\")\r\n" + 
				"    Step: Select x power 2\r\n" + 
				"		When  $UIAAutomationId=\"xpower2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*100$\")\r\n";  
		// states
		List<State> states = new ArrayList<State>();
		// Create begin state
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget> stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 2
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID102");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 9999999");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID102");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID102");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID102");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 2;index++){
			actionSetSizes.add(1);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum0Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin step oracle failure!"));
		return new Item(settings, proxy, expression, 1, 2, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem9(){
		// test case 9: case 8, but ApplyDefaultOnMismatch is true
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, true);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"  Selection: click()\r\n" +
				"  Scenario: Take power 2 of a two digit number\r\n" + 
				"    Step: Select 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*1$\")\r\n" + 
				"    Step: Select 0\r\n" + 
				"		When  $UIAAutomationId=\"num0Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*10$\")\r\n" + 
				"    Step: Select x power 2\r\n" + 
				"		When  $UIAAutomationId=\"xpower2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*100$\")\r\n";  
		// states
		List<State> states = new ArrayList<State>();
		// Create begin state
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget>stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 2
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID102");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 9999999");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID102");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID102");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID102");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 3
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID103");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 100");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID103");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID103");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID103");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 3;index++){
			actionSetSizes.add(1);
		}
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum0Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDxpower2Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		verdicts.add(new Verdict(Verdict.SEVERITY_MIN, "Default applied for Tgherkin step mismatch"));
		verdicts.add(Verdict.OK);
		return new Item(settings, proxy, expression, 1, 3, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem10(){
		// test case 10: case 9, but ContinueToApplyDefault is true
		// settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, true);
		settings.set(ConfigTags.ContinueToApplyDefault, true);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"  Selection: click()\r\n" +
				"  Scenario: Take power 2 of a two digit number\r\n" + 
				"    Step: Select 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*1$\")\r\n" + 
				"    Step: Select 0\r\n" + 
				"		When  $UIAAutomationId=\"num0Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*10$\")\r\n" + 
				"    Step: Select x power 2\r\n" + 
				"		When  $UIAAutomationId=\"xpower2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*100$\")\r\n";  
		// states
		List<State> states = new ArrayList<State>();
		// Create begin state
		states.add(setUpGeneralState());
		// Create after state for Step 1
		List<Widget>stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID101");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 1");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID101");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID101");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID101");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		State state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 2
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID102");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 9999999");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID102");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID102");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID102");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 3
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID103");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 100");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.Abstract_R_ID, "Abstract_R_ID103");
		widget.set(Tags.Abstract_R_T_ID, "Abstract_R_T_ID103");
		widget.set(Tags.Abstract_R_T_P_ID, "Abstract_R_T_P_ID103");
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 2;index++){
			actionSetSizes.add(1);
		}
		actionSetSizes.add(17);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum0Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteID1",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteID2",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum0Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum2Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum3Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum4Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum5Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum6Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum7Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum8Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum9Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDxpower2Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDsquareRootButton",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDequalButton",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDclearButton",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDplusButton",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		verdicts.add(new Verdict(Verdict.SEVERITY_MIN, "Default applied for Tgherkin step mismatch"));
		verdicts.add(new Verdict(Verdict.SEVERITY_MIN, "Default applied after a Tgherkin step mismatch"));
		return new Item(settings, proxy, expression, 1, 3, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem11(){
		// test case 11: step oracle failure on step 1
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Oracle: $Title=\"Title1\"\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"	Oracle: $Title=\"Title2\"\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $Title=\"Title3\"\r\n" + 
				"    Step: Step 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $Title=\"Title1\"";  
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin step oracle failure!"));			
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem12(){
		// test case 12: scenario oracle failure on step 1
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Oracle: $Title=\"Title1\"\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"	Oracle: $Title=\"Title3\"\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $Title=\"Title2\"\r\n" + 
				"    Step: Step 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $Title=\"Title1\"";  
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin scenario oracle failure!"));	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem13(){
		// test case 13: feature oracle failure on step 1
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Oracle: $Title=\"Title3\"\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"	Oracle: $Title=\"Title1\"\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" click()\r\n" + 
				"		Then  $Title=\"Title2\"\r\n" + 
				"    Step: Step 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $Title=\"Title1\"";  
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin feature oracle failure!"));	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem14(){
		// test case 14: scenario outline oracle failure on step 1
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Oracle: $Title=\"Title1\"\r\n" + 
				"  Scenario Outline: Scenario 1\r\n" + 
				"	Oracle: $Title=\"Title3\"\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=<buttonID1> click()\r\n" + 
				"		Then  $Title=\"Title2\"\r\n" + 
				"    Step: Step 2\r\n" + 
				"		When  $UIAAutomationId=<buttonID2> click()\r\n" + 
				"		Then  $Title=\"Title1\"\r\n" + 
				"    Examples:\r\n" + 
				"	|buttonID1|buttonID2|	\r\n" + 
				"	|num1Button|num2Button|\r\n";  
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin scenario outline oracle failure!"));	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	private Item setupItem15(){
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"Text\" anyGesture()\r\n"; 
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(4);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDText",ActionRoles.ClickTypeInto));
		actionSet.add(new ActionMatcher("ConcreteIDText",ActionRoles.MouseMove));
		actionSet.add(new ActionMatcher("ConcreteIDText",ActionRoles.DropDown));
		actionSet.add(new ActionMatcher("ConcreteIDText",ActionRoles.RightClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem16(){
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" anyGesture()\r\n"; 
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(6);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LDoubleClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.MouseMove));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.DropDown));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.RightClickAt));
		// triple click has no role!
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",null));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem17(){
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Selection: click() rightClick()\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" anyGesture()\r\n"; 
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(2);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.RightClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem18(){
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Selection: click() rightClick()\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"  Selection: anyGesture()\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" anyGesture()\r\n"; 
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(2);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.LeftClickAt));
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.RightClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}

	private Item setupItem19(){
		// settings		
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ConfidenceThreshold, 1.0);
		// proxy
		ActionWidgetProxy proxy = new TestProtocol();
		// expression
		String expression = "Feature: Feature 1 \r\n" + 
				"  Selection: click() rightClick()\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"  Selection: rightClick()\r\n" + 
				"    Step: Step 1\r\n" + 
				"		When  $UIAAutomationId=\"num1Button\" anyGesture()\r\n"; 
		// states
		List<State> states = setUpGeneralstates(2);
		// action set sizes
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		// action sets
		List<Set<ActionMatcher>> actionSetList = new ArrayList<Set<ActionMatcher>>();
		Set<ActionMatcher> actionSet = new HashSet<ActionMatcher>();
		actionSet.add(new ActionMatcher("ConcreteIDnum1Button",ActionRoles.RightClickAt));
		actionSetList.add(actionSet);
		// verdicts
		List<Verdict> verdicts = new ArrayList<Verdict>();
		verdicts.add(Verdict.OK);	
		return new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts);
	}
	
	/**
	 * Test document.
	 */
	@Test
	public void test() {
		for (Item item : setUpItems()) {
			Document document = Utils.getDocumentModel(item.getExpression());
			// process document in sequences and actions
			int index = 0;
			int sequenceCount = 0;
			int stepCount = 0;
			while (document.moreSequences()) {
				sequenceCount++;
				document.beginSequence();
				while (document.moreActions(item.getSettings())) {
					stepCount++;
					Set<Action> actions = document.deriveActions(item.getSettings(), item.getStates().get(index), item.getProxy());
					assertSame(actions.size(), item.getActionSetSizes().get(index));
					assertTrue(actionSetValid(actions, item.getActionSetList().get(index))); 
					Verdict verdict = document.getVerdict(item.getSettings(), item.getStates().get(index + 1));
					assertEquals(verdict, item.getVerdicts().get(index));
					index++;
				}
			}
			assertSame(sequenceCount, item.getSequences());
			assertSame(stepCount, item.getSteps());
		}
	}
		
	private boolean actionSetValid(Set<Action> actions, Set<ActionMatcher> expectedActions) {
		if (actions.size() != expectedActions.size()) {
			return false;
		}
		// match on target id
		for (Action action :actions) {
			boolean matchFound = false;
			for (ActionMatcher actionMatcher: expectedActions) {
				if (actionMatcher.match(action)) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				return false;
			}			
		}
		return true;
	}
	
	private class TestProtocol extends DocumentProtocol{
		@Override
		public boolean isUnfiltered(Widget w){
			return true;
		}
	}
	
	// matching of an action occurs on target id and role, except for mouse moves
	private class ActionMatcher{		
		private String targetID;
		private Role role;
		private ActionMatcher(String targetID, Role role) {
			super();
			this.targetID = targetID;
			this.role = role;
		}
		private boolean match(Action action){
			if (role == null) {
				if (action.get(Tags.Role, null) == null) {
					return targetID.equals(action.get(Tags.TargetID, ""));
				}
			}
			if (role.equals(ActionRoles.MouseMove)) {
				// mouse move action has no TargetID tag
				return role.equals(action.get(Tags.Role, null));
			}
			return targetID.equals(action.get(Tags.TargetID, "")) && role.equals(action.get(Tags.Role, null));
		}
	}
	
	private class Item{
		private Settings settings;
		private ActionWidgetProxy proxy;		
		private String expression;
		private int sequences;
		private int steps;
		private List<State> states;
		private List<Integer> actionSetSizes;
		private List<Set<ActionMatcher>> actionSetList;
		private List<Verdict> verdicts;
		private Item(Settings settings, ActionWidgetProxy proxy, String expression, int sequences, int steps, List<State> states, List<Integer> actionSetSizes, List<Set<ActionMatcher>> actionSetList, List<Verdict> verdicts) {
			super();
			this.settings = settings;
			this.proxy = proxy;
			this.expression = expression;
			this.sequences = sequences;
			this.steps = steps;
			this.states = states;
			this.actionSetSizes = actionSetSizes;
			this.actionSetList = actionSetList;
			this.verdicts = verdicts;
		}
		private Settings getSettings() {
			return settings;
		}
		private ActionWidgetProxy getProxy() {
			return proxy;
		}
		private String getExpression() {
			return expression;
		}
		private int getSequences() {
			return sequences;
		}
		private int getSteps() {
			return steps;
		}
		private List<State> getStates() {
			return states;
		}
		private List<Integer> getActionSetSizes() {
			return actionSetSizes;
		}
		private List<Set<ActionMatcher>> getActionSetList() {
			return actionSetList;
		}
		private List<Verdict> getVerdicts() {
			return verdicts;
		}
	}
}
