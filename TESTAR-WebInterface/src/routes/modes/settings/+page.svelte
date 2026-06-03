<script lang="ts">
	import { onMount } from 'svelte';
	import ModeFormFooter from '$lib/components/mode/ModeFormFooter.svelte';
	import ModeSettingsFields from '$lib/components/mode/ModeSettingsFields.svelte';

	type Conf = Record<string, unknown>;

	let fieldsRef: { save: () => Promise<void> };
	let conf: Conf | null = null;

	let loading = true;
	let noProtocol = false;
	let loadMessage: string | null = null;

	onMount(async () => {
		try {
			const res = await fetch('/api/conf');
			if (res.status === 409) {
				noProtocol = true;
				return;
			}
			if (!res.ok) {
				loadMessage = `Request failed (${res.status})`;
				alert(`Request failed (${res.status})`);
				return;
			}
			conf = (await res.json()) as Conf;
		} catch {
			loadMessage = 'Could not reach the settings API.';
		} finally {
			loading = false;
		}
	});
</script>

<svelte:head>
	<title>General settings — TESTAR</title>
</svelte:head>

<div class="page">
	<header class="page__intro">
		<p class="page__eyebrow">Configuration</p>
		<h1 class="page__title">General settings</h1>
		<p class="page__lede">
			Values here map to the active protocol’s <code>test.settings</code>. Use the search box to jump to a field,
			then save when you are done.
		</p>
	</header>

	{#if loading}
		<div class="card card--muted" role="status">
			<p class="card__text">Loading settings…</p>
		</div>
	{:else if noProtocol}
		<div class="card card--warn" role="alert">
			<h2 class="card__title">No protocol selected</h2>
			<p class="card__text">
				Choose a setting on the home page first so the backend knows which protocol to load.
			</p>
			<a class="btn btn--primary" href="/">Go to home</a>
		</div>
	{:else if loadMessage}
		<div class="card card--warn" role="alert">
			<h2 class="card__title">Could not load settings</h2>
			<p class="card__text">{loadMessage}</p>
		</div>
	{:else if conf}
		<ModeSettingsFields
			bind:this={fieldsRef}
			bind:conf
			allowlist={null}
			sectionTitle="All settings"
			filterId="settings-filter"
		/>
		<ModeFormFooter
			helpText="Search or scroll fields, edit what you need, then save once here."
		/>
	{/if}
</div>

<style>
	.page {
		max-width: 1100px;
		margin: 0 auto;
		display: flex;
		flex-direction: column;
		gap: 20px;
	}

	.page__intro {
		margin-bottom: 4px;
	}

	.page__eyebrow {
		margin: 0 0 6px;
		font-size: 12px;
		font-weight: 600;
		letter-spacing: 0.08em;
		text-transform: uppercase;
		color: #2563eb;
	}

	.page__title {
		margin: 0 0 8px;
		font-size: 28px;
		line-height: 1.2;
		color: #0f172a;
	}

	.page__lede {
		margin: 0;
		max-width: 640px;
		color: #475569;
		font-size: 15px;
		line-height: 1.55;
	}

	.page__lede code {
		font-size: 0.92em;
		padding: 1px 6px;
		border-radius: 6px;
		background: #e2e8f0;
		color: #0f172a;
	}

	.card {
		background: #ffffff;
		border: 1px solid rgba(148, 163, 184, 0.35);
		border-radius: 16px;
		padding: 18px 20px;
		box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
	}

	.card--muted {
		background: #f8fafc;
		color: #475569;
	}

	.card--warn {
		border-color: #fecaca;
		background: #fff5f5;
	}

	.card__title {
		margin: 0 0 8px;
		font-size: 18px;
		color: #7f1d1d;
	}

	.card__text {
		margin: 0 0 14px;
		color: #991b1b;
		line-height: 1.5;
	}

	.btn {
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

	.btn--primary {
		background: #2563eb;
		color: #ffffff;
		box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
	}

	.btn--primary:hover {
		transform: translateY(-1px);
		box-shadow: 0 10px 22px rgba(37, 99, 235, 0.28);
	}
</style>
