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
package org.fruit.alayer;

import java.util.Arrays;

import org.fruit.Assert;
import org.fruit.UnFunc;
import org.fruit.alayer.exceptions.WidgetNotFoundException;

public final class IndexFinder implements Searcher, Finder {
  private static final long serialVersionUID = 2879822217515069377L;
  final int[] indices;
  transient YieldFirst yf;

  public IndexFinder(int[] indices) {
    Assert.notNull(indices);
    this.indices = indices;
  }

  public SearchFlag apply(Widget start, UnFunc<Widget, SearchFlag> visitor) {
    Assert.notNull(start, visitor);
    Widget current = start;
    for (int idx: indices) {
      if (idx >= 0 && idx < current.childCount()) {
        current = current.child(idx);
      } else {
        return SearchFlag.OK;
      }
    }
    return visitor.apply(current);
  }

  public String toString() {
    return "IndexSearcher: " + Arrays.toString(indices);
  }

  public Widget apply(Widget start) throws WidgetNotFoundException {
    if (yf == null) {
      yf = new YieldFirst();
    }
    apply(start, yf);
    return yf.result();
  }

  @Override
  public Widget getCachedWidget() {
    // TODO Auto-generated method stub
    return null;
  }
}
