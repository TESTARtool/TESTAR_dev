const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');
const {createEnvironment} = require('./support/export-dialog-dom.cjs');
const {traceSnapshot} = require('./support/model-export-fixture.cjs');

async function exportFromPage(relativePage, live, format) {
    const resources = path.resolve(__dirname, '../../resources');
    const page = fs.readFileSync(path.join(resources, relativePage), 'utf8');
    const ui = createEnvironment();
    const requests = [];
    const snapshot = traceSnapshot();
    const context = vm.createContext({
        ...ui.environment,
        fetch: async url => {
            assert.equal(live, true, 'Offline export must not fetch data');
            requests.push(url);
            if (url.includes('format=inventory')) {
                return new Response(JSON.stringify(snapshot));
            }
            return new Response(JSON.stringify({type: 'progress', phase: 'graph'}) + '\n'
                + JSON.stringify({type: 'progress', phase: 'transfer'}) + '\n'
                + JSON.stringify({type: 'snapshot', snapshot}) + '\n');
        },
        __TESTAR_ELEMENTS__: snapshot.elements,
        __TESTAR_WIDGET_TREES__: snapshot.widgetTrees,
        __TESTAR_IMAGES__: snapshot.images,
        __TESTAR_RUN__: snapshot.metadata
    });
    context.window = context;
    for (const script of ['model-json-export.js', 'model-export-runtime.js', 'model-export-controls.js']) {
        vm.runInContext(fs.readFileSync(path.join(resources, 'graphs/js', script), 'utf8'), context);
    }
    let initialization = page.match(/<script>\s*([\s\S]*?)<\/script>/)[1];
    if (live) {
        initialization = initialization.split('// global object')[0].replaceAll('${exportModelIdentifier}', 'model-1');
    }
    vm.runInContext(initialization, context);
    await ui.get('export-model').fire();
    ui.get('model-export-format').value = format;
    await ui.get('model-export-format').fire('change');
    await ui.get('model-export-submit').fire();
    assert.deepEqual(ui.downloads, [`model_${format}.zip`]);
    assert.equal(ui.get('export-model').disabled, false);
    assert.ok(ui.get('model-export-status').textContent.includes(`Downloaded model_${format}.zip`));
    assert.equal(ui.archive().type, 'application/zip');
    assert.equal(ui.workers.every(worker => worker.terminated), true);
    assert.equal(ui.timers.size, 0);
    return {archive: Buffer.from(await ui.archive().arrayBuffer()), requests};
}

for (const format of ['abstract', 'hybrid', 'concrete', 'traces']) {
    test(`live and offline ${format} page exports produce the same downloadable package`, async () => {
        const offline = await exportFromPage('graphs-static/index.html', false, format);
        const live = await exportFromPage('graphs/graph.jsp', true, format);
        assert.deepEqual(offline.requests, []);
        assert.deepEqual(live.requests, [
            'model-export-data?modelIdentifier=model-1&format=inventory&includeWidgetTrees=false&includeScreenshots=false',
            `model-export-data?modelIdentifier=model-1&format=${format}&includeWidgetTrees=false&includeScreenshots=true&progress=true`
        ]);
        assert.deepEqual(offline.archive, live.archive);
        assert.equal(offline.archive.readUInt32LE(0), 0x04034b50);
    });
}
