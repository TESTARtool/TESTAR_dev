/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class StateModelLaunchDto {

    private final String url;
    private final String message;

    public StateModelLaunchDto(String url, String message) {
        this.url = url;
        this.message = message;
    }

    public String url() {
        return url;
    }

    public String message() {
        return message;
    }
}
