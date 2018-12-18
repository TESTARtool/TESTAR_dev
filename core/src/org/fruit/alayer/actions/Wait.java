/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.actions;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;

/**
 * An action that simply waits a given amount of seconds.
 */
public final class Wait extends TaggableBase implements Action {

  private static final long serialVersionUID = 8248189921206790701L;
  private final double waitTime;
  private final boolean oveheadDuration;

  public Wait(double waitTime) { this(waitTime, false); }

  public Wait(double waitTime, boolean overheadDuration) {
    Assert.isTrue(waitTime >= 0);
    this.oveheadDuration = overheadDuration;
    this.waitTime = waitTime;
  }

  public void run(SUT system, State state, double duration) {
    Assert.isTrue(duration >= 0);
    Util.pause(waitTime);
    if (!oveheadDuration)
      Util.pause(Math.max(0, waitTime - duration));  // sleep the rest of the time
  }

  public String toString() {
    return "Wait for " + (oveheadDuration ? "exactly ": "") + waitTime + " seconds";
  }

  // by urueda
  @Override
  public String toString(Role... discardParameters) {
    return toString();
  }

  // by urueda
  @Override
  public String toShortString() {
    Role r = get(Tags.Role, null);
    if (r != null)
      return r.toString();
    else
      return toString();
  }

  // by urueda
  @Override
  public String toParametersString() {
    return "(" + waitTime + ")";
  }

}
