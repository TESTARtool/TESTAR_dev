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
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.PositionException;

public final class MouseMove extends TaggableBase implements Action {

  private static final long serialVersionUID = 3689287467588080030L;
  private final Position position;
  private final double minDuration;

  public MouseMove(Point point) {
    this(new AbsolutePosition(point), 0);
  }

  public MouseMove(double x, double y) {
    this(new AbsolutePosition(x, y), 0);
  }

  public MouseMove(Position position) { this(position, 0); }

  public MouseMove(Position position, double minDuration) {
    Assert.notNull(position);
    Assert.isTrue(minDuration >= 0);
    this.position = position;
    this.minDuration = minDuration;
  }

  public String toString() {
    return "Move mouse to " + position.toString() + ".";
  }

  // by urueda
  @Override
  public String toString(Role... discardParameters) {
    for (Role r: discardParameters) {
      if (r.name().equals(ActionRoles.MouseMove.name()))
        return "Mouse moved";
    }
    return toString();
  }

  public void run(SUT system, State state, double duration) {
    try {
      Assert.notNull(system, state);
      Point p = position.apply(state);
      Util.moveCursor(system.get(Tags.StandardMouse), p.x(), p.y(), Math.max(duration, minDuration));
    } catch(NoSuchTagException tue) {
      throw new ActionFailedException(tue);
    } catch(PositionException pe) {
      throw new ActionFailedException(pe);
    }
  }

  // by urueda
  @Override
  public String toShortString() {
    Role r = get(Tags.Role, null);
    if (r != null)
      return r.toString() + toParametersString();
    else
      return toString();
  }

  // by urueda
  @Override
  public String toParametersString() {
    //return position.toString();
    return "";
  }
}
