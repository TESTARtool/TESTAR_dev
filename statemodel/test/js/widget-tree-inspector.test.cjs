const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');
const {render, initializePage} = require('../../resources/graphs-static/js/widget-tree-inspector.js');
const {createEnvironment} = require('./support/export-dialog-dom.cjs');
const {node, edge} = require('./support/model-export-fixture.cjs');

const viewerDirectory = path.resolve(__dirname, '../../resources/graphs-static');
const pageHtml = fs.readFileSync(path.join(viewerDirectory, 'widget-tree.html'), 'utf8');

function createPage(search = '?state=%2312%3A34', metadata = {runId: 'run-1', modelIdentifier: 'model-1', widgetTreesCaptured: true}) {
    const ui = createEnvironment();
    for (const [, tag, id] of pageHtml.matchAll(/<(p|main) id="([^"]+)"/g)) {
        const element = ui.environment.document.createElement(tag);
        element.id = id;
        ui.environment.document.body.appendChild(element);
    }
    ui.environment.location = {protocol: 'file:', search};
    ui.environment.__TESTAR_ELEMENTS__ = [node('#12:34', 'ConcreteState', {stateId: 'CS-captured'})];
    ui.environment.__TESTAR_WIDGET_TREES__ = {'#12:34': capturedTree()};
    ui.environment.__TESTAR_RUN__ = metadata;
    ui.environment.fetch = () => assert.fail('The inspector must use local snapshot scripts');
    Object.defineProperty(ui.environment, 'opener', {get: () => assert.fail('The inspector must be independent of the graph tab')});
    return ui;
}

function capturedTree() {
    return [
        node('root', 'Widget', {ConcreteID: 'WC-root', AbstractID: 'WA-root', Role: 'page', Path: '[]'}),
        node('child10', 'Widget', {ConcreteID: 'WC10', AbstractID: 'WA10', Role: 'button', Path: '[10]',
            WebTextContent: '<script>captured text</script>', WebId: 'save'}),
        node('child2', 'Widget', {ConcreteID: 'WC2', Role: 'link', Path: '[2]', WebName: 'About'}),
        edge('ten', 'isChildOf', 'child10', 'root'),
        edge('two', 'isChildOf', 'child2', 'root')
    ];
}

test('captured hierarchy expands lazily in numeric sibling order with read-only selected attributes', async () => {
    const ui = createEnvironment();
    const container = ui.environment.document.createElement('section');
    const tree = capturedTree();
    const original = JSON.stringify(tree);
    render(container, tree, ui.environment);
    const list = container.find(node => node.className === 'widget-tree-nodes');
    assert.equal(list.children.length, 1);
    assert.equal(container.find(node => node.textContent === 'About'), null);
    const toggle = list.find(node => node['aria-label'] === 'Expand child widgets');
    await toggle.fire();
    assert.equal(toggle['aria-expanded'], 'true');
    const children = list.children[0].children.find(node => node.tagName === 'ul');
    assert.deepEqual(children.children.map(item => item.find(node => node.className === 'widget-tree-select').textContent),
        ['link: About', 'button: <script>captured text</script>']);
    const selected = children.children[1].find(node => node.className === 'widget-tree-select');
    await selected.fire();
    const properties = container.find(node => node.className === 'widget-tree-properties');
    const values = properties.find(node => node.tagName === 'table').children.map(row => row.children.map(cell => cell.textContent));
    assert.ok(values.some(([name, value]) => name === 'WebId' && value === 'save'));
    assert.ok(values.some(([name, value]) => name === 'AbstractWidgetID' && value === 'WA10'));
    assert.equal(properties.find(node => node.textContent === '<script>captured text</script>').tagName, 'td');
    assert.equal(container.find(node => node.tagName === 'input'), null);
    assert.equal(selected['aria-pressed'], 'true');
    await toggle.fire();
    assert.equal(children.hidden, true);
    await toggle.fire();
    assert.equal(children.children.length, 2);
    assert.equal(JSON.stringify(tree), original);
});

test('empty and corrupt captured trees show understandable feedback without network access', () => {
    const ui = createEnvironment();
    ui.environment.fetch = () => assert.fail('The inspector must use captured data');
    const container = ui.environment.document.createElement('section');
    render(container, [], ui.environment);
    assert.ok(container.find(node => /unavailable/.test(node.textContent)));
    const tree = capturedTree();
    tree.push(edge('cycle', 'isChildOf', 'root', 'child10'));
    render(container, tree, ui.environment);
    assert.ok(container.find(node => /Unable to inspect.*cycle/.test(node.textContent)));
});

test('standalone inspection identifies the selected concrete state and snapshot with two read-only panels', () => {
    const ui = createPage('?state=%2312%3A34');
    const original = JSON.stringify(ui.environment.__TESTAR_WIDGET_TREES__);
    initializePage(ui.environment);
    assert.equal(ui.get('state-summary').textContent, 'Concrete state: CS-captured | Graph ID: #12:34');
    assert.equal(ui.get('snapshot-summary').textContent, 'Snapshot: run-1 | Model: model-1');
    assert.equal(ui.environment.document.title, 'Widget Tree - CS-captured');
    const content = ui.get('widget-tree-content');
    assert.deepEqual(content.children.map(node => node.className), ['widget-tree-hierarchy', 'widget-tree-properties']);
    assert.ok(content.find(node => node.textContent === 'WC-root'));
    assert.equal(content.find(node => node.tagName === 'input'), null);
    assert.equal(JSON.stringify(ui.environment.__TESTAR_WIDGET_TREES__), original);
});

test('missing, unknown, and non-concrete state selections explain how to open an inspection', () => {
    for (const search of ['', '?state=unknown', '?state=abstract', '?state=action', '?state=../../other-model']) {
        const ui = createPage(search);
        ui.environment.__TESTAR_ELEMENTS__.push(node('abstract', 'AbstractState'), edge('action', 'ConcreteAction', 'a', 'b'));
        initializePage(ui.environment);
        assert.equal(ui.get('state-summary').textContent, 'No captured concrete state selected.');
        assert.match(ui.get('widget-tree-content').textContent, /Select a concrete state in the graph/);
        assert.equal(ui.get('widget-tree-content').children.length, 0);
    }
});

test('standalone inspection distinguishes intentionally omitted trees from unavailable trees', () => {
    for (const captured of [false, true]) {
        const ui = createPage('?state=%2312%3A34', {widgetTreesCaptured: captured});
        ui.environment.__TESTAR_WIDGET_TREES__ = captured ? {'#12:34': []} : {};
        initializePage(ui.environment);
        assert.match(ui.get('widget-tree-content').textContent, captured ? /unavailable/ : /not captured/);
        assert.match(ui.get('state-summary').textContent, /CS-captured/);
    }
});

test('state and snapshot labels render captured markup as text and accept array class names', () => {
    const ui = createPage('?state=%2312%3A34', {runId: '<script>run</script>', modelIdentifier: '<img src=x>'});
    ui.environment.__TESTAR_ELEMENTS__[0].classes = ['ConcreteState', 'Selected'];
    ui.environment.__TESTAR_ELEMENTS__[0].data.ConcreteID = '<script>state</script>';
    initializePage(ui.environment);
    assert.equal(ui.get('state-summary').textContent, 'Concrete state: <script>state</script> | Graph ID: #12:34');
    assert.equal(ui.get('state-summary').children.length, 0);
    assert.equal(ui.get('snapshot-summary').textContent, 'Snapshot: <script>run</script> | Model: <img src=x>');
    assert.equal(ui.get('snapshot-summary').children.length, 0);
});

test('packaged inspector scripts load through file URLs and work again without an opener on reload', () => {
    for (let load = 0; load < 2; load++) {
        const ui = createPage('?state=%2312%3A34');
        const environment = ui.environment;
        const scripts = {
            'model/elements.js': `window.__TESTAR_ELEMENTS__ = ${JSON.stringify(environment.__TESTAR_ELEMENTS__)};`,
            'model/widget-trees.js': `window.__TESTAR_WIDGET_TREES__ = ${JSON.stringify(environment.__TESTAR_WIDGET_TREES__)};`,
            'run.js': `window.__TESTAR_RUN__ = ${JSON.stringify(environment.__TESTAR_RUN__)};`
        };
        delete environment.TestarModelExport;
        delete environment.__TESTAR_ELEMENTS__;
        delete environment.__TESTAR_WIDGET_TREES__;
        delete environment.__TESTAR_RUN__;
        environment.window = environment;
        const context = vm.createContext(environment);
        for (const [, source, inline] of pageHtml.matchAll(/<script(?: src="([^"]+)")?>([\s\S]*?)<\/script>/g)) {
            let script = inline;
            if (source) {
                assert.doesNotMatch(source, /^(https?:|\/)/);
                script = scripts[source] ?? fs.readFileSync(source === 'js/model-json-export.js'
                    ? path.resolve(viewerDirectory, '../graphs/js/model-json-export.js') : path.join(viewerDirectory, source), 'utf8');
            }
            vm.runInContext(script, context, {filename: source || 'widget-tree.html'});
        }
        for (const [, stylesheet] of pageHtml.matchAll(/<link rel="stylesheet" href="([^"]+)"/g)) {
            assert.ok(fs.existsSync(path.join(viewerDirectory, stylesheet)));
        }
        assert.ok(ui.get('widget-tree-content').find(node => node.textContent === 'WC-root'));
        assert.equal(environment.document.title, 'Widget Tree - CS-captured');
        assert.match(pageHtml, /href="index.html"/);
    }
});
