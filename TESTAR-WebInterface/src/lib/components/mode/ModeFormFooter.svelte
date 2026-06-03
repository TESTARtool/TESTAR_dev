<script lang="ts">

	export let helpText = '';

</script>

<div class="mf">
	<p class="mf__help">{helpText}</p>
	<div class="mf__row">
	</div>
</div>

<style>
	.mf {
		margin-top: 8px;
		padding-top: 16px;
		border-top: 1px solid rgba(148, 163, 184, 0.35);
		display: flex;
		flex-direction: column;
		gap: 10px;
		align-items: flex-start;
	}

	.mf__help {
		margin: 0;
		font-size: 13px;
		line-height: 1.45;
		color: #64748b;
		max-width: 640px;
	}

	.mf__row {
		display: flex;
		justify-content: flex-end;
		width: 100%;
	}

	/* .mf__btn {
		padding: 10px 22px;
		border-radius: 999px;
		border: none;
		background: #2563eb;
		color: #fff;
		font-weight: 600;
		font-size: 14px;
		cursor: pointer;
		box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
		transition:
			transform 0.12s ease,
			box-shadow 0.12s ease,
			opacity 0.12s ease;
	}

	.mf__btn:hover:not(:disabled) {
		transform: translateY(-1px);
		box-shadow: 0 10px 22px rgba(37, 99, 235, 0.28);
	}

	.mf__btn:disabled {
		opacity: 0.65;
		cursor: not-allowed;
	} */
</style>
