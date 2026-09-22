<script>
    import CliModeView from "./CliModeView.svelte";
    import RunTestarView from "./RunTestarView.svelte";
    import SpyModeView from "../spy/SpyModeView.svelte";

    export let cliAgentSettings;
    export let cliStatus;
    export let currentPage = "";
    export let savedCliAgentSettings;
    export let saving = false;
    export let scriptlessStatus = null;
    export let selectedWorkspaceName = "";
    export let selectedWorkspaceSutConnector = "";
    export let selectedWorkspaceSutConnectorValue = "";
    export let selectedWorkspaceCliStateProjectionMode = "";
    export let spyState = null;
    export let executeSpyAction = () => {};
    export let executeSpyWidgetDefaultAction = () => {};
    export let executeSpyWidgetDirectType = () => {};
    export let refreshRemoteSpyMode = () => {};
    export let runCliManualCommand = () => {};
    export let saveCliAgentSettings = () => {};
    export let startCliAgentSession = () => {};
    export let startCliManualSession = () => {};
    export let startGenerate = () => {};
    export let startLocalSpyMode = () => {};
    export let startRemoteSpyMode = () => {};
    export let stopCliAgentSession = () => {};
    export let stopCliManualSession = () => {};
    export let stopGenerate = () => {};
    export let stopLocalSpyMode = () => {};
    export let stopRemoteSpyMode = () => {};
</script>

{#if currentPage === "run"}
    <RunTestarView
        saving={saving}
        scriptlessStatus={scriptlessStatus}
        selectedWorkspaceName={selectedWorkspaceName}
        selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
        startGenerate={startGenerate}
        stopGenerate={stopGenerate}
    />
{/if}

{#if currentPage === "spy"}
    <SpyModeView
        scriptlessStatus={scriptlessStatus}
        saving={saving}
        selectedWorkspaceName={selectedWorkspaceName}
        selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
        spyState={spyState}
        startRemoteSpyMode={startRemoteSpyMode}
        refreshRemoteSpyMode={refreshRemoteSpyMode}
        stopRemoteSpyMode={stopRemoteSpyMode}
        startLocalSpyMode={startLocalSpyMode}
        stopLocalSpyMode={stopLocalSpyMode}
        executeSpyAction={executeSpyAction}
        executeSpyWidgetDefaultAction={executeSpyWidgetDefaultAction}
        executeSpyWidgetDirectType={executeSpyWidgetDirectType}
    />
{/if}

{#if currentPage === "cli"}
    {#key `cli:${selectedWorkspaceName}`}
        <CliModeView
            cliAgentSettings={cliAgentSettings}
            cliStatus={cliStatus}
            savedCliAgentSettings={savedCliAgentSettings}
            saving={saving}
            saveCliAgentSettings={saveCliAgentSettings}
            selectedWorkspaceSutConnector={selectedWorkspaceSutConnector}
            selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
            selectedWorkspaceCliStateProjectionMode={selectedWorkspaceCliStateProjectionMode}
            selectedWorkspaceAvailableInCli={Boolean(selectedWorkspaceName)}
            startCliAgentSession={startCliAgentSession}
            startCliManualSession={startCliManualSession}
            runCliManualCommand={runCliManualCommand}
            stopCliAgentSession={stopCliAgentSession}
            stopCliManualSession={stopCliManualSession}
        />
    {/key}
{/if}
