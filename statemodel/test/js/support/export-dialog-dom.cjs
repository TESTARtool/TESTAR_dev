const exporter = require('../../../resources/graphs/js/model-json-export.js');
const runtime = require('../../../resources/graphs/js/model-export-runtime.js');
const {createWorkerEnvironment} = require('./export-worker.cjs');

function createEnvironment() {
    const downloads = [];
    const worker = createWorkerEnvironment();
    const timers = new Set();

    class Element {
        constructor(tag) {
            this.tagName = tag;
            this.children = [];
            this.handlers = new Map();
            this.dataset = {};
            this.disabled = false;
            this.checked = false;
            this.value = '';
            this.textContent = '';
            this.open = false;
        }

        appendChild(child) {
            child.parent = this;
            this.children.push(child);
        }

        replaceChildren() {
            this.children = [];
        }

        setAttribute(name, value) {
            this[name] = value;
        }

        removeAttribute(name) {
            delete this[name];
        }

        addEventListener(event, handler) {
            this.handlers.set(event, handler);
        }

        showModal() {
            this.open = true;
        }

        close() {
            this.open = false;
        }

        remove() {
            this.parent.children = this.parent.children.filter(child => child !== this);
        }

        click() {
            downloads.push(this.download);
        }

        async fire(name = 'click') {
            for (let ancestor = this; ancestor; ancestor = ancestor.parent) {
                if (ancestor.disabled && name !== 'cancel') {
                    return;
                }
            }
            const event = {defaultPrevented: false, preventDefault() { this.defaultPrevented = true; }};
            await this.handlers.get(name)?.(event);
            if (name === 'cancel' && !event.defaultPrevented) {
                this.close();
            }
            return event;
        }

        find(predicate) {
            if (predicate(this)) {
                return this;
            }
            for (const child of this.children) {
                const found = child.find(predicate);
                if (found) {
                    return found;
                }
            }
            return null;
        }
    }

    const body = new Element('body');
    for (const id of ['export-model', 'model-export-status', 'model-export-controls']) {
        const node = new Element('div');
        node.id = id;
        body.appendChild(node);
    }
    const environment = {
        ...worker.environment,
        document: {body, createElement: tag => new Element(tag),
            getElementById: id => body.find(node => node.id === id)},
        TestarModelExport: exporter,
        TestarModelExportRuntime: runtime,
        setTimeout: handler => handler(),
        setInterval(handler, milliseconds) {
            const timer = setInterval(handler, milliseconds);
            timers.add(timer);
            return timer;
        },
        clearInterval(timer) {
            timers.delete(timer);
            clearInterval(timer);
        },
        URLSearchParams,
        TextEncoder,
        TextDecoder,
        atob
    };
    return {environment, downloads, revoked: worker.revoked, workers: worker.workers, timers,
        get: id => environment.document.getElementById(id),
        dialog: () => body.find(node => node.tagName === 'dialog'),
        group: key => body.find(node => node.dataset.group === key), archive: worker.archive};
}

module.exports = {createEnvironment};
