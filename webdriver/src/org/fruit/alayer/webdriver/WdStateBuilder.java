package org.fruit.alayer.webdriver;

import org.fruit.Assert;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;

import java.util.concurrent.*;

public class WdStateBuilder implements StateBuilder {
  private static final long serialVersionUID = -8640937524707589772L;

  private static final int _defaultThreadPoolCount = 1;

  private final double timeOut;
  private transient ExecutorService executor;

  public WdStateBuilder(double timeOut) {
    Assert.isTrue(timeOut > 0);
    this.timeOut = timeOut;

    // Needed to be able to schedule asynchornous tasks conveniently.
    executor = Executors.newFixedThreadPool(_defaultThreadPoolCount);
  }

  @Override
  public State apply(SUT system) throws StateBuildException {
    try {
      Future<WdState> future = executor.submit(new WdStateFetcher(system));
      return future.get((long) (timeOut), TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      throw new StateBuildException(e);
    }
    catch (ExecutionException e) {
      e.printStackTrace();
      throw new StateBuildException(e);
    }
    catch (TimeoutException e) {
      // TODO
      System.out.println();
      e.printStackTrace();
      System.out.println();
      Utils.logAndEnd();

      WdState ret = new WdState(WdStateFetcher.buildRoot(system));
      ret.set(Tags.Role, Roles.Process);
      ret.set(Tags.NotResponding, true);
      return ret;
    }
  }
}
