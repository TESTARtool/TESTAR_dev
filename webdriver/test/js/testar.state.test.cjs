const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

const source = readFileSync(path.join(__dirname, '../../resources/web-extension/js/testar.state.js'), 'utf8');

function createPage() {
    const document = {};
    const elements = [];
    function element(tag, parent = null, id = '') {
        const node = {
            nodeType: 1, localName: tag, tagName: tag.toUpperCase(), id, ownerDocument: document,
            parentElement: parent, children: [], shadowRoot: null,
            getRootNode: () => document,
            getBoundingClientRect: () => ({ left: 0, top: 0, width: 100, height: 30 })
        };
        if (parent) {
            parent.children.push(node);
        }
        elements.push(node);
        return node;
    }
    function matchesPart(node, part) {
        if (part.startsWith('#')) {
            const id = part.slice(1).replace(/\\([0-9a-f]+)\s?/gi, (_, code) => String.fromCodePoint(parseInt(code, 16)));
            return node.id === id;
        }
        const match = /^(\w+)(?::nth-of-type\((\d+)\))?$/.exec(part);
        if (!match || node.localName !== match[1]) {
            return false;
        }
        const siblings = node.parentElement ? node.parentElement.children.filter(sibling => sibling.localName === node.localName) : [node];
        return !match[2] || siblings.indexOf(node) + 1 === Number(match[2]);
    }
    document.querySelectorAll = selector => elements.filter(node => {
        const parts = selector.split(' > ');
        let current = node;
        for (let index = parts.length - 1; index >= 0; index--) {
            if (!current || !matchesPart(current, parts[index])) {
                return false;
            }
            current = current.parentElement;
        }
        return true;
    });
    const html = element('html');
    const body = element('body', html);
    const context = {
        document, console: { warn() {} },
        CSS: { escape: value => value.replace(/[^a-z0-9_-]/gi, character => '\\' + character.codePointAt(0).toString(16) + ' ') },
        PerformanceObserver: class { observe() {} },
        getComputedStyle: () => ({ opacity: '1' }),
        window: { innerWidth: 1000, innerHeight: 800 }
    };
    vm.runInNewContext(source, context);
    return { context, document, element, body };
}

test('generates unique selectors immediately without loading another script', () => {
    const { context, document, element, body } = createPage();
    const select = element('select', body, 'country');

    assert.equal(context.getCssSelectorTestar(select), '#country');
    assert.equal(document.head, undefined);
});

test('escapes special characters in IDs', () => {
    const { context, element, body } = createPage();
    const select = element('select', body, 'form:country');

    assert.equal(context.getCssSelectorTestar(select), '#form\\3a country');
});

test('distinguishes nameless siblings using an identified ancestor and nth-of-type', () => {
    const { context, document, element, body } = createPage();
    const form = element('form', body, 'shipping');
    element('select', form);
    element('input', form);
    const select = element('select', form);
    const selector = context.getCssSelectorTestar(select);

    assert.equal(selector, '#shipping > select:nth-of-type(2)');
    assert.deepEqual(document.querySelectorAll(selector), [select]);
});

test('builds a unique path when no ancestors have IDs', () => {
    const { context, document, element, body } = createPage();
    const select = element('select', element('form', body));

    assert.equal(context.getCssSelectorTestar(select), 'html > body > form > select');
    assert.deepEqual(document.querySelectorAll(context.getCssSelectorTestar(select)), [select]);
});

test('uses a path instead of a duplicated ID', () => {
    const { context, document, element, body } = createPage();
    element('select', body, 'duplicate');
    const select = element('select', body, 'duplicate');

    assert.equal(context.getCssSelectorTestar(select), 'html > body > select:nth-of-type(2)');
    assert.deepEqual(document.querySelectorAll(context.getCssSelectorTestar(select)), [select]);
});

test('leaves selectors empty for iframe and shadow-root elements', () => {
    const { context, element, body } = createPage();
    const iframeSelect = element('select', body);
    iframeSelect.ownerDocument = {};
    const shadowSelect = element('select', body);
    shadowSelect.getRootNode = () => ({ host: body });

    assert.equal(context.getCssSelectorTestar(iframeSelect), '');
    assert.equal(context.getCssSelectorTestar(shadowSelect), '');
});

test('returns empty when CSS escaping is unavailable', () => {
    const { context, element, body } = createPage();
    const select = element('select', body);
    delete context.CSS;

    assert.equal(context.getCssSelectorTestar(select), '');
});

test('returns empty when generation fails or does not uniquely resolve the element', () => {
    const { context, document, element, body } = createPage();
    const select = element('select', body);
    document.querySelectorAll = () => [];
    assert.equal(context.getCssSelectorTestar(select), '');

    document.querySelectorAll = () => { throw new Error('unavailable'); };
    assert.equal(context.getCssSelectorTestar(select), '');

    select.getRootNode = () => { throw new Error('unavailable root'); };
    assert.equal(context.getCssSelectorTestar(select), '');
});

function prepareWrapper(context) {
    for (const name of ['getAttributeMapTestar', 'getNameTestar', 'getElementLength', 'getEffectiveBackgroundColor',
        'getZIndexTestar', 'getRectTestar', 'getDimensionsTestar', 'getIsBlockedTestar', 'isClickableTestar', 'getXPath']) {
        context[name] = () => '';
    }
}

test('packs the selector alongside the other captured element properties', () => {
    const { context, element, body } = createPage();
    prepareWrapper(context);
    const select = element('select', body, 'country');

    const packed = context.wrapElementTestar(select, 0, 0, []);

    assert.equal(packed.cssSelector, '#country');
    assert.equal(packed.element, select);
});

test('ignored cssSelector skips generation rather than just hiding the result', () => {
    const { context, element, body } = createPage();
    prepareWrapper(context);
    context.getCssSelectorTestar = () => { throw new Error('generation must not run'); };

    const packed = context.wrapElementTestar(element('select', body), 0, 0, ['cssSelector']);

    assert.equal(packed.cssSelector, '');
});
