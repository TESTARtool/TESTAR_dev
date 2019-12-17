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

package org.fruit.alayer.webdriver;

import org.fruit.Assert;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;

import java.util.concurrent.*;

public class WdStateBuilder implements StateBuilder {
  private static final long serialVersionUID = -8640937524707589772L;

  private static final int defaultThreadPoolCount = 1;

  private final double timeOut;
  private transient ExecutorService executor;

  public WdStateBuilder(double timeOut) {
    Assert.isTrue(timeOut > 0);
    this.timeOut = timeOut;

    // Needed to be able to schedule asynchronous tasks conveniently.
    executor = Executors.newFixedThreadPool(defaultThreadPoolCount);
  }

  @Override
  public WdState apply(SUT system) throws StateBuildException {
    try {
      Future<WdState> future = executor.submit(new WdStateFetcher(system));
      return future.get((long) (timeOut), TimeUnit.SECONDS);
    }
    catch (InterruptedException | ExecutionException e) {
    	e.printStackTrace();
      throw new StateBuildException(e.getMessage());
    }
    catch (TimeoutException e) {
      WdRootElement wdRootElement = WdStateFetcher.buildRoot(system);
      WdState wdState = new WdState(wdRootElement);
      wdState.set(Tags.Role, Roles.Process);
      wdState.set(Tags.NotResponding, true);
      return wdState;
    }
  }
}
