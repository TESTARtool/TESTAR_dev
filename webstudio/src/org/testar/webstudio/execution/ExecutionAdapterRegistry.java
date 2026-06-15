/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ExecutionAdapterRegistry {

    private final Map<ExecutionBackend, ExecutionAdapter> adapters;

    public ExecutionAdapterRegistry(Collection<ExecutionAdapter> adapters) {
        this.adapters = new EnumMap<>(ExecutionBackend.class);
        for (ExecutionAdapter adapter : adapters) {
            this.adapters.put(adapter.backend(), adapter);
        }
    }

    public ExecutionAdapter adapterFor(ExecutionBackend backend) {
        ExecutionAdapter adapter = adapters.get(backend);
        if (adapter == null) {
            throw new IllegalArgumentException("No execution adapter configured for backend=" + backend.id());
        }
        return adapter;
    }

    public List<ExecutionBackend> availableBackends() {
        return new ArrayList<>(adapters.keySet());
    }
}
