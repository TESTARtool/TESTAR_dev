<script>
    export let scriptlessStatus = null;
    export let saving = false;
    export let selectedWorkspaceName = "";
    export let spyState = null;
    export let startLocalSpyMode;
    export let startRemoteSpyMode;
    export let refreshRemoteSpyMode;
    export let stopLocalSpyMode;
    export let stopRemoteSpyMode;
    export let executeSpyAction;
    export let executeSpyWidgetDefaultAction;
    export let executeSpyWidgetDirectType;

    let selectedWidgetId = "";
    let screenshotImageElement;
    let screenshotFrameElement;
    let hitCandidateIds = [];
    let renderedScreenshotPath = "";
    let widgetById = new Map();
    let widgetChildrenCountById = new Map();
    let widgetDepthById = new Map();
    let directTypeText = "";
    let localSpyRunning = false;
    let remoteSpyRunning = false;
    let anySpyRunning = false;
    let spyRunLabel = "waiting user execution";

    function screenshotUrl(screenshotPath) {
        if (!screenshotPath) {
            return "";
        }

        return `/api/spy/screenshot?path=${encodeURIComponent(screenshotPath)}`;
    }

    function clearWidgetSelection() {
        selectedWidgetId = "";
    }

    function isInspectableWidget(widget) {
        if (!widget) {
            return false;
        }

        if ((widget.width || 0) <= 0 || (widget.height || 0) <= 0) {
            return false;
        }

        return true;
    }

    function widgetArea(widget) {
        return (widget?.width || 0) * (widget?.height || 0);
    }

    function isGenericContainerWidget(widget) {
        if (!widget) {
            return false;
        }

        const role = String(widget.role || "").toLowerCase();
        const label = String(widget.label || "").trim().toLowerCase();
        const childCount = widgetChildrenCountById.get(widget.id) || 0;
        const largeAreaThreshold = (spyState?.screenshotWidth || 0) * (spyState?.screenshotHeight || 0) * 0.20;
        const isLargeContainer = childCount > 0 && widgetArea(widget) >= largeAreaThreshold;

        return role === "body"
            || role === "div"
            || role === "state"
            || role === "document"
            || role === "pane"
            || role === "panel"
            || label === "body"
            || label === "div"
            || isLargeContainer;
    }

    function containsPoint(widget, x, y) {
        return x >= widget.x
            && y >= widget.y
            && x <= widget.x + widget.width
            && y <= widget.y + widget.height;
    }

    function resolveWidgetById(widgetId) {
        return widgetById.get(widgetId) || null;
    }

    function sortedMatchingWidgets(widgets) {
        return [...widgets].sort((left, right) => {
            const leftIsRoot = left.parentId ? 0 : 1;
            const rightIsRoot = right.parentId ? 0 : 1;
            if (leftIsRoot !== rightIsRoot) {
                return leftIsRoot - rightIsRoot;
            }

            const depthDifference = (widgetDepthById.get(right.id) || 0) - (widgetDepthById.get(left.id) || 0);
            if (depthDifference !== 0) {
                return depthDifference;
            }

            const leftLeaf = (widgetChildrenCountById.get(left.id) || 0) === 0 ? 1 : 0;
            const rightLeaf = (widgetChildrenCountById.get(right.id) || 0) === 0 ? 1 : 0;
            if (leftLeaf !== rightLeaf) {
                return rightLeaf - leftLeaf;
            }

            const areaDifference = widgetArea(left) - widgetArea(right);
            if (areaDifference !== 0) {
                return areaDifference;
            }

            return String(left.label || "").localeCompare(String(right.label || ""));
        });
    }

    function hitTestWidget(clientX, clientY) {
        if (!screenshotImageElement || !spyState?.screenshotWidth || !spyState?.screenshotHeight) {
            return "";
        }

        const imageBounds = screenshotImageElement.getBoundingClientRect();
        if (!imageBounds.width || !imageBounds.height) {
            return "";
        }

        const imageX = clientX - imageBounds.left;
        const imageY = clientY - imageBounds.top;
        const stateX = (imageX / imageBounds.width) * spyState.screenshotWidth;
        const stateY = (imageY / imageBounds.height) * spyState.screenshotHeight;

        const matchingWidgets = overlayWidgets.filter((widget) => containsPoint(widget, stateX, stateY));
        if (matchingWidgets.length === 0) {
            hitCandidateIds = [];
            return "";
        }

        const specificWidgets = matchingWidgets.filter((widget) => !isGenericContainerWidget(widget));
        const rankedWidgets = sortedMatchingWidgets(specificWidgets.length > 0 ? specificWidgets : matchingWidgets);
        hitCandidateIds = rankedWidgets.slice(0, 12).map((widget) => widget.id);

        return rankedWidgets[0].id;
    }

    function handleCanvasClick(event) {
        const widgetId = hitTestWidget(event.clientX, event.clientY);
        if (!widgetId) {
            clearWidgetSelection();
            return;
        }

        selectedWidgetId = selectedWidgetId === widgetId ? "" : widgetId;
    }

    function toggleDisplayedWidgetSelection() {
        if (!selectedWidgetId) {
            clearWidgetSelection();
            return;
        }

        selectedWidgetId = "";
    }

    function handleFrameKeyDown(event) {
        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            toggleDisplayedWidgetSelection();
        }
    }

    function outlineStyle(widget) {
        if (!widget || !screenshotImageElement || !spyState?.screenshotWidth || !spyState?.screenshotHeight) {
            return "";
        }

        const imageWidth = screenshotImageElement.clientWidth || 1;
        const imageHeight = screenshotImageElement.clientHeight || 1;
        const scaleX = imageWidth / spyState.screenshotWidth;
        const scaleY = imageHeight / spyState.screenshotHeight;
        const frameBounds = screenshotFrameElement?.getBoundingClientRect();
        const imageBounds = screenshotImageElement.getBoundingClientRect();
        const offsetLeft = frameBounds ? (imageBounds.left - frameBounds.left) : 0;
        const offsetTop = frameBounds ? (imageBounds.top - frameBounds.top) : 0;

        return [
            `left:${offsetLeft + (widget.x * scaleX)}px`,
            `top:${offsetTop + (widget.y * scaleY)}px`,
            `width:${Math.max(1, widget.width * scaleX)}px`,
            `height:${Math.max(1, widget.height * scaleY)}px`
        ].join(";");
    }

    function visibleProperties(widget) {
        if (!widget) {
            return [];
        }

        const metadata = [
            ["WidgetId", widget.id || ""],
            ["AbstractID", widget.properties?.AbstractID || ""],
            ["ConcreteID", widget.properties?.ConcreteID || ""],
            ["ParentId", widget.parentId || ""],
            ["Role", widget.role || ""],
            ["Enabled", String(widget.enabled)],
            ["X", String(Math.round(widget.x || 0))],
            ["Y", String(Math.round(widget.y || 0))],
            ["Width", String(Math.round(widget.width || 0))],
            ["Height", String(Math.round(widget.height || 0))]
        ];
        const propertyEntries = Object.entries(widget.properties || {})
            .filter(([key]) => key !== "AbstractID" && key !== "ConcreteID");

        return [...metadata, ...propertyEntries]
            .filter(([, value]) => value !== null && value !== undefined && String(value).trim() !== "");
    }

    function handleExecuteDefaultAction() {
        if (displayedWidget?.id) {
            executeSpyWidgetDefaultAction(displayedWidget.id);
        }
    }

    function handleExecuteDirectType() {
        if (displayedWidget?.id) {
            executeSpyWidgetDirectType(displayedWidget.id, directTypeText);
        }
    }

    $: if (selectedWidgetId && !(spyState?.widgets || []).some((widget) => widget.id === selectedWidgetId)) {
        selectedWidgetId = "";
    }

    $: widgetById = new Map((spyState?.widgets || []).map((widget) => [widget.id, widget]));
    $: widgetChildrenCountById = new Map();
    $: {
        for (const widget of (spyState?.widgets || [])) {
            widgetChildrenCountById.set(widget.id, 0);
        }

        for (const widget of (spyState?.widgets || [])) {
            if (widget.parentId) {
                widgetChildrenCountById.set(
                    widget.parentId,
                    (widgetChildrenCountById.get(widget.parentId) || 0) + 1
                );
            }
        }
    }

    $: widgetDepthById = new Map();
    $: {
        for (const widget of (spyState?.widgets || [])) {
            let depth = 0;
            let currentParentId = widget.parentId;

            while (currentParentId) {
                depth += 1;
                currentParentId = widgetById.get(currentParentId)?.parentId || "";
            }

            widgetDepthById.set(widget.id, depth);
        }
    }

    $: if ((spyState?.screenshotPath || "") !== renderedScreenshotPath) {
        renderedScreenshotPath = spyState?.screenshotPath || "";
        selectedWidgetId = "";
        hitCandidateIds = [];
        screenshotImageElement = undefined;
    }

    $: displayedWidget = resolveWidgetById(selectedWidgetId);
    $: displayedWidgetActions = displayedWidget?.id
        ? (spyState?.actions || []).filter((action) => action.targetWidgetId === displayedWidget.id)
        : (spyState?.actions || []);
    $: overlayWidgets = (spyState?.widgets || [])
        .filter((widget) => isInspectableWidget(widget))
        .sort((left, right) => widgetArea(right) - widgetArea(left));
    $: hitCandidates = hitCandidateIds
        .map((widgetId) => resolveWidgetById(widgetId))
        .filter((widget) => widget !== null);
    $: widgetBrowserItems = [...overlayWidgets]
        .filter((widget) => widget.parentId)
        .sort((left, right) => {
            const depthDifference = (widgetDepthById.get(right.id) || 0) - (widgetDepthById.get(left.id) || 0);
            if (depthDifference !== 0) {
                return depthDifference;
            }

            const areaDifference = widgetArea(left) - widgetArea(right);
            if (areaDifference !== 0) {
                return areaDifference;
            }

            return String(left.label || "").localeCompare(String(right.label || ""));
        })
        .slice(0, 80);
    $: localSpyRunning = scriptlessStatus?.status === "running" && scriptlessStatus?.mode === "Spy";
    $: remoteSpyRunning = spyState?.status === "running";
    $: anySpyRunning = localSpyRunning || remoteSpyRunning;
    $: spyRunLabel = localSpyRunning
        ? "running spy local"
        : (remoteSpyRunning ? "running spy remote" : "waiting user execution");
</script>

<section class="panel panel-wide status-panel spy-mode-panel">
    <div class="status-panel-header">
        <div>
            <p class="eyebrow">Inspector</p>
            <h2>Spy Mode Run</h2>
        </div>
        <div class="spy-run-indicator" aria-hidden="true">
            <div class="progress-track spy-run-track">
                <div
                    class:progress-running={anySpyRunning}
                    class:progress-idle={!anySpyRunning}
                    class="progress-bar spy-run-progress"
                ></div>
            </div>
        </div>
        <span class="spy-run-label">{spyRunLabel}</span>
        <div class="button-row">
            <button
                on:click={startLocalSpyMode}
                disabled={!selectedWorkspaceName || saving || anySpyRunning}
            >
                Run Local Spy Mode
            </button>
            <button
                on:click={startRemoteSpyMode}
                disabled={!selectedWorkspaceName || saving || anySpyRunning}
            >
                Run Remote Spy Mode
            </button>
            <button class="secondary" on:click={refreshRemoteSpyMode} disabled={saving || !remoteSpyRunning}>
                Refresh
            </button>
            <button class="secondary" on:click={stopLocalSpyMode} disabled={saving || !localSpyRunning}>
                Stop Local
            </button>
            <button class="secondary" on:click={stopRemoteSpyMode} disabled={saving || !remoteSpyRunning}>
                Stop Remote
            </button>
        </div>
    </div>

    <div class="spy-layout">
        <section class="status-card spy-inspector-panel spy-inspector-panel-left">
            <div class="spy-inspector-section spy-inspector-section-widget">
                <div class="spy-hover-panel-header">
                    <div>
                        <span class="eyebrow">Widget</span>
                        <h3>{displayedWidget?.label || "No widget selected"}</h3>
                    </div>
                    {#if displayedWidget}
                        <span class="spy-hover-badge">Selected</span>
                    {/if}
                </div>

                {#if displayedWidget}
                    <div class="spy-hover-panel-copy spy-inspector-copy">
                        <span>{displayedWidget.role}</span>
                        <span>
                            x:{Math.round(displayedWidget.x)} y:{Math.round(displayedWidget.y)}
                            w:{Math.round(displayedWidget.width)} h:{Math.round(displayedWidget.height)}
                        </span>
                        <span>Children: {(spyState?.widgets || []).filter((widget) => widget.parentId === displayedWidget.id).length}</span>
                    </div>

                    <div class="spy-action-controls">
                        <button
                            class="secondary"
                            type="button"
                            on:click={handleExecuteDefaultAction}
                            disabled={saving}
                        >
                            Default Action
                        </button>
                        <div class="spy-direct-type-row">
                            <input
                                type="text"
                                bind:value={directTypeText}
                                placeholder="Type text into selected widget"
                                disabled={saving}
                            />
                            <button
                                class="secondary"
                                type="button"
                                on:click={handleExecuteDirectType}
                                disabled={saving || !directTypeText.trim()}
                            >
                                Direct Type
                            </button>
                        </div>
                    </div>
                {/if}

                <div class="spy-hover-properties spy-inspector-properties">
                    {#if displayedWidget}
                        {#each visibleProperties(displayedWidget) as [key, value]}
                            <div class="spy-hover-property-row">
                                <strong>{key}</strong>
                                <span>{value}</span>
                            </div>
                        {/each}
                    {:else}
                        <p class="progress-message">Click a widget in the screenshot or select one from the widget browser.</p>
                    {/if}
                </div>
            </div>

            <div class="spy-inspector-section">
                <div class="spy-hover-panel-header">
                    <div>
                        <span class="eyebrow">{hitCandidates.length > 0 ? "Candidates" : "Browser"}</span>
                        <h3>{hitCandidates.length > 0 ? "Widgets At Last Click" : "Widget Browser"}</h3>
                    </div>
                </div>

                <div class="spy-candidate-list">
                    {#if hitCandidates.length > 0 || widgetBrowserItems.length > 0}
                        {#each (hitCandidates.length > 0 ? hitCandidates : widgetBrowserItems) as candidate}
                            <button
                                class:selected={displayedWidget?.id === candidate.id}
                                class="source-item spy-candidate-item"
                                type="button"
                                on:click={() => selectedWidgetId = candidate.id}
                            >
                                <span>{candidate.label || candidate.role || candidate.id}</span>
                                <small>{candidate.id}</small>
                            </button>
                        {/each}
                    {:else}
                        <p class="progress-message">No widgets are available for inspection.</p>
                    {/if}
                </div>
            </div>
        </section>

        <section class="status-card spy-canvas-panel">
            <div class="spy-canvas-header">
                <h3>{localSpyRunning ? "Local Spy Runtime" : "State Screenshot"}</h3>
                <p>
                    {#if localSpyRunning}
                        TESTAR Spy is running in the installed distribution. Use the native TESTAR Spy window directly while Web Studio shows the runtime console.
                    {:else}
                        {displayedWidget?.label || "Click the state screenshot to inspect a widget. Use the browser on the left if overlapping widgets need manual selection."}
                    {/if}
                </p>
            </div>
            {#if localSpyRunning}
                <div class="spy-screenshot-stage spy-local-stage">
                    <pre class="code spy-local-console">{scriptlessStatus?.consoleOutput || scriptlessStatus?.message || "Local Spy Mode is running."}</pre>
                </div>
            {:else if spyState?.screenshotPath}
                <div class="spy-screenshot-stage">
                    {#key spyState.screenshotPath}
                        <button
                            bind:this={screenshotFrameElement}
                            class="spy-screenshot-frame spy-screenshot-button"
                            type="button"
                            aria-label="Spy state screenshot inspector"
                            on:click={handleCanvasClick}
                            on:keydown={handleFrameKeyDown}
                        >
                            <img
                                bind:this={screenshotImageElement}
                                class="spy-screenshot-image"
                                src={screenshotUrl(spyState.screenshotPath)}
                                alt="Spy state screenshot"
                            />

                            {#if displayedWidget}
                                <div
                                    class:selected={selectedWidgetId === displayedWidget.id}
                                    class="spy-widget-outline"
                                    style={outlineStyle(displayedWidget)}
                                ></div>
                            {/if}
                        </button>
                    {/key}
                </div>
            {:else}
                <div class="composition-modal-empty">
                    <h4>No screenshot available</h4>
                    <p>Start Spy Mode and refresh the state to render the current SUT screenshot.</p>
                </div>
            {/if}
        </section>

        <section class="status-card spy-inspector-panel spy-inspector-panel-right">
            <div class="spy-inspector-section spy-inspector-section-actions">
                <div class="spy-hover-panel-header">
                    <div>
                        <span class="eyebrow">Actions</span>
                        <h3>{displayedWidget ? "Derived Actions" : "Available Derived Actions"}</h3>
                    </div>
                </div>

                <div class="spy-candidate-list">
                    {#if displayedWidgetActions.length > 0}
                        {#each displayedWidgetActions as action}
                            <button
                                class="source-item spy-candidate-item"
                                type="button"
                                on:click={() => executeSpyAction(action.id)}
                                disabled={saving}
                            >
                                <span>{action.description}</span>
                                <small>{action.role}</small>
                            </button>
                        {/each}
                    {:else}
                        <p class="progress-message">No derived actions are currently mapped to the selected widget.</p>
                    {/if}
                </div>
            </div>
        </section>
    </div>
</section>
