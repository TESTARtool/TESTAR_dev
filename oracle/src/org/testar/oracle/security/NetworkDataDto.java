/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import java.util.Map;

public class NetworkDataDto {
    public String requestId;
    public int sequence;
    public String type;
    public Map<String, String> data;
}
