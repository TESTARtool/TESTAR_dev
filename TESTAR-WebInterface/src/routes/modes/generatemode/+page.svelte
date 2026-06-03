<script lang="ts">
    import { onMount } from "svelte";
    import { runMode } from "$lib/components/api.js";
    import ModeFormFooter from "$lib/components/mode/ModeFormFooter.svelte";
    import ModePageShell from "$lib/components/mode/ModePageShell.svelte";
    import ModeSettingsFields from "$lib/components/mode/ModeSettingsFields.svelte";
    import ProtocolPicker from "$lib/components/settingcomponents/ProtocolPicker.svelte";
    import { goto } from "$app/navigation";
    import { expectedSequences } from "$lib/stores/progress";

    type Conf = Record<string, unknown>;

    let fieldsRef: { save: () => Promise<void> };
    let conf: Conf | null = null;
    let modeKeys: string[] = [];
    let loading = true;
    let loadMessage: string | null = null;
    let protocolRequired = false;
    let protocols: Record<string, string> = {};
    let selectedProtocol = "";
	let stateModelsNotice: string | null = null;

    async function loadModeData() {
        loadMessage = null;
        protocolRequired = false;
        conf = null;
        try {
            const rAvail = await fetch("/api/conf/generate/available");
            if (!rAvail.ok) {
                loadMessage = `Could not load Generate field list (${rAvail.status})`;
                alert(`Could not load Generate field list (${rAvail.status})`);
                return;
            }
            modeKeys = (await rAvail.json()) as string[];

            const rConf = await fetch("/api/conf");
            if (rConf.status === 409) {
                const protoRes = await fetch("/api/conf/protocol/available");
                if (!protoRes.ok) {
                    loadMessage = `Could not load protocols (${protoRes.status})`;
                    alert(`Could not load protocols (${protoRes.status})`);
                    return;
                }
                protocols = await protoRes.json();
                protocolRequired = true;
                return;
            }
            if (!rConf.ok) {
                loadMessage = `Could not load settings (${rConf.status})`;
                alert(`Could not load settings (${rConf.status})`);
                return;
            }
            conf = (await rConf.json()) as Conf;
        } catch {
            loadMessage = "Could not reach the settings API.";
        }
    }

    async function postProtocol() {
        try {
            const res = await fetch("/api/conf/protocol", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(selectedProtocol),
            });
            if (!res.ok) {
                loadMessage = `Protocol selection failed (${res.status})`;
                alert(`Protocol selection failed (${res.status})`);
                return;
            }
            selectedProtocol = "";
            await loadModeData();
        } catch {
            loadMessage = "Protocol selection failed.";
        }
    }

    onMount(async () => {
        await loadModeData();
        loading = false;
    });

    function runGenerateMode() {
        if (!conf) {
            alert("Could not load settings, running generate mode failed")
            return;
        }

        if (typeof conf.Sequences != "number") {
            alert("Sequences is not a number");
            return;
        }

        expectedSequences.set(conf.Sequences);
        runMode("Generate", conf);
        goto("/modes/viewmode");
    }

	function showStateModels() {
		stateModelsNotice =
			'State models are not available yet. The backend API will be connected in a later task.';
		// Future: 
        // const res = await fetch('/api/...'); 
        // if (!res.ok) {
        //      alert()
        // }
	}
</script>

<svelte:head>
    <title>Generate mode — TESTAR</title>
</svelte:head>

<ModePageShell
    title="Generate Mode"
    description="Generate test sequences automatically. TESTAR derives a set of possible actions for the current state that the GUI of the SUT is in."
    runLabel="Run generate mode"
    onRun={runGenerateMode}
    {loading}
    noProtocol={false}
    {loadMessage}
    >
    
	<button
		slot="actions"
		class="mp__btn mp__btn--primary"
		type="button"
		on:click={showStateModels}
	>
		View State Models
	</button>

	{#if stateModelsNotice}
		<p class="mock-notice" role="status">{stateModelsNotice}</p>
	{/if}

	{#if protocolRequired}
		<ProtocolPicker bind:selectedProtocol {protocols} onSubmit={postProtocol} />
	{:else if conf}
		<ModeSettingsFields
			bind:this={fieldsRef}
			bind:conf
			allowlist={modeKeys}
			sectionTitle="Generate mode fields"
			filterId="filter-generate"
		/>
		<ModeFormFooter
			helpText="Change the values you need, then save. When everything looks right, press 'run generate mode' above."
		/>
	{/if}
</ModePageShell>

<style>
	.mock-notice {
		margin: 0;
		padding: 12px 16px;
		border-radius: 12px;
		border: 1px solid rgba(148, 163, 184, 0.35);
		background: #f8fafc;
		color: #475569;
		font-size: 14px;
	}

    .mp__btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		padding: 10px 18px;
		border-radius: 999px;
		border: 1px solid transparent;
		font-weight: 600;
		font-size: 14px;
		cursor: pointer;
		text-decoration: none;
		transition:
			transform 0.12s ease,
			box-shadow 0.12s ease,
			background 0.12s ease;
	}

    .mp__btn--primary {
		background: #2563eb;
		color: #ffffff;
		box-shadow: 0 10px 20px rgba(37, 99, 235, 0.25);
	}

	.mp__btn--primary:hover {
		transform: translateY(-1px);
		box-shadow: 0 12px 24px rgba(37, 99, 235, 0.3);
	}
</style>
