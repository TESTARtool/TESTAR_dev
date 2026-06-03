<script lang="ts">
	import { onMount } from 'svelte';
	import { runMode } from '$lib/components/api.js';
	import ModeFormFooter from '$lib/components/mode/ModeFormFooter.svelte';
	import ModePageShell from '$lib/components/mode/ModePageShell.svelte';
	import ModeSettingsFields from '$lib/components/mode/ModeSettingsFields.svelte';
	import ProtocolPicker from '$lib/components/settingcomponents/ProtocolPicker.svelte';

	type Conf = Record<string, unknown>;

	let fieldsRef: { save: () => Promise<void> };
	let conf: Conf | null = null;
	let modeKeys: string[] = [];
	let loading = true;
	let loadMessage: string | null = null;
	let protocolRequired = false;
	let protocols: Record<string, string> = {};
	let selectedProtocol = '';

	async function loadModeData() {
		loadMessage = null;
		protocolRequired = false;
		conf = null;
		try {
			const rAvail = await fetch('/api/conf/replay/available');
			if (!rAvail.ok) {
				loadMessage = `Could not load Replay field list (${rAvail.status})`;
				alert(`Could not load Replay field list (${rAvail.status})`);
				return;
			}
			modeKeys = (await rAvail.json()) as string[];

			const rConf = await fetch('/api/conf');
			if (rConf.status === 409) {
				const protoRes = await fetch('/api/conf/protocol/available');
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
			loadMessage = 'Could not reach the settings API.';
		}
	}

	async function postProtocol() {
		try {
			const res = await fetch('/api/conf/protocol', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(selectedProtocol),
			});
			if (!res.ok) {
				loadMessage = `Protocol selection failed (${res.status})`;
				alert(`Protocol selection failed (${res.status})`);
				return;
			}
			selectedProtocol = '';
			await loadModeData();
		} catch {
			loadMessage = 'Protocol selection failed.';
		}
	}

	onMount(async () => {
		await loadModeData();
		loading = false;
	});

	function runReplayMode() {
		if (!conf) {
			alert('Could not find settings');
			return;
		}
		runMode('Replay', conf);
		alert('Running Replay Mode');
	}
</script>

<svelte:head>
	<title>Replay mode — TESTAR</title>
</svelte:head>

<ModePageShell
	title="Replay Mode"
	description="Re-execute an existing test sequence. When you click the REPLAY button, a file explorer window allows you to select the sequence that you want to replay or view."
	runLabel="Run replay mode"
	onRun={runReplayMode}
	{loading}
	noProtocol={false}
	{loadMessage}
>
	{#if protocolRequired}
		<ProtocolPicker bind:selectedProtocol {protocols} onSubmit={postProtocol} />
	{:else if conf}
		<ModeSettingsFields
			bind:this={fieldsRef}
			bind:conf
			allowlist={modeKeys}
			sectionTitle="Replay mode fields"
			filterId="filter-replay"
		/>
		<ModeFormFooter
			helpText="Set replay options, save, then press Run replay mode above."
		/>
	{/if}
</ModePageShell>
