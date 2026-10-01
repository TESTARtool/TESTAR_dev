/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import org.testar.android.AndroidAppiumFramework;
import org.testar.core.Assert;
import org.testar.core.alayer.Roles;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.core.tag.Tags;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            // If the driver became unresponsive during non-state fetcher calls like actions or logcat
            if (AndroidAppiumFramework.isDriverUnresponsive()) {
                AndroidAppiumFramework.resetDriverUnresponsive();
                return buildNotRespondingState("");
            }

            Future<AndroidState> future = executor.submit(new AndroidStateFetcher(system));
            AndroidState state = future.get((long) (timeOut), TimeUnit.SECONDS);

            // If the driver became unresponsive during state fetch calls
            if (AndroidAppiumFramework.isDriverUnresponsive()) {
                String stateFeedback = state.get(Tags.StateFeedback, "");
                AndroidAppiumFramework.resetDriverUnresponsive();
                return buildNotRespondingState(stateFeedback);
            }

            return state;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new StateBuildException(e.getMessage());
        } catch (TimeoutException e) {
            return buildNotRespondingState("");
        }
    }

    private AndroidState buildNotRespondingState(String feedback) {
        AndroidRootElement rootElement = new AndroidRootElement();
        rootElement.timeStamp = System.currentTimeMillis();
        rootElement.pid = -1;
        rootElement.isRunning = false;
        rootElement.isForeground = false;

        AndroidState androidState = new AndroidState(rootElement);
        androidState.set(Tags.Role, Roles.Process);
        androidState.set(Tags.NotResponding, true);
        if (feedback != null && !feedback.isEmpty()) {
            androidState.set(Tags.StateFeedback, feedback);
        }
        return androidState;
    }
}
