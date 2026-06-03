<script lang="ts">
    export let endpoint: string = '/api/results/download';
    export let filename: string = 'results';

    async function downloadResults() {
        try {
            const res = await fetch(endpoint);

            if (!res.ok) {
                throw { status: res.status };
            }

            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = filename;

            document.body.appendChild(a);
            a.click();
            a.remove();

            window.URL.revokeObjectURL(url);

        } catch (err: any) {
            console.error(err);

            if (err.status === 409) {
                alert("No results available");
            } else {
                alert("err");
            }
        }
    }
</script>

<button on:click={downloadResults} {...$$restProps}>
    <slot>Download</slot>
</button>