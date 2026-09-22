<script>
    import { committedSelectChangeState } from "./committedSelectModel.js";
    import { menuHasActivePage, resultMenuItems, runModeMenuItems, testConfigurationMenuItems } from "./webStudioNavigation.js";
    import { WEB_STUDIO_ROLES, roleSelectorChangeState } from "./webStudioRoles.js";

    // Implements WS-UX-TOP-NAV-ROLES-001: workspace selector, role selector, grouped menus, and stable top nav layout.
    export let activeNavMenu = "";
    export let currentPage = "";
    export let currentRole = WEB_STUDIO_ROLES.ADVANCED;
    export let selectedWorkspaceName = "";
    export let workspaces = [];
    export let onWorkspaceManage = () => {};
    export let onWorkspaceChange = () => {};
    export let onRoleChange = () => {};
    export let onToggleMenu = () => {};
    export let onNavigateFromMenu = () => {};
    export let onNavigateToTestOracles = () => {};
    export let onNavigateToTestGoals = () => {};
    export let onNavigateToSpy = () => {};

    function changeRole(event) {
        const nextState = roleSelectorChangeState(currentRole, event.currentTarget.value);
        event.currentTarget.value = nextState.displayedRole;
        onRoleChange(nextState.requestedRole);
    }

    function changeWorkspace(event) {
        const nextState = committedSelectChangeState(selectedWorkspaceName, event.currentTarget.value);
        event.currentTarget.value = nextState.displayedValue;
        onWorkspaceChange(nextState.requestedValue);
    }
</script>

<nav class="panel panel-wide page-nav">
    <div class="page-nav-workspace">
        <button type="button" class="secondary page-workspace-action" on:click={onWorkspaceManage}>
            Workspace
        </button>
        <select
            id="page-workspace-select"
            value={selectedWorkspaceName}
            on:change={changeWorkspace}
        >
            {#each workspaces as workspace}
                <option value={workspace.name}>{workspace.name}</option>
            {/each}
        </select>
    </div>
    <div class="page-nav-role">
        <label for="page-role-select">Role</label>
        <select
            id="page-role-select"
            value={currentRole}
            on:change={changeRole}
        >
            <option value={WEB_STUDIO_ROLES.BASIC}>Basic</option>
            <option value={WEB_STUDIO_ROLES.ADVANCED}>Advanced</option>
        </select>
    </div>
    <div class="page-nav-menu">
        <button
            class:secondary={!menuHasActivePage(testConfigurationMenuItems(currentRole), currentPage)}
            type="button"
            on:click={() => onToggleMenu("configure")}
        >
            &#9881;&#65039; Test Configuration &#9662;
        </button>
        {#if activeNavMenu === "configure"}
            <div class="page-nav-dropdown">
                {#each testConfigurationMenuItems(currentRole) as item}
                    <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => onNavigateFromMenu(item)}>
                        {item.label}
                    </button>
                {/each}
            </div>
        {/if}
    </div>
    <button class:secondary={currentPage !== "oracles"} on:click={onNavigateToTestOracles}>
        &#128302; Test Oracles
    </button>
    <button class:secondary={currentPage !== "test-goals"} on:click={onNavigateToTestGoals}>
        &#127919; Test Goals
    </button>
    <button class:secondary={currentPage !== "spy"} on:click={onNavigateToSpy}>
        &#128269; Spy Mode
    </button>
    <div class="page-nav-menu">
        <button
            class:secondary={!menuHasActivePage(runModeMenuItems(), currentPage)}
            type="button"
            on:click={() => onToggleMenu("run")}
        >
            &#128260; Run Modes &#9662;
        </button>
        {#if activeNavMenu === "run"}
            <div class="page-nav-dropdown">
                {#each runModeMenuItems() as item}
                    <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => onNavigateFromMenu(item)}>
                        {item.label}
                    </button>
                {/each}
            </div>
        {/if}
    </div>
    <div class="page-nav-menu">
        <button
            class:secondary={!menuHasActivePage(resultMenuItems(currentRole), currentPage)}
            type="button"
            on:click={() => onToggleMenu("results")}
        >
            &#128065;&#65039; View Results &#9662;
        </button>
        {#if activeNavMenu === "results"}
            <div class="page-nav-dropdown">
                {#each resultMenuItems(currentRole) as item}
                    <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => onNavigateFromMenu(item)}>
                        {item.label}
                    </button>
                {/each}
            </div>
        {/if}
    </div>
</nav>
