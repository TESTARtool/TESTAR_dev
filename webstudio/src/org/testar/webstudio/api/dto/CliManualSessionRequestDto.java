/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class CliManualSessionRequestDto {

    private String platform;
    private String target;

    public String platform() {
        if (platform == null || platform.isBlank()) {
            return "webdriver";
        }

        return platform.trim();
    }

    public String target() {
        if (target == null) {
            return "";
        }

        return target.trim();
    }
}
