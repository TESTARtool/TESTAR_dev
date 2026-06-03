<script lang="ts">
	type Conf = Record<string, unknown>;

	export let conf: Conf | null = null;
	export let allowlist: string[] | null = null;
	export let sectionTitle = 'Mode settings';
	export let filterId = 'mode-settings-filter';
	export let enumSelects: Record<string, { value: string; label: string }[]> = {};
	/** When true, Save is shown next to the search bar —if false, the save button is below all settings */
	export let saveButtonOnTop = true;

	let arrayEdits: Record<string, string> = {};
	let filter = '';
	let saving = false;
	let saveBanner: 'ok' | 'err' | null = null;
	let saveDetail = '';

	$: sortedKeys =
		conf == null
			? []
			: (() => {
					const keys = Object.keys(conf);
					const use =
						allowlist && allowlist.length > 0
							? keys.filter((k) => allowlist.includes(k))
							: keys;
					return use.sort((a, b) => a.localeCompare(b));
				})();

	$: visibleKeys = filter.trim()
		? sortedKeys.filter((k) => {
				if (!conf) return false;
				const q = filter.toLowerCase();
				const v = conf[k];
				return (
					k.toLowerCase().includes(q) ||
					(typeof v !== 'object' && String(v).toLowerCase().includes(q))
				);
			})
		: sortedKeys;

	$: if (conf) {
		for (const k of sortedKeys) {
			const v = conf[k];
			if (Array.isArray(v) && arrayEdits[k] === undefined) {
				arrayEdits[k] = JSON.stringify(v);
			}
		}
	}

	function humanKey(key: string) {
		return key
			.replace(/([a-z])([A-Z])/g, '$1 $2')
			.replace(/_/g, ' ');
	}

	function setBoolean(key: string, checked: boolean) {
		if (!conf) return;
		conf = { ...conf, [key]: checked };
	}

	function setNumber(key: string, raw: string) {
		if (!conf) return;
		const n = Number(raw);
		conf = { ...conf, [key]: Number.isFinite(n) ? n : conf[key] };
	}

	function setString(key: string, value: string) {
		if (!conf) return;
		conf = { ...conf, [key]: value };
	}

	export async function save() {
		if (!conf) return;
		saving = true;
		saveBanner = null;
		saveDetail = '';
		try {
			const payload: Conf = { ...conf };
			for (const k of Object.keys(arrayEdits)) {
				if (!(k in payload)) continue;
				if (!Array.isArray(conf[k])) continue;
				try {
					const parsed = JSON.parse(arrayEdits[k]);
					if (!Array.isArray(parsed)) {
						saveBanner = 'err';
						saveDetail = `${k}: expected a JSON array.`;
						return;
					}
					payload[k] = parsed;
				} catch {
					saveBanner = 'err';
					saveDetail = `${k}: invalid JSON.`;
					return;
				}
			}

			const res = await fetch('/api/conf', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload),
			});

			if (!res.ok) {
				const text = await res.text();
				let msg = `Save failed (${res.status})`;
				try {
					const j = JSON.parse(text) as { error?: string };
					if (j.error) msg = j.error;
				} catch {
					if (text) msg = text;
				}
				saveBanner = 'err';
				saveDetail = msg;
				return;
			}
			saveBanner = 'ok';
			saveDetail = 'Changes were written to test.settings.';
		} finally {
			saving = false;
		}
	}
</script>

{#if conf}
	<div class="wrap">
		<h2 class="section-title">{sectionTitle}</h2>

		{#if saveBanner}
			<div
				class="banner"
				class:banner--ok={saveBanner === 'ok'}
				class:banner--err={saveBanner === 'err'}
				role="status"
			>
				{saveDetail}
			</div>
		{/if}

		<div class="toolbar card">
			<label class="sr-only" for={filterId}>Filter settings</label>
			<input
				id={filterId}
				class="toolbar__search"
				type="search"
				placeholder="Search by name or value…"
				autocomplete="off"
				bind:value={filter}
			/>
			<div class="toolbar__actions">
				<span class="toolbar__meta">{visibleKeys.length} of {sortedKeys.length} fields</span>
				{#if saveButtonOnTop}
					<button class="btn btn--primary" type="button" disabled={saving} on:click={save}>
						{saving ? 'Saving…' : 'Save changes'}
					</button>
				{/if}
			</div>
		</div>

		<div class="grid">
			{#each visibleKeys as key (key)}
				{@const v = conf[key]}
				<section class="field card">
					<div class="field__head">
						<h3 class="field__title" id="label-{filterId}-{key}">{humanKey(key)}</h3>
						<code class="field__key">{key}</code>
					</div>

					<div class="field__control">
						{#if enumSelects[key]}
							<select
								class="input"
								aria-labelledby="label-{filterId}-{key}"
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								{#each enumSelects[key] as opt (opt.value)}
									<option value={opt.value}>{opt.label}</option>
								{/each}
							</select>
						{:else if key === "DataStoreMode"}
							<select
								class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								<option value="none">None</option>
								<option value="instant">Instant</option>
								<option value="delayed">Delayed</option>
								<option value="hybrid">Hybrid</option>
							</select>	
						{:else if key === "DataStoreType"}
							<select  
							    class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								<option value="remote">Remote</option>
								<option value="plocal">pLocal</option>
							</select>
						{:else if key === "ActionSelectionAlgorithm"}
							<select 
							    class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								<option value="unvisited">Unvisited actions first</option>
								<option value="random">Random selection</option>
							</select>
						{:else if key === "SUTConnector"}
                            <select  
							    class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
                                <option value="SUT_CONNECTOR_CMDLINE">Command Line</option>
                                <option value="SUT_CONNECTOR_WINDOW_TITLE">SUT Window Title</option>
                                <option value="SUT_CONNECTOR_PROCESS_NAME">SUT Process Name</option>
                                <option value="SUT_CONNECTOR_WEBDRIVER">Web Driver</option>
                            </select>
						{:else if key === "LlmPlatform"}
							<select  
							    class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								<option value="OpenAI" selected>OpenAI</option>
								<option value="Gemini">Gemini</option>
							</select>
						{:else if key === "LlmReasoning"}
							<select  
							    class="input"
								name={key}
								id={key}
								value={String(v ?? '')}
								on:change={(e) => setString(key, e.currentTarget.value)}
							>
								<option value="default" selected>Default</option>
								<option value="minimal">Minimal</option>
								<option value="low">Low</option>
								<option value="medium">Medium</option>
								<option value="high">High</option>
							</select>
						{:else if typeof v === 'boolean'}
							<label class="toggle">
								<input
									type="checkbox"
									checked={v}
									on:change={(e) => setBoolean(key, e.currentTarget.checked)}
								/>
								<span class="toggle__ui" aria-hidden="true"></span>
								<span class="toggle__text">{v ? 'Enabled' : 'Disabled'}</span>
							</label>
						{:else if typeof v === 'number'}
							<input
								class="input"
								type="number"
								step="any"
								aria-labelledby="label-{filterId}-{key}"
								value={v}
								on:input={(e) => setNumber(key, e.currentTarget.value)}
							/>
						{:else if Array.isArray(v)}
							<p class="field__hint">JSON array.</p>
							<textarea
								class="input input--area input--mono"
								rows="4"
								aria-labelledby="label-{filterId}-{key}"
								bind:value={arrayEdits[key]}
							></textarea>
						{:else if v !== null && typeof v === 'object'}
							<p class="field__hint">JSON object (read-only here)</p>
							<pre class="json-block">{JSON.stringify(v, null, 2)}</pre>
						{:else}
							{#if typeof v === 'string' && v.length > 120}
								<textarea
									class="input input--area"
									rows="4"
									aria-labelledby="label-{filterId}-{key}"
									value={v}
									on:input={(e) => setString(key, e.currentTarget.value)}
								></textarea>
							{:else}
								<input
									class="input"
									type="text"
									aria-labelledby="label-{filterId}-{key}"
									value={v == null ? '' : String(v)}
									on:input={(e) => setString(key, e.currentTarget.value)}
								/>
							{/if}
						{/if}
					</div>
				</section>
			{/each}
		</div>

		{#if !saveButtonOnTop}
			<div class="footer-actions">
				<button class="btn btn--primary" type="button" disabled={saving} on:click={save}>
					{saving ? 'Saving…' : 'Save changes'}
				</button>
			</div>
		{/if}
	</div>
{/if}

<style>
	.wrap {
		display: flex;
		flex-direction: column;
		gap: 16px;
	}

	.section-title {
		margin: 0;
		font-size: 18px;
		font-weight: 600;
		color: #0f172a;
	}

	.card {
		background: #ffffff;
		border: 1px solid rgba(148, 163, 184, 0.35);
		border-radius: 16px;
		padding: 18px 20px;
		box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
	}

	.toolbar {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		gap: 14px;
		justify-content: space-between;
	}

	.toolbar__search {
		flex: 1 1 220px;
		min-width: 0;
		padding: 10px 14px;
		border-radius: 12px;
		border: 1px solid rgba(148, 163, 184, 0.55);
		font-size: 14px;
	}

	.toolbar__actions {
		display: flex;
		align-items: center;
		gap: 12px;
		flex-wrap: wrap;
	}

	.toolbar__meta {
		font-size: 13px;
		color: #64748b;
	}

	.banner {
		padding: 12px 16px;
		border-radius: 12px;
		font-size: 14px;
		line-height: 1.45;
	}

	.banner--ok {
		background: #ecfdf3;
		border: 1px solid #bbf7d0;
		color: #14532d;
	}

	.banner--err {
		background: #fef2f2;
		border: 1px solid #fecaca;
		color: #7f1d1d;
	}

	.grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
		gap: 14px;
	}

	.field__head {
		display: flex;
		flex-direction: column;
		gap: 4px;
		margin-bottom: 10px;
	}

	.field__title {
		margin: 0;
		font-size: 16px;
		font-weight: 600;
		color: #0f172a;
	}

	.field__key {
		font-size: 12px;
		color: #64748b;
		background: #f1f5f9;
		padding: 2px 8px;
		border-radius: 6px;
		width: fit-content;
	}

	.field__hint {
		margin: 0 0 8px;
		font-size: 12px;
		color: #64748b;
	}

	.field__control {
		display: flex;
		flex-direction: column;
		gap: 8px;
	}

	.input {
		width: 100%;
		box-sizing: border-box;
		padding: 10px 12px;
		border-radius: 10px;
		border: 1px solid rgba(148, 163, 184, 0.65);
		font-size: 14px;
		color: #0f172a;
		background: #fff;
	}

	.input:focus {
		outline: 2px solid rgba(37, 99, 235, 0.35);
		outline-offset: 1px;
		border-color: #2563eb;
	}

	.input--area {
		resize: vertical;
		min-height: 88px;
		font-family: inherit;
	}

	.input--mono {
		font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
		font-size: 13px;
	}

	.json-block {
		margin: 0;
		padding: 12px;
		border-radius: 10px;
		background: #f8fafc;
		border: 1px solid #e2e8f0;
		font-size: 12px;
		overflow: auto;
		max-height: 200px;
	}

	.toggle {
		display: inline-flex;
		align-items: center;
		gap: 10px;
		cursor: pointer;
		user-select: none;
	}

	.toggle input {
		position: absolute;
		opacity: 0;
		width: 0;
		height: 0;
	}

	.toggle__ui {
		width: 44px;
		height: 24px;
		border-radius: 999px;
		background: #cbd5e1;
		position: relative;
		transition: background 0.15s ease;
		flex-shrink: 0;
	}

	.toggle__ui::after {
		content: '';
		position: absolute;
		top: 3px;
		left: 3px;
		width: 18px;
		height: 18px;
		border-radius: 50%;
		background: #fff;
		box-shadow: 0 1px 3px rgba(15, 23, 42, 0.2);
		transition: transform 0.15s ease;
	}

	.toggle input:checked + .toggle__ui {
		background: #22c55e;
	}

	.toggle input:checked + .toggle__ui::after {
		transform: translateX(20px);
	}

	.toggle__text {
		font-size: 14px;
		color: #334155;
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

	.btn:disabled {
		opacity: 0.55;
		cursor: not-allowed;
	}

	.btn--primary {
		background: #2563eb;
		color: #ffffff;
		box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
	}

	.btn--primary:hover:not(:disabled) {
		transform: translateY(-1px);
		box-shadow: 0 10px 22px rgba(37, 99, 235, 0.28);
	}

	.footer-actions {
		display: flex;
		justify-content: flex-end;
		padding-bottom: 4px;
	}

	.sr-only {
		position: absolute;
		width: 1px;
		height: 1px;
		padding: 0;
		margin: -1px;
		overflow: hidden;
		clip: rect(0, 0, 0, 0);
		white-space: nowrap;
		border: 0;
	}
</style>
