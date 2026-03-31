/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.alayer;

import org.testar.android.state.AndroidElement;
import org.testar.core.alayer.HitTester;

public class AndroidHitTester implements HitTester {
	private static final long serialVersionUID = -5963729249658717638L;

	private final AndroidElement element;

	public AndroidHitTester(AndroidElement element) {
		this.element = element;
	}

	@Override
	public boolean apply(double x, double y) {
		return element.root.visibleAt(element, x, y);
	}

	@Override
	public boolean apply(double x, double y, boolean obscuredByChildFeature) {
		return element.root.visibleAt(element, x, y, obscuredByChildFeature);
	}

}
