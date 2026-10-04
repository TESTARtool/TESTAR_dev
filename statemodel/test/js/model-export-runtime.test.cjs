const assert = require('node:assert/strict');
const test = require('node:test');
const exporter = require('../../resources/graphs/js/model-json-export.js');
const runtime = require('../../resources/graphs/js/model-export-runtime.js');
const {createWorkerEnvironment} = require('./support/export-worker.cjs');
const {traceSnapshot, node, edge} = require('./support/model-export-fixture.cjs');

function streamEnvironment(text, chunkSize = 17) {
    const bytes = new TextEncoder().encode(text);
    let offset = 0;
    let cancelled = false;
    let released = false;
    const reader = {
        async read() {
            if (offset === bytes.length) {
                return {done: true};
            }
            const value = bytes.slice(offset, offset + chunkSize);
            offset += value.length;
            return {value, done: false};
        },
        async cancel() { cancelled = true; },
        releaseLock() { released = true; }
    };
    const requests = [];
    return {environment: {TextDecoder, fetch: async url => {
        requests.push(url);
        return {ok: true, body: {getReader: () => reader}};
    }}, requests, closed: () => cancelled && released};
}

test('streamed progress arrives before an unparsed snapshot and preserves split UTF-8', async () => {
    const snapshot = traceSnapshot();
    snapshot.metadata.title = 'caf\u00e9 \u4e2d\u6587';
    const source = [
        {type: 'progress', phase: 'trees', completed: 1, total: 3},
        {type: 'progress', phase: 'trees', completed: 3, total: 3},
        {type: 'progress', phase: 'transfer'},
        {type: 'snapshot', snapshot}
    ].map(event => JSON.stringify(event)).join('\n');
    const stream = streamEnvironment(source);
    const progress = [];
    const result = await runtime.loadSnapshot('export?format=hybrid', false, event => progress.push(event), stream.environment);
    assert.deepEqual(stream.requests, ['export?format=hybrid&progress=true']);
    assert.deepEqual(progress.slice(0, 2).map(event => [event.completed, event.total]), [[1, 3], [3, 3]]);
    assert.ok(progress.some(event => event.bytes > 0));
    assert.deepEqual(JSON.parse(result.serializedSnapshot).snapshot, snapshot);
    assert.equal(stream.closed(), true);
});

test('stream errors and truncated snapshots fail instead of downloading partial data', async () => {
    for (const source of [JSON.stringify({type: 'error', message: 'Preparation failed'}) + '\n',
        JSON.stringify({type: 'progress', phase: 'trees', completed: 1, total: 3}) + '\n']) {
        const stream = streamEnvironment(source);
        await assert.rejects(runtime.loadSnapshot('export?format=hybrid', false, () => {}, stream.environment),
            /Preparation failed|ended before/);
        assert.equal(stream.closed(), true);
    }
});

test('inventory remains lightweight JSON without streaming preparation', async () => {
    const requests = [];
    const inventory = {metadata: {}, propertyInventory: {widgets: ['WebName']}};
    const result = await runtime.loadSnapshot('export?format=inventory', true, () => assert.fail('No artifact preparation'),
        {fetch: async url => { requests.push(url); return {ok: true, json: async () => inventory}; }});
    assert.deepEqual(requests, ['export?format=inventory']);
    assert.equal(result.metadata.live, true);
    assert.deepEqual(result.propertyInventory, inventory.propertyInventory);
});

test('real background worker produces the same ZIP as direct export and reports packaging progress', async () => {
    const fixture = createWorkerEnvironment();
    const progress = [];
    const snapshot = traceSnapshot();
    const options = {includeWidgetTrees: true, includeScreenshots: true};
    const environment = {...fixture.environment, TestarModelExport: exporter};
    const result = await runtime.prepare(snapshot, 'traces', options, event => progress.push(event), environment);
    const bundle = exporter.buildBundle(snapshot, 'traces', options);
    assert.deepEqual(result.archive, exporter.createZip(bundle.files));
    assert.equal(result.warningCount, bundle.jsonModel.Warnings.length);
    assert.ok(progress.some(event => event.phase === 'states' && event.completed > 0));
    assert.ok(progress.some(event => event.phase === 'zip' && event.completed === event.total));
    assert.equal(fixture.workers[0].terminated, true);
    assert.deepEqual(fixture.revoked, ['blob:worker-1']);
    assert.equal(JSON.stringify(snapshot).includes('customLabel'), false);
});

test('backend snapshot parsing and malformed data errors happen in the worker with cleanup', async () => {
    const fixture = createWorkerEnvironment();
    const environment = {...fixture.environment, TestarModelExport: exporter};
    const snapshot = traceSnapshot();
    const result = await runtime.prepare({serializedSnapshot: JSON.stringify({type: 'snapshot', snapshot})},
        'concrete', {includeWidgetTrees: false, includeScreenshots: false}, () => {}, environment);
    assert.ok(result.archive.byteLength > 0);
    await assert.rejects(runtime.prepare({serializedSnapshot: '{invalid'}, 'concrete', {}, () => {}, environment),
        /JSON|property name|Unexpected/);
    assert.equal(fixture.workers.every(worker => worker.terminated), true);
    assert.deepEqual(fixture.revoked, ['blob:worker-1', 'blob:worker-2']);
});

test('worker startup failures and unavailable workers give explicit errors', async () => {
    await assert.rejects(runtime.prepare({}, 'hybrid', {}, () => {}, {}), /Background export is unavailable/);
    const fixture = createWorkerEnvironment();
    await assert.rejects(runtime.prepare({}, 'hybrid', {}, () => {}, {
        ...fixture.environment, TestarModelExport: exporter,
        Worker: class { constructor() { throw new Error('Worker blocked'); } }
    }), /Worker blocked/);
    assert.deepEqual(fixture.revoked, ['blob:worker-1']);
});

test('a 500-state captured export packages in the background while the main thread remains available', async () => {
    const fixture = createWorkerEnvironment();
    const snapshot = {metadata: {modelIdentifier: 'large-model'}, elements: [node('as1', 'AbstractState', {stateId: 'SA1'})],
        widgetTrees: {}, images: {}};
    for (let index = 0; index < 500; index++) {
        const id = `cs${index}`;
        snapshot.elements.push(node(id, 'ConcreteState', {stateId: `SC${index}`, AbstractID: 'SA1', WebTitle: `Page ${index}`}));
        snapshot.elements.push(edge(`ab${index}`, 'isAbstractedBy', id, 'as1'));
        snapshot.widgetTrees[id] = [node(`root${index}`, 'Widget', {ConcreteID: `WC${index}`, WebName: 'Root'})];
    }
    const progress = [];
    let finished = false;
    const preparing = runtime.prepare(snapshot, 'hybrid', {includeWidgetTrees: true, includeScreenshots: false},
        event => progress.push(event), {...fixture.environment, TestarModelExport: exporter}).then(result => {
        finished = true;
        return result;
    });
    await new Promise(resolve => setTimeout(resolve, 0));
    assert.equal(finished, false, 'The main thread can service other events during worker preparation');
    const result = await preparing;
    assert.ok(result.archive.byteLength > 0);
    assert.ok(progress.some(event => event.phase === 'states' && event.completed === 500));
    assert.equal(fixture.workers[0].terminated, true);
});
