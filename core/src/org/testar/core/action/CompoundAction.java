/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

/**
 * An action that is composed of several other actions.
 */
public final class CompoundAction extends TaggableBase implements Action {

    private static final long serialVersionUID = -5836624942752268573L;
    private final List<Action> actions;
    private final List<Double> relativeDurations;

    public static final class Builder {
        private List<Double> relativeDurations = Util.newArrayList();
        private List<Action> actions = Util.newArrayList();
        double durationSum = 0.0;

        public Builder add(Action a, double relativeDuration) {
            Assert.notNull(a);
            Assert.isTrue(relativeDuration >= 0);
            relativeDurations.add(relativeDuration);
            actions.add(a);
            durationSum += relativeDuration;
            return this;
        }

        public CompoundAction build() {
            Assert.isTrue(durationSum > 0.0, "Sum of durations needs to be larger than 0!");

            // normalize
            for (int i = 0; i < relativeDurations.size(); i++) {
                relativeDurations.set(i, relativeDurations.get(i) / durationSum);
            }
            return new CompoundAction(this);
        }

        public CompoundAction build(Widget originWidget) {
            CompoundAction compoundAction = build();
            compoundAction.mapOriginWidget(originWidget);
            return compoundAction;
        }
    }

    private CompoundAction(Builder b) {
        this.actions = b.actions;
        this.relativeDurations = b.relativeDurations;
        this.set(Tags.Role, ActionRoles.CompoundAction);

        int index = 1;
        StringBuilder sbDesc = new StringBuilder("");
        for (Action a : actions) {
            sbDesc.append("[(Action n" + (index++) + ") " + a.get(Tags.Desc,"") + "] ");
        }
        this.set(Tags.Desc, sbDesc.toString());
    }

    public CompoundAction(Action... actions) {
        Assert.notNull((Object) actions);
        this.actions = Arrays.asList(actions);
        this.relativeDurations = Util.newArrayList();

        for (int i = 0; i < actions.length; i++) {
            relativeDurations.add(1.0 / actions.length);
        }
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void run(SUT system, State state, double duration) {
        for (int i = 0; i < actions.size(); i++) {
            actions.get(i).run(system, state, relativeDurations.get(i) * duration);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Compound Action =");
        for (Action a : actions) {
            sb.append(Util.lineSep()).append(a.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString(Role... discardParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("Compound Action =");
        for (Action a : actions) {
            sb.append(Util.lineSep()).append(a.toString(discardParameters));
        }
        return sb.toString();
    }

    @Override
    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        Role r = get(Tags.Role, null);
        if (r != null) {
            sb.append(r.toString());
        } else {
            sb.append("UNDEF");
        }
        HashSet<String> parameters = new HashSet<>();
        for (Action a : actions) {
            parameters.add(a.toParametersString());
        }
        for (String p : parameters) {
            sb.append(p);
        }
        return sb.toString();
    }

    @Override
    public String toParametersString() {
        StringBuilder params = new StringBuilder("");
        for (Action a : actions) {
            params.append(a.toParametersString());
        }
        return params.toString();
    }
}
