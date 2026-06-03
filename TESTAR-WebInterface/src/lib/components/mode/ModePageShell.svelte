<script lang="ts">
	import DownloadButton from "$lib/components/DownloadButton.svelte";

	export let title: string;
	export let description: string;
	export let runLabel: string;
	export let onRun: () => void;
	export let loading = true;
	export let noProtocol = false;
	export let loadMessage: string | null = null;

	let stopNotice: string | null = null;

	function stopRun() {
		stopNotice =
			'Stopping TESTAR is not available yet. The backend stop API will be connected in a later task.';
		// Future: 
		// await fetch('/api/stop/', { method: 'POST' });
	}
</script>

<section class="mp">
	<header class="mp__header">
		<div>
			<p class="mp__eyebrow">Mode</p>
			<h1 class="mp__title">{title}</h1>
			<p class="mp__description">{description}</p>
		</div>
    	<div class="page__actions">
			<button class="mp__btn mp__btn--danger" type="button" on:click={stopRun}>Stop</button>
			<button class="mp__btn mp__btn--primary" type="button" on:click={onRun}>
				{runLabel}
			</button>
			<DownloadButton class="mp__btn mp__btn--primary svelte-199mm92" type="button">
            	Download results
        	</DownloadButton>
			<slot name="actions"/>
		</div>
	</header>

	{#if loading}
		<div class="mp__card mp__card--muted" role="status">
			<p class="mp__card-text">Loading…</p>
		</div>
	{:else if noProtocol}
		<div class="mp__card mp__card--warn" role="alert">
			<h2 class="mp__card-title">No protocol selected</h2>
			<p class="mp__card-text">Choose a setting on the home page first so the backend can load settings.</p>
			<a class="mp__btn mp__btn--primary" href="/">Go to home</a>
		</div>
	{:else if loadMessage}
		<div class="mp__card mp__card--warn" role="alert">
			<h2 class="mp__card-title">Could not load</h2>
			<p class="mp__card-text">{loadMessage}</p>
		</div>
	{:else}
		<div class="mp__slot">
			{#if stopNotice}
				<p class="mp__mock-notice" role="status">{stopNotice}</p>
			{/if}
			<slot />
		</div>
	{/if}
</section>

<style>
	.mp {
		max-width: 1100px;
		margin: 0 auto;
		display: flex;
		flex-direction: column;
		gap: 20px;
	}

	.mp__header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 20px;
		padding: 24px 28px;
		border-radius: 20px;
		background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
		border: 1px solid rgba(148, 163, 184, 0.2);
		box-shadow: 0 18px 36px rgba(15, 23, 42, 0.08);
		flex-wrap: wrap;
	}

	.mp__eyebrow {
		margin: 0 0 6px;
		font-size: 12px;
		letter-spacing: 0.18em;
		text-transform: uppercase;
		color: #64748b;
		font-weight: 600;
	}

	.mp__title {
		margin: 0 0 8px;
		color: #0f172a;
		font-size: 28px;
		line-height: 1.15;
	}

	.mp__description {
		margin: 0 0 8px;
		color: #0f172a;
		font-size: 15px;
	}

	.mp__slot {
		display: flex;
		flex-direction: column;
		gap: 16px;
	}

	.mp__card {
		border-radius: 16px;
		padding: 18px 20px;
		border: 1px solid rgba(148, 163, 184, 0.35);
		box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
	}

	.mp__card--muted {
		background: #f8fafc;
		color: #475569;
	}

	.mp__card--warn {
		border-color: #fecaca;
		background: #fff5f5;
	}

	.mp__card-title {
		margin: 0 0 8px;
		font-size: 18px;
		color: #7f1d1d;
	}

	.mp__card-text {
		margin: 0 0 14px;
		color: #991b1b;
		line-height: 1.5;
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

	/* .mp__btn--secondary {
		background: #fff;
		color: #1e40af;
		border: 1px solid rgba(37, 99, 235, 0.45);
		box-shadow: none;
	}

	.mp__btn--secondary:hover {
		transform: translateY(-1px);
		background: #eff6ff;
	} */

	.mp__btn--danger {
		background: #b91c1c;
		color: #fff;
		border: 1px solid rgba(220, 38, 38, 0.45);
		box-shadow: none;
	}

	.mp__btn--danger:hover {
		transform: translateY(-1px);
		background: #b44141b4;
	}

	.mp__mock-notice {
		margin: 0;
		padding: 12px 16px;
		border-radius: 12px;
		border: 1px solid rgba(148, 163, 184, 0.35);
		background: #f8fafc;
		color: #475569;
		font-size: 14px;
	}

	.page__actions {
        margin-left: auto;
        display: flex;
        gap: 10px;
    }
</style>
