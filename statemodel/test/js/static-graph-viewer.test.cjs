const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');
const cytoscape = require('../../resources/graphs-static/js/cytoscape.min.js');

const script = fs.readFileSync(path.resolve(__dirname, '../../resources/graphs-static/js/viewer.js'), 'utf8');

function openViewer(images = {}, trees = {}, metadata = {}, records = []) {
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
    const elements = [{group: 'nodes', data: {id: 'n1'}, classes: 'ConcreteState'}, ...records];
    vm.runInNewContext(script, {
        document,
        window: {__TESTAR_ELEMENTS__: elements, __TESTAR_IMAGES__: images,
            __TESTAR_WIDGET_TREES__: trees, __TESTAR_RUN__: metadata},
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
        elements,
        select(className, id = 'n1', value = 'value') {
            handlers.get('tap')({target: {
                isNode: () => !['ConcreteAction', 'AbstractAction', 'SequenceStep'].includes(className),
                classes: () => className,
                data: () => ({...elements.find(element => element.data.id === id)?.data, id, title: value}),
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

test('final verdicts are shown with sequence context on the final occurrence and its shared concrete state', () => {
    const viewer = openViewer({}, {}, {}, [
        {classes: 'TestSequence', data: {id: 'seq1', sequenceId: 'SEQ1', finalStateOccurrenceId: 'SEQ1-N2',
            finalVerdicts: [{Severity: 'LLM_COMPLETE', SeverityValue: 0.04, Info: 'Goal achieved <script>unsafe</script>'}]}},
        {classes: ['TestSequence'], data: {id: 'seq2', sequenceId: 'SEQ2', finalStateOccurrenceId: 'SEQ2-N1',
            finalVerdicts: [{Severity: 'LLM_INVALID', SeverityValue: 0.91, Info: 'Other goal failed.'}]}},
        {classes: 'SequenceNode', data: {id: 'sn1', nodeId: 'SEQ1-N1', sequenceId: 'SEQ1'}},
        {classes: 'SequenceNode', data: {id: 'sn2', nodeId: 'SEQ1-N2', sequenceId: 'SEQ1'}},
        {classes: ['SequenceNode'], data: {id: 'sn3', nodeId: 'SEQ2-N1', sequenceId: 'SEQ2'}},
        {classes: 'Accessed', data: {id: 'access1', source: 'sn1', target: 'n1'}},
        {classes: 'Accessed', data: {id: 'access2', source: 'sn2', target: 'n1'}},
        {classes: 'Accessed', data: {id: 'access3', source: 'sn3', target: 'n1'}}
    ]);
    const stateHtml = viewer.select('ConcreteState');
    assert.match(stateHtml, /Final Verdicts/);
    assert.match(stateHtml, /Sequence: SEQ1/);
    assert.match(stateHtml, /Sequence: SEQ2/);
    assert.match(stateHtml, /Final occurrence: SEQ1-N2/);
    assert.match(stateHtml, /LLM_COMPLETE/);
    assert.match(stateHtml, /LLM_INVALID/);
    assert.match(stateHtml, /&lt;script&gt;unsafe&lt;\/script&gt;/);
    assert.doesNotMatch(stateHtml, /<script>/);
    assert.doesNotMatch(viewer.select('SequenceNode', 'sn1'), /Final Verdicts/);
    const finalHtml = viewer.select('SequenceNode', 'sn2');
    assert.match(finalHtml, /LLM_COMPLETE/);
    assert.doesNotMatch(finalHtml, /LLM_INVALID/);
    const sequenceHtml = viewer.select('TestSequence', 'seq2');
    assert.match(sequenceHtml, /LLM_INVALID/);
    assert.doesNotMatch(sequenceHtml, /\[object Object\]/);
    assert.equal(viewer.elements[0].data.finalVerdicts, undefined);
});

test('unresolved final verdicts remain on their sequence without attaching to an unrelated occurrence', () => {
    const viewer = openViewer({}, {}, {}, [
        {classes: 'TestSequence', data: {id: 'seq1', sequenceId: 'SEQ1', finalStateOccurrenceId: 'SEQ2-N1',
            finalVerdicts: [{Severity: 'LLM_COMPLETE', SeverityValue: 0.04, Info: 'Goal achieved.'}]}},
        {classes: 'SequenceNode', data: {id: 'sn1', nodeId: 'SEQ2-N1', sequenceId: 'SEQ2'}},
        {classes: 'Accessed', data: {id: 'access1', source: 'sn1', target: 'n1'}}
    ]);
    assert.match(viewer.select('TestSequence', 'seq1'), /Final state occurrence unavailable/);
    assert.doesNotMatch(viewer.select('ConcreteState'), /Final Verdicts/);
    assert.doesNotMatch(viewer.select('SequenceNode', 'sn1'), /Final Verdicts/);
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

test('concrete selection links to an independent browser tab without touching the embedded source', () => {
    const tree = [{classes: 'Widget', data: {id: 'root', ConcreteID: 'WC1', Role: 'page'}}];
    const viewer = openViewer({}, {n1: tree}, {widgetTreesCaptured: true});
    const html = viewer.select('ConcreteState');
    assert.match(html, /<a class="inspect-widget-tree" href="widget-tree.html\?state=n1" target="_blank" rel="noopener noreferrer">Inspect Widget Tree<\/a>/);
    assert.doesNotMatch(html, /captured-widget-tree|<button/);
    assert.doesNotMatch(viewer.select('AbstractState'), /Inspect Widget Tree/);
    assert.doesNotMatch(viewer.select('ConcreteAction'), /Inspect Widget Tree/);
    assert.notEqual(viewer.options.elements[0].data, viewer.elements[0].data);
    viewer.options.elements[0].data.customLabel = 'CS-1';
    assert.equal(viewer.elements[0].data.customLabel, undefined);
});

test('inspection links encode the graph record ID rather than interpreting it as markup or a path', () => {
    const id = '#12:34 & "state"/../?';
    const tree = [{classes: 'Widget', data: {id: 'root', ConcreteID: 'WC1', Role: 'page'}}];
    const viewer = openViewer({}, {[id]: tree}, {widgetTreesCaptured: true});
    assert.ok(viewer.select('ConcreteState', id).includes(`href="widget-tree.html?state=${encodeURIComponent(id)}"`));
});

test('omitted and unavailable trees have distinct feedback and no unusable inspection button', () => {
    const omitted = openViewer({}, {}, {widgetTreesCaptured: false}).select('ConcreteState');
    assert.match(omitted, /not captured/);
    assert.doesNotMatch(omitted, /Inspect Widget Tree/);
    const unavailable = openViewer({}, {n1: []}, {widgetTreesCaptured: true}).select('ConcreteState');
    assert.match(unavailable, /unavailable/);
    assert.doesNotMatch(unavailable, /Inspect Widget Tree/);
});
