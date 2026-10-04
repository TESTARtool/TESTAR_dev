(function (root) {
    "use strict";

    async function loadSnapshot(url, inventory, progress, environment = root) {
        const response = await environment.fetch(url + (inventory ? "" : "&progress=true"));
        if (!response.ok) {
            throw new Error("Unable to load model export data. Check the analysis server log.");
        }
        if (inventory) {
            const snapshot = await response.json();
            snapshot.metadata.live = true;
            return snapshot;
        }
        const reader = response.body.getReader();
        const decoder = new environment.TextDecoder();
        let fragments = [];
        let snapshot = null;
        let receiving = false;
        let receivedBytes = 0;
        function accept(line) {
            if (!line.trim()) {
                return;
            }
            // Keep the large JSON payload unparsed until it reaches the background worker.
            if (line.startsWith('{"type":"snapshot",')) {
                snapshot = {serializedSnapshot: line};
                return;
            }
            const event = JSON.parse(line);
            if (event.type === "error") {
                throw new Error(event.message);
            }
            if (event.type === "progress") {
                receiving = event.phase === "transfer";
                progress(event);
            }
        }
        try {
            while (true) {
                const {value, done} = await reader.read();
                const chunk = done ? decoder.decode() : decoder.decode(value, {stream: true});
                // Scan each incoming chunk once instead of repeatedly scanning the growing snapshot.
                let start = 0;
                let end = chunk.indexOf("\n");
                while (end !== -1) {
                    fragments.push(chunk.slice(start, end));
                    accept(fragments.join(""));
                    fragments = [];
                    start = end + 1;
                    end = chunk.indexOf("\n", start);
                }
                fragments.push(chunk.slice(start));
                receivedBytes += value?.byteLength || 0;
                if (receiving) {
                    progress({phase: "transfer", bytes: receivedBytes});
                }
                if (done) {
                    accept(fragments.join(""));
                    break;
                }
            }
        } finally {
            await reader.cancel();
            reader.releaseLock();
        }
        if (!snapshot) {
            throw new Error("The export stream ended before the model was received. Please retry.");
        }
        return snapshot;
    }

    function workerMain() {
        self.onmessage = event => {
            try {
                const {input, format, options} = event.data;
                const progress = update => self.postMessage({type: "progress", ...update});
                progress({phase: "parse"});
                const snapshot = input.serializedSnapshot ? JSON.parse(input.serializedSnapshot).snapshot : input;
                if (input.serializedSnapshot) {
                    snapshot.metadata.live = true;
                }
                const bundle = self.TestarModelExport.buildBundle(snapshot, format, options, progress);
                const archive = self.TestarModelExport.createZip(bundle.files, progress);
                self.postMessage({type: "result", archive: archive.buffer, warningCount: bundle.jsonModel.Warnings.length}, [archive.buffer]);
            } catch (error) {
                const message = error instanceof RangeError
                    ? "The model exceeds browser memory limits. Try excluding widget trees or screenshots."
                    : error.message;
                self.postMessage({type: "error", message});
            }
        };
    }

    function workerSource(exporter) {
        return exporter.workerSource() + `\n(${workerMain.toString()})();`;
    }

    function prepare(input, format, options, progress, environment = root) {
        if (!environment.Worker) {
            return Promise.reject(new Error("Background export is unavailable in this browser. Please use a current browser with Web Workers enabled."));
        }
        return new Promise((resolve, reject) => {
            let worker;
            let url;
            let settled = false;
            function finish(error, result) {
                if (settled) {
                    return;
                }
                settled = true;
                worker?.terminate();
                if (url) {
                    environment.URL.revokeObjectURL(url);
                }
                if (error) {
                    reject(error);
                } else {
                    resolve(result);
                }
            }
            try {
                url = environment.URL.createObjectURL(new environment.Blob([workerSource(environment.TestarModelExport)], {type: "text/javascript"}));
                worker = new environment.Worker(url);
                worker.onmessage = event => {
                    if (settled) {
                        return;
                    }
                    if (event.data.type === "progress") {
                        progress(event.data);
                    } else if (event.data.type === "result") {
                        finish(null, {archive: new Uint8Array(event.data.archive), warningCount: event.data.warningCount});
                    } else if (event.data.type === "error") {
                        finish(new Error(event.data.message));
                    }
                };
                worker.onerror = () => finish(new Error("The background export worker failed. The model may exceed browser memory limits; try excluding widget trees or screenshots."));
                worker.postMessage({input, format, options});
            } catch (error) {
                finish(error);
            }
        });
    }

    const api = {loadSnapshot, prepare, workerSource};
    root.TestarModelExportRuntime = api;
    if (typeof module !== "undefined" && module.exports) {
        module.exports = api;
    }
})(globalThis);
