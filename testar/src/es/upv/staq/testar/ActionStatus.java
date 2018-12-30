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


package es.upv.staq.testar;

import org.fruit.alayer.Action;

/**
 * UI action status information.
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ActionStatus {

  private Action action;

  private boolean actionSucceeded,
          problems,
          userEventAction;

  /**
   * Constructor.
   */
  public ActionStatus() {
    this.action = null;
    this.actionSucceeded = true;
    this.problems = false;
    this.userEventAction = false;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public boolean isActionSucceeded() {
    return actionSucceeded;
  }

  public boolean setActionSucceeded(boolean actionSucceeded) {
    this.actionSucceeded = actionSucceeded;
    return actionSucceeded;
  }

  public boolean isProblems() {
    return problems;
  }

  public void setProblems(boolean problems) {
    this.problems = problems;
  }

  public boolean isUserEventAction() {
    return userEventAction;
  }

  public void setUserEventAction(boolean userEventAction) {
    this.userEventAction = userEventAction;
  }

}
