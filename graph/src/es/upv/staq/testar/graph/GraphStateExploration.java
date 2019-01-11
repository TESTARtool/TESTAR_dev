/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

package es.upv.staq.testar.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;

/**
 * Graph state exploration status.
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphStateExploration {

  private Set<String> unexploredActions; // actions IDs (abstract)

  // textbox fields => typing actions with oo texts parameter => huge set population
  private Map<String,Integer> unexploredTypings; // action ID (abstract) x typed count

  public GraphStateExploration() {
    this.unexploredActions = new HashSet<String>();
    this.unexploredTypings = new HashMap<String,Integer>();
  }

  public void updateUnexploredActions(IEnvironment env, IGraphState gs,
                    Set<Action> availableActions, Set<String> exploredActions) {
    String aid;
    Role role;
    synchronized(this.unexploredActions) {
      for (Action a: availableActions) {
        aid = a.get(Tags.AbstractID);
        role = a.get(Tags.Role, null);
        if (role != null && Role.isOneOf(role, ActionRoles.Type, ActionRoles.ClickTypeInto)) { // typing action
          if (this.unexploredTypings.get(aid) == null)
            this.unexploredTypings.put(aid,new Integer(0));
          else
            continue; // skip loop action
        } else if (exploredActions.contains(aid))
          continue; // skip explored action
        this.unexploredActions.add(aid);
      }
    }
  }

  /**
   *
   * @param aid Abstract action ID.
   */
  public void actionExplored(String aid) {
    synchronized(this.unexploredActions) {
      Integer typedCount = this.unexploredTypings.get(aid);
      if (typedCount == null)
        this.unexploredActions.remove(aid);
      else {
        if (typedCount.intValue() + 1 >= Grapher.TYPING_TEXTS_FOR_EXECUTED_ACTION)
          this.unexploredActions.remove(aid);
        else
          this.unexploredTypings.put(aid, new Integer(typedCount.intValue() + 1));
      }
    }
  }

  /**
   *
   * @param aid Abstract action ID.
   */
  public void actionUnexplored(String aid) {
    synchronized(this.unexploredActions) {
      this.unexploredActions.add(aid);
    }
  }

  public int getUnexploredActionsSize() {
    synchronized(this.unexploredActions) {
      return this.unexploredActions.size();
    }
  }

  public String getUnexploredActionsString() {
    synchronized(this.unexploredActions) {
      if (this.unexploredActions.isEmpty())
        return "[]";
      else {
        StringBuilder sb = new StringBuilder();
        for (String ua: this.unexploredActions) {
          sb.append(ua + ",");
        }
        return "[" + sb.toString().substring(0,sb.length()-1) + "]";
      }
    }
  }

}
