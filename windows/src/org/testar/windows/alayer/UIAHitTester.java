/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.alayer;

import org.testar.core.alayer.HitTester;
import org.testar.windows.state.UIAElement;

public final class UIAHitTester implements HitTester {
    private static final long serialVersionUID = 1134479951851719957L;
    private final UIAElement el;
    public UIAHitTester(UIAElement el){    this.el = el; }
    public String toString(){ return "UIAHitTester"; }
    public boolean apply(double x, double y) { return el.root.visibleAt(el, x, y); }
    
    // begin by urueda
    
    @Override
    public boolean apply(double x, double y, boolean obscuredByChildFeature) {
        return el.root.visibleAt(el, x, y, obscuredByChildFeature);
    }
    
    // end by urueda
    
}
