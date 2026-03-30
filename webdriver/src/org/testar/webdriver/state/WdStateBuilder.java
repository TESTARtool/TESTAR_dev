/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.testar.core.Assert;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.alayer.Roles;
import org.testar.core.state.SUT;
import org.testar.core.state.StateBuilder;
import org.testar.core.tag.Tags;

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
      WdState state = future.get((long) (timeOut), TimeUnit.SECONDS);
      // When the SUT has a valid windowHandle store it in the state, it's required to create well aligned screenshots.
      if (system.get(Tags.HWND, null) != null){
        state.set(Tags.HWND, system.get(Tags.HWND));
      }
      return state;
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
