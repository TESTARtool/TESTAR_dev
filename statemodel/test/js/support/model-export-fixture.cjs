function node(id, classes, data) {
    return {group: 'nodes', classes, data: {id, ...data}};
}

function edge(id, classes, source, target, data = {}) {
    return {group: 'edges', classes, data: {id, source, target, ...data}};
}

function traceSnapshot() {
    const elements = [
        node('as1', 'AbstractState', {stateId: 'SA1', isInitial: 'true'}),
        node('as2', 'AbstractState', {stateId: 'SA2'}),
        node('cs1', 'ConcreteState', {stateId: 'SC1', AbstractID: 'SA1', WebTitle: 'Start'}),
        node('cs2', 'ConcreteState', {stateId: 'SC2', AbstractID: 'SA2', WebTitle: 'Target'}),
        node('cs3', 'ConcreteState', {stateId: 'SC3', AbstractID: 'SA1', WebTitle: 'Not in a trace'}),
        edge('ab1', 'isAbstractedBy', 'cs1', 'as1'),
        edge('ab2', 'isAbstractedBy', 'cs2', 'as2'),
        edge('ab3', 'isAbstractedBy', 'cs3', 'as1'),
        edge('aa1', 'AbstractAction', 'as1', 'as1', {actionId: 'AAclick', concreteActionIds: '[AC1, AC10]'}),
        edge('aa2', 'AbstractAction', 'as1', 'as2', {actionId: 'AAclick', concreteActionIds: ['AC1']}),
        edge('ca1', 'ConcreteAction', 'cs1', 'cs1', {uid: 'loop', actionId: 'AC1', AbstractID: 'WA1',
            ConcreteID: 'WC1', Role: 'click'}),
        edge('ca2', 'ConcreteAction', 'cs1', 'cs2', {uid: 'branch', actionId: 'AC1', AbstractID: 'WA1',
            ConcreteID: 'WC1', Role: 'click'}),
        edge('ca3', 'ConcreteAction', 'cs3', 'cs1', {uid: 'unused', actionId: 'AC10', AbstractID: 'WA1'}),
        node('seq1', 'TestSequence', {sequenceId: 'SEQ1', startDateTime: '2026-10-03T10:00:00Z', verdict: 'OK'}),
        node('seq2', 'TestSequence', {sequenceId: 'SEQ2', startDateTime: '2026-10-03T11:00:00Z', verdict: 'FAILED'}),
        node('sn1', 'SequenceNode', {sequenceId: 'SEQ1', nodeId: 'SEQ1-N0', nodeNr: '0', concreteStateId: 'SC1',
            timestamp: '2026-10-03T10:00:00Z', containsErrors: 'false'}),
        node('sn2', 'SequenceNode', {sequenceId: 'SEQ1', nodeId: 'SEQ1-N2', nodeNr: '2', concreteStateId: 'SC1',
            timestamp: '2026-10-03T10:00:01Z'}),
        node('sn3', 'SequenceNode', {sequenceId: 'SEQ1', nodeId: 'SEQ1-N10', nodeNr: '10', concreteStateId: 'SC1',
            timestamp: '2026-10-03T10:00:02Z'}),
        node('sn4', 'SequenceNode', {sequenceId: 'SEQ2', nodeId: 'SEQ2-N0', nodeNr: 0, concreteStateId: 'SC1'}),
        node('sn5', 'SequenceNode', {sequenceId: 'SEQ2', nodeId: 'SEQ2-N1', nodeNr: 1, concreteStateId: 'SC2'}),
        edge('first1', 'FirstNode', 'seq1', 'sn1'),
        edge('first2', 'FirstNode', 'seq2', 'sn4'),
        edge('access1', 'Accessed', 'sn1', 'cs1'),
        edge('access2', 'Accessed', 'sn2', 'cs1'),
        edge('access3', 'Accessed', 'sn3', 'cs1'),
        edge('access4', 'Accessed', 'sn4', 'cs1'),
        edge('access5', 'Accessed', 'sn5', 'cs2'),
        edge('ss1', 'SequenceStep', 'sn1', 'sn2', {stepId: 'step1', concreteActionId: 'AC1', concreteActionUid: 'loop',
            timestamp: '2026-10-03T10:00:01Z', actionDescription: 'Click again'}),
        edge('ss2', 'SequenceStep', 'sn2', 'sn3', {stepId: 'step2', concreteActionId: 'AC1', concreteActionUid: 'loop',
            timestamp: '2026-10-03T10:00:02Z', actionDescription: 'Click again'}),
        edge('ss3', 'SequenceStep', 'sn4', 'sn5', {stepId: 'step3', concreteActionId: 'AC1', concreteActionUid: 'branch'})
    ];
    return {metadata: {modelIdentifier: 'model-1', widgetTreesCaptured: true, screenshotsCaptured: true}, elements,
        widgetTrees: Object.fromEntries(['cs1', 'cs2', 'cs3'].map(id => [id,
            [node(id, 'Widget', {ConcreteID: id, WebName: 'Root'})]])),
        images: Object.fromEntries(['cs1', 'cs2', 'cs3', 'ca1', 'ca2', 'ca3'].map(id => [id, 'data:image/png;base64,AQID']))};
}

module.exports = {traceSnapshot, node, edge};
