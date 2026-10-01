const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

const source = readFileSync(path.join(__dirname, '../../resources/web-extension/js/testar.canvas.js'), 'utf8');

function createHost() {
    return {
        lastElementChild: null,
        appendChild(element) {
            if (element.parentNode) {
                element.parentNode.lastElementChild = null;
            }
            element.parentNode = this;
            this.lastElementChild = element;
        }
    };
}

function createCanvasContext({ popover = null, modal = null, contentZIndex = 'auto' } = {}) {
    const body = createHost();
    const canvas = { style: { zIndex: '' }, parentNode: null };
    body.appendChild(canvas);
    const content = { style: { zIndex: contentZIndex } };
    const document = {
        body,
        querySelector(selector) {
            return selector === ':popover-open' ? popover : modal;
        },
        querySelectorAll() {
            return [content, canvas];
        }
    };
    const window = {
        getComputedStyle(element) {
            return { zIndex: element.style.zIndex };
        }
    };
    const context = { document, window, testar_canvas: canvas };
    vm.runInNewContext(source, context);
    return { body, canvas, context };
}

test('keeps the canvas above ordinary page content', () => {
    const { body, canvas, context } = createCanvasContext({ contentZIndex: '100' });

    context.ensureCanvasOnTop();

    assert.equal(canvas.parentNode, body);
    assert.equal(canvas.style.zIndex, 101);
});

test('moves the canvas into an open modal', () => {
    const modal = createHost();
    const { canvas, context } = createCanvasContext({ modal });

    context.ensureCanvasOnTop();

    assert.equal(canvas.parentNode, modal);
    assert.equal(modal.lastElementChild, canvas);
});

test('prefers an open popover over a modal', () => {
    const popover = createHost();
    const modal = createHost();
    const { canvas, context } = createCanvasContext({ popover, modal });

    context.ensureCanvasOnTop();

    assert.equal(canvas.parentNode, popover);
});

test('observes page changes that can move the canvas', () => {
    const body = createHost();
    body.scrollLeft = 0;
    body.scrollTop = 0;
    const canvas = { style: { zIndex: '' }, getContext: () => ({}) };
    const documentElement = { clientWidth: 800, clientHeight: 600, scrollLeft: 0, scrollTop: 0 };
    const document = {
        body,
        documentElement,
        createElement: () => canvas,
        querySelector: () => null,
        querySelectorAll: () => [canvas]
    };
    const window = {
        addEventListener() {},
        getComputedStyle: element => ({ zIndex: element.style.zIndex })
    };
    let observation;
    class MutationObserver {
        observe(target, options) {
            observation = { target, options };
        }
    }
    const context = { document, window, MutationObserver };
    vm.runInNewContext(source, context);

    assert.equal(context.addCanvasTestar(), 'object');
    assert.equal(observation.target, documentElement);
    assert.equal(observation.options.childList, true);
    assert.equal(observation.options.attributes, true);
});
