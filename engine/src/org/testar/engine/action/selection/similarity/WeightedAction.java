/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testar.core.action.Action;

// https://gamedev.stackexchange.com/questions/162976
public class WeightedAction {

    private class Entry {
        double accumulatedWeight;
        Action action;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private Random rand = new Random();

    public void addEntry(Action action, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.action = action;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public Action getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.action;
            }
        }
        return null; //should only happen when there are no entries
    }
}
