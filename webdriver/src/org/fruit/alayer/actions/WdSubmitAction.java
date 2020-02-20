/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.fruit.alayer.actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdSubmitAction extends TaggableBase implements Action {
  private String formId;

  public WdSubmitAction(String formId) {
    this.formId = formId;
    this.set(Tags.Role, WdActionRoles.SubmitScript);
    this.set(Tags.Desc, "Execute Webdriver script to submit an action into " + formId);
  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    String form = String.format("document.getElementById('%s')", formId);
    try {
      WdDriver.executeScript(String.format("%s.submit();", form));
    }
    catch (Exception wde) {
      // The form can not be found by id, let's try by name
      if (wde.getMessage().contains("Cannot read property 'submit' of null")) {
        form = String.format("document.getElementsByName('%s')[0]", formId);
        WdDriver.executeScript(String.format("%s.submit();", form));
      }
      // Let's try by clicking on the submit button
      else if (wde.getMessage().contains("submit is not a function")) {
        WdDriver.executeScript(String.format(
            "%s.querySelector('input[type=\"submit\"]').click();", form));
      }
    }
  }

  @Override
  public String toShortString() {
    return "Submit form : '" + formId + "'";
  }

  @Override
  public String toParametersString() {
    return toShortString();
  }

  @Override
  public String toString(Role... discardParameters) {
    return toShortString();
  }
}
