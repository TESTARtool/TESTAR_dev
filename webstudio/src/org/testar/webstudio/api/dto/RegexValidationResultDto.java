/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class RegexValidationResultDto {

    private final boolean valid;
    private final String message;
    private final Integer errorIndex;

    public RegexValidationResultDto(boolean valid, String message, Integer errorIndex) {
        this.valid = valid;
        this.message = message;
        this.errorIndex = errorIndex;
    }

    public boolean valid() {
        return valid;
    }

    public String message() {
        return message;
    }

    public Integer errorIndex() {
        return errorIndex;
    }
}
