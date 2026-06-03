<script lang="ts">
	export let protocols: Record<string, string> = {};
	export let selectedProtocol = '';
	export let onSubmit: () => void | Promise<void>;
</script>

<div class="proto">
	<h3 class="proto__title">Select protocol first</h3>
	<select bind:value={selectedProtocol} class="proto__select">
		<option value="">Select a protocol…</option>
		{#each Object.entries(protocols) as [key, value] (key)}
			<option value={value}>{key}</option>
		{/each}
	</select>
	<button type="button" class="proto__btn" disabled={!selectedProtocol} on:click={onSubmit}>
		Set protocol
	</button>
</div>

<style>
	.proto {
		border-radius: 16px;
		padding: 20px 22px;
		border: 1px solid rgba(148, 163, 184, 0.35);
		background: #fffbeb;
		box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
	}
	.proto__title {
		margin: 0 0 12px;
		font-size: 18px;
		color: #92400e;
	}
	.proto__select {
		display: block;
		width: 100%;
		max-width: 420px;
		margin-bottom: 12px;
		padding: 8px 10px;
		border-radius: 8px;
		border: 1px solid rgba(148, 163, 184, 0.55);
	}
	.proto__btn {
		padding: 10px 18px;
		border-radius: 999px;
		border: none;
		background: #2563eb;
		color: #fff;
		font-weight: 600;
		cursor: pointer;
	}
	.proto__btn:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}
</style>
