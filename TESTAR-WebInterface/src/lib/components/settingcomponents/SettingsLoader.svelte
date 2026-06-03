<script lang="ts">
    import { onMount } from "svelte";

    export let mode: string; 
    export let conf: any = undefined;
    export let settings: any = undefined;
    
    let protocolRequired = false;
    let protocols: Record<string, string> = {};
    let selectedProtocol = "";

    onMount(async () => {
        const res = await fetch(`/api/conf/${mode.toLowerCase()}/available`);
        settings = await res.json();        
        await loadconf();
    });

    async function postProtocol(selected: string) {
        try {
            const res = await fetch('/api/conf/protocol', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(selected)
            });
            if (!res.ok) throw new Error(`error: ${res.status}`);
            await loadconf();
        } catch (err) {
            console.error(err);
        }
    }

    async function loadconf() {
        protocolRequired = false;
        const res = await fetch("/api/conf");

        if (res.status === 409) {
            protocolRequired = true;
            const protoRes = await fetch("/api/conf/protocol/available");
            protocols = await protoRes.json();
            return;
        }

        conf = await res.json();
    }
</script>

{#if conf}
    {#each Object.keys(conf) as key (key)}
        {#if settings.includes(key)}
            <div>
                <label for={key}>{key}</label>

                {#if typeof conf[key] === "boolean"}
                    <input type="checkbox" bind:checked={conf[key]} />
                {:else if typeof conf[key] === "number"}
                    <input type="number" step="any" bind:value={conf[key]} />
                {:else}
                    <input type="text" bind:value={conf[key]} />
                {/if}
            </div>
        {/if}
    {/each}
{:else if protocolRequired}
    <div>
        <h3>Select protocol first</h3>

        <select bind:value={selectedProtocol}>
            <option value="">Select a protocol...</option>
            {#each Object.entries(protocols) as [key, value]}
                <option value={value}>{key}</option>
            {/each}
        </select>

        <button on:click={() => postProtocol(selectedProtocol)} disabled={!selectedProtocol}>
            Set protocol
        </button>
    </div>
{:else}
    <p>Something went wrong...</p>
{/if}