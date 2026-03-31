/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testar.android.AndroidAppiumFramework;
import org.testar.core.Assert;
import org.testar.core.alayer.*;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.core.tag.Tags;


public class AndroidStateBuilder implements StateBuilder {
	private static final long serialVersionUID = -4016081519369476126L;

	private static final int defaultThreadPoolCount = 1;
	private final double timeOut;
	private transient ExecutorService executor;

	public AndroidStateBuilder(double timeOut) {
		Assert.isTrue(timeOut > 0);
		this.timeOut = timeOut;

		// Needed to be able to schedule asynchronous tasks conveniently.
		executor = Executors.newFixedThreadPool(defaultThreadPoolCount);
	}

	@Override
	public State apply(SUT system) throws StateBuildException {
		try {
			// If the driver became unresponsive during non-state fetcher calls like actions or logact
			if (AndroidAppiumFramework.isDriverUnresponsive()) {
				AndroidAppiumFramework.resetDriverUnresponsive();
				AndroidRootElement rootElement = AndroidStateFetcher.buildRoot(system);
				AndroidState androidState = new AndroidState(rootElement);
				androidState.set(Tags.Role, Roles.Process);
				androidState.set(Tags.NotResponding, true);
				return androidState;
			}

			Future<AndroidState> future = executor.submit(new AndroidStateFetcher(system));
			AndroidState state = future.get((long) (timeOut), TimeUnit.SECONDS);

			// If the driver became unresponsive during state fetch calls
			if (AndroidAppiumFramework.isDriverUnresponsive()) {
				AndroidAppiumFramework.resetDriverUnresponsive();
				AndroidRootElement rootElement = AndroidStateFetcher.buildRoot(system);
				AndroidState androidState = new AndroidState(rootElement);
				androidState.set(Tags.Role, Roles.Process);
				androidState.set(Tags.NotResponding, true);
				return androidState;
			}

			return state;
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new StateBuildException(e.getMessage());
		}
		catch (TimeoutException e) {
			AndroidRootElement rootElement = AndroidStateFetcher.buildRoot(system);
			AndroidState androidState = new AndroidState(rootElement);
			androidState.set(Tags.Role, Roles.Process);
			androidState.set(Tags.NotResponding, true);

			return androidState;
		}
	}
}
