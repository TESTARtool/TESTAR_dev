/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceFileUpdateDto {

    private String content;

    public String content() {
        if (content == null) {
            return "";
        }

        return content;
    }
}
