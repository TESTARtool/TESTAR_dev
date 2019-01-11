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

import java.util.Set;

/**
 * Graph action/edge.
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public interface IGraphAction {

  //public Action getAction();

  public String getConcreteID();
  public String getAbstractID();

  public void setStateshot(String scrShotPath);
  public String getStateshot();

  /**
   * @param memUsage In KB.
   */
  public void setMemUsage(int memUsage);
  public int getMemUsage();

  /**
   * @param cpuUsage User x system ... In ms.
   */
  public void setCPUsage(long[] cpuUsage);
  public long[] getCPUsage();

  public String getRole();

  public String getDetailedName();
  public void setDetailedName(String detailedName);

  public String getTargetWidgetID();
  public void setTargetWidgetID(String targetWidgetID);

  public String getSourceStateID();
  public void setSourceStateID(String SourceStateID);
  public Set<String> getTargetStateIDs();
  public void addTargetStateID(String targetStateID);

  public int getCount();
  public void setCount(int count);
  public void incCount();

  public String getOrder(String targetStateID);
  public String getOrder(Set<String> targetStatesID);
  public void addOrder(String targetStateID, String order);
  public String getLastOrder(String targetStateID);

  public void knowledge(boolean k);
  public boolean knowledge();
  public void revisited(boolean r);
  public boolean revisited();

}
