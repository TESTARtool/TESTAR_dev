/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class StateModelStatusDto {

    private final String status;
    private final String url;
    private final String message;
    private final boolean running;

    public StateModelStatusDto(String status, String url, String message, boolean running) {
        this.status = status;
        this.url = url;
        this.message = message;
        this.running = running;
    }

    public String status() {
        return status;
    }

    public String url() {
        return url;
    }

    public String message() {
        return message;
    }

    public boolean running() {
        return running;
    }
}
