<script>
    import { tick } from "svelte";
    import { aboutPartners, aboutResearchProjects } from "./aboutProject.js";

    export let open = false;
    export let onClose = () => {};

    let closeButton;
    let previousFocus;

    $: if (open) {
        previousFocus = document.activeElement;
        tick().then(() => closeButton?.focus());
    }

    function close() {
        onClose();
        tick().then(() => previousFocus?.focus());
    }

    function closeFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            close();
        }
    }

    function handleKeydown(event) {
        if (open && event.key === "Escape") {
            close();
        }
    }
</script>

<svelte:window on:keydown={handleKeydown} />

{#if open}
    <div class="composition-modal-backdrop about-backdrop" role="presentation" on:click={closeFromBackdrop}>
        <div class="composition-modal about-dialog" role="dialog" aria-modal="true" aria-labelledby="about-dialog-title">
            <div class="about-dialog-heading">
                <div>
                    <span class="about-dialog-eyebrow">About the project</span>
                    <h2 id="about-dialog-title">TESTAR</h2>
                    <p>Scriptless testing framework for exploring applications and finding SUT issues through configurable test oracles.</p>
                </div>
                <button type="button" class="secondary" bind:this={closeButton} on:click={close}>Close</button>
            </div>

            <div class="about-dialog-body">
                <div class="about-brand">
                    <img src="/about/testar_logo.png" alt="TESTAR logo" />
                    <p>Web Studio brings workspace configuration, test execution, and results together in the browser for local or remote execution.</p>
                </div>

                <section class="about-support" aria-labelledby="about-partners-title">
                    <h3 id="about-partners-title">Research partners</h3>
                    <div class="about-logo-grid about-partner-grid">
                        {#each aboutPartners as partner (partner.name)}
                            <div class="about-logo-card">
                                <img src={partner.logo} alt={`${partner.name} logo`} />
                            </div>
                        {/each}
                    </div>
                </section>

                <section class="about-support" aria-labelledby="about-projects-title">
                    <h3 id="about-projects-title">Research projects that have supported TESTAR</h3>
                    <div class="about-logo-grid about-project-grid">
                        {#each aboutResearchProjects as project (project.name)}
                            <div class="about-logo-card">
                                <img src={project.logo} alt={`${project.name} logo`} />
                            </div>
                        {/each}
                    </div>
                </section>
            </div>
        </div>
    </div>
{/if}
