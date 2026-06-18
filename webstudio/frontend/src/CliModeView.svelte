<script>
    export let cliAgentSettings = null;
    export let cliStatus = null;
    export let saving = false;
    export let selectedWorkspaceAvailableInCli = false;
    export let saveCliAgentSettings;
    export let startCliAgentSession;
    export let startCliManualSession;
    export let runCliManualCommand;
    export let stopCliAgentSession;
    export let stopCliManualSession;

    let manualPlatform = "webdriver";
    let manualTarget = "";
    let manualCommandLine = "sessionStatus";

    $: manualSessionRunning = cliStatus?.status === "running" && cliStatus?.mode === "manual";
    $: agentSessionRunning = cliStatus?.status === "running" && cliStatus?.mode === "agent";
    $: cliSessionRunning = manualSessionRunning || agentSessionRunning;

    function startSession() {
        startCliManualSession(manualPlatform, manualTarget);
    }

    function startAgentSession() {
        startCliAgentSession(manualPlatform, manualTarget);
    }

    function runCommand() {
        runCliManualCommand(manualCommandLine);
    }

    function stopCurrentSession() {
        if (manualSessionRunning) {
            stopCliManualSession();
            return;
        }

        if (agentSessionRunning) {
            stopCliAgentSession();
        }
    }
</script>

<section class="panel panel-wide status-panel cli-mode-panel">
    <div class="status-panel-header">
        <div>
            <p class="eyebrow">CLI</p>
            <h2>Run CLI Mode</h2>
        </div>
        <div class="button-row">
            <button on:click={startSession} disabled={saving || cliSessionRunning || !selectedWorkspaceAvailableInCli || !manualTarget.trim()}>
                Start Manual CLI Session
            </button>
            <button
                type="button"
                on:click={startAgentSession}
                disabled={saving || cliSessionRunning || !selectedWorkspaceAvailableInCli || !manualTarget.trim()}
            >
                Start Agent CLI Execution
            </button>
            <button
                type="button"
                class="secondary"
                on:click={stopCurrentSession}
                disabled={saving || !cliSessionRunning}
            >
                Stop CLI Execution
            </button>
        </div>
    </div>

    <div class="cli-mode-layout">
        <section class="status-card cli-mode-column cli-mode-column-left">
            <div class="cli-mode-section cli-mode-section-compact">
                <div class="cli-mode-section-header">
                    <span class="eyebrow">Manual CLI Session</span>
                </div>
                <p class="progress-message">Start a CLI session, then inspect state, derive actions, execute actions, and stop the session.</p>
                {#if !selectedWorkspaceAvailableInCli}
                    <p class="progress-message">The selected profile is not available in the CLI settings distribution.</p>
                {/if}
                <label class="field-label" for="cli-manual-platform">Platform</label>
                <select id="cli-manual-platform" bind:value={manualPlatform}>
                    <option value="webdriver">webdriver</option>
                    <option value="windows">windows</option>
                    <option value="android">android</option>
                </select>
                <label class="field-label" for="cli-manual-target">Target</label>
                <input id="cli-manual-target" bind:value={manualTarget} placeholder="https://para.testar.org/ or notepad.exe" />
            </div>

            <div class="cli-mode-section cli-mode-section-commands">
                <div class="cli-mode-section-header">
                    <span class="eyebrow">Manual CLI Commands</span>
                </div>
                <p class="progress-message">Use or enter the CLI commands.</p>
                <label class="field-label" for="cli-manual-command">CLI command</label>
                <div class="cli-command-row">
                    <input
                        id="cli-manual-command"
                        bind:value={manualCommandLine}
                        placeholder='getState, getDerivedActions, executeAction click "Log In"'
                    />
                    <button class="secondary" type="button" on:click={runCommand} disabled={saving || agentSessionRunning || !manualCommandLine.trim()}>
                        Run
                    </button>
                </div>
                <div class="cli-quick-actions cli-quick-actions-grid">
                    <span>Default Control Commands</span>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("sessionStatus")} disabled={saving || agentSessionRunning}>sessionStatus</button>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("getState")} disabled={saving || agentSessionRunning}>getState</button>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("getStateScreenshot")} disabled={saving || agentSessionRunning}>getStateScreenshot</button>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("getDerivedActions")} disabled={saving || agentSessionRunning}>getDerivedActions</button>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("stopSession")} disabled={saving || agentSessionRunning}>stopSession</button>
                    <button class="secondary" type="button" on:click={() => runCliManualCommand("shutdownDaemon")} disabled={saving || agentSessionRunning}>stopDaemon</button>
                </div>
                <div class="cli-command-help">
                    <span>Execute Action Examples</span>
                    <code>executeAction click "Log In"</code>
                    <code>executeAction type "username" "john"</code>
                    <code>executeAction select "account" "12345"</code>
                </div>
            </div>
        </section>

        <section class="status-card cli-mode-console-panel">
            <div class="cli-mode-section-header">
                <div>
                    <span class="eyebrow">Runtime</span>
                    <h3>CLI Console</h3>
                </div>
                <span class="run-outcomes-progress">{cliStatus?.status || "idle"}</span>
            </div>
            <p class="progress-message">{cliStatus?.message || "CLI backend is idle."}</p>
            <pre class="code console-output cli-console-output">{cliStatus?.consoleOutput || "No CLI output yet."}</pre>
        </section>

        <section class="status-card cli-mode-column cli-mode-column-right">
            <div class="cli-mode-section cli-mode-section-agent">
                <div class="cli-mode-section-header">
                    <span class="eyebrow">Agent CLI Settings</span>
                </div>
                <p class="progress-message">Codex settings for agent-controlled CLI mode.</p>
                <label class="field-label" for="cli-agent-api-key">API key env var</label>
                <input id="cli-agent-api-key" bind:value={cliAgentSettings.apiKeyEnvVarName} />
                <label class="field-label" for="cli-agent-model">Model</label>
                <input id="cli-agent-model" bind:value={cliAgentSettings.model} />
                <label class="field-label" for="cli-agent-base-url">Base URL</label>
                <input id="cli-agent-base-url" bind:value={cliAgentSettings.baseUrl} placeholder="Optional" />
                <div class="cli-settings-grid">
                    <div>
                        <label class="field-label" for="cli-agent-reasoning">Reasoning effort</label>
                        <select id="cli-agent-reasoning" bind:value={cliAgentSettings.reasoningEffort}>
                            <option value="low">low</option>
                            <option value="medium">medium</option>
                            <option value="high">high</option>
                        </select>
                    </div>
                    <div>
                        <label class="field-label" for="cli-agent-sandbox">Sandbox mode</label>
                        <select id="cli-agent-sandbox" bind:value={cliAgentSettings.sandboxMode}>
                            <option value="read-only">read-only</option>
                            <option value="workspace-write">workspace-write</option>
                            <option value="danger-full-access">danger-full-access</option>
                        </select>
                    </div>
                </div>
                <div class="cli-settings-grid">
                    <div>
                        <label class="field-label" for="cli-agent-approval">Approval policy</label>
                        <select id="cli-agent-approval" bind:value={cliAgentSettings.approvalPolicy}>
                            <option value="never">never</option>
                            <option value="on-request">on-request</option>
                            <option value="on-failure">on-failure</option>
                            <option value="untrusted">untrusted</option>
                        </select>
                    </div>
                    <div class="cli-agent-flags">
                        <label><input type="checkbox" bind:checked={cliAgentSettings.networkAccessEnabled} /> Network access</label>
                        <label><input type="checkbox" bind:checked={cliAgentSettings.skipGitRepoCheck} /> Skip git repo check</label>
                    </div>
                </div>
                <label class="field-label" for="cli-agent-prompt-title">Prompt title</label>
                <input id="cli-agent-prompt-title" bind:value={cliAgentSettings.promptTitle} />
                <label class="field-label" for="cli-agent-prompt">Prompt</label>
                <textarea id="cli-agent-prompt" bind:value={cliAgentSettings.promptText} rows="4"></textarea>
                <div class="button-row cli-mode-section-footer">
                    <button type="button" on:click={saveCliAgentSettings} disabled={saving}>
                        Save Agent CLI Settings
                    </button>
                </div>
            </div>
        </section>
    </div>
</section>
