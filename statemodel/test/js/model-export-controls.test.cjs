const assert = require('node:assert/strict');
const test = require('node:test');
const {attach} = require('../../resources/graphs/js/model-export-controls.js');
const {createEnvironment} = require('./support/export-dialog-dom.cjs');
const {traceSnapshot} = require('./support/model-export-fixture.cjs');

function snapshot(live = true) {
    return {metadata: {modelIdentifier: 'model-1', live, widgetTreesCaptured: live, screenshotsCaptured: true},
        elements: [{classes: 'AbstractState', data: {id: 'as1', stateId: 'SA1', WebTitle: 'Home'}}],
        widgetTrees: {cs1: [{classes: 'Widget', data: {id: 'w1', WebName: 'Root', Path: '[]'}}]}, images: {}};
}

test('dialog loads lightweight inventory and exports with default artifact choices', async () => {
    const ui = createEnvironment();
    const requests = [];
    attach(async options => { requests.push(options); return snapshot(); }, ui.environment);

    await ui.get('export-model').fire();
    assert.equal(ui.dialog().open, true);
    assert.deepEqual(requests, [{format: 'inventory', includeWidgetTrees: false, includeScreenshots: false}]);
    assert.equal(ui.get('model-export-format').value, 'hybrid');
    assert.equal(ui.get('model-export-trees').checked, false);
    assert.equal(ui.get('model-export-images').checked, true);
    assert.equal(ui.group('widgets').disabled, true);
    await ui.get('model-export-submit').fire();
    assert.equal(requests[1].includeWidgetTrees, false);
    assert.equal(requests[1].includeScreenshots, true);
    assert.deepEqual(ui.downloads, ['model_hybrid.zip']);
    assert.deepEqual(ui.revoked, ['blob:worker-1', 'blob:export']);
    assert.equal(ui.workers[0].terminated, true);
    assert.equal(ui.timers.size, 0);
    assert.equal(ui.dialog().open, false);
    assert.equal(ui.get('export-model').disabled, false);
});

test('busy export blocks duplicate requests, closing and escape, and supports failure retry', async () => {
    const ui = createEnvironment();
    let resolve;
    let reject;
    let requests = 0;
    attach(options => {
        if (options.format === 'inventory') {
            return Promise.resolve(snapshot());
        }
        requests++;
        return new Promise((done, failed) => { resolve = done; reject = failed; });
    }, ui.environment);
    await ui.get('export-model').fire();
    const first = ui.get('model-export-submit').fire();
    assert.equal(ui.get('model-export-cancel').disabled, true);
    await ui.get('model-export-submit').fire();
    await ui.get('model-export-cancel').fire();
    const escape = await ui.dialog().fire('cancel');
    assert.equal(escape.defaultPrevented, true);
    assert.equal(ui.dialog().open, true);
    assert.equal(requests, 1);
    reject(new Error('Datastore unavailable'));
    await first;
    assert.deepEqual(ui.downloads, []);
    assert.match(ui.get('model-export-feedback').textContent, /Datastore unavailable/);
    assert.equal(ui.get('model-export-submit').disabled, false);
    const retry = ui.get('model-export-submit').fire();
    resolve(snapshot());
    await retry;
    assert.deepEqual(ui.downloads, ['model_hybrid.zip']);
});

test('offline snapshot availability disables uncaptured trees without disabling the export', async () => {
    const ui = createEnvironment();
    attach(async () => snapshot(false), ui.environment);
    await ui.get('export-model').fire();
    assert.equal(ui.get('model-export-trees').disabled, true);
    assert.match(ui.get('model-export-availability').textContent, /not captured/);
    assert.equal(ui.get('model-export-images').disabled, false);
    assert.equal(ui.get('model-export-submit').disabled, false);
    await ui.get('model-export-submit').fire();
    assert.deepEqual(ui.downloads, ['model_hybrid.zip']);
});

test('property search and bulk selections affect the confirmed download options', async () => {
    const ui = createEnvironment();
    let selected;
    attach(async options => { selected = options; return snapshot(); }, ui.environment);
    await ui.get('export-model').fire();
    const group = ui.group('states');
    const search = group.find(node => node.type === 'search');
    search.value = 'webtitle';
    await search.fire('input');
    const visible = group.find(node => node.tagName === 'label' && node.children.some(child => child.textContent === 'WebTitle'));
    assert.equal(visible.hidden, false);
    search.value = 'not-present';
    await search.fire('input');
    assert.equal(visible.hidden, true);
    await group.find(node => node.textContent === 'Clear selection').fire();
    await ui.get('model-export-submit').fire();
    assert.deepEqual(selected.properties.states, []);
    await ui.get('export-model').fire();
    const next = ui.group('states');
    await next.find(node => node.textContent === 'Clear selection').fire();
    await next.find(node => node.textContent === 'Select all').fire();
    await ui.get('model-export-submit').fire();
    assert.deepEqual(selected.properties.states, ['WebTitle']);
});

test('cancelled inventory cannot overwrite a newer dialog', async () => {
    const ui = createEnvironment();
    let resolve;
    let calls = 0;
    attach(() => ++calls === 1 ? new Promise(done => { resolve = done; }) : Promise.resolve(snapshot(false)), ui.environment);
    const first = ui.get('export-model').fire();
    await ui.get('model-export-cancel').fire();
    await ui.get('export-model').fire();
    resolve(snapshot(true));
    await first;
    assert.equal(ui.get('model-export-trees').disabled, true);
    assert.match(ui.get('model-export-availability').textContent, /not captured/);
});

test('inventory failures allow cancel and reopening to recover', async () => {
    const ui = createEnvironment();
    let fail = true;
    attach(async () => {
        if (fail) {
            throw new Error('Analysis unavailable');
        }
        return snapshot();
    }, ui.environment);
    await ui.get('export-model').fire();
    assert.match(ui.get('model-export-feedback').textContent, /Analysis unavailable/);
    assert.equal(ui.get('model-export-submit').disabled, true);
    await ui.get('model-export-cancel').fire();
    fail = false;
    await ui.get('export-model').fire();
    assert.equal(ui.get('model-export-submit').disabled, false);
});

test('all formats are selectable and sequence properties are shown only for trace downloads', async () => {
    const ui = createEnvironment();
    const requests = [];
    attach(async options => { requests.push(options); return traceSnapshot(); }, ui.environment);
    await ui.get('export-model').fire();
    const format = ui.get('model-export-format');
    assert.deepEqual(format.children.map(option => [option.value, Boolean(option.disabled)]),
        [['abstract', false], ['hybrid', false], ['concrete', false], ['traces', false]]);
    assert.equal(ui.group('sequences').hidden, true);
    format.value = 'concrete';
    await format.fire('change');
    assert.equal(ui.group('sequences').hidden, true);
    await ui.get('model-export-submit').fire();
    assert.equal(requests[1].format, 'concrete');
    assert.deepEqual(ui.downloads, ['model_concrete.zip']);
    await ui.get('export-model').fire();
    format.value = 'traces';
    await format.fire('change');
    assert.equal(ui.group('sequences').hidden, false);
    assert.equal(ui.group('sequences').disabled, false);
    await ui.group('sequences').find(element => element.textContent === 'Clear selection').fire();
    await ui.get('model-export-submit').fire();
    assert.equal(requests[3].format, 'traces');
    assert.deepEqual(requests[3].properties.sequences, []);
    assert.deepEqual(ui.downloads, ['model_concrete.zip', 'model_traces.zip']);
});

test('collapsed advanced properties default to semantic fields and allow visual fields explicitly', async () => {
    const ui = createEnvironment();
    const data = snapshot();
    Object.assign(data.elements[0].data, {WebId: 'home', Role: 'page', Shape: '[0,0,800,600]',
        WebComputedColor: '#fff', WebComputedFontSize: '12px', customLabel: 'Presentation', CustomTag: 'optional'});
    let selected;
    attach(async options => { selected = options; return data; }, ui.environment);
    await ui.get('export-model').fire();
    assert.equal(ui.get('model-export-advanced').open, false);
    await ui.get('model-export-submit').fire();
    assert.deepEqual(selected.properties.states, ['Role', 'WebId', 'WebTitle']);
    await ui.get('export-model').fire();
    const states = ui.group('states');
    await states.find(node => node.textContent === 'Select all').fire();
    await ui.get('model-export-submit').fire();
    assert.ok(selected.properties.states.includes('WebComputedColor'));
    assert.equal(selected.properties.states.includes('customLabel'), false);
    await ui.get('export-model').fire();
    await ui.group('states').find(node => node.textContent === 'Select all').fire();
    await ui.group('states').find(node => node.textContent === 'Use semantic defaults').fire();
    await ui.get('model-export-submit').fire();
    assert.deepEqual(selected.properties.states, ['Role', 'WebId', 'WebTitle']);
});

test('live preparation counts and transfer progress remain visible until background packaging finishes', async () => {
    const ui = createEnvironment();
    let report;
    let finish;
    attach((options, progress) => {
        if (options.format === 'inventory') {
            return Promise.resolve(snapshot());
        }
        report = progress;
        return new Promise(resolve => { finish = resolve; });
    }, ui.environment);
    await ui.get('export-model').fire();
    const saving = ui.get('model-export-submit').fire();
    report({phase: 'trees', completed: 25, total: 500});
    assert.match(ui.get('model-export-feedback').textContent, /Collecting widget trees: 25\/500.*elapsed/);
    assert.equal(ui.get('model-export-progress').value, 25);
    assert.equal(ui.get('model-export-progress').max, 500);
    report({phase: 'transfer', bytes: 10485760});
    assert.match(ui.get('model-export-feedback').textContent, /10.0 MB received/);
    assert.equal(ui.get('model-export-progress').value, undefined);
    assert.equal(ui.get('model-export-progress').hidden, false);
    assert.equal(ui.timers.size, 1);
    finish(snapshot());
    await saving;
    assert.equal(ui.get('model-export-progress').hidden, true);
    assert.equal(ui.timers.size, 0);
    assert.equal(ui.workers[0].terminated, true);
});

test('background worker failure keeps the dialog open with retry controls and no partial download', async () => {
    const ui = createEnvironment();
    attach(async () => snapshot(), ui.environment);
    const worker = ui.environment.Worker;
    ui.environment.Worker = class { constructor() { throw new Error('Background worker blocked'); } };
    await ui.get('export-model').fire();
    await ui.get('model-export-submit').fire();
    assert.deepEqual(ui.downloads, []);
    assert.equal(ui.dialog().open, true);
    assert.equal(ui.get('model-export-submit').disabled, false);
    assert.match(ui.get('model-export-feedback').textContent, /Background worker blocked/);
    assert.equal(ui.timers.size, 0);
    ui.environment.Worker = worker;
    await ui.get('model-export-submit').fire();
    assert.deepEqual(ui.downloads, ['model_hybrid.zip']);
});
