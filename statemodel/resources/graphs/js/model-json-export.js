(function initializeModelExporter(root) {
    "use strict";

    function hasClass(element, name) {
        const classes = element.classes || [];
        return (Array.isArray(classes) ? classes : classes.split(/\s+/)).includes(name);
    }

    function ordered(elements) {
        return [...elements].sort((first, second) => compare(first.data.id, second.data.id));
    }

    function compare(first, second) {
        return first < second ? -1 : first > second ? 1 : 0;
    }

    const sequenceIdentityFields = ["sequenceId", "nodeId", "stepId", "nodeNr", "timestamp", "startDateTime",
        "concreteStateId", "concreteActionId", "concreteActionUid", "finalVerdicts", "finalStateOccurrenceId"];

    const semanticProperties = new Set([
        "Role", "Title", "Name", "Text", "Value", "Desc", "Description", "InputText", "Path", "ToolTipText", "TargetID",
        "Enabled", "Blocked", "Foreground", "IsRunning", "NotResponding", "ControlType", "AutomationId", "ClassName", "ValuePattern",
        "WebTitle", "WebGenericTitle", "WebName", "WebId", "WebWidgetId", "WebWidgetName", "WebTagName",
        "WebTextContent", "WebInnerText", "WebValue", "WebAlt", "WebHelpText", "WebPlaceholder", "WebType",
        "WebHref", "WebSrc", "WebXPath", "WebCssSelector", "WebCssClasses", "WebAutomationId", "LinkReference",
        "WebAriaLabel", "WebAriaRole", "WebAriaLabelledBy", "WebAriaDescribedBy", "WebAriaDisabled",
        "WebAriaChecked", "WebAriaSelected", "WebAriaExpanded", "WebAriaPressed", "WebAriaValueText",
        "WebIsEnabled", "WebIsDisabled", "WebIsBlocked", "WebIsHidden", "WebIsDisplayed", "WebIsActuallyVisible",
        "WebIsChecked", "WebIsSelected", "WebIsClickable", "WebIsKeyboardFocusable", "WebHasKeyboardFocus",
        "UIAName", "UIAAutomationId", "UIAClassName", "UIAControlType", "UIAIsEnabled", "UIAValueValue", "UIAHasKeyboardFocus",
        "AndroidText", "AndroidHint", "AndroidAccessibilityId", "AndroidResourceId", "AndroidClassName", "AndroidPackageName", "AndroidXpath",
        "AndroidEnabled", "AndroidClickable", "AndroidCheckable", "AndroidChecked", "AndroidSelected",
        "AndroidFocusable", "AndroidFocused", "AndroidDisplayed",
        "modelIdentifier", "uid", "widgetId", "actionDescription", "containsErrors", "oracleVerdictCode", "verdict"
    ].map(name => name.toLowerCase()));

    function defaultProperties(inventory) {
        return Object.fromEntries(Object.entries(inventory).map(([group, names]) =>
            [group, names.filter(name => semanticProperties.has(name.toLowerCase()))]));
    }

    function properties(data, selection, identityFields = []) {
        return Object.fromEntries(Object.keys(data).sort()
            .filter(key => !["id", "source", "target", "parent", "customLabel", "stateId", "actionId", "AbstractID", "ConcreteID",
                "concreteActionIds", "isInitial", ...identityFields].includes(key))
            .filter(key => !selection || selection.includes(key))
            .map(key => [key, data[key]]));
    }

    function propertyInventory(snapshot) {
        if (snapshot.propertyInventory) {
            return snapshot.propertyInventory;
        }
        const groups = {states: new Set(), actions: new Set(), transitions: new Set(), widgets: new Set(), sequences: new Set()};
        for (const element of snapshot.elements || []) {
            const group = hasClass(element, "AbstractState") || hasClass(element, "ConcreteState") ? "states"
                : hasClass(element, "ConcreteAction") ? "actions"
                : hasClass(element, "AbstractAction") || hasClass(element, "UnvisitedAbstractAction")
                    || hasClass(element, "SequenceStep") ? "transitions"
                : hasClass(element, "TestSequence") || hasClass(element, "SequenceNode") ? "sequences" : null;
            if (group) {
                const identities = hasClass(element, "TestSequence") || hasClass(element, "SequenceNode")
                    || hasClass(element, "SequenceStep") ? sequenceIdentityFields : [];
                Object.keys(properties(element.data, null, identities)).forEach(key => {
                    groups[group].add(key);
                    if (hasClass(element, "ConcreteAction")) {
                        groups.transitions.add(key);
                    }
                });
            }
        }
        for (const tree of Object.values(snapshot.widgetTrees || {})) {
            for (const element of tree.filter(widget => hasClass(widget, "Widget"))) {
                Object.keys(properties(element.data)).forEach(key => groups.widgets.add(key));
            }
        }
        return Object.fromEntries(Object.entries(groups).map(([key, values]) => [key, [...values].sort()]));
    }

    function identifier(value, label) {
        if (typeof value !== "string" || !value) {
            throw new Error(`Missing ${label}.`);
        }
        return value;
    }

    // Analysis graph properties can contain a Java collection rendered as "[AC1, AC2]".
    function actionIds(value) {
        if (Array.isArray(value)) {
            return value;
        }
        return typeof value === "string" ? value.replace(/^\[|\]$/g, "").split(",")
            .map(id => id.trim()).filter(Boolean) : [];
    }

    function buildWidgetTree(elements, selectedProperties) {
        const widgets = new Map(ordered(elements.filter(element => hasClass(element, "Widget")))
            .map(element => [element.data.id, element]));
        const children = new Map();
        const childIds = new Set();
        for (const edge of elements.filter(element => hasClass(element, "isChildOf"))) {
            if (!widgets.has(edge.data.source) || !widgets.has(edge.data.target)) {
                throw new Error("Widget tree contains an unresolved parent or child.");
            }
            const siblings = children.get(edge.data.target) || [];
            siblings.push(edge.data.source);
            children.set(edge.data.target, siblings);
            childIds.add(edge.data.source);
        }
        const visited = new Set();
        function node(id) {
            if (visited.has(id)) {
                throw new Error("Widget tree contains a cycle or multiple parents.");
            }
            visited.add(id);
            const data = widgets.get(id).data;
            const siblings = children.get(id) || [];
            siblings.sort((first, second) => {
                const firstPath = String(widgets.get(first).data.Path || "").match(/\d+/g) || [];
                const secondPath = String(widgets.get(second).data.Path || "").match(/\d+/g) || [];
                for (let index = 0; index < Math.min(firstPath.length, secondPath.length); index++) {
                    const difference = Number(firstPath[index]) - Number(secondPath[index]);
                    if (difference) {
                        return difference;
                    }
                }
                return firstPath.length - secondPath.length || compare(first, second);
            });
            return {
                AbstractWidgetID: data.AbstractID || null,
                ConcreteWidgetID: data.ConcreteID || null,
                Properties: properties(data, selectedProperties),
                Children: siblings.map(node)
            };
        }
        const tree = [...widgets.keys()].filter(id => !childIds.has(id)).map(node);
        if (visited.size !== widgets.size) {
            throw new Error("Widget tree contains a cycle.");
        }
        return tree;
    }

    function occurrenceState(occurrence, elements, concreteNodes) {
        const links = elements.filter(element => hasClass(element, "Accessed") && element.data.source === occurrence.data.id);
        const matches = concreteNodes.filter(state => (links.length
            ? links.some(link => link.data.target === state.data.id)
            : occurrence.data.concreteStateId && (state.data.stateId === occurrence.data.concreteStateId
                || state.data.ConcreteID === occurrence.data.concreteStateId))
            && (!occurrence.data.concreteStateId || state.data.stateId === occurrence.data.concreteStateId
                || state.data.ConcreteID === occurrence.data.concreteStateId));
        return matches.length === 1 ? matches[0] : null;
    }

    function occurrenceActions(step, source, target, concreteEdges) {
        if (!source || !target || !step.data.concreteActionId) {
            return [];
        }
        return concreteEdges.filter(edge => edge.data.source === source.data.id && edge.data.target === target.data.id
            && edge.data.actionId === step.data.concreteActionId
            && (!step.data.concreteActionUid || edge.data.uid === step.data.concreteActionUid));
    }

    function recordedOrder(value) {
        return /^(0|[1-9]\d*)$/.test(String(value)) && Number.isSafeInteger(Number(value)) ? Number(value) : null;
    }

    function buildTraces(elements, concreteNodes, concreteEdges, abstraction, selections, warnings) {
        const sequences = new Map();
        const occurrences = new Map();
        const selectedStates = new Set();
        const selectedActions = new Set();
        const unassignedSteps = [];
        for (const record of elements.filter(element => hasClass(element, "TestSequence"))) {
            const id = identifier(record.data.sequenceId, "sequence ID");
            if (sequences.has(id)) {
                throw new Error(`Duplicate sequence ID: ${id}.`);
            }
            const finalVerdicts = record.data.finalVerdicts || [];
            if (!Array.isArray(finalVerdicts)) {
                throw new Error(`Invalid final verdict list: ${id}.`);
            }
            sequences.set(id, {SequenceID: id, GraphNodeID: record.data.id, StartTime: record.data.startDateTime || null,
                FinalVerdicts: finalVerdicts.map(verdict => ({...verdict})),
                FinalStateOccurrenceID: record.data.finalStateOccurrenceId || null,
                Properties: properties(record.data, selections.sequences, sequenceIdentityFields),
                StartOccurrenceIDs: [], StateOccurrences: [], Steps: []});
        }
        for (const record of elements.filter(element => hasClass(element, "SequenceNode"))) {
            const sequenceId = identifier(record.data.sequenceId, "occurrence sequence ID");
            if (!sequences.has(sequenceId)) {
                warnings.add(`Sequence record unavailable: ${sequenceId}`);
                sequences.set(sequenceId, {SequenceID: sequenceId, GraphNodeID: null, StartTime: null,
                    FinalVerdicts: [], FinalStateOccurrenceID: null,
                    Properties: {}, StartOccurrenceIDs: [], StateOccurrences: [], Steps: []});
            }
            const state = occurrenceState(record, elements, concreteNodes);
            const order = recordedOrder(record.data.nodeNr);
            if (!state) {
                warnings.add(`Occurrence state unresolved: ${record.data.id}`);
            } else {
                selectedStates.add(state.data.id);
            }
            if (order === null) {
                warnings.add(`Occurrence order unavailable: ${record.data.id}`);
            }
            const occurrence = {OccurrenceID: record.data.nodeId || record.data.id, GraphNodeID: record.data.id, Order: order,
                Timestamp: record.data.timestamp || null, ConcreteStateID: state
                    ? state.data.ConcreteID || state.data.stateId : record.data.concreteStateId || null,
                AbstractStateID: state ? abstraction.get(state.data.id) || null : null,
                StateGraphNodeID: state?.data.id || null, AssociationStatus: state ? "resolved" : "unresolved",
                Properties: properties(record.data, selections.sequences, sequenceIdentityFields)};
            occurrences.set(record.data.id, {sequenceId, state, occurrence});
            sequences.get(sequenceId).StateOccurrences.push(occurrence);
        }
        for (const first of elements.filter(element => hasClass(element, "FirstNode"))) {
            const sequence = [...sequences.values()].find(record => record.GraphNodeID === first.data.source);
            const occurrence = occurrences.get(first.data.target);
            if (!sequence || !occurrence || occurrence.sequenceId !== sequence.SequenceID) {
                warnings.add(`Sequence start unresolved: ${first.data.id}`);
                continue;
            }
            sequence.StartOccurrenceIDs.push(occurrence.occurrence.OccurrenceID);
        }
        for (const record of elements.filter(element => hasClass(element, "SequenceStep"))) {
            const source = occurrences.get(record.data.source);
            const target = occurrences.get(record.data.target);
            if (source && target && source.sequenceId !== target.sequenceId) {
                throw new Error(`Sequence step crosses sequence boundaries: ${record.data.id}.`);
            }
            const matches = occurrenceActions(record, source?.state, target?.state, concreteEdges);
            const action = matches.length === 1 ? matches[0] : null;
            if (!action) {
                warnings.add(`Occurrence action ${matches.length > 1 ? "ambiguous" : "unresolved"}: ${record.data.id}`);
            } else {
                selectedActions.add(action.data.id);
            }
            const step = {OccurrenceID: record.data.stepId || record.data.id, GraphEdgeID: record.data.id,
                SourceGraphNodeID: record.data.source || null, TargetGraphNodeID: record.data.target || null,
                SourceOccurrenceID: source?.occurrence.OccurrenceID || null, TargetOccurrenceID: target?.occurrence.OccurrenceID || null,
                Order: target?.occurrence.Order ?? null, Timestamp: record.data.timestamp || null,
                ConcreteActionID: record.data.concreteActionId || null, ConcreteActionUID: record.data.concreteActionUid || null,
                ConcreteTransitionID: action?.data.id || null,
                AssociationStatus: action ? "resolved" : matches.length > 1 ? "ambiguous" : "unresolved",
                Properties: properties(record.data, selections.transitions, sequenceIdentityFields)};
            const sequenceId = source?.sequenceId || target?.sequenceId;
            if (sequenceId) {
                sequences.get(sequenceId).Steps.push(step);
            } else {
                warnings.add(`Sequence for step unavailable: ${record.data.id}`);
                unassignedSteps.push(step);
            }
        }
        const byOrder = (first, second) => (first.Order ?? Infinity) - (second.Order ?? Infinity)
            || compare(first.GraphNodeID || first.GraphEdgeID, second.GraphNodeID || second.GraphEdgeID);
        for (const sequence of sequences.values()) {
            sequence.StartOccurrenceIDs = [...new Set(sequence.StartOccurrenceIDs)].sort();
            sequence.StateOccurrences.sort(byOrder);
            sequence.Steps.sort(byOrder);
            const finalOccurrence = sequence.StateOccurrences.find(occurrence => occurrence.OccurrenceID === sequence.FinalStateOccurrenceID);
            sequence.FinalStateAssociationStatus = finalOccurrence ? "resolved"
                : sequence.FinalStateOccurrenceID || sequence.FinalVerdicts.length ? "unresolved" : "none";
            if (sequence.FinalStateAssociationStatus === "unresolved") {
                warnings.add(`Final state occurrence unresolved: ${sequence.SequenceID}`);
            }
            if (sequence.StateOccurrences.length && !sequence.StartOccurrenceIDs.length) {
                warnings.add(`Sequence start unavailable: ${sequence.SequenceID}`);
            }
        }
        return {Sequences: [...sequences.values()].sort((first, second) => compare(first.SequenceID, second.SequenceID)),
            UnassignedSteps: unassignedSteps, selectedStates, selectedActions};
    }

    function buildBundle(snapshot, format, options = {}, progress = () => {}) {
        progress({phase: "transform"});
        if (!["abstract", "hybrid", "concrete", "traces"].includes(format)) {
            throw new Error("Choose abstract, hybrid, concrete, or traces JSON export.");
        }
        if (!Array.isArray(snapshot.elements) || !snapshot.elements.length) {
            throw new Error("No model graph data is available.");
        }
        const includeWidgetTrees = options.includeWidgetTrees ?? snapshot.metadata?.widgetTreesCaptured !== false;
        const includeScreenshots = options.includeScreenshots ?? snapshot.metadata?.screenshotsCaptured !== false;
        const selections = options.properties || {};
        if (includeWidgetTrees && snapshot.metadata?.widgetTreesCaptured === false) {
            throw new Error("Widget trees were not captured in this snapshot.");
        }
        if (includeScreenshots && snapshot.metadata?.screenshotsCaptured === false) {
            throw new Error("Screenshots were not captured in this snapshot.");
        }
        const elements = ordered(snapshot.elements);
        const abstractNodes = elements.filter(element => hasClass(element, "AbstractState"));
        const needsAbstractModel = format === "abstract" || format === "hybrid";
        if (needsAbstractModel && !abstractNodes.length) {
            throw new Error("No abstract model states are available.");
        }
        const concreteNodes = elements.filter(element => hasClass(element, "ConcreteState"));
        const abstractEdges = elements.filter(element => hasClass(element, "AbstractAction")
            || hasClass(element, "UnvisitedAbstractAction"));
        const concreteEdges = elements.filter(element => hasClass(element, "ConcreteAction"));
        const abstracts = new Map(abstractNodes.map(element => [element.data.id,
            identifier(element.data.stateId, "abstract state ID")]));
        const concretes = new Map(concreteNodes.map(element => [element.data.id, element]));
        const abstraction = new Map();
        for (const edge of elements.filter(element => hasClass(element, "isAbstractedBy"))) {
            if (concretes.has(edge.data.source) && abstracts.has(edge.data.target)) {
                abstraction.set(edge.data.source, abstracts.get(edge.data.target));
            }
        }
        for (const state of concreteNodes) {
            const abstractId = abstraction.get(state.data.id) || state.data.AbstractID;
            if (needsAbstractModel && ![...abstracts.values()].includes(abstractId)) {
                throw new Error("Concrete state has no corresponding abstract state.");
            }
            abstraction.set(state.data.id, abstractId);
        }

        const warnings = new Set();
        const files = new Map();
        let statesPrepared = 0;
        function screenshot(element, kind) {
            const dataUrl = (snapshot.images || {})[element.data.id];
            if (!dataUrl) {
                warnings.add(`${kind} screenshot unavailable: ${element.data.id}`);
                return null;
            }
            const filename = `screenshots/${kind}/${encodeURIComponent(element.data.id)}.png`;
            files.set(filename, decodeImage(dataUrl));
            return filename;
        }
        function stateArtifact(element) {
            statesPrepared++;
            if (statesPrepared === 1 || statesPrepared % 25 === 0) {
                progress({phase: "states", completed: statesPrepared});
            }
            const data = element.data;
            const treeElements = includeWidgetTrees ? (snapshot.widgetTrees || {})[data.id] || [] : [];
            if (includeWidgetTrees && !treeElements.length) {
                warnings.add(`Widget tree unavailable: ${data.id}`);
            }
            return {
                ConcreteStateID: identifier(data.ConcreteID || data.stateId, "concrete state ID"),
                Properties: properties(data, selections.states),
                ...(includeWidgetTrees ? {WidgetTree: buildWidgetTree(treeElements, selections.widgets)} : {}),
                ...(includeScreenshots ? {Screenshot: screenshot(element, "states")} : {})
            };
        }

        function abstractActionFor(concrete) {
            const sourceId = abstraction.get(concrete.data.source);
            const targetId = abstraction.get(concrete.data.target);
            return abstractEdges.filter(edge => sourceId && targetId && abstracts.get(edge.data.source) === sourceId
                && abstracts.get(edge.data.target) === targetId
                && actionIds(edge.data.concreteActionIds).includes(concrete.data.actionId));
        }
        function concreteInstance(element) {
            const source = concretes.get(element.data.source);
            const target = concretes.get(element.data.target);
            if (!source || !target) {
                throw new Error("Concrete action has an unresolved state endpoint.");
            }
            return {element, SourceConcreteStateID: identifier(source.data.ConcreteID || source.data.stateId, "concrete state ID"),
                TargetConcreteStateID: identifier(target.data.ConcreteID || target.data.stateId, "concrete state ID")};
        }
        function actionArtifact(instance) {
            const data = instance.element.data;
            return {
                ConcreteActionID: identifier(data.actionId, "concrete action ID"),
                AbstractWidgetID: data.AbstractID || null,
                ConcreteWidgetID: data.ConcreteID || null,
                SourceConcreteStateID: instance.SourceConcreteStateID,
                TargetConcreteStateID: instance.TargetConcreteStateID,
                Properties: properties(data, selections.actions),
                ...(includeScreenshots ? {Screenshot: screenshot(instance.element, "actions")} : {})
            };
        }
        const metadata = snapshot.metadata || {};
        const inventory = propertyInventory(snapshot);
        function bundle(contents) {
            const groups = format === "traces" ? ["states", "actions", "transitions", "widgets", "sequences"]
                : ["states", "actions", "transitions", "widgets"];
            const jsonModel = {
                Metadata: {SchemaVersion: "1.0", Format: format, ModelIdentifier: metadata.modelIdentifier || null,
                    ModelScope: "accumulated", ...(metadata.runId ? {SnapshotRunID: metadata.runId} : {}),
                    Options: {IncludeWidgetTrees: includeWidgetTrees, IncludeScreenshots: includeScreenshots,
                        Properties: Object.fromEntries(groups.map(group => [group, selections[group] || inventory[group]]))}},
                ...contents,
                Warnings: [...warnings].sort()
            };
            progress({phase: "json"});
            files.set(`model_${format}.json`, new TextEncoder().encode(JSON.stringify(jsonModel, null, 2)));
            return {jsonModel, files: [...files].map(([name, bytes]) => ({name, bytes}))};
        }
        if (!needsAbstractModel) {
            const traces = format === "traces" ? buildTraces(elements, concreteNodes, concreteEdges, abstraction, selections, warnings) : null;
            const selectedStates = format === "traces" ? concreteNodes.filter(state => traces.selectedStates.has(state.data.id)) : concreteNodes;
            const selectedActions = format === "traces" ? concreteEdges.filter(edge => traces.selectedActions.has(edge.data.id)) : concreteEdges;
            const initialNodes = new Set(elements.filter(element => hasClass(element, "FirstNode")).map(first => {
                const sequence = elements.find(element => hasClass(element, "TestSequence") && element.data.id === first.data.source);
                const occurrence = elements.find(element => hasClass(element, "SequenceNode") && element.data.id === first.data.target);
                return sequence && occurrence && sequence.data.sequenceId === occurrence.data.sequenceId
                    ? occurrenceState(occurrence, elements, concreteNodes)?.data.id : null;
            }));
            const states = selectedStates.map(element => ({GraphNodeID: element.data.id,
                AbstractStateID: abstraction.get(element.data.id) || null,
                IsInitial: initialNodes.has(element.data.id) || element.data.isInitial === true || element.data.isInitial === "true",
                ...stateArtifact(element)}));
            const actions = selectedActions.map(element => {
                const matches = abstractActionFor(element);
                if (matches.length !== 1) {
                    warnings.add(`Abstract action lineage ${matches.length > 1 ? "ambiguous" : "unavailable"}: ${element.data.id}`);
                }
                return {TransitionID: element.data.id, ConcreteActionUID: element.data.uid || null,
                    AbstractActionID: matches.length === 1 ? matches[0].data.actionId : null,
                    ...actionArtifact(concreteInstance(element))};
            });
            return bundle({InitialStates: [...new Set(states.filter(state => state.IsInitial).map(state => state.ConcreteStateID))].sort(),
                ConcreteStates: states, ConcreteActions: actions,
                ConcreteTransitions: actions.map(action => ({TransitionID: action.TransitionID,
                    ConcreteActionID: action.ConcreteActionID, AbstractActionID: action.AbstractActionID,
                    SourceConcreteStateID: action.SourceConcreteStateID, TargetConcreteStateID: action.TargetConcreteStateID,
                    Properties: properties(elements.find(element => element.data.id === action.TransitionID).data,
                        selections.transitions)})),
                ...(format === "traces" ? {Sequences: traces.Sequences, UnassignedSteps: traces.UnassignedSteps} : {})});
        }

        const states = abstractNodes.map(element => {
            const concreteStates = concreteNodes.filter(state => abstraction.get(state.data.id) === element.data.stateId);
            const artifacts = (format === "abstract" ? concreteStates.slice(0, 1) : concreteStates).map(stateArtifact);
            return {
                AbstractStateID: element.data.stateId,
                IsInitial: element.data.isInitial === true || element.data.isInitial === "true" || hasClass(element, "isInitial"),
                Properties: properties(element.data, selections.states),
                ...(format === "abstract" ? {RepresentativeConcreteState: artifacts[0] || null} : {ConcreteStates: artifacts})
            };
        });

        const instancesByEdge = new Map(abstractEdges.map(edge => [edge.data.id, []]));
        for (const concrete of concreteEdges) {
            const matches = abstractActionFor(concrete);
            if (matches.length !== 1) {
                throw new Error(`Cannot uniquely resolve the abstract action for ${concrete.data.actionId}.`);
            }
            // ConcreteAction.AbstractID/ConcreteID identify the origin widget, not the action.
            instancesByEdge.get(matches[0].data.id).push(concreteInstance(concrete));
        }
        const actionsById = new Map();
        const transitions = abstractEdges.map(edge => {
            const actionId = identifier(edge.data.actionId, "abstract action ID");
            const sourceId = abstracts.get(edge.data.source);
            const targetId = abstracts.get(edge.data.target) || null;
            if (!sourceId || (!targetId && !hasClass(edge, "UnvisitedAbstractAction"))) {
                throw new Error("Abstract action has an unresolved state endpoint.");
            }
            const instances = instancesByEdge.get(edge.data.id);
            if (!actionsById.has(actionId)) {
                actionsById.set(actionId, {AbstractActionID: actionId, artifacts: []});
            }
            actionsById.get(actionId).artifacts.push(...instances);
            return {
                AbstractActionID: actionId,
                SourceAbstractStateID: sourceId,
                TargetAbstractStateID: targetId,
                Visited: !hasClass(edge, "UnvisitedAbstractAction"),
                Properties: properties(edge.data, selections.transitions),
                ...(format === "hybrid" ? {ConcreteInstances: instances.map(instance => ({
                    ConcreteActionID: instance.element.data.actionId,
                    SourceConcreteStateID: instance.SourceConcreteStateID,
                    TargetConcreteStateID: instance.TargetConcreteStateID
                }))} : {})
            };
        });
        const actions = [...actionsById.values()].sort((first, second) => compare(first.AbstractActionID, second.AbstractActionID))
            .map(action => {
                const instances = ordered(action.artifacts.map(instance => instance.element));
                const selected = format === "abstract" ? instances.slice(0, 1) : instances;
                const artifacts = selected.map(element => actionArtifact(action.artifacts.find(instance => instance.element === element)));
                return {
                    AbstractActionID: action.AbstractActionID,
                    ...(format === "abstract" ? {RepresentativeConcreteAction: artifacts[0] || null} : {ConcreteInstances: artifacts})
                };
            });
        return bundle({
            InitialStates: states.filter(state => state.IsInitial).map(state => state.AbstractStateID),
            AbstractStates: states,
            AbstractActions: actions,
            AbstractTransitions: transitions
        });
    }

    function decodeImage(dataUrl) {
        const match = /^data:image\/png;base64,([A-Za-z0-9+/=\r\n]+)$/.exec(dataUrl);
        if (!match) {
            throw new Error("Invalid exported PNG screenshot data.");
        }
        return Uint8Array.from(atob(match[1]), character => character.charCodeAt(0));
    }

    function crc32(bytes) {
        let crc = 0xffffffff;
        for (const byte of bytes) {
            crc ^= byte;
            for (let bit = 0; bit < 8; bit++) {
                crc = (crc >>> 1) ^ ((crc & 1) ? 0xedb88320 : 0);
            }
        }
        return (crc ^ 0xffffffff) >>> 0;
    }

    // STORE ZIP entries avoid a new browser dependency and work when opened through file://.
    function createZip(files, progress = () => {}) {
        if (files.length > 65535) {
            throw new Error("Export has too many files for a ZIP package.");
        }
        const encoder = new TextEncoder();
        const entries = files.map((file, index) => {
            progress({phase: "checksums", completed: index + 1, total: files.length});
            return {...file, filename: encoder.encode(file.name), crc: crc32(file.bytes)};
        });
        let localSize = 0;
        let centralSize = 0;
        for (const entry of entries) {
            if (entry.filename.length > 65535 || entry.bytes.length > 0xffffffff) {
                throw new Error("Export exceeds ZIP size limits.");
            }
            entry.offset = localSize;
            localSize += 30 + entry.filename.length + entry.bytes.length;
            centralSize += 46 + entry.filename.length;
        }
        if (localSize + centralSize + 22 > 0xffffffff) {
            throw new Error("Export exceeds ZIP size limits.");
        }
        const bytes = new Uint8Array(localSize + centralSize + 22);
        const view = new DataView(bytes.buffer);
        let centralOffset = localSize;
        let completed = 0;
        for (const entry of entries) {
            const offset = entry.offset;
            view.setUint32(offset, 0x04034b50, true);
            view.setUint16(offset + 4, 20, true);
            view.setUint16(offset + 6, 0x0800, true);
            view.setUint16(offset + 12, 33, true);
            view.setUint32(offset + 14, entry.crc, true);
            view.setUint32(offset + 18, entry.bytes.length, true);
            view.setUint32(offset + 22, entry.bytes.length, true);
            view.setUint16(offset + 26, entry.filename.length, true);
            bytes.set(entry.filename, offset + 30);
            bytes.set(entry.bytes, offset + 30 + entry.filename.length);
            view.setUint32(centralOffset, 0x02014b50, true);
            view.setUint16(centralOffset + 4, 20, true);
            view.setUint16(centralOffset + 6, 20, true);
            view.setUint16(centralOffset + 8, 0x0800, true);
            view.setUint16(centralOffset + 14, 33, true);
            view.setUint32(centralOffset + 16, entry.crc, true);
            view.setUint32(centralOffset + 20, entry.bytes.length, true);
            view.setUint32(centralOffset + 24, entry.bytes.length, true);
            view.setUint16(centralOffset + 28, entry.filename.length, true);
            view.setUint32(centralOffset + 42, offset, true);
            bytes.set(entry.filename, centralOffset + 46);
            centralOffset += 46 + entry.filename.length;
            progress({phase: "zip", completed: ++completed, total: entries.length});
        }
        view.setUint32(centralOffset, 0x06054b50, true);
        view.setUint16(centralOffset + 8, entries.length, true);
        view.setUint16(centralOffset + 10, entries.length, true);
        view.setUint32(centralOffset + 12, centralSize, true);
        view.setUint32(centralOffset + 16, localSize, true);
        return bytes;
    }

    const api = {buildBundle, buildWidgetTree, propertyInventory, defaultProperties, createZip,
        workerSource: () => `(${initializeModelExporter.toString()})(globalThis);`};
    root.TestarModelExport = api;
    if (typeof module !== "undefined" && module.exports) {
        module.exports = api;
    }
})(globalThis);
