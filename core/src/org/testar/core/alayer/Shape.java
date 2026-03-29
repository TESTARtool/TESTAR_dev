/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.io.Serializable;

public interface Shape extends Serializable {
    double x();
    double y();
    double width();
    double height();
    boolean contains(double x, double y);
    void paint(Canvas canvas, Pen pen);
}
