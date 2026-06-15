import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";

export default defineConfig({
    plugins: [svelte()],
    server: {
        host: "127.0.0.1",
        port: 5173
    },
    build: {
        outDir: "../target/generated/webstudio-frontend/web",
        emptyOutDir: true
    }
});
