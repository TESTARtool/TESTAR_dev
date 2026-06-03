<script lang="ts">
	import { onMount } from 'svelte';
	import { runMode } from '$lib/components/api.js';
	import ModeFormFooter from '$lib/components/mode/ModeFormFooter.svelte';
	import ModePageShell from '$lib/components/mode/ModePageShell.svelte';
	import ModeSettingsFields from '$lib/components/mode/ModeSettingsFields.svelte';
	import ProtocolPicker from '$lib/components/settingcomponents/ProtocolPicker.svelte';

	type Conf = Record<string, unknown>;
	type TagBuckets = Record<string, string[]>;

	const SUT_CONNECTOR_OPTIONS = [
		{ value: 'SUT_CONNECTOR_CMDLINE', label: 'Command line' },
		{ value: 'SUT_CONNECTOR_WINDOW_TITLE', label: 'SUT window title' },
		{ value: 'SUT_CONNECTOR_PROCESS_NAME', label: 'SUT process name' },
		{ value: 'SUT_CONNECTOR_WEBDRIVER', label: 'WebDriver' },
	];

	let fieldsRef: { save: () => Promise<void> };
	let spysettings: string[] = [];
	let conf: any;
	let all_tags: TagBuckets | null = null;
	let selected_tags = new Map<string, boolean>();
	let loading = true;
	let loadMessage: string | null = null;
	let protocolRequired = false;
	let protocols: Record<string, string> = {};
	let selectedProtocol = '';
	

	function initSelectedTags() {
		if (!all_tags) return;
		const map = new Map<string, boolean>();
		for (const category of Object.keys(all_tags)) {
			const arr = all_tags[category];
			if (!Array.isArray(arr)) continue;
			for (const tag of arr) {
				map.set(tag, false);
			}
		}
		selected_tags = map;
	}

	function toggleTag(tag: string, value: boolean) {
		selected_tags.set(tag, value);
		selected_tags = new Map(selected_tags);
	}

	function selectAll(category: string, value: boolean) {
		if (!all_tags) return;
		const arr = all_tags[category];
		if (!Array.isArray(arr)) return;
		for (const tag of arr) {
			selected_tags.set(tag, value);
		}
		selected_tags = new Map(selected_tags);
	}

	function categoryAllSelected(category: string): boolean {
		if (!all_tags) return false;
		const arr = all_tags[category];
		if (!Array.isArray(arr) || arr.length === 0) return false;
		return arr.every((tag) => selected_tags.get(tag));
	}

	function applySpyTagsToConf() {
		if (!conf) {
			alert("No configuration found. No changes were made!")
			return;
		}
		const selected: string[] = [];
		for (const [tag, isSelected] of selected_tags) {
			if (isSelected) selected.push(tag);
		}


		selected_tags.keys().forEach(key => {
			if (selected_tags.get(key)) {
				conf.SpyTagAttributes.add(selected_tags.get(key))
			}
		});

		conf = { ...conf, SpyTagAttributes: selected };
	}

	async function loadSpyData() {
		loadMessage = null;
		protocolRequired = false;
		conf = null;
		all_tags = null;
		try {
			const rConf = await fetch('/api/conf/');
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

			const [rAvail, rTags] = await Promise.all([
				fetch('/api/conf/spy/available'),
				fetch('/api/conf/spy/tags'),
			]);
			if (!rAvail.ok) {
				loadMessage = `Could not load Spy field list (${rAvail.status})`;
				alert(`Could not load Spy field list (${rAvail.status})`);

				return;
			}
			if (!rTags.ok) {
				loadMessage = `Could not load Spy tags (${rTags.status})`;
				alert(`Could not load Spy tags (${rTags.status})`);
				return;
			}
			spysettings = (await rAvail.json()) as string[];
			all_tags = (await rTags.json()) as TagBuckets;
			initSelectedTags();
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
			await loadSpyData();
		} catch {
			loadMessage = 'Protocol selection failed.';
		}
	}

	onMount(async () => {
		await loadSpyData();
		loading = false;
	});

	function runSpyMode() {
		if (!conf) {
			alert("No configuration found. Cannot Run Spymode")
			return;
		}
		applySpyTagsToConf();

		runMode('Spy', conf);
		alert('Running Spy Mode');
	}

	$: spyFieldKeys = spysettings.filter((k) => k !== 'SpyTagAttributes');
</script>

<svelte:head>
	<title>Spy mode — TESTAR</title>
</svelte:head>

<ModePageShell
	title="Spy Mode"
	description="Check the SUT widgets that TESTAR can recognise at the GUI level. The SPY mode helps you to configure TESTAR by allowing you to inspect the widget controls of the GUI."
	runLabel="Run spy mode"
	onRun={runSpyMode}
	{loading}
	noProtocol={false}
	{loadMessage}
>
	{#if protocolRequired}
		<ProtocolPicker bind:selectedProtocol {protocols} onSubmit={postProtocol} />
	{:else if conf && all_tags}
		<ModeSettingsFields
			bind:this={fieldsRef}
			bind:conf
			allowlist={spyFieldKeys}
			sectionTitle="Spy mode fields"
			filterId="filter-spy-fields"
			enumSelects={{ SUTConnector: SUT_CONNECTOR_OPTIONS }}
		/>

		<section class="tag-card" aria-labelledby="spy-tags-title">
			<div class="tag-card__inner">
				<h2 id="spy-tags-title" class="tag-card__title">Spy Mode Tags</h2>
				<div class="tag-card__body">
					{#each Object.keys(all_tags) as category (category)}
						<details class="tag-card__group">
							<summary><b>{category}</b></summary>
							<input
								id={`select-all-${category}`}
								type="checkbox"
								checked={categoryAllSelected(category)}
								on:change={(e) =>
									selectAll(category, (e.target as HTMLInputElement).checked)}
							/>
							<b><label for={`select-all-${category}`}>Select all</label></b>
							<br />
							{#each all_tags[category] as tag (tag)}
								<input
									id={`${category}-${tag}`}
									type="checkbox"
									checked={selected_tags.get(tag)}
									on:change={(e) =>
										toggleTag(tag, (e.target as HTMLInputElement).checked)}
								/>
								<label for={`${category}-${tag}`}>{tag}</label>
								<br />
							{/each}
						</details>
					{/each}
				</div>
			</div>
		</section>

		<ModeFormFooter
			helpText="Adjust fields and tag checkboxes, save, then use Run spy mode."
		/>
	{/if}
</ModePageShell>

<style>
	.tag-card {
		background: #ffffff;
		border: 1px solid rgba(148, 163, 184, 0.35);
		border-radius: 16px;
		padding: 20px 22px;
		box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
	}

	.tag-card__inner {
		width: 100%;
		max-width: 100%;
		box-sizing: border-box;
		background-color: #f1f5f9;
		border-radius: 12px;
		padding: 16px 18px;
		min-height: 200px;
	}

	.tag-card__title {
		margin: 0 0 12px;
		font-size: 18px;
		font-weight: 600;
		color: #0f172a;
	}

	.tag-card__body {
		font-size: 14px;
		color: #0f172a;
	}

	.tag-card__group {
		margin-bottom: 10px;
	}

	.tag-card__group summary {
		cursor: pointer;
		margin-bottom: 6px;
	}
</style>
