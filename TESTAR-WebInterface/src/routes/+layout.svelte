<script lang="ts">
    import {onMount} from "svelte";
    import {initWebSocket, wsStatus} from "$lib/stores/websocket";
    import favicon from '$lib/assets/favicon.png';
    import Header from '$lib/components/Header.svelte';
    import ModeSelectionSidebar from '$lib/components/ModeSelectionSidebar.svelte';
    import Sidebar from '$lib/components/Sidebar.svelte';
    import '../app.css';

    let {children} = $props();

    onMount(() => {
        initWebSocket();
    })
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<Header />

<div class="layout">
	<Sidebar />
	<ModeSelectionSidebar />
	
	<main>
		{@render children()}
	</main>
</div>

<style>
	.layout {
		display: flex;
		position: relative;
	}

	main {
		flex: 1;
		padding: 50px;
	}
</style>
