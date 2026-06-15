/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class ValidationResultDto {

    private final boolean valid;
    private final String message;

    public ValidationResultDto(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean valid() {
        return valid;
    }

    public String message() {
        return message;
    }
}
