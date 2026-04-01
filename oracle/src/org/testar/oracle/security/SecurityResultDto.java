/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

public class SecurityResultDto {
    public SecurityResultDto(String path, String cwe, String result)
    {
        this.path = path;
        this.cwe = cwe;
        this.result = result;
    }

    public String path;
    public String cwe;
    public String result;
}