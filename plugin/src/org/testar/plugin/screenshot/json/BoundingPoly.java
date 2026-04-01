/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot.json;

public class BoundingPoly {
    Vertice[] vertices;

    public BoundingPoly(Vertice[] vertices) {
        this.vertices = vertices;
    }
}
