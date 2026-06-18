/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.spy;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.testar.config.ConfigTags;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.config.settings.Settings;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.alayer.Shape;
import org.testar.core.alayer.Role;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformSession;
import org.testar.plugin.PlatformSessionSpecFactory;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.screenshot.ScreenshotProviderFactory;
import org.testar.scriptless.util.TriggerActionUtil;
import org.testar.webstudio.api.dto.SpyActionDto;
import org.testar.webstudio.api.dto.SpyStateDto;
import org.testar.webstudio.api.dto.SpyWidgetDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class RemoteSpyService {

    private static final Duration SCREENSHOT_WAIT_TIMEOUT = Duration.ofSeconds(2);
    private static final long SCREENSHOT_WAIT_STEP_MILLIS = 100L;

    private final WorkspaceService workspaceService;
    private final RemoteSpyDebugLog debugLog;

    private PlatformSession activeSession;
    private PlatformSessionSpecification activeSessionSpec;
    private String activeWorkspace;
    private SpyStateDto currentState;
    private State currentCapturedState;
    private Map<String, Widget> currentWidgetsById = Map.of();
    private Map<String, String> currentWidgetIdsByPath = Map.of();
    private Map<String, Action> currentDerivedActionsById = Map.of();

    public RemoteSpyService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.debugLog = new RemoteSpyDebugLog(workspaceService.testarHomeDirectory().resolve("spy-debug.log"));
    }

    public RemoteSpyDebugLog debugLog() {
        return debugLog;
    }

    public synchronized SpyStateDto startRemoteSpy(String workspaceName) {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.startRemoteSpy invoked for workspace=" + workspaceName);
        stopRemoteSpy();

        Path workspaceDirectory = workspaceService.workspaceDirectory(workspaceName);
        Path testSettingsPath = workspaceDirectory.resolve("test.settings");

        try {
            Settings.setSettingsPath(workspaceDirectory.toString());
            debugLog.log("RemoteSpyService.startRemoteSpy settingsPath=" + workspaceDirectory);
            Settings settings = Settings.loadSettings(new String[0], testSettingsPath.toString());
            normalizeSettingsPaths(settings);
            debugLog.log(
                "RemoteSpyService.startRemoteSpy normalized resources composition="
                    + settings.get(ConfigTags.CustomCompositionResource, "")
                    + " policies="
                    + settings.get(ConfigTags.CustomPoliciesResource, "")
            );
            activeSessionSpec = PlatformSessionSpecFactory.fromSettings(settings);
            debugLog.log(
                "RemoteSpyService.startRemoteSpy sessionSpec os="
                    + activeSessionSpec.getOperatingSystem()
                    + " target="
                    + activeSessionSpec.getTarget()
            );
            activeSession = PlatformOrchestrator.openSpySession(activeSessionSpec);
            activeWorkspace = workspaceName;
            currentState = captureInitialState();
            debugLog.log("RemoteSpyService.startRemoteSpy completed in " + elapsedMillis(startedAt) + " ms");
            return currentState;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.startRemoteSpy failed after " + elapsedMillis(startedAt) + " ms", exception);
            stopRemoteSpy();
            throw new IllegalStateException("Unable to start Spy Mode for workspace " + workspaceName, exception);
        }
    }

    public synchronized SpyStateDto refreshRemoteSpy() {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.refreshRemoteSpy invoked");
        PlatformSession session = requireActiveSession();

        try {
            debugLog.log("RemoteSpyService.refreshRemoteSpy requesting state");
            State state = session.getState();
            debugLog.log("RemoteSpyService.refreshRemoteSpy state captured");
            currentState = toSpyState(state);
            debugLog.log(
                "RemoteSpyService.refreshRemoteSpy completed in "
                    + elapsedMillis(startedAt)
                    + " ms screenshot="
                    + currentState.screenshotPath()
                    + " widgets="
                    + currentState.widgets().size()
            );
            return currentState;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.refreshRemoteSpy failed after " + elapsedMillis(startedAt) + " ms", exception);
            throw new IllegalStateException("Unable to refresh Spy Mode state", exception);
        }
    }

    public synchronized SpyStateDto executeRemoteSpyAction(String actionId) {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.executeRemoteSpyAction actionId=" + actionId);
        requireActiveSession();

        Action action = currentDerivedActionsById.get(actionId);
        if (action == null) {
            throw new IllegalArgumentException("Unknown Spy derived action: " + actionId);
        }

        try {
            activeSession.executeAction(action);
            SpyStateDto refreshedState = refreshRemoteSpy();
            debugLog.log("RemoteSpyService.executeRemoteSpyAction completed in " + elapsedMillis(startedAt) + " ms");
            return refreshedState;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.executeRemoteSpyAction failed after " + elapsedMillis(startedAt) + " ms", exception);
            throw new IllegalStateException("Unable to execute Spy derived action", exception);
        }
    }

    public synchronized SpyStateDto executeRemoteSpyDefaultWidgetAction(String widgetId) {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.executeRemoteSpyDefaultWidgetAction widgetId=" + widgetId);
        requireActiveSession();

        Widget widget = requireCurrentWidget(widgetId);
        try {
            Action directAction = TriggerActionUtil.triggeredClickAction(requireCurrentState(), widget);
            executeDirectAction(requireCurrentState(), directAction);
            SpyStateDto refreshedState = refreshRemoteSpy();
            debugLog.log("RemoteSpyService.executeRemoteSpyDefaultWidgetAction completed in " + elapsedMillis(startedAt) + " ms");
            return refreshedState;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.executeRemoteSpyDefaultWidgetAction failed after " + elapsedMillis(startedAt) + " ms", exception);
            throw new IllegalStateException("Unable to execute default widget action", exception);
        }
    }

    public synchronized SpyStateDto executeRemoteSpyDirectType(String widgetId, String text) {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.executeRemoteSpyDirectType widgetId=" + widgetId + " textLength=" + (text == null ? 0 : text.length()));
        requireActiveSession();

        Widget widget = requireCurrentWidget(widgetId);
        String inputText = text == null ? "" : text;
        try {
            Action directAction = TriggerActionUtil.triggeredTypeAction(requireCurrentState(), widget, inputText, true);
            executeDirectAction(requireCurrentState(), directAction);
            SpyStateDto refreshedState = refreshRemoteSpy();
            debugLog.log("RemoteSpyService.executeRemoteSpyDirectType completed in " + elapsedMillis(startedAt) + " ms");
            return refreshedState;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.executeRemoteSpyDirectType failed after " + elapsedMillis(startedAt) + " ms", exception);
            throw new IllegalStateException("Unable to type into selected widget", exception);
        }
    }

    public synchronized SpyStateDto remoteSpyStatus() {
        if (activeSession == null || currentState == null) {
            return new SpyStateDto(
                "idle",
                "",
                "",
                "",
                "Spy Mode is idle",
                "",
                0,
                0,
                List.of(),
                List.of()
            );
        }

        return currentState;
    }

    public synchronized SpyStateDto stopRemoteSpy() {
        long startedAt = System.nanoTime();
        debugLog.log("RemoteSpyService.stopRemoteSpy invoked activeSession=" + (activeSession != null));
        if (activeSession != null) {
            try {
                activeSession.stopSystem();
            } catch (Exception ignored) {
                debugLog.log("RemoteSpyService.stopRemoteSpy stopSystem failed", ignored);
            }
            try {
                activeSession.close();
            } catch (Exception ignored) {
                debugLog.log("RemoteSpyService.stopRemoteSpy close failed", ignored);
            }
        }

        activeSession = null;
        activeSessionSpec = null;
        activeWorkspace = null;
        currentCapturedState = null;
        currentWidgetsById = Map.of();
        currentWidgetIdsByPath = Map.of();
        currentDerivedActionsById = Map.of();
        currentState = new SpyStateDto(
            "idle",
            "",
            "",
            "",
            "Spy Mode stopped",
            "",
            0,
            0,
            List.of(),
            List.of()
        );
        debugLog.log("RemoteSpyService.stopRemoteSpy completed in " + elapsedMillis(startedAt) + " ms");
        return currentState;
    }

    public synchronized Path resolveScreenshot(String screenshotPath) {
        if (screenshotPath == null || screenshotPath.isBlank()) {
            throw new IllegalArgumentException("Spy screenshot path is required");
        }

        Path screenshot = resolveScreenshotPath(screenshotPath);
        Path availableScreenshot = awaitScreenshotFile(screenshot);
        if (availableScreenshot == null) {
            debugLog.log("RemoteSpyService.resolveScreenshot missing file=" + screenshot);
            throw new IllegalArgumentException("Spy screenshot not found: " + screenshot.getFileName());
        }

        return availableScreenshot;
    }

    private PlatformSession requireActiveSession() {
        if (activeSession == null) {
            throw new IllegalStateException("No active Spy Mode session. Start one first.");
        }

        return activeSession;
    }

    private void normalizeSettingsPaths(Settings settings) {
        Path testarHomeDirectory = workspaceService.testarHomeDirectory();
        normalizePathSetting(settings, ConfigTags.CustomCompositionResource, testarHomeDirectory);
        normalizePathSetting(settings, ConfigTags.CustomPoliciesResource, testarHomeDirectory);
    }

    private void normalizePathSetting(Settings settings, org.testar.core.tag.Tag<String> tag, Path baseDirectory) {
        String configuredPath = settings.get(tag, "").trim();
        if (configuredPath.isBlank()) {
            return;
        }

        Path candidate = Path.of(configuredPath);
        if (candidate.isAbsolute()) {
            settings.set(tag, candidate.normalize().toString());
            return;
        }

        settings.set(tag, baseDirectory.resolve(candidate).normalize().toString());
    }

    private SpyStateDto toSpyState(State state) {
        Shape rootShape = state.get(Tags.Shape, null);
        double offsetX = rootShape == null ? 0.0d : rootShape.x();
        double offsetY = rootShape == null ? 0.0d : rootShape.y();

        Map<Widget, String> widgetIds = new IdentityHashMap<>();
        Map<String, Integer> emittedWidgetIds = new LinkedHashMap<>();
        List<SpyWidgetDto> widgets = new ArrayList<>();
        traverseWidgetTree(state, null, widgetIds, emittedWidgetIds, widgets, offsetX, offsetY);
        currentCapturedState = state;
        currentWidgetsById = indexWidgetsById(widgetIds);
        currentWidgetIdsByPath = indexWidgetIdsByPath(widgetIds);
        List<SpyActionDto> actions = refreshDerivedActions();

        String resolvedScreenshotPath = captureSpyScreenshot(state);
        int[] screenshotSize = resolveScreenshotSize(resolvedScreenshotPath);
        widgets.sort(Comparator
            .comparingDouble((SpyWidgetDto widget) -> widget.width() * widget.height())
            .thenComparing(SpyWidgetDto::label));

        return new SpyStateDto(
            "running",
            activeWorkspace == null ? "" : activeWorkspace,
            activeSessionSpec == null ? "" : activeSessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT),
            activeSessionSpec == null ? "" : activeSessionSpec.getTarget(),
            "Spy Mode session active",
            resolvedScreenshotPath,
            screenshotSize[0],
            screenshotSize[1],
            widgets,
            actions
        );
    }

    private SpyStateDto captureInitialState() {
        try {
            debugLog.log("RemoteSpyService.captureInitialState requesting state");
            State initialState = requireActiveSession().getState();
            debugLog.log("RemoteSpyService.captureInitialState state captured");
            return toSpyState(initialState);
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.captureInitialState failed, returning placeholder state", exception);
            return new SpyStateDto(
                "running",
                activeWorkspace == null ? "" : activeWorkspace,
                activeSessionSpec == null ? "" : activeSessionSpec.getOperatingSystem().name().toLowerCase(Locale.ROOT),
                activeSessionSpec == null ? "" : activeSessionSpec.getTarget(),
                "Spy Mode session started. Refresh the state to inspect the SUT.",
                "",
                0,
                0,
                List.of(),
                List.of()
            );
        }
    }

    private void traverseWidgetTree(
        Widget widget,
        String parentId,
        Map<Widget, String> widgetIds,
        Map<String, Integer> emittedWidgetIds,
        List<SpyWidgetDto> widgets,
        double offsetX,
        double offsetY
    ) {
        String widgetId = buildWidgetId(widget, parentId, widgets.size(), emittedWidgetIds);
        widgetIds.put(widget, widgetId);
        widgets.add(toSpyWidget(widget, widgetId, parentId, offsetX, offsetY));

        for (int childIndex = 0; childIndex < widget.childCount(); childIndex++) {
            traverseWidgetTree(widget.child(childIndex), widgetId, widgetIds, emittedWidgetIds, widgets, offsetX, offsetY);
        }
    }

    private SpyWidgetDto toSpyWidget(Widget widget, String widgetId, String parentId, double offsetX, double offsetY) {
        Shape shape = widget.get(Tags.Shape, null);
        Role role = widget.get(Tags.Role, null);
        Map<String, String> properties = new LinkedHashMap<>();
        putProperty(properties, "Desc", widget.get(Tags.Desc, ""));
        putProperty(properties, "Role", String.valueOf(widget.get(Tags.Role, null)));
        putProperty(properties, "Title", widget.get(Tags.Title, ""));
        putProperty(properties, "Text", widget.get(Tags.Text, ""));
        putProperty(properties, "Path", widget.get(Tags.Path, ""));
        putProperty(properties, "AbstractID", widget.get(Tags.AbstractID, ""));
        putProperty(properties, "ConcreteID", widget.get(Tags.ConcreteID, ""));
        putProperty(properties, "Enabled", String.valueOf(widget.get(Tags.Enabled, false)));
        putProperty(properties, "Blocked", String.valueOf(widget.get(Tags.Blocked, false)));
        putProperty(properties, "Foreground", String.valueOf(widget.get(Tags.Foreground, false)));
        putProperty(properties, "ValuePattern", widget.get(Tags.ValuePattern, ""));
        putProperty(properties, "LinkReference", widget.get(Tags.LinkReference, ""));

        return new SpyWidgetDto(
            widgetId,
            parentId,
            widgetLabel(widget),
            String.valueOf(role),
            shape == null ? 0.0d : shape.x() - offsetX,
            shape == null ? 0.0d : shape.y() - offsetY,
            shape == null ? 0.0d : shape.width(),
            shape == null ? 0.0d : shape.height(),
            widget.get(Tags.Enabled, false),
            properties
        );
    }

    private String buildWidgetId(Widget widget, String parentId, int index, Map<String, Integer> emittedWidgetIds) {
        String baseId = baseWidgetId(widget, parentId, index);
        int duplicateCount = emittedWidgetIds.getOrDefault(baseId, 0);
        emittedWidgetIds.put(baseId, duplicateCount + 1);
        if (duplicateCount == 0) {
            return baseId;
        }

        return baseId + "#" + duplicateCount;
    }

    private String baseWidgetId(Widget widget, String parentId, int index) {
        String path = widget.get(Tags.Path, "").trim();
        if (!path.isBlank()) {
            return sanitizeIdentifier(path) + "_" + index;
        }

        String label = widgetLabel(widget).trim();
        if (!label.isBlank()) {
            return sanitizeIdentifier(label) + "_" + index;
        }

        String role = String.valueOf(widget.get(Tags.Role, null)).trim();
        if (!role.isBlank() && !"null".equalsIgnoreCase(role)) {
            return sanitizeIdentifier(role) + "_" + index;
        }

        String parentPrefix = parentId == null || parentId.isBlank()
            ? "widget"
            : sanitizeIdentifier(parentId);

        return parentPrefix + "_" + index;
    }

    private String sanitizeIdentifier(String value) {
        return value
            .replaceAll("[^A-Za-z0-9._-]+", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_+", "")
            .replaceAll("_+$", "");
    }

    private Map<String, Widget> indexWidgetsById(Map<Widget, String> widgetIds) {
        Map<String, Widget> widgetsById = new LinkedHashMap<>();
        for (Map.Entry<Widget, String> entry : widgetIds.entrySet()) {
            widgetsById.put(entry.getValue(), entry.getKey());
        }
        return widgetsById;
    }

    private Map<String, String> indexWidgetIdsByPath(Map<Widget, String> widgetIds) {
        Map<String, String> widgetIdsByPath = new LinkedHashMap<>();
        for (Map.Entry<Widget, String> entry : widgetIds.entrySet()) {
            String path = entry.getKey().get(Tags.Path, "").trim();
            if (!path.isBlank()) {
                widgetIdsByPath.put(path, entry.getValue());
            }
        }
        return widgetIdsByPath;
    }

    private List<SpyActionDto> refreshDerivedActions() {
        try {
            Set<Action> derivedActions = requireActiveSession().getDerivedActions();
            Map<String, Action> actionsById = new LinkedHashMap<>();
            List<SpyActionDto> actionDtos = new ArrayList<>();
            int index = 0;
            for (Action action : derivedActions) {
                String actionId = "action_" + index++;
                actionsById.put(actionId, action);
                actionDtos.add(new SpyActionDto(
                    actionId,
                    action.get(Tags.Desc, action.toShortString()),
                    String.valueOf(action.get(Tags.Role, null)),
                    resolveTargetWidgetId(action)
                ));
            }
            currentDerivedActionsById = actionsById;
            return actionDtos;
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.refreshDerivedActions failed", exception);
            currentDerivedActionsById = Map.of();
            return List.of();
        }
    }

    private String resolveTargetWidgetId(Action action) {
        Widget originWidget = action.get(Tags.OriginWidget, null);
        if (originWidget == null) {
            return "";
        }

        String path = originWidget.get(Tags.Path, "").trim();
        if (!path.isBlank() && currentWidgetIdsByPath.containsKey(path)) {
            return currentWidgetIdsByPath.get(path);
        }

        String abstractId = originWidget.get(Tags.AbstractID, "").trim();
        if (!abstractId.isBlank()) {
            for (Map.Entry<String, Widget> entry : currentWidgetsById.entrySet()) {
                if (abstractId.equals(entry.getValue().get(Tags.AbstractID, ""))) {
                    return entry.getKey();
                }
            }
        }

        return "";
    }

    private State requireCurrentState() {
        if (currentCapturedState == null) {
            throw new IllegalStateException("No Spy state is currently loaded. Refresh the state first.");
        }

        return currentCapturedState;
    }

    private Widget requireCurrentWidget(String widgetId) {
        Widget widget = currentWidgetsById.get(widgetId);
        if (widget == null) {
            throw new IllegalArgumentException("Unknown Spy widget: " + widgetId);
        }

        return widget;
    }

    private void executeDirectAction(State state, Action action) {
        CodingManager.buildIDs(state, Set.of(action));
        activeSession.executeAction(action);
    }

    private int[] resolveScreenshotSize(String screenshotPath) {
        if (screenshotPath == null || screenshotPath.isBlank()) {
            return new int[] { 0, 0 };
        }

        try {
            BufferedImage image = ImageIO.read(Path.of(screenshotPath).toFile());
            if (image == null) {
                return new int[] { 0, 0 };
            }

            return new int[] { image.getWidth(), image.getHeight() };
        } catch (IOException exception) {
            return new int[] { 0, 0 };
        }
    }

    private String widgetLabel(Widget widget) {
        String desc = widget.get(Tags.Desc, "").trim();
        if (!desc.isBlank()) {
            return desc;
        }

        String title = widget.get(Tags.Title, "").trim();
        if (!title.isBlank()) {
            return title;
        }

        String text = widget.get(Tags.Text, "").trim();
        if (!text.isBlank()) {
            return text;
        }

        return String.valueOf(widget.get(Tags.Role, null));
    }

    private void putProperty(Map<String, String> properties, String key, String value) {
        if (value != null && !value.isBlank() && !"null".equals(value)) {
            properties.put(key, value);
        }
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000L;
    }

    private String captureSpyScreenshot(State state) {
        try {
            AWTCanvas screenshot = ScreenshotProviderFactory.current().getStateshotBinary(state);
            if (screenshot == null) {
                debugLog.log("RemoteSpyService.captureSpyScreenshot returned null screenshot");
                return "";
            }

            Path screenshotDirectory = workspaceService.testarHomeDirectory().resolve("spy-screenshots");
            Files.createDirectories(screenshotDirectory);
            Path screenshotPath = screenshotDirectory
                .resolve("spy-" + System.currentTimeMillis() + ".png")
                .toAbsolutePath()
                .normalize();
            screenshot.saveAsPng(screenshotPath.toString());
            debugLog.log("RemoteSpyService.captureSpyScreenshot saved " + screenshotPath);
            return screenshotPath.toString();
        } catch (Exception exception) {
            debugLog.log("RemoteSpyService.captureSpyScreenshot failed", exception);
            return "";
        }
    }

    private Path resolveScreenshotPath(String screenshotPath) {
        Path screenshot = Path.of(screenshotPath);
        if (screenshot.isAbsolute()) {
            return screenshot.normalize();
        }

        return workspaceService.testarHomeDirectory().resolve(screenshot).toAbsolutePath().normalize();
    }

    private Path awaitScreenshotFile(Path screenshot) {
        long deadline = System.nanoTime() + SCREENSHOT_WAIT_TIMEOUT.toNanos();
        while (System.nanoTime() < deadline) {
            if (Files.isRegularFile(screenshot)) {
                return screenshot;
            }

            try {
                Thread.sleep(SCREENSHOT_WAIT_STEP_MILLIS);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                debugLog.log("RemoteSpyService.awaitScreenshotFile interrupted for " + screenshot, interruptedException);
                return null;
            }
        }

        return Files.isRegularFile(screenshot) ? screenshot : null;
    }
}
