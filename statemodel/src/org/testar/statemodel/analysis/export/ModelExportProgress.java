/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.export;

/** Counts completed preparation work; a zero total denotes an indeterminate phase. */
public record ModelExportProgress(String phase, int completed, int total) { }
