/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.coverage;

public interface CodeCoverage {

	public void getSequenceCoverage();
	public void getActionCoverage(String actionCount);

}
