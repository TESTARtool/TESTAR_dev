/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.process.ProcessHandle;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

public abstract class SUTBase implements SUT {

    private Map<Tag<?>, Object> tagValues = Util.newHashMap();
    boolean allFetched;

    protected AutomationCache nativeAutomationCache = null; // by urueda

    @Override
    public AutomationCache getNativeAutomationCache() {
        return this.nativeAutomationCache;
    }
    
    public final <T> T get(Tag<T> tag) throws NoSuchTagException {
        T ret = get(tag, null);
        if (ret == null) {
            throw new NoSuchTagException(tag);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public final <T> T get(Tag<T> tag, T defaultValue) {
        Assert.notNull(tag);
        T ret = (T) tagValues.get(tag);
        if (ret == null && !tagValues.containsKey(tag)) {
            ret = fetch(tag);
        }
        return ret == null ? defaultValue : ret;
    }

    public final Iterable<Tag<?>> tags() {
        Set<Tag<?>> domain = Util.newHashSet();
        domain.addAll(tagDomain());
        domain.addAll(tagValues.keySet());
        Set<Tag<?>> ret = Util.newHashSet();

        for (Tag<?> t : domain) {
            if (tagValues.containsKey(t)) {
                if (tagValues.get(t) != null) {
                    ret.add(t);
                }
            } else {
                if (fetch(t) != null) {
                    ret.add(t);
                }
            }
        }
        return ret;
    }

    protected <T> T fetch(Tag<T> tag) {
        return null;
    }

    protected Set<Tag<?>> tagDomain() {
        return Collections.emptySet();
    }

    public <T> void set(Tag<T> tag, T value) {
        Assert.notNull(tag, value);
        Assert.isTrue(tag.type().isInstance(value), "Value not of type required by this tag!");
        tagValues.put(tag, value);
    }

    public void remove(Tag<?> tag) {
        tagValues.put(Assert.notNull(tag), null);
    }

    /**
     * Retrieves the running processes.
     * @return A list of pairs &lt;PID,NAME&gt; with the PID/NAME of running processes.
     */
    @Override
    public List<Pair<Long, String>> getRunningProcesses() {
        List<Pair<Long, String>> runningProcesses = Util.newArrayList();
        for (ProcessHandle ph : Util.makeIterable(this.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))) {
            runningProcesses.add(Pair.from(ph.pid(), ph.name()));
        }
        return runningProcesses;
    }
}
