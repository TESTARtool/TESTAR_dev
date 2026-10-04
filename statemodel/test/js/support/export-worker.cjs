const {Worker: NodeWorker} = require('node:worker_threads');

// Execute the browser's generated worker source in a real background thread.
function createWorkerEnvironment() {
    const blobs = new Map();
    const revoked = [];
    const workers = [];
    let archive;
    let nextId = 0;
    class BrowserWorker {
        constructor(url) {
            this.source = blobs.get(url).text();
            this.terminated = false;
            workers.push(this);
        }

        async postMessage(message) {
            const source = await this.source;
            if (this.terminated) {
                return;
            }
            this.thread = new NodeWorker(`
                const {parentPort} = require('node:worker_threads');
                globalThis.self = globalThis;
                self.postMessage = (data, transfer) => parentPort.postMessage(data, transfer);
                parentPort.on('message', data => self.onmessage({data}));
                ${source}
            `, {eval: true});
            this.thread.on('message', data => this.onmessage?.({data}));
            this.thread.on('error', error => this.onerror?.(error));
            this.thread.postMessage(message);
        }

        terminate() {
            this.terminated = true;
            this.thread?.terminate();
        }
    }
    const environment = {
        Worker: BrowserWorker,
        Blob,
        URL: {
            createObjectURL(blob) {
                const url = blob.type === 'application/zip' ? 'blob:export' : `blob:worker-${++nextId}`;
                blobs.set(url, blob);
                if (blob.type === 'application/zip') {
                    archive = blob;
                }
                return url;
            },
            revokeObjectURL(url) {
                revoked.push(url);
                blobs.delete(url);
            }
        }
    };
    return {environment, revoked, workers, archive: () => archive};
}

module.exports = {createWorkerEnvironment};
