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

import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;

public interface IGraphState {

  //public State getState();

  public String getConcreteID();
  public String getAbstract_R_ID();
  public String getAbstract_R_T_ID();
  public String getAbstract_R_T_P_ID();

  public Map<String,Integer> getStateWidgetsExecCount();
  public String getParent(String widgetID);
  public Map<String,String> getWidgetProperties(String widgetID);

  public void setStateshot(String scrShotPath);
  public String getStateshot();

  public int getCount();
  public void setCount(int count);
  public void incCount();

  public void updateUnexploredActions(IEnvironment env,
                    Set<Action> availableActions,
                    Set<String> exploredActions);

  public int getUnexploredActionsSize();
  public String getUnexploredActionsString();

  /**
   *
   * @param aid Abstract action ID.
   */
  public void actionExplored(String aid);
  /**
   *
   * @param aid Abstract action ID.
   */
  public void actionUnexplored(String aid);

  public Verdict getVerdict();

  public void actionExecuted(String targetWidgetID);

  public void knowledge(boolean k);
  public boolean knowledge();
  public void revisited(boolean r);
  public boolean revisited();

}
