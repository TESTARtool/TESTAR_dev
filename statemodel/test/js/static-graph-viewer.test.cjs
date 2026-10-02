const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');
const cytoscape = require('../../resources/graphs-static/js/cytoscape.min.js');

const script = fs.readFileSync(path.resolve(__dirname, '../../resources/graphs-static/js/viewer.js'), 'utf8');

function openViewer(images = {}) {
    const controls = new Map();
    const handlers = new Map();
    let options;
    const collection = {
        size: () => 0,
        forEach: () => {},
        addClass: () => {},
        removeClass: () => {}
    };
    const graph = {
        ready: callback => callback(),
        $: () => collection,
        fit: () => {},
        layout: () => ({run: () => {}}),
        on: (event, selector, callback) => handlers.set(event, callback)
    };
    const document = {
        getElementById(id) {
            if (!controls.has(id)) {
                controls.set(id, {innerHTML: '', addEventListener: () => {}, querySelector: () => null});
            }
            return controls.get(id);
        }
    };
    const elements = [{group: 'nodes', data: {id: 'n1'}, classes: 'ConcreteState'}];
    vm.runInNewContext(script, {
        document,
        window: {__TESTAR_ELEMENTS__: elements, __TESTAR_IMAGES__: images},
        cytoscape(configuration) {
            options = configuration;
            return graph;
        },
        fetch() {
            assert.fail('Portable exports should use embedded data without network access');
        }
    });
    return {
        options,
        controls,
        select(className, id = 'n1', value = 'value') {
            handlers.get('tap')({target: {
                isNode: () => className === 'ConcreteState',
                classes: () => className,
                data: () => ({id, title: value}),
                hasClass: name => name === className
            }});
            return controls.get('info-content').innerHTML;
        }
    };
}

test('initializes a graph from embedded elements without fetching data', () => {
    const viewer = openViewer();
    assert.equal(viewer.options.elements[0].data.id, 'n1');
    assert.equal(viewer.options.elements[0].classes, 'ConcreteState');
    assert.equal(viewer.controls.get('stats-abstract-states').textContent, 'Abstract states: 0');
});

test('shows embedded state and action screenshots', () => {
    const viewer = openViewer({n1: 'data:image/png;base64,AQID', e1: 'data:image/png;base64,BAUG'});
    assert.match(viewer.select('ConcreteState'), /src="data:image\/png;base64,AQID"/);
    assert.match(viewer.select('ConcreteAction', 'e1'), /src="data:image\/png;base64,BAUG"/);
});

test('missing screenshots show explicit messages instead of broken image links', () => {
    const viewer = openViewer();
    const state = viewer.select('ConcreteState');
    const action = viewer.select('ConcreteAction', 'e1');
    assert.match(state, /State screenshot unavailable/);
    assert.match(action, /Action screenshot unavailable/);
    assert.doesNotMatch(state + action, /<img/);
    const concreteStyle = viewer.options.style.find(entry => entry.selector === '.ConcreteState');
    assert.equal(concreteStyle.style['background-image']({data: () => 'missing'}), undefined);
});

test('selection information escapes captured SUT text', () => {
    const viewer = openViewer();
    const html = viewer.select('ConcreteState', 'n1', '<script>alert("SUT")</script>');
    assert.match(html, /&lt;script&gt;alert\(&quot;SUT&quot;\)&lt;\/script&gt;/);
    assert.doesNotMatch(html, /<script>/);
});

test('packaged graph library loads the exported node and edge format', () => {
    const graph = cytoscape({headless: true, elements: [
        {group: 'nodes', data: {id: 'n1'}, classes: 'AbstractState'},
        {group: 'nodes', data: {id: 'n2'}, classes: 'ConcreteState'},
        {group: 'edges', data: {id: 'e1', source: 'n2', target: 'n1'}, classes: 'isAbstractedBy'}
    ]});
    assert.equal(graph.nodes().length, 2);
    assert.equal(graph.edges().length, 1);
    assert.equal(graph.getElementById('e1').target().id(), 'n1');
    graph.destroy();
});
