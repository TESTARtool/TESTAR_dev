<script lang="ts">
	import { browser } from '$app/environment';
	import logo from '$lib/assets/testar.png';
    import { runMode } from '$lib/components/api';
	import { onMount } from 'svelte';

	const SELECTED_SETTING_STORAGE_KEY = 'testar-webinterface:selected-setting';

	let selectedSetting = '';

	function persistSelectedSetting(value: string) {
		if (!browser) return;
		if (value) localStorage.setItem(SELECTED_SETTING_STORAGE_KEY, value);
		else localStorage.removeItem(SELECTED_SETTING_STORAGE_KEY);
	}

    function handleClick(){
        alert('Test button clicked.');
    }

    function runGenerateMode() {
        runMode("Generate", {});
        alert("Running Generate Mode");
    }

    function postProtocol(selectedSetting: String){
      fetch('/api/conf/protocol', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(selectedSetting)
      })
      .then(response => {
          if (!response.ok) {
              throw new Error(`Server error: ${response.status}`);
          }
          return response.json();
      })
      .then(data => {
          console.log('Success:', data);
      })
      .catch(error => {
          console.error('Error:', error);
      });
    }

  type ProtocolMap = Record<string, string>;

  let protocols: ProtocolMap = {};
  let loading = true;
  let error: string | null = null;

	onMount(async () => {
		try {
			const res = await fetch('/api/conf/protocol/available'); // returns: { "01_desktop_calculator": "01_desktop_calculator", ... }
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			protocols = (await res.json()) as ProtocolMap;

			if (browser) {
				const saved = localStorage.getItem(SELECTED_SETTING_STORAGE_KEY);
				const validValues = new Set(Object.values(protocols));
				if (saved && validValues.has(saved)) {
					selectedSetting = saved;
					postProtocol(saved);
				} else if (saved) {
					localStorage.removeItem(SELECTED_SETTING_STORAGE_KEY);
				}
			}
		} catch (e) {
			error = e instanceof Error ? e.message : 'Failed to load settings';
		} finally {
			loading = false;
		}
	});

  // [{ key, value }, ...] so we can #each
  $: protocolOptions = Object.entries(protocols).map(([key, value]) => ({ key, value }));
</script>


<section class="hero">
  <div class="hero__content">
    <div class="hero__badge">TESTAR Web Interface</div>
    <h1>Automated testing, organized and ready to run</h1>
    <p>
      Configure your system under test, choose a mode, and launch reliable runs
      with clear settings and repeatable workflows.
    </p>
  </div>
  <div class="hero__panel" aria-hidden="true">
    <img class="hero__logo" src={logo} alt="TESTAR logo" />
    <div class="hero__glow"></div>
  </div>
</section>

<section class="setting-first">
  <div class="setting-card" class:is-ready={!!selectedSetting}>
    <h2>1. Select the desired setting (Required)</h2>

    <p>
      {#if selectedSetting}
        Great — setting selected. You can move to the next step.
      {:else}
        Please choose a setting first. This is important before running any mode.
      {/if}
    </p>

		<select
			class="help__select"
			bind:value={selectedSetting}
			disabled={loading || !!error}
			on:change={() => {
				postProtocol(selectedSetting);
				persistSelectedSetting(selectedSetting);
			}}
		>
      <option value="">
        {#if loading}
          Loading settings...
        {:else if error}
          Failed to load settings
        {:else}
          Select a setting...
        {/if}
      </option>

      {#each protocolOptions as opt (opt.key)}
        <option value={opt.value}>{opt.key}</option>
      {/each}
    </select>

    {#if error}
      <p class="error">{error}</p>
    {/if}
  </div>
</section>

<section class="help">
  <div class="help__header">
    <h2>Help & guide</h2>
    <p>Quick steps to get you up and running with TESTAR.</p>
  </div>

  <div class="help__grid">
    <div class="help__card">
      <h3>1. Pick a setting</h3>
      <p>Select the desired setting from the dropdown.</p>
    </div>
    <div class="help__card">
      <h3>2. Open a mode page and configure settings</h3>
      <p>Go to a mode page, then fill in General Settings and mode-specific settings there.</p>
    </div>
    <div class="help__card">
      <h3>3. Run your session</h3>
      <p>Start the run from that mode page after all required settings are filled.</p>
    </div>
  </div>

  {#if selectedSetting === 'webdriver_generic'}
    <div class="help__card help__card--webdriver">
      <h3>Webdriver Generic (separate notes)</h3>
      <p>Start WebDriver, set your target URL, and configure allowed domains/paths if required.</p>
    </div>
  {/if}
</section>

<style>
  :global(body) {
    background: #f5f7fb;
  }

  .hero {
    display: grid;
    grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
    gap: 32px;
    align-items: center;
    background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
    border: 1px solid rgba(148, 163, 184, 0.2);
    border-radius: 24px;
    padding: 36px;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  }

  .hero__content h1 {
    margin: 10px 0 12px;
    font-size: 36px;
    line-height: 1.1;
    color: #0f172a;
  }

  .hero__content p {
    margin: 0 0 20px;
    color: #475569;
    font-size: 16px;
    max-width: 520px;
  }

  .hero__badge {
    display: inline-flex;
    align-items: center;
    padding: 6px 12px;
    border-radius: 999px;
    background: rgba(37, 99, 235, 0.12);
    color: #1d4ed8;
    font-weight: 600;
    font-size: 12px;
    letter-spacing: 0.06em;
    text-transform: uppercase;
  }

  .hero__actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .btn {
    padding: 10px 18px;
    border-radius: 999px;
    border: 1px solid transparent;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
  }

  .btn--primary {
    background: #2563eb;
    color: #ffffff;
    box-shadow: 0 10px 20px rgba(37, 99, 235, 0.25);
  }

  .btn--primary:hover {
    transform: translateY(-1px);
    box-shadow: 0 12px 24px rgba(37, 99, 235, 0.3);
  }

  .btn--ghost {
    background: #ffffff;
    border-color: rgba(148, 163, 184, 0.4);
    color: #0f172a;
  }

  .btn--ghost:hover {
    background: #f8fafc;
  }

  .hero__panel {
    position: relative;
    border-radius: 20px;
    background: #0f172a;
    padding: 32px;
    display: grid;
    place-items: center;
    min-height: 240px;
    overflow: hidden;
  }

  .hero__logo {
    width: 140px;
    height: auto;
    z-index: 1;
    filter: drop-shadow(0 12px 20px rgba(15, 23, 42, 0.35));
  }

  .hero__glow {
    position: absolute;
    width: 220px;
    height: 220px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.5), transparent 65%);
  }

  @media (max-width: 900px) {
    .hero {
      grid-template-columns: 1fr;
    }

    .hero__panel {
      order: -1;
    }
  }

  .help {
    margin-top: 28px;
    display: grid;
    gap: 16px;
  }

  .setting-first {
    margin-top: 20px;
  }

  .setting-card {
    background: #fff5f5;
    border: 1px solid #fecaca;
    border-left: 6px solid #ef4444;
    border-radius: 16px;
    padding: 18px;
    box-shadow: 0 10px 20px rgba(127, 29, 29, 0.06);
  }

  .setting-card h2 {
    margin: 0 0 8px;
    color: #7f1d1d;
    font-size: 22px;
  }

  .setting-card p {
    margin: 0 0 12px;
    color: #991b1b;
  }

  .setting-card.is-ready {
    background: #f0fdf4;
    border-color: #bbf7d0;
    border-left-color: #22c55e;
    box-shadow: 0 10px 20px rgba(20, 83, 45, 0.08);
  }

  .setting-card.is-ready h2 {
    color: #14532d;
  }

  .setting-card.is-ready p {
    color: #166534;
  }

  .help__header h2 {
    margin: 0 0 6px;
    color: #0f172a;
    font-size: 24px;
  }

  .help__header p {
    margin: 0;
    color: #475569;
  }

  .help__grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 16px;
  }

  .help__card {
    background: #ffffff;
    border-radius: 16px;
    border: 1px solid rgba(148, 163, 184, 0.2);
    padding: 18px;
    box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
  }

  .help__card h3 {
    margin: 0 0 6px;
    color: #0f172a;
    font-size: 18px;
  }

  .help__card p {
    margin: 0;
    color: #475569;
  }

  .help__card--webdriver {
    border-left: 4px solid #2563eb;
  }

  .help__select {
    width: 100%;
    padding: 10px 12px;
    border-radius: 10px;
    border: 1px solid #fca5a5;
    background: #ffffff;
    color: #0f172a;
    font-size: 14px;
  }

  .setting-card.is-ready .help__select {
    border-color: #86efac;
  }
</style>
