package nl.ou.testar.tgherkin.model;

import static org.fruit.alayer.windows.UIARoles.UIAButton;
import static org.fruit.alayer.windows.UIARoles.UIAText;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fruit.alayer.Action;
import org.fruit.alayer.Rect;
import org.fruit.alayer.State;
import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.windows.UIATags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.DocumentBuilder;
import nl.ou.testar.tgherkin.TgherkinErrorListener;
import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.ActionWidgetProxy;
import nl.ou.testar.tgherkin.model.Document;
import nl.ou.testar.tgherkin.protocol.DocumentProtocol;


/**
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
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		generalWidgets.add(widget);
		return generalWidgets;
	}
	
	private List<Item> setUpItems(){
		// Create settings
		Settings settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, false);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ClickFilter,"(?!x)x");
		// Create proxy
		ActionWidgetProxy proxy = new TestProtocol();
		//
		List<Item> itemList = new ArrayList<Item>();
		// test case 1
		String expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"	Selection: click()\r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Step 1 \r\n" + 
				"		When  $Title=\"Title1\" click()\r\n" + 
				"    Step: Step 2 \r\n" + 
				"		When  $Title=\"Title1\" or $Title=\"Title2\" or $Title=\"Title3\" click()";
		List<State> states = new ArrayList<State>();
		List<Widget> stateWidgets = setUpGeneralWidgets();
		State state = new TestState(stateWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		// use same state for all steps
		for (int index = 1;index <= 3;index++){
			states.add(state);	
		}
		List<Integer> actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		actionSetSizes.add(2);
		List<Set<String>> actionSetList = new ArrayList<Set<String>>();
		Set<String> actionSet = new HashSet<String>();
		actionSet.add("ConcreteID1");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteID1");
		actionSet.add("ConcreteID2");
		actionSetList.add(actionSet);		
		List<Verdict> verdicts = new ArrayList<Verdict>();
		// use same verdict for all steps
		for (int index = 1;index <= 2;index++){
			verdicts.add(Verdict.OK);	
		}
		itemList.add(new Item(settings, proxy, expression, 1, 2, states, actionSetSizes, actionSetList, verdicts));
		// test case 2
		expression = "Feature: Feature 1 \r\n" + 
				"\r\n" + 
				"	Selection: click()\r\n" + 
				"\r\n" + 
				"  Scenario: Scenario 1\r\n" + 
				"    Step: Step 1 \r\n" + 
				"		When  $Title=\"Title1\" click()\r\n" + 
				"    Step: Step 2 \r\n" + 
				"		Range 10 10\r\n" + 
				"		When  $Title=\"Title1\" or $Title=\"Title2\" or $Title=\"Title3\" click()";
		// use same state for all steps
		for (int index = 1;index <= 12;index++){
			states.add(state);	
		}
		actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		for (int index = 1;index <= 10;index++){
			actionSetSizes.add(2);
		}
		actionSetList = new ArrayList<Set<String>>();
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteID1");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteID1");
		actionSet.add("ConcreteID2");
		for (int index = 1;index <= 10; index++) {
			actionSetList.add(actionSet);
		}
		verdicts = new ArrayList<Verdict>();
		// use same verdict for all steps
		for (int index = 1;index <= 11;index++){
			verdicts.add(Verdict.OK);	
		}
		itemList.add(new Item(settings, proxy, expression, 1, 11, states, actionSetSizes, actionSetList, verdicts));
		// test case 3
		expression = "Feature: Perform calculations with windows calculator \r\n" + 
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
		// use same state for all steps
		for (int index = 1;index <= 21;index++){
			states.add(state);	
		}
		actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 20;index++){
			actionSetSizes.add(1);
		}
		//
		actionSetList = new ArrayList<Set<String>>();
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum2Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum3Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum9Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum4Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum7Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum6Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum8Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum1Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		verdicts = new ArrayList<Verdict>();
		// use same verdict for all steps
		for (int index = 1;index <= 20;index++){
			verdicts.add(Verdict.OK);	
		}
		itemList.add(new Item(settings, proxy, expression, 4, 20, states, actionSetSizes, actionSetList, verdicts));
		// test case 4
		expression = "Feature: Perform calculations with windows calculator \r\n" + 
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
		// use same state for all steps
		for (int index = 1;index <= 23;index++){
			states.add(state);	
		}
		actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 22;index++){
			actionSetSizes.add(1);
		}
		actionSetList = new ArrayList<Set<String>>();
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum2Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum3Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDclearButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum8Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum1Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum9Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum4Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum7Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum6Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum1Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum2Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		verdicts = new ArrayList<Verdict>();
		// use same verdict for all steps
		for (int index = 1;index <= 22;index++){
			verdicts.add(Verdict.OK);	
		}
		itemList.add(new Item(settings, proxy, expression, 5, 22, states, actionSetSizes, actionSetList, verdicts));
		// test case 5
		expression = "Feature: Perform calculations with windows calculator \r\n" + 
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
		states = new ArrayList<State>();
		// Create begin state
		states.add(state);
		// Create after state for Step 1
		List<Widget> stepWidgets = setUpGeneralWidgets();
		Widget widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID16");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 2
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID17");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorExpression");
		widget.set(Tags.Title, "Weergave is 2 + ");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 3
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID18");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 3");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		// Create after state for Step 4
		stepWidgets = setUpGeneralWidgets();		
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID19");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 5");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);	
		actionSetSizes = new ArrayList<Integer>();
		for (int index = 1;index <= 4;index++){
			actionSetSizes.add(1);
		}
		actionSetList = new ArrayList<Set<String>>();
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum2Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDplusButton");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum3Button");
		actionSetList.add(actionSet);
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDequalButton");
		actionSetList.add(actionSet);		
		verdicts = new ArrayList<Verdict>();
		// use same verdict for all steps
		for (int index = 1;index <= 4;index++){
			verdicts.add(Verdict.OK);	
		}
		itemList.add(new Item(settings, proxy, expression, 1, 4, states, actionSetSizes, actionSetList, verdicts));
		// test case: step oracle failure 
		expression = "Feature: Perform calculations with windows calculator \r\n" + 
				"\r\n" + 
				"  Scenario: Add two numbers\r\n" + 
				"    Step: Select 2\r\n" + 
				"		When  $UIAAutomationId=\"num2Button\" click()\r\n" + 
				"		Then  $UIAAutomationId=\"CalculatorResults\" and matches($Title,\"^.*9$\")\r\n";  
		states = new ArrayList<State>();
		// Create begin state
		states.add(state);
		// Create after state for Step 1
		stepWidgets = setUpGeneralWidgets();
		widget = new StdWidget();		
		widget.set(Tags.ConcreteID, "ConcreteID16");
		widget.set(Tags.Role, UIAText);
		widget.set(UIATags.UIAAutomationId, "CalculatorResults");
		widget.set(Tags.Title, "Weergave is 2");
		widget.set(Tags.Blocked, false);
		widget.set(Tags.Enabled, true);
		widget.set(UIATags.UIAIsKeyboardFocusable,true);
		widget.set(Tags.Shape, Rect.from(1304.0, 536.0, 120.0, 49.0));
		widget.set(Tags.MaxZIndex, 15.0);
		widget.set(Tags.ZIndex, 15.0);
		stepWidgets.add(widget);		
		state = new TestState(stepWidgets);	
		state.set(Tags.MaxZIndex, 15.0);
		states.add(state);
		actionSetSizes = new ArrayList<Integer>();
		actionSetSizes.add(1);
		actionSetList = new ArrayList<Set<String>>();
		//
		actionSet = new HashSet<String>();
		actionSet.add("ConcreteIDnum2Button");
		actionSetList.add(actionSet);
		verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Step.TGHERKIN_FAILURE, "Tgherkin step oracle failure!"));	
		itemList.add(new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts));
		// test case 7: same as test case 6, but ApplyDefaultOnMismatch set to true
		settings = new Settings();
		settings.set(ConfigTags.ApplyDefaultOnMismatch, true);
		settings.set(ConfigTags.ContinueToApplyDefault, false);
		settings.set(ConfigTags.RepeatTgherkinScenarios, false);
		settings.set(ConfigTags.GenerateTgherkinReport, false);
		settings.set(ConfigTags.ReportDerivedGestures, false);
		settings.set(ConfigTags.ReportState, false);
		settings.set(ConfigTags.ForceToSequenceLength, false);
		settings.set(ConfigTags.ClickFilter,"(?!x)x");
		verdicts = new ArrayList<Verdict>();
		verdicts.add(new Verdict(Verdict.SEVERITY_MIN, "Default applied for Tgherkin step mismatch"));	
		itemList.add(new Item(settings, proxy, expression, 1, 1, states, actionSetSizes, actionSetList, verdicts));
		return itemList;
	}
	
	
	
	@Test
	public void test() {
		for (Item item : setUpItems()) {
			Document document = getModel(item.getExpression());
			// process document in sequences and actions
			int index = 0;
			int sequenceCount = 0;
			int stepCount = 0;
			while (document.moreSequences()) {
				sequenceCount++;
				document.beginSequence();
				while (document.moreActions(item.getSettings())) {
					stepCount++;
					Set<Action> actions = document.deriveActions(item.getStates().get(index), item.getSettings(), item.getProxy());
					assertSame(actions.size(), item.getActionSetSizes().get(index));
					assertTrue(actionSetValid(actions, item.getActionSetList().get(index))); 
					Verdict verdict = document.getVerdict(item.getStates().get(index + 1), item.getSettings());
					assertEquals(verdict, item.getVerdicts().get(index));
					index++;
				}
			}
			assertSame(sequenceCount, item.getSequences());
			assertSame(stepCount, item.getSteps());
		}
	}
		
	private Document getModel(String expression) {
		ANTLRInputStream inputStream = new ANTLRInputStream(expression);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		TgherkinParser parser = new TgherkinParser(new CommonTokenStream(lexer));
	    TgherkinErrorListener errorListener = new TgherkinErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		Document document = new DocumentBuilder().visitDocument(parser.document());
		List<String> errorList = errorListener.getErrorList();
		if (errorList.size() == 0) {
			// post-processing check
			errorList = document.check();
		}
		if (errorList.size() != 0) {
			for(String errorText : errorList) {
				LogSerialiser.log(errorText, LogSerialiser.LogLevel.Info);
			}
			throw new TgherkinException("Invalid Tgherkin document, see log for details");
		}
		return document;
	}
	
	private boolean actionSetValid(Set<Action> actions, Set<String> expectedActions) {
		if (actions.size() != expectedActions.size()) {
			return false;
		}
		// match on target id
		for (Action action :actions) {
			if (!expectedActions.contains(action.get(Tags.TargetID, ""))) {
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
	
	private class Item{
		Settings settings;
		ActionWidgetProxy proxy;		
		String expression;
		int sequences;
		int steps;
		List<State> states;
		List<Integer> actionSetSizes;
		List<Set<String>> actionSetList;
		List<Verdict> verdicts;
		private Item(Settings settings, ActionWidgetProxy proxy, String expression, int sequences, int steps, List<State> states, List<Integer> actionSetSizes, List<Set<String>> actionSetList, List<Verdict> verdicts) {
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
		private List<Set<String>> getActionSetList() {
			return actionSetList;
		}
		private List<Verdict> getVerdicts() {
			return verdicts;
		}
	}
}
