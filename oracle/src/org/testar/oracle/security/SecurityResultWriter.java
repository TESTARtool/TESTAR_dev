/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

public interface SecurityResultWriter {
    void WriteVisit(String url);
    void WriteResult(String url, String cwe, String description);
}
