/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


package es.upv.staq.testar.algorithms;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import es.upv.staq.testar.graph.GraphEdge;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * Context: evotestar
 *
 * JSON based test' action selection algorithm.
 * Default behavior: RANDOM (when no proper action can be selected).
 *
 * Based on fraalpe2 TFM @2016.
 *
 * A third party tool is in charge of generating evolutionary algorithms in the form of
 * json individuals. Then, evotestar interprets such an individual and
 * dynamically transforms it into a TESTAR action selection algorithm.
 *
 * @author: urueda
 *
 */
public class JSONWalker extends AbstractWalker {

  // json individuals definitions
  // ############################

  // json structure

  public static final String JSON_STRUCTURE_IF = "IF";
  public static final String JSON_STRUCTURE_THEN = "THEN";

  // number-value TERMINALS

  public static final String JSON_TERMINAL_RND = "RND";                         // a random number
  public static final String JSON_TERMINAL_nActions = "nActions";                     // number of actions available for a state
  public static final String JSON_TERMINAL_nActionsLeftClick = "nActionsLeftClick";   // number of left clicking actions available for a state
  public static final String JSON_TERMINAL_nActionsTypeInto = "nActionsTypeInto";     // number of typing actions available for a state
  public static final String JSON_TERMINAL_nExecutedLeftClick = "nExecutedLeftClick"; // number of left clicking actions executed for a state
  public static final String JSON_TERMINAL_nExecutedTypeInto = "nExecutedTypeInto";   // number of typing actions executed for a state

  // type-value TERMINALS

  public static final String JSON_TERMINAL_tLeftClick = "tLeftClick";        // a type indicating left clicking actions
  public static final String JSON_TERMINAL_tTypeInto = "tTypeInto";         // a type indicating typing actions
  public static final String JSON_TERMINAL_tAny = "tAny";                   // a type indicating any action
  public static final String JSON_TERMINAL_tPreviousAction = "tPreviousAction"; // a type indicating the type of a previously executed action

  // 'if' FUNCTIONS

  public static final String JSON_FUNCTION_LT = "LT";   // 'less' logical operator between numbers
  public static final String JSON_FUNCTION_LE = "LE";   // 'less or equal' logical operator between numbers
  public static final String JSON_FUNCTION_EQ = "EQ";   // 'equal' logical operator between numbers or types
  public static final String JSON_FUNCTION_AND = "AND"; // 'and' logical operator between conditions
  public static final String JSON_FUNCTION_OR = "OR";   // 'or' logical operator between conditions
  public static final String JSON_FUNCTION_NOT = "NOT"; // 'not' logical operator for a condition

  //public static final String JSON_FUNCTION_NEQ = "NEQ"; // unused
  //public static final String JSON_FUNCTION_NAND = "NAND"; // unused
  //public static final String JSON_FUNCTION_NOR = "NOR"; // unused

  // 'then/else' FUNCTIONS

  public static final String JSON_FUNCTION_NULL = "NULL"; // picks a random action

  public static final String JSON_FUNCTION_pickLeastExecuted = "pickLeastExecuted"; // (type,percent): picks the n action from least executed 'type' actions, such that n = list-size * percent and list is ordered from less to more executed
  public static final String JSON_FUNCTION_pickMostExecuted = "pickMostExecuted"; // (type,percent): picks the n action from most executed 'type' actions, such that n = list-size * percent and list is ordered from more to less executed
  public static final String JSON_FUNCTION_pick = "pick"; // (type,percent): picks the n action from 'type' actions list, such that n = list-size * percent

  public static final String JSON_FUNCTION_pickAnyUnexecuted = "pickAnyUnexecuted"; // (type): picks randomly from non executed 'type' actions
  public static final String JSON_FUNCTION_pickDifferentFrom = "pickDifferentFrom"; // (type): picks randomly different from 'type' actions; if 'type' is tAny then RANDOM
  public static final String JSON_FUNCTION_pickSameAs = "pickSameAs"; // (type): picks randomly same as 'type' actions

  // ############################
  // json individuals definitions

  private static enum SORT_ORDER { LEAST, MOST, NONE };

  private final String EVOTESTAR_OUT_TAG = "<evotestar>";

  private Random rnd;
  private List<String> jsonIF;
  private List<String> jsonTHEN;
  private boolean jsonSyntaxValid;

  private IEnvironment env = null;
  private State currentState = null;
  private Set<Action> currentActions = null,
                currentLeftClickActions = null,
                currentTypeIntoActions = null;
  private Action lastAction = null;

  public JSONWalker(Random rnd) {
    this.rnd = rnd;
    scanJSON("individuo.json");
  }

  private void scanJSON(String jsonFilePath) {
    JSONObject jsonObject;
    this.jsonIF = new ArrayList<String>();
    this.jsonTHEN = new ArrayList<String>();
    try {
      @SuppressWarnings("unchecked")
      Iterator<JSONObject> iterator = ((JSONArray) (new JSONParser()).parse(new FileReader(jsonFilePath))).iterator();
      while (iterator.hasNext()) {
        this.jsonSyntaxValid = false;
        jsonObject = iterator.next();
        this.jsonIF.add(((String) jsonObject.get(JSON_STRUCTURE_IF)).replaceAll("'","")); // no quotes
        this.jsonTHEN.add(((String) jsonObject.get(JSON_STRUCTURE_THEN)).replaceAll("'","")); // no quotes
        System.out.println(EVOTESTAR_OUT_TAG + "IF: " + jsonIF);
        System.out.println(EVOTESTAR_OUT_TAG + "THEN: " + jsonTHEN);
        this.jsonSyntaxValid = true;
      }
    } catch (FileNotFoundException e) {
      System.out.println(EVOTESTAR_OUT_TAG + "File <" + jsonFilePath + "> not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println(EVOTESTAR_OUT_TAG + "I/O exception in <" + jsonFilePath + ">");
      e.printStackTrace();
    } catch (ParseException e) {
      System.out.println(EVOTESTAR_OUT_TAG + "Parsing exception in <" + jsonFilePath + ">");
      e.printStackTrace();
    }
  }

  private int getJsonTerminal(String jsonTerminalTag) throws Exception {
    switch (jsonTerminalTag) {
    case JSON_TERMINAL_RND:
      return rnd.nextInt();
    case JSON_TERMINAL_nActions:
      return this.currentActions.size();
    case JSON_TERMINAL_nActionsLeftClick:
      return this.currentLeftClickActions.size();
    case JSON_TERMINAL_nActionsTypeInto:
      return this.currentTypeIntoActions.size();
    case JSON_TERMINAL_nExecutedLeftClick:
      return this.env.getLeftClicks(this.env.get(this.currentState));
    case JSON_TERMINAL_nExecutedTypeInto:
      return this.env.getTypesInto(this.env.get(this.currentState));
    case JSON_TERMINAL_tLeftClick:
      return JSON_TERMINAL_tLeftClick.hashCode();
    case JSON_TERMINAL_tTypeInto:
      return JSON_TERMINAL_tTypeInto.hashCode();
    case JSON_TERMINAL_tAny:
      return JSON_TERMINAL_tAny.hashCode();
    case JSON_TERMINAL_tPreviousAction:
      String previousActionType = getPreviousActionType();
      if (previousActionType != null) {
        switch(previousActionType) {
        case JSON_TERMINAL_tLeftClick:
          return JSON_TERMINAL_tLeftClick.hashCode();
        case JSON_TERMINAL_tTypeInto:
          return JSON_TERMINAL_tTypeInto.hashCode();
        }
      }
      return 0; // default
    default:
      throw new Exception("json format not valid - Unknown terminal tag <" + jsonTerminalTag + ">");
    }
  }

  private boolean processJsonCondition(String condition) throws Exception{
    //System.out.println(EVOTESTAR_OUT_TAG + "processing json condition <" + condition + ">");
    String[] jsonConditionTags = condition.split(",");
    for (int i=0; i<jsonConditionTags.length; i++) {
      String jsonConditionTag = jsonConditionTags[i];
      switch(jsonConditionTag) {
      case JSON_FUNCTION_LT:
        return isLT(getJsonTerminal(jsonConditionTags[i-1]), getJsonTerminal(jsonConditionTags[i+1]));
      case JSON_FUNCTION_LE:
        return isLE(getJsonTerminal(jsonConditionTags[i-1]), getJsonTerminal(jsonConditionTags[i+1]));
      case JSON_FUNCTION_EQ:
        return isEQ(getJsonTerminal(jsonConditionTags[i-1]), getJsonTerminal(jsonConditionTags[i+1]));
      case JSON_TERMINAL_RND:
      case JSON_TERMINAL_nActions:
      case JSON_TERMINAL_nActionsLeftClick:
      case JSON_TERMINAL_nActionsTypeInto:
      case JSON_TERMINAL_nExecutedLeftClick:
      case JSON_TERMINAL_nExecutedTypeInto:
      case JSON_TERMINAL_tLeftClick:
      case JSON_TERMINAL_tTypeInto:
      case JSON_TERMINAL_tAny:
      case JSON_TERMINAL_tPreviousAction:
        break; // expected terminals
      default:
        throw new Exception("json format not valid - Unknown <if> tag <" + jsonConditionTag + ">");
      }
    }
    throw new Exception("json format not valid - Unsatisfied <if> condition <" + condition + ">");
  }

  private boolean processJsonIF(String jsonIF, boolean partialResult) throws Exception{
    //System.out.println(EVOTESTAR_OUT_TAG +  "processing jsonIF: " + jsonIF + " (" + partialResult + ")");
    if (jsonIF == null || jsonIF.isEmpty())
      return partialResult;

    char jsonIFStarting = jsonIF.charAt(0);
    if (jsonIFStarting == ' ')
      return processJsonIF(jsonIF.substring(1), partialResult);
    else if (jsonIFStarting == ',')
      return processJsonIF(jsonIF.substring(1), partialResult);
    else if (jsonIFStarting == '[') {
      String[] split = jsonIF.split("]");
      boolean cond = processJsonCondition(split[0].substring(1).replaceAll(" ", "")); // no spaces
      if (split.length > 1)
        return processJsonIF(jsonIF.substring(jsonIF.indexOf(']')+1), cond);
      else
        return cond;
    } else if (jsonIFStarting == '(') {
      int groupCount = 1;
      for (int i=1; i<jsonIF.length(); i++) { // lets find matching ')'
        if (jsonIF.charAt(i) == '(')
          groupCount++;
        else if (jsonIF.charAt(i) == ')')
          groupCount--;
        if (groupCount == 0) { // found matching ')'
          boolean cond = processJsonIF(jsonIF.substring(1, i), partialResult);
          if (i+1 == jsonIF.length())
            return cond;
          else
            return processJsonIF(jsonIF.substring(i+1), cond);
        }
      }
    } else if (jsonIF.startsWith(JSON_FUNCTION_AND)) {
      return partialResult && processJsonIF(jsonIF.substring(JSON_FUNCTION_AND.length()), partialResult);
    } else if (jsonIF.startsWith(JSON_FUNCTION_OR))
      return partialResult || processJsonIF(jsonIF.substring(JSON_FUNCTION_OR.length()), partialResult);
    else if (jsonIF.startsWith(JSON_FUNCTION_NOT))
      return !processJsonIF(jsonIF.substring(jsonIF.indexOf(',')+1), partialResult);
    throw new Exception("json format not valid - Unexpected <if> condition <" + jsonIF + ">");
  }

  private Action processJsonAction(String jsonAction) throws Exception{
    //System.out.println(EVOTESTAR_OUT_TAG + "processing jsonAction: <" + jsonAction + ">");
    if (jsonAction == null || jsonAction.isEmpty())
      return null; // did not found a suitable action

    char jsonActionStarting = jsonAction.charAt(0);
    if (jsonActionStarting == '[')
      return processJsonAction(jsonAction.substring(1,jsonAction.length()-1));
    else {
      String[] jsonActionTags = jsonAction.replaceAll(" ", "").split(",");
      String jsonFunctionTag = jsonActionTags[0];
      //System.out.println(EVOTESTAR_OUT_TAG + "\tjsonFunction: <" + jsonFunctionTag + ">");
      //System.out.print(EVOTESTAR_OUT_TAG + "\tjsonParameters: ");
      //for (int i=1; i<jsonActionTags.length; i++)
      //  System.out.print("<" + jsonActionTags[i] + ">, ");
      //System.out.print("\n");
      float percent = -1.0f;
      SORT_ORDER sorder = SORT_ORDER.NONE;
      Set<Action> sourceActions;
      String switchJumpTo = jsonFunctionTag;
      do { // switch with goto cases
        switch(switchJumpTo) {
        case JSON_FUNCTION_NULL:
          return new ArrayList<Action>(this.currentActions).get(rnd.nextInt(this.currentActions.size()));
        case JSON_FUNCTION_pickLeastExecuted:
          sorder = SORT_ORDER.LEAST;
          switchJumpTo = JSON_FUNCTION_pick; continue; // goto switch
        case JSON_FUNCTION_pickMostExecuted:
          sorder = SORT_ORDER.MOST;
          switchJumpTo = JSON_FUNCTION_pick; continue; // goto switch
        case JSON_FUNCTION_pick:
          try {
            percent = new Float(jsonActionTags[2]).floatValue();
          } catch(java.lang.NumberFormatException nfe) {
            System.out.println(EVOTESTAR_OUT_TAG + "Not a 0..1 range <" + jsonActionTags[2] + ">");
          }
          switchJumpTo = JSON_FUNCTION_pickSameAs; continue; // goto switch
        case JSON_FUNCTION_pickSameAs:
        case JSON_FUNCTION_pickDifferentFrom:
          sourceActions = getSourceActions(jsonActionTags[1], switchJumpTo.equals(JSON_FUNCTION_pickDifferentFrom));
          if (sourceActions != null)
            return pick(sourceActions,percent,sorder);
          break;
        case JSON_FUNCTION_pickAnyUnexecuted:
          sourceActions = getSourceActions(jsonActionTags[1], false);
          if (sourceActions != null && !sourceActions.isEmpty()) {
            IGraphState gs = this.env.get(this.currentState);
            Collection<GraphEdge> execStateEdges = env.getOutgoingActions(gs);
            if (execStateEdges != null && !execStateEdges.isEmpty()) {
              Set<String> execStateActions = new HashSet<String>();
              for (GraphEdge edge: execStateEdges)
                execStateActions.add(edge.getActionID());
              Set<Action> unexecutedActions = new HashSet<Action>();
              String targetW;
              for (Action a: sourceActions) {
                targetW = this.env.get(a).getTargetWidgetID();
                if (targetW != null) {
                  Integer tc = gs.getStateWidgetsExecCount().get(targetW);
                  if (tc != null) {
                    if (tc.intValue() > 9) // todo: apply -DTT parameter
                      continue; // e.g. prevent too many typing actions with different texts
                  }
                }
                if (!execStateActions.contains(a.get(Tags.ConcreteID)))
                  unexecutedActions.add(a);
              }
              if (!unexecutedActions.isEmpty())
                return new ArrayList<Action>(unexecutedActions).get(rnd.nextInt(unexecutedActions.size()));
            } else
              return new ArrayList<Action>(sourceActions).get(rnd.nextInt(sourceActions.size()));
          }
          break;
        default:
          throw new Exception("json format not valid - Unknown <then/else> tag <" + jsonFunctionTag + ">");
        }
        break; // switch completed, break the loop
      } while (true); // switch with goto cases
    }
    return null; // did not found a suitable action
  }

  private Set<Action> getSourceActions(String actionFunctionTag, boolean differentFrom) {
    Set<Action> sourceActions = null;
    if (actionFunctionTag.equals(JSON_TERMINAL_tLeftClick)) {
      sourceActions = differentFrom ? getComplement(this.currentActions,this.currentLeftClickActions): this.currentLeftClickActions;
    } else if (actionFunctionTag.equals(JSON_TERMINAL_tTypeInto)) {
      sourceActions = differentFrom ? getComplement(this.currentActions,this.currentTypeIntoActions): this.currentTypeIntoActions;
    } else if (actionFunctionTag.equals(JSON_TERMINAL_tAny)) { // differentFrom -> non sense (would be empty set)
      sourceActions = this.currentActions;
    } else if (actionFunctionTag.equals(JSON_TERMINAL_tPreviousAction)) {
      String previousActionType = getPreviousActionType();
      if (previousActionType != null) {
        switch(previousActionType) {
        case JSON_TERMINAL_tLeftClick:
          sourceActions = differentFrom ? getComplement(this.currentActions,this.currentLeftClickActions): this.currentLeftClickActions;
          break;
        case JSON_TERMINAL_tTypeInto:
          sourceActions = differentFrom ? getComplement(this.currentActions,this.currentTypeIntoActions):this.currentTypeIntoActions;
          break;
        default:
          sourceActions = this.currentActions;
        }
      }
    }
    return sourceActions;
  }

  private Set<Action> getComplement(Set<Action> fullset, Set<Action> subset) {
    if (subset == null || subset.isEmpty())
      return fullset;
    Set<Action> compleset = new HashSet<Action>(fullset);
    compleset.removeAll(subset);
    return compleset;
  }

  private void scanActionsTypes(Set<Action> actions) {
    Role ar;
    for (Action a: actions) {
      ar = a.get(Tags.Role, null);
      if (ar != null) {
        if (ar.name().equals(ActionRoles.LeftClick.name()) ||
          ar.name().equals(ActionRoles.LeftClickAt.name())) {
          this.currentLeftClickActions.add(a);
        } else if (ar.name().equals(ActionRoles.ClickTypeInto.name()) ||
               ar.name().equals(ActionRoles.Type.name())) {
          this.currentTypeIntoActions.add(a);
        }
      }
    }
  }

  @Override
  public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
    this.env = env;
    this.currentState = state;
    this.currentActions = actions;
    this.currentLeftClickActions = new HashSet<Action>();
    this.currentTypeIntoActions = new HashSet<Action>();

    Action selectedAction = null;

    if (this.jsonSyntaxValid) {
      int rulesCount = this.jsonIF.size(), // == this.jsonTHEN.size()
        ruleIndex = 0;
      String ruleIF, ruleTHEN;
      do {
        ruleIF = this.jsonIF.get(ruleIndex);
        ruleTHEN = this.jsonTHEN.get(ruleIndex);
        try {
          scanActionsTypes(actions);
          boolean jsonConditionValue = processJsonIF(ruleIF,true); // true = neutral boolean
          System.out.println(EVOTESTAR_OUT_TAG + "json <IF> evalutes to: " + jsonConditionValue);
          selectedAction = (jsonConditionValue ? processJsonAction(ruleTHEN): null);
          if (selectedAction != null)
            System.out.println(EVOTESTAR_OUT_TAG + "json <action> selected: " + selectedAction.get(Tags.ConcreteID));
        } catch (PatternSyntaxException pse) {
          System.out.println(EVOTESTAR_OUT_TAG + "json pattern syntax exception selecting action: " + pse.getMessage());
          selectedAction = null;
        } catch (Exception e) {
          System.out.println(EVOTESTAR_OUT_TAG + "json exception selecting action: " + e.getMessage());
          e.printStackTrace(); // debug
          selectedAction = null;
        }
        ruleIndex++;
      } while (selectedAction == null && ruleIndex < rulesCount);
    }

    if (selectedAction == null) { // json individual unable to select an action
      //selectedAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE); // (ESC should penalize its fitness, but NOP could be better)
      //CodingManager.buildIDs(this.currentState, selectedAction);
      //System.out.println(EVOTESTAR_OUT_TAG + "json <action> = <ESC>");
      selectedAction = new ArrayList<Action>(this.currentActions).get(rnd.nextInt(this.currentActions.size())); // default: random
      System.out.println(EVOTESTAR_OUT_TAG + "json <action> selected: <RANDOM>");
    }
    this.lastAction = selectedAction;
    return selectedAction;
  }

  private boolean isLT(int leftValue, int rightValue) {
    if (leftValue < rightValue)
      return true;
    else
      return false;
  }

  private boolean isLE(int leftValue, int rightValue) {
    if (leftValue <= rightValue)
      return true;
    else
      return false;
  }

  private boolean isEQ(int leftValue, int rightValue) {
    if (leftValue == rightValue)
      return true;
    else
      return false;
  }

  private String getPreviousActionType() {
    if (this.lastAction != null) {
      Role r = this.lastAction.get(Tags.Role, null);
      if (r != null) {
        if (r.name().equals(ActionRoles.LeftClick.name()) ||
          r.name().equals(ActionRoles.LeftClickAt.name())) {
          return JSON_TERMINAL_tLeftClick;
        } else if (r.name().equals(ActionRoles.ClickTypeInto.name()) ||
               r.name().equals(ActionRoles.Type.name())) {
          return JSON_TERMINAL_tTypeInto;
        }
      }
    }
    return null;
  }

  // percent: range 0.0 .. 1.0 (otherwise => random)
  private Action pick(Set<Action> sourceActions, float percent, SORT_ORDER sorder) {
    if (sourceActions.isEmpty())
      return null; // did not find a suitable action
    if (percent < 0.0f || percent > 1.0f) {
      System.out.println(EVOTESTAR_OUT_TAG + "Action range <" + percent + "> out of scope");
      return new ArrayList<Action>(sourceActions).get(rnd.nextInt(sourceActions.size())); // random
    }
    return sort(sourceActions,sorder)[Math.round(((float)sourceActions.size()-1.0f)*percent)];
  }

  private Action[] sort(Set<Action> actions, SORT_ORDER sorder) {
    Action[] sortedActions = actions.toArray(new Action[actions.size()]);
    if (sorder == SORT_ORDER.NONE) // nothing to sort
      return sortedActions;
    Arrays.sort(sortedActions, new Comparator<Action>() {
      @Override
      public int compare(Action a1, Action a2) { // -1 => a1, +1 => a2
        IGraphAction ga1 = env.get(a1),
               ga2 = env.get(a2);
        if (ga1 == null && ga2 == null)
          return 0;
        else if (ga1 == null)
          return sorder == SORT_ORDER.LEAST ? -1: 1 ;
        else if (ga2 == null)
          return sorder == SORT_ORDER.LEAST ? 1: -1 ;
        else { // ga1 && ga2 != null
          int e1 = ga1.getCount(),
            e2 = ga2.getCount();
          if (e1 == e2)
            return 0;
          else if (e1 < e2)
            return sorder == SORT_ORDER.LEAST ? -1: 1 ;
          else // e1 > e2
            return sorder == SORT_ORDER.LEAST ? 1: -1 ;
        }
      }
    });
    return sortedActions;
  }

}
