const assert = require('node:assert/strict');
const test = require('node:test');
const {buildBundle, buildWidgetTree, propertyInventory, defaultProperties, createZip} = require('../../resources/graphs/js/model-json-export.js');
const {traceSnapshot} = require('./support/model-export-fixture.cjs');

function node(id, classes, data) {
    return {group: 'nodes', classes, data: {id, ...data}};
}

function edge(id, classes, source, target, data = {}) {
    return {group: 'edges', classes, data: {id, source, target, ...data}};
}

function fixture() {
    const elements = [
        node('as1', ['AbstractState', 'isInitial'], {stateId: 'SA1', isInitial: 'true'}),
        node('as2', 'AbstractState', {stateId: 'SA2'}),
        node('black', 'BlackHole', {}),
        node('cs1', 'ConcreteState', {stateId: 'SC1', AbstractID: 'SA1', WebTitle: 'caf\u00e9'}),
        node('cs2', 'ConcreteState', {ConcreteID: 'SC2', AbstractID: 'SA1'}),
        node('cs3', 'ConcreteState', {ConcreteID: 'SC3', AbstractID: 'SA2'}),
        edge('connector1', 'isAbstractedBy', 'cs1', 'as1'),
        edge('connector2', 'isAbstractedBy', 'cs2', 'as1'),
        edge('connector3', 'isAbstractedBy', 'cs3', 'as2'),
        edge('aa1', 'AbstractAction', 'as1', 'as2', {actionId: 'AAclick', concreteActionIds: '[AC1, AC10]'}),
        edge('aa2', 'AbstractAction', 'as1', 'as2', {actionId: 'AAtype', concreteActionIds: ['AC2']}),
        edge('aa3', 'AbstractAction', 'as1', 'as1', {actionId: 'AAclick', concreteActionIds: '[AC1]'}),
        edge('aa4', 'UnvisitedAbstractAction', 'as2', 'black', {actionId: 'AAunvisited'}),
        edge('ca1', 'ConcreteAction', 'cs1', 'cs3', {actionId: 'AC1', AbstractID: 'WA1', ConcreteID: 'WC1', Role: 'click'}),
        edge('ca2', 'ConcreteAction', 'cs2', 'cs3', {actionId: 'AC10', AbstractID: 'WA1', ConcreteID: 'WC2', Role: 'click'}),
        edge('ca3', 'ConcreteAction', 'cs1', 'cs3', {actionId: 'AC2', AbstractID: 'WA1', ConcreteID: 'WC1',
            Role: 'type', InputText: 'Saab \u4e2d\u6587'}),
        edge('ca4', 'ConcreteAction', 'cs1', 'cs2', {actionId: 'AC1', AbstractID: 'WA1', ConcreteID: 'WC1'})
    ];
    const widgetTree = [
        node('root', 'Widget', {ConcreteID: 'WCroot', AbstractID: 'WAroot', Path: '[]'}),
        node('child10', 'Widget', {ConcreteID: 'WC10', AbstractID: 'WA1', Path: '[0, 10]'}),
        node('child2', 'Widget', {ConcreteID: 'WC2', AbstractID: 'WA1', Path: '[0, 2]'}),
        edge('parent10', 'isChildOf', 'child10', 'root'),
        edge('parent2', 'isChildOf', 'child2', 'root')
    ];
    return {metadata: {modelIdentifier: 'model-1', runId: 'run-1'}, elements,
        widgetTrees: {cs1: widgetTree, cs2: widgetTree, cs3: []},
        images: {cs1: 'data:image/png;base64,AQID', cs2: 'data:image/png;base64,BAUG',
            ca1: 'data:image/png;base64,BwgJ', ca4: 'data:image/png;base64,CgsM'}};
}

test('semantic defaults select available text, identifiers and status without visual measurements or colors', () => {
    const available = {
        states: ['Title', 'WebId', 'Shape', 'WebComputedBackgroundColor', 'CustomTag'],
        actions: ['Role', 'InputText', 'actionDescription', 'TargetID', 'WebBoundingRectangle'],
        transitions: ['uid', 'Role', 'WebDisplayedWidth'],
        widgets: ['WebName', 'WebTextContent', 'WebXPath', 'AndroidText', 'AndroidAccessibilityId', 'AndroidResourceId',
            'UIAName', 'UIAAutomationId', 'UIAValueValue', 'Enabled', 'AndroidBounds', 'WebComputedFontSize'],
        sequences: ['verdict', 'containsErrors', 'CustomTag']
    };
    const original = JSON.stringify(available);
    assert.deepEqual(defaultProperties(available), {
        states: ['Title', 'WebId'],
        actions: ['Role', 'InputText', 'actionDescription', 'TargetID'],
        transitions: ['uid', 'Role'],
        widgets: ['WebName', 'WebTextContent', 'WebXPath', 'AndroidText', 'AndroidAccessibilityId', 'AndroidResourceId',
            'UIAName', 'UIAAutomationId', 'UIAValueValue', 'Enabled'],
        sequences: ['verdict', 'containsErrors']
    });
    assert.equal(JSON.stringify(available), original);
    assert.deepEqual(defaultProperties({states: ['OnlyCustomProperty']}), {states: []});
});

test('semantic defaults preserve essential identities and let advanced selection include custom visual properties', () => {
    const snapshot = fixture();
    snapshot.elements.find(element => element.data.id === 'cs1').data.Shape = '[10,20,800,600]';
    snapshot.elements.find(element => element.data.id === 'cs1').data.WebComputedColor = '#fff';
    const inventory = propertyInventory(snapshot);
    const selected = defaultProperties(inventory);
    const result = buildBundle(snapshot, 'concrete', {properties: selected, includeWidgetTrees: true, includeScreenshots: false});
    const state = result.jsonModel.ConcreteStates.find(state => state.ConcreteStateID === 'SC1');
    assert.deepEqual(state.Properties, {WebTitle: 'caf\u00e9'});
    assert.equal(state.AbstractStateID, 'SA1');
    assert.equal(state.WidgetTree[0].ConcreteWidgetID, 'WCroot');
    assert.ok(result.jsonModel.ConcreteTransitions.some(transition => transition.ConcreteActionID === 'AC1'));
    const advanced = buildBundle(snapshot, 'concrete', {properties: inventory, includeWidgetTrees: false, includeScreenshots: false});
    assert.equal(advanced.jsonModel.ConcreteStates.find(state => state.ConcreteStateID === 'SC1').Properties.WebComputedColor, '#fff');
});

test('CSS selectors flow through widget property inventory, semantic defaults and selection', () => {
    const snapshot = fixture();
    snapshot.widgetTrees.cs1[0].data.WebCssSelector = '#shipping > select:nth-of-type(2)';
    const inventory = propertyInventory(snapshot);
    assert.ok(inventory.widgets.includes('WebCssSelector'));
    const selected = defaultProperties(inventory);
    assert.ok(selected.widgets.includes('WebCssSelector'));

    const bundle = buildBundle(snapshot, 'concrete', {properties: selected, includeWidgetTrees: true});
    const exported = bundle.jsonModel.ConcreteStates.find(state => state.ConcreteStateID === 'SC1');
    assert.equal(exported.WidgetTree[0].Properties.WebCssSelector, '#shipping > select:nth-of-type(2)');

    selected.widgets = [];
    const filtered = buildBundle(snapshot, 'concrete', {properties: selected, includeWidgetTrees: true});
    const filteredState = filtered.jsonModel.ConcreteStates.find(state => state.ConcreteStateID === 'SC1');
    assert.equal(filteredState.WidgetTree[0].Properties.WebCssSelector, undefined);
});

test('abstract export keeps one deterministic artifact and every abstract transition', () => {
    const {jsonModel, files} = buildBundle(fixture(), 'abstract');
    assert.deepEqual(jsonModel.InitialStates, ['SA1']);
    assert.equal(jsonModel.AbstractStates[0].RepresentativeConcreteState.ConcreteStateID, 'SC1');
    assert.equal(jsonModel.AbstractStates[0].RepresentativeConcreteState.Properties.WebTitle, 'caf\u00e9');
    assert.equal(jsonModel.AbstractActions.length, 3);
    assert.equal(jsonModel.AbstractTransitions.length, 4);
    const action = jsonModel.AbstractActions.find(entry => entry.AbstractActionID === 'AAclick');
    assert.equal(action.RepresentativeConcreteAction.ConcreteActionID, 'AC1');
    assert.equal(action.RepresentativeConcreteAction.AbstractWidgetID, 'WA1');
    assert.notEqual(action.AbstractActionID, action.RepresentativeConcreteAction.AbstractWidgetID);
    assert.equal(jsonModel.Metadata.SnapshotRunID, 'run-1');
    assert.deepEqual(files.filter(file => file.name.endsWith('.png')).map(file => file.name),
        ['screenshots/states/cs1.png', 'screenshots/actions/ca1.png']);
});

test('hybrid export preserves concrete states and distinct transitions for actions on the same widget', () => {
    const {jsonModel, files} = buildBundle(fixture(), 'hybrid');
    assert.deepEqual(jsonModel.AbstractStates[0].ConcreteStates.map(state => state.ConcreteStateID), ['SC1', 'SC2']);
    const click = jsonModel.AbstractActions.find(action => action.AbstractActionID === 'AAclick');
    assert.deepEqual(click.ConcreteInstances.map(instance => instance.ConcreteActionID), ['AC1', 'AC10', 'AC1']);
    const type = jsonModel.AbstractActions.find(action => action.AbstractActionID === 'AAtype');
    assert.equal(type.ConcreteInstances[0].AbstractWidgetID, 'WA1');
    assert.equal(type.ConcreteInstances[0].Properties.InputText, 'Saab \u4e2d\u6587');
    const returned = jsonModel.AbstractTransitions.find(transition => transition.SourceAbstractStateID === 'SA1'
        && transition.TargetAbstractStateID === 'SA1');
    assert.deepEqual(returned.ConcreteInstances, [{ConcreteActionID: 'AC1', SourceConcreteStateID: 'SC1', TargetConcreteStateID: 'SC2'}]);
    const screenshots = click.ConcreteInstances.map(instance => instance.Screenshot);
    assert.deepEqual(screenshots, ['screenshots/actions/ca1.png', null, 'screenshots/actions/ca4.png']);
    assert.equal(new Set(files.map(file => file.name)).size, files.length);
});

test('repeated observations retain all concrete transitions under one abstract self-loop', () => {
    const ids = ['AC_OBSERVATION_SC1_SC2', 'AC_OBSERVATION_SC2_SC3', 'AC_OBSERVATION_SC3_SC4'];
    const snapshot = {elements: [
        node('as1', 'AbstractState', {stateId: 'SA1'}),
        ...['SC1', 'SC2', 'SC3', 'SC4'].flatMap((stateId, index) => [
            node(`cs${index + 1}`, 'ConcreteState', {stateId}),
            edge(`connector${index + 1}`, 'isAbstractedBy', `cs${index + 1}`, 'as1')
        ]),
        edge('aa1', 'AbstractAction', 'as1', 'as1', {actionId: 'AA_OBSERVATION', concreteActionIds: `[${ids.join(', ')}]`}),
        ...ids.map((actionId, index) => edge(`ca${index + 1}`, 'ConcreteAction', `cs${index + 1}`, `cs${index + 2}`,
            {actionId, AbstractID: 'SA1', Role: 'Process'}))
    ]};
    const options = {includeWidgetTrees: false, includeScreenshots: false};

    const hybrid = buildBundle(snapshot, 'hybrid', options).jsonModel;
    assert.equal(hybrid.AbstractActions.length, 1);
    assert.equal(hybrid.AbstractActions[0].AbstractActionID, 'AA_OBSERVATION');
    assert.deepEqual(hybrid.AbstractActions[0].ConcreteInstances.map(action => action.ConcreteActionID), ids);
    assert.deepEqual(hybrid.AbstractStates[0].ConcreteStates.map(state => state.ConcreteStateID), ['SC1', 'SC2', 'SC3', 'SC4']);
    assert.equal(hybrid.AbstractTransitions.length, 1);
    assert.deepEqual(hybrid.AbstractTransitions[0].ConcreteInstances, ids.map((actionId, index) => ({
        ConcreteActionID: actionId, SourceConcreteStateID: `SC${index + 1}`, TargetConcreteStateID: `SC${index + 2}`
    })));
    assert.deepEqual(hybrid.Warnings, []);

    const abstract = buildBundle(snapshot, 'abstract', options).jsonModel;
    assert.equal(abstract.AbstractActions[0].RepresentativeConcreteAction.ConcreteActionID, ids[0]);
    assert.equal(abstract.AbstractTransitions[0].AbstractActionID, 'AA_OBSERVATION');
    assert.deepEqual(abstract.Warnings, []);

    snapshot.elements.find(element => element.data.id === 'aa1').data.concreteActionIds = `[${ids[0]}]`;
    for (const format of ['abstract', 'hybrid']) {
        assert.throws(() => buildBundle(snapshot, format, options), /Cannot uniquely resolve the abstract action/);
    }
});

test('unvisited actions and missing artifacts have explicit representations', () => {
    const {jsonModel} = buildBundle(fixture(), 'hybrid');
    const unvisited = jsonModel.AbstractTransitions.find(transition => transition.AbstractActionID === 'AAunvisited');
    assert.equal(unvisited.Visited, false);
    assert.equal(unvisited.TargetAbstractStateID, null);
    assert.deepEqual(unvisited.ConcreteInstances, []);
    assert.equal(jsonModel.AbstractStates[1].ConcreteStates[0].Screenshot, null);
    assert.deepEqual(jsonModel.AbstractStates[1].ConcreteStates[0].WidgetTree, []);
    assert.ok(jsonModel.Warnings.includes('Widget tree unavailable: cs3'));
    assert.ok(jsonModel.Warnings.includes('actions screenshot unavailable: ca2'));
});

test('multiple initial states and abstract states without concrete records remain exportable', () => {
    const snapshot = fixture();
    snapshot.elements.find(element => element.data.id === 'as2').data.isInitial = 'true';
    snapshot.elements.push(node('as3', 'AbstractState', {stateId: 'SA3', isInitial: true}));
    const {jsonModel} = buildBundle(snapshot, 'abstract');
    assert.deepEqual(jsonModel.InitialStates, ['SA1', 'SA2', 'SA3']);
    assert.equal(jsonModel.AbstractStates[2].RepresentativeConcreteState, null);
    const unvisited = jsonModel.AbstractActions.find(action => action.AbstractActionID === 'AAunvisited');
    assert.equal(unvisited.RepresentativeConcreteAction, null);
});

test('every screenshot reference resolves to one packaged file', () => {
    for (const format of ['abstract', 'hybrid', 'concrete', 'traces']) {
        const bundle = buildBundle(traceSnapshot(), format);
        const screenshots = new Set(bundle.files.filter(file => file.name.endsWith('.png')).map(file => file.name));
        const references = new Set();
        function visit(value) {
            if (!value || typeof value !== 'object') {
                return;
            }
            if (value.Screenshot) {
                assert.ok(screenshots.has(value.Screenshot), `Missing screenshot ${value.Screenshot}`);
                references.add(value.Screenshot);
            }
            Object.values(value).forEach(visit);
        }
        visit(bundle.jsonModel);
        assert.deepEqual(references, screenshots);
    }
});

test('exports are deterministic and do not mutate graph or widget-tree data', () => {
    const snapshot = fixture();
    const before = JSON.stringify(snapshot);
    for (const format of ['abstract', 'hybrid', 'concrete']) {
        const expected = buildBundle(snapshot, format);
        assert.equal(JSON.stringify(snapshot), before);
        const reversed = structuredClone(snapshot);
        reversed.elements.reverse();
        reversed.widgetTrees.cs1.reverse();
        reversed.widgetTrees.cs2.reverse();
        assert.deepEqual(buildBundle(reversed, format), expected);
    }
});

test('widget trees preserve hierarchy and numeric sibling order', () => {
    const tree = buildWidgetTree(fixture().widgetTrees.cs1);
    assert.equal(tree.length, 1);
    assert.deepEqual(tree[0].Children.map(child => child.ConcreteWidgetID), ['WC2', 'WC10']);
    assert.deepEqual(tree[0].Children[0].Children, []);
    assert.throws(() => buildWidgetTree([node('w', 'Widget', {}), edge('e', 'isChildOf', 'w', 'w')]), /cycle/);
});

test('action mapping matches whole concrete IDs and refuses ambiguous or unresolved relationships', () => {
    const snapshot = fixture();
    snapshot.elements.find(element => element.data.id === 'aa2').data.concreteActionIds = '[AC2, AC100]';
    assert.equal(buildBundle(snapshot, 'hybrid').jsonModel.AbstractActions.length, 3);
    snapshot.elements.find(element => element.data.id === 'aa2').data.concreteActionIds = '[AC2, AC10]';
    assert.throws(() => buildBundle(snapshot, 'hybrid'), /uniquely resolve/);
    assert.throws(() => buildBundle({elements: []}, 'abstract'), /No model/);
    assert.throws(() => buildBundle(fixture(), 'unknown-format'), /Choose abstract, hybrid, concrete, or traces/);
});

test('ZIP contains UTF-8 JSON and available screenshot bytes with correct entry metadata', () => {
    const bundle = buildBundle(fixture(), 'hybrid');
    const bytes = createZip(bundle.files);
    const view = new DataView(bytes.buffer);
    const entries = new Map();
    let offset = 0;
    while (view.getUint32(offset, true) === 0x04034b50) {
        assert.equal(view.getUint16(offset + 6, true), 0x0800);
        assert.equal(view.getUint16(offset + 8, true), 0);
        const size = view.getUint32(offset + 18, true);
        const nameLength = view.getUint16(offset + 26, true);
        const name = new TextDecoder().decode(bytes.slice(offset + 30, offset + 30 + nameLength));
        entries.set(name, bytes.slice(offset + 30 + nameLength, offset + 30 + nameLength + size));
        offset += 30 + nameLength + size;
    }
    assert.deepEqual(entries.get('screenshots/states/cs1.png'), Uint8Array.from([1, 2, 3]));
    assert.deepEqual(JSON.parse(new TextDecoder().decode(entries.get('model_hybrid.json'))), bundle.jsonModel);
    const end = bytes.length - 22;
    assert.equal(view.getUint32(end, true), 0x06054b50);
    assert.equal(view.getUint16(end + 10, true), entries.size);
    assert.equal(view.getUint32(end + 16, true), offset);
    const checksumZip = createZip([{name: 'test.txt', bytes: new TextEncoder().encode('123456789')}]);
    assert.equal(new DataView(checksumZip.buffer).getUint32(14, true), 0xcbf43926);
});

test('both viewers load the same exporter and offline controls use embedded data', () => {
    const fs = require('node:fs');
    const path = require('node:path');
    const staticPage = fs.readFileSync(path.resolve(__dirname, '../../resources/graphs-static/index.html'), 'utf8');
    const livePage = fs.readFileSync(path.resolve(__dirname, '../../resources/graphs/graph.jsp'), 'utf8');
    for (const page of [staticPage, livePage]) {
        assert.match(page, /js\/model-json-export.js/);
        assert.match(page, /js\/model-export-controls.js/);
        assert.match(page, /export-model/);
        assert.match(page, /css\/model-export.css/);
    }
    assert.match(staticPage, /widget-trees.js/);
    assert.match(staticPage, /widgetTrees: window.__TESTAR_WIDGET_TREES__/);
    assert.doesNotMatch(staticPage, /fetch\(/);
    assert.match(livePage, /model-export-data\?/);
});

test('configured properties preserve mandatory identities and hierarchy without changing persisted data', () => {
    const snapshot = fixture();
    snapshot.widgetTrees.cs1[0].data.WebName = 'Root';
    const before = JSON.stringify(snapshot);
    const bundle = buildBundle(snapshot, 'hybrid', {includeWidgetTrees: true, includeScreenshots: false,
        properties: {states: ['WebTitle'], actions: [], transitions: [], widgets: ['WebName']}});
    const state = bundle.jsonModel.AbstractStates[0].ConcreteStates[0];
    assert.deepEqual(state.Properties, {WebTitle: 'caf\u00e9'});
    assert.equal(state.ConcreteStateID, 'SC1');
    assert.deepEqual(state.WidgetTree[0].Properties, {WebName: 'Root'});
    assert.equal(state.WidgetTree[0].Children.length, 2);
    assert.equal(state.WidgetTree[0].Children[0].ConcreteWidgetID, 'WC2');
    const action = bundle.jsonModel.AbstractActions[0].ConcreteInstances[0];
    assert.deepEqual(action.Properties, {});
    assert.ok(action.ConcreteActionID);
    assert.ok(action.SourceConcreteStateID);
    assert.ok(bundle.jsonModel.AbstractTransitions[0].SourceAbstractStateID);
    assert.deepEqual(bundle.jsonModel.AbstractTransitions[0].Properties, {});
    assert.equal(JSON.stringify(snapshot), before);
    assert.deepEqual(bundle.jsonModel.Metadata.Options.Properties.actions, []);
});

test('intentionally excluded artifacts do not decode bytes, validate trees, or create warnings', () => {
    const snapshot = fixture();
    snapshot.images.cs1 = 'invalid png bytes';
    snapshot.widgetTrees.cs1 = [edge('broken', 'isChildOf', 'missing', 'absent')];
    const bundle = buildBundle(snapshot, 'hybrid', {includeWidgetTrees: false, includeScreenshots: false});
    assert.deepEqual(bundle.jsonModel.Warnings, []);
    assert.equal(bundle.files.length, 1);
    const state = bundle.jsonModel.AbstractStates[0].ConcreteStates[0];
    assert.equal('WidgetTree' in state, false);
    assert.equal('Screenshot' in state, false);
    assert.equal('Screenshot' in bundle.jsonModel.AbstractActions[0].ConcreteInstances[0], false);
    assert.equal(bundle.jsonModel.Metadata.Options.IncludeWidgetTrees, false);
    assert.equal(bundle.jsonModel.Metadata.Options.IncludeScreenshots, false);
});

test('property inventory includes captured names and accepts the server name-only inventory', () => {
    const snapshot = fixture();
    snapshot.widgetTrees.cs1[0].data.CustomTag = 'one';
    snapshot.widgetTrees.cs1[1].data.CustomTag = 'two';
    const inventory = propertyInventory(snapshot);
    assert.ok(inventory.states.includes('WebTitle'));
    assert.ok(inventory.actions.includes('InputText'));
    assert.ok(inventory.transitions.includes('InputText'));
    assert.ok(inventory.widgets.includes('CustomTag'));
    assert.ok(inventory.widgets.includes('Path'));
    assert.equal(inventory.widgets.filter(name => name === 'CustomTag').length, 1);
    assert.equal(inventory.transitions.includes('source'), false);
    assert.equal(inventory.widgets.includes('id'), false);
    assert.equal(inventory.states.includes('stateId'), false);
    assert.deepEqual(propertyInventory({propertyInventory: inventory}), inventory);
});

test('offline exports refuse uncaptured artifacts and keep available data exportable', () => {
    const snapshot = fixture();
    snapshot.metadata.widgetTreesCaptured = false;
    assert.throws(() => buildBundle(snapshot, 'hybrid', {includeWidgetTrees: true}), /not captured/);
    const bundle = buildBundle(snapshot, 'hybrid', {includeWidgetTrees: false, includeScreenshots: true});
    assert.equal('WidgetTree' in bundle.jsonModel.AbstractStates[0].ConcreteStates[0], false);
    assert.ok(bundle.files.some(file => file.name.endsWith('.png')));
});

test('concrete export keeps every transition record, origin widget, and actual sequence start', () => {
    const {jsonModel, files} = buildBundle(traceSnapshot(), 'concrete');
    assert.deepEqual(jsonModel.InitialStates, ['SC1']);
    assert.deepEqual(jsonModel.ConcreteStates.map(state => state.ConcreteStateID), ['SC1', 'SC2', 'SC3']);
    assert.equal(jsonModel.ConcreteStates[2].IsInitial, false);
    assert.equal(jsonModel.ConcreteStates[2].AbstractStateID, 'SA1');
    const clickTransitions = jsonModel.ConcreteTransitions.filter(transition => transition.ConcreteActionID === 'AC1');
    assert.deepEqual(clickTransitions.map(transition => [transition.TransitionID,
        transition.SourceConcreteStateID, transition.TargetConcreteStateID]), [['ca1', 'SC1', 'SC1'], ['ca2', 'SC1', 'SC2']]);
    assert.equal(new Set(jsonModel.ConcreteActions.map(action => action.TransitionID)).size, 3);
    assert.equal(jsonModel.ConcreteActions[0].AbstractActionID, 'AAclick');
    assert.equal(jsonModel.ConcreteActions[0].AbstractWidgetID, 'WA1');
    assert.notEqual(jsonModel.ConcreteActions[0].ConcreteActionID, jsonModel.ConcreteActions[0].ConcreteWidgetID);
    assert.deepEqual(jsonModel.ConcreteActions.slice(0, 2).map(action => action.Screenshot),
        ['screenshots/actions/ca1.png', 'screenshots/actions/ca2.png']);
    assert.equal(jsonModel.Warnings.length, 0);
    assert.ok(files.some(file => file.name === 'model_concrete.json'));
});

test('sequence traces preserve repeated actions and numeric occurrence order with deduplicated artifacts', () => {
    const snapshot = traceSnapshot();
    snapshot.elements.reverse();
    const before = JSON.stringify(snapshot);
    const bundle = buildBundle(snapshot, 'traces');
    const {jsonModel} = bundle;
    assert.equal(JSON.stringify(snapshot), before);
    assert.deepEqual(bundle, buildBundle(traceSnapshot(), 'traces'));
    assert.deepEqual(jsonModel.InitialStates, ['SC1']);
    const sequence = jsonModel.Sequences[0];
    assert.equal(sequence.SequenceID, 'SEQ1');
    assert.equal(sequence.StartTime, '2026-10-03T10:00:00Z');
    assert.deepEqual(sequence.StartOccurrenceIDs, ['SEQ1-N0']);
    assert.deepEqual(sequence.StateOccurrences.map(occurrence => occurrence.Order), [0, 2, 10]);
    assert.deepEqual(sequence.Steps.map(step => step.OccurrenceID), ['step1', 'step2']);
    assert.deepEqual(sequence.Steps.map(step => step.ConcreteActionID), ['AC1', 'AC1']);
    assert.deepEqual(sequence.Steps.map(step => step.ConcreteTransitionID), ['ca1', 'ca1']);
    assert.equal(sequence.Steps[0].SourceOccurrenceID, 'SEQ1-N0');
    assert.equal(sequence.Steps[1].SourceOccurrenceID, 'SEQ1-N2');
    assert.equal(sequence.Steps[1].Timestamp, '2026-10-03T10:00:02Z');
    assert.equal(jsonModel.Sequences[1].Steps[0].ConcreteTransitionID, 'ca2');
    assert.deepEqual(jsonModel.ConcreteStates.map(state => state.ConcreteStateID), ['SC1', 'SC2']);
    assert.deepEqual(jsonModel.ConcreteActions.map(action => action.TransitionID), ['ca1', 'ca2']);
    assert.equal(jsonModel.Warnings.length, 0);
    assert.equal(bundle.files.filter(file => file.name === 'screenshots/actions/ca1.png').length, 1);
});

test('unresolved trace actions retain their occurrence instead of borrowing an action by abstract identity', () => {
    const snapshot = traceSnapshot();
    snapshot.elements.find(element => element.data.id === 'ss2').data.concreteActionUid = 'not-recorded';
    snapshot.elements.push(edge('orphan', 'SequenceStep', 'unknown-source', 'unknown-target', {concreteActionId: 'AC1'}));
    const {jsonModel} = buildBundle(snapshot, 'traces');
    const step = jsonModel.Sequences[0].Steps[1];
    assert.equal(step.OccurrenceID, 'step2');
    assert.equal(step.ConcreteActionID, 'AC1');
    assert.equal(step.ConcreteActionUID, 'not-recorded');
    assert.equal(step.ConcreteTransitionID, null);
    assert.equal(step.AssociationStatus, 'unresolved');
    assert.ok(jsonModel.Warnings.includes('Occurrence action unresolved: ss2'));
    assert.equal(jsonModel.UnassignedSteps.length, 1);
    assert.equal(jsonModel.UnassignedSteps[0].SourceGraphNodeID, 'unknown-source');
    assert.equal(jsonModel.UnassignedSteps[0].TargetOccurrenceID, null);
});

test('ambiguous trace actions remain explicit and unique endpoint matching never matches AC1 to AC10', () => {
    const snapshot = traceSnapshot();
    delete snapshot.elements.find(element => element.data.id === 'ss1').data.concreteActionUid;
    const duplicate = structuredClone(snapshot.elements.find(element => element.data.id === 'ca1'));
    duplicate.data.id = 'ca-duplicate';
    duplicate.data.uid = 'other-loop';
    duplicate.data.actionId = 'AC10';
    snapshot.elements.push(duplicate);
    let model = buildBundle(snapshot, 'traces').jsonModel;
    assert.equal(model.Sequences[0].Steps[0].ConcreteTransitionID, 'ca1');
    duplicate.data.actionId = 'AC1';
    model = buildBundle(snapshot, 'traces').jsonModel;
    assert.equal(model.Sequences[0].Steps[0].AssociationStatus, 'ambiguous');
    assert.equal(model.Sequences[0].Steps[0].ConcreteTransitionID, null);
    assert.equal(model.Sequences[0].Steps[1].ConcreteTransitionID, 'ca1');
});

test('missing trace state links preserve occurrences and missing order without inventing a state or action', () => {
    const snapshot = traceSnapshot();
    const node = snapshot.elements.find(element => element.data.id === 'sn2');
    node.data.concreteStateId = 'SC-missing';
    delete node.data.nodeNr;
    snapshot.elements.find(element => element.data.id === 'access2').data.target = 'cs-missing';
    const {jsonModel} = buildBundle(snapshot, 'traces');
    const occurrence = jsonModel.Sequences[0].StateOccurrences.find(record => record.OccurrenceID === 'SEQ1-N2');
    assert.equal(occurrence.ConcreteStateID, 'SC-missing');
    assert.equal(occurrence.StateGraphNodeID, null);
    assert.equal(occurrence.Order, null);
    assert.equal(occurrence.AssociationStatus, 'unresolved');
    assert.equal(jsonModel.Sequences[0].Steps.filter(step => step.ConcreteTransitionID === null).length, 2);
    assert.ok(jsonModel.Warnings.includes('Occurrence state unresolved: sn2'));
    assert.ok(jsonModel.Warnings.includes('Occurrence order unavailable: sn2'));
});

test('property selection keeps mandatory sequence chronology and identifiers and skips optional artifacts', () => {
    const snapshot = traceSnapshot();
    snapshot.images.cs1 = 'invalid bytes';
    snapshot.widgetTrees.cs1 = [edge('broken', 'isChildOf', 'unknown', 'absent')];
    const bundle = buildBundle(snapshot, 'traces', {includeWidgetTrees: false, includeScreenshots: false,
        properties: {states: ['WebTitle'], actions: [], transitions: [], sequences: [], widgets: []}});
    const sequence = bundle.jsonModel.Sequences[0];
    assert.deepEqual(sequence.Properties, {});
    assert.deepEqual(sequence.StateOccurrences[0].Properties, {});
    assert.deepEqual(sequence.Steps[0].Properties, {});
    assert.equal(sequence.Steps[0].Timestamp, '2026-10-03T10:00:01Z');
    assert.equal(sequence.StateOccurrences[1].Order, 2);
    assert.equal(sequence.StateOccurrences[1].ConcreteStateID, 'SC1');
    assert.equal(sequence.Steps[0].ConcreteTransitionID, 'ca1');
    assert.equal(bundle.files.length, 1);
    assert.deepEqual(bundle.jsonModel.Warnings, []);
    assert.equal('Screenshot' in bundle.jsonModel.ConcreteActions[0], false);
    assert.equal('WidgetTree' in bundle.jsonModel.ConcreteStates[0], false);
    assert.deepEqual(bundle.jsonModel.Metadata.Options.Properties.sequences, []);
    const inventory = propertyInventory(snapshot);
    assert.ok(inventory.sequences.includes('verdict'));
    assert.ok(inventory.transitions.includes('actionDescription'));
    assert.equal(inventory.sequences.includes('nodeNr'), false);
    assert.equal(inventory.transitions.includes('concreteActionUid'), false);
});

test('empty sequences and start-only traces are exportable and sequence boundaries are validated', () => {
    const snapshot = traceSnapshot();
    snapshot.elements = snapshot.elements.filter(element => !['sn2', 'sn3', 'ss1', 'ss2', 'access2', 'access3'].includes(element.data.id));
    snapshot.elements.push(node('empty', 'TestSequence', {sequenceId: 'SEQ3'}));
    let model = buildBundle(snapshot, 'traces').jsonModel;
    assert.equal(model.Sequences[0].StateOccurrences.length, 1);
    assert.deepEqual(model.Sequences[0].Steps, []);
    assert.deepEqual(model.Sequences[2].StateOccurrences, []);
    assert.equal(model.Sequences[2].StartTime, null);
    snapshot.elements.find(element => element.data.id === 'ss3').data.target = 'sn1';
    assert.throws(() => buildBundle(snapshot, 'traces'), /crosses sequence boundaries/);
});

test('concrete exports preserve available data when abstract lineage is unavailable', () => {
    const snapshot = traceSnapshot();
    snapshot.elements = snapshot.elements.filter(element => !['AbstractState', 'AbstractAction', 'isAbstractedBy'].includes(element.classes));
    snapshot.elements.filter(element => element.classes === 'ConcreteState').forEach(element => { delete element.data.AbstractID; });
    const {jsonModel} = buildBundle(snapshot, 'concrete', {includeScreenshots: false, includeWidgetTrees: false});
    assert.equal(jsonModel.ConcreteStates.length, 3);
    assert.equal(jsonModel.ConcreteStates[0].AbstractStateID, null);
    assert.equal(jsonModel.ConcreteActions[0].AbstractActionID, null);
    assert.equal(jsonModel.ConcreteTransitions.length, 3);
    assert.ok(jsonModel.Warnings.includes('Abstract action lineage unavailable: ca1'));
});

test('concrete transition properties are selectable without removing identities or screenshot references', () => {
    const snapshot = traceSnapshot();
    const options = {includeWidgetTrees: false, includeScreenshots: true,
        properties: {states: ['WebTitle'], actions: [], transitions: ['Role']}};
    const {jsonModel} = buildBundle(snapshot, 'concrete', options);
    assert.deepEqual(jsonModel.ConcreteActions[0].Properties, {});
    assert.deepEqual(jsonModel.ConcreteTransitions[0].Properties, {Role: 'click'});
    assert.equal(jsonModel.ConcreteTransitions[0].TransitionID, 'ca1');
    assert.equal(jsonModel.ConcreteTransitions[0].ConcreteActionID, 'AC1');
    assert.equal(jsonModel.ConcreteActions[0].Screenshot, 'screenshots/actions/ca1.png');
    assert.equal('WidgetTree' in jsonModel.ConcreteStates[0], false);
    assert.ok(propertyInventory(snapshot).transitions.includes('Role'));
});
