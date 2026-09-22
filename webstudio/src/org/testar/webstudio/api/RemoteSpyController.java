/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import java.nio.file.Files;

import org.testar.webstudio.api.dto.SpyStateDto;
import org.testar.webstudio.spy.RemoteSpyDebugLog;
import org.testar.webstudio.spy.RemoteSpyService;

public final class RemoteSpyController {

    private final RemoteSpyService remoteSpyService;
    private final RemoteSpyDebugLog debugLog;

    public RemoteSpyController(RemoteSpyService remoteSpyService) {
        this.remoteSpyService = remoteSpyService;
        this.debugLog = remoteSpyService.debugLog();
    }

    public SpyStateDto startRemoteSpy(String workspaceName) {
        debugLog.log("RemoteSpyController.startRemoteSpy workspace=" + workspaceName);
        return remoteSpyService.startRemoteSpy(workspaceName);
    }

    public SpyStateDto refreshRemoteSpy() {
        debugLog.log("RemoteSpyController.refreshRemoteSpy");
        return remoteSpyService.refreshRemoteSpy();
    }

    public SpyStateDto remoteSpyStatus() {
        return remoteSpyService.remoteSpyStatus();
    }

    public SpyStateDto executeRemoteSpyAction(String actionId) {
        debugLog.log("RemoteSpyController.executeRemoteSpyAction actionId=" + actionId);
        return remoteSpyService.executeRemoteSpyAction(actionId);
    }

    public SpyStateDto executeRemoteSpyDefaultWidgetAction(String widgetId) {
        debugLog.log("RemoteSpyController.executeRemoteSpyDefaultWidgetAction widgetId=" + widgetId);
        return remoteSpyService.executeRemoteSpyDefaultWidgetAction(widgetId);
    }

    public SpyStateDto executeRemoteSpyDirectType(String widgetId, String text) {
        debugLog.log("RemoteSpyController.executeRemoteSpyDirectType widgetId=" + widgetId + " textLength=" + (text == null ? 0 : text.length()));
        return remoteSpyService.executeRemoteSpyDirectType(widgetId, text);
    }

    public SpyStateDto stopRemoteSpy() {
        debugLog.log("RemoteSpyController.stopRemoteSpy");
        return remoteSpyService.stopRemoteSpy();
    }

    public byte[] screenshot(String screenshotPath) {
        try {
            return Files.readAllBytes(remoteSpyService.resolveScreenshot(screenshotPath));
        } catch (Exception exception) {
            debugLog.log("RemoteSpyController.screenshot failed path=" + screenshotPath, exception);
            throw new IllegalStateException("Unable to read Spy Mode screenshot: " + screenshotPath, exception);
        }
    }
}
