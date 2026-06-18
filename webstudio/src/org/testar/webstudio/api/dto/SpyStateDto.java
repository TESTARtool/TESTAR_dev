/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class SpyStateDto {

    private final String status;
    private final String workspace;
    private final String platform;
    private final String target;
    private final String message;
    private final String screenshotPath;
    private final int screenshotWidth;
    private final int screenshotHeight;
    private final List<SpyWidgetDto> widgets;
    private final List<SpyActionDto> actions;

    public SpyStateDto(
        String status,
        String workspace,
        String platform,
        String target,
        String message,
        String screenshotPath,
        int screenshotWidth,
        int screenshotHeight,
        List<SpyWidgetDto> widgets,
        List<SpyActionDto> actions
    ) {
        this.status = status;
        this.workspace = workspace;
        this.platform = platform;
        this.target = target;
        this.message = message;
        this.screenshotPath = screenshotPath;
        this.screenshotWidth = screenshotWidth;
        this.screenshotHeight = screenshotHeight;
        this.widgets = widgets;
        this.actions = actions;
    }

    public String status() {
        return status;
    }

    public String workspace() {
        return workspace;
    }

    public String platform() {
        return platform;
    }

    public String target() {
        return target;
    }

    public String message() {
        return message;
    }

    public String screenshotPath() {
        return screenshotPath;
    }

    public int screenshotWidth() {
        return screenshotWidth;
    }

    public int screenshotHeight() {
        return screenshotHeight;
    }

    public List<SpyWidgetDto> widgets() {
        return widgets;
    }

    public List<SpyActionDto> actions() {
        return actions;
    }
}
