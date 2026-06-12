function exportHybridJSON() {
    if (!beginExport()) return;

    exportHybridJSONInternal()
        .then((exportResult) => {
            showModelExportResult(exportResult, "Hybrid JSON export finished.");
        })
        .catch((err) => {
            console.error("Hybrid JSON model generation failed:", err);
        })
        .finally(() => {
            endExport();
        });
}

async function exportHybridJSONInternal() {
    const initialAbstractIds = getHybridInitialAbstractStateIds();
    const hybridExport = await generateHybridModelJSON(initialAbstractIds);
    return persistModelExports(
        "model_hybrid.json",
        hybridExport.jsonModel,
        null,
        null,
        hybridExport.screenshotExports,
        null
    );
}

function getHybridInitialAbstractStateIds() {
    const initialAbstractIds = new Set();
    cy.$(".AbstractState")
        .filter((ele) => ele.data("isInitial") === "true")
        .forEach((ele) => {
            initialAbstractIds.add(ele.data("stateId"));
        });
    return initialAbstractIds;
}

async function generateHybridModelJSON(initialAbstractIds) {
    const initialStatesMap = new Map(); // AbstractID -> initial state data

    const concreteStates = [];
    const concreteActions = [];
    const concreteTransitions = [];
    const screenshotExports = [];

    // 1) Collect abstract states, actions, and transitions data
    const stateMap = new Map(); // AbstractID -> state data
    const actionMap = new Map(); // Abstract action ID -> action data
    const transitionMap = new Map(); // Abstract transition tuple -> transition data

    // Iterate over each ConcreteState element in the graph
    cy.$(".ConcreteState").forEach((ele) => {
        const abstractID = ele.data("AbstractID");
        const concreteID = ele.data("ConcreteID");
        const webTitle = ele.data("WebTitle");
        const webHref = ele.data("WebHref");

        if (!stateMap.has(abstractID)) {
            stateMap.set(abstractID, {
                AbstractStateID: abstractID,
                WebTitle: webTitle,
                WebHref: webHref,
                // Representative concrete state used to fetch one widget tree for this abstract state.
                ConcreteStateID: concreteID,
                // Traceability to every concrete state merged into this abstract state.
                ConcreteStateIDs: [],
                CyElementId: ele.id()
            });
        }

        // Save the concrete id in the abstract state of the map
        const state = stateMap.get(abstractID);
        if (!state.ConcreteStateIDs.includes(concreteID)) {
            state.ConcreteStateIDs.push(concreteID);
        }

        // Collect all initial states, deduplicated by AbstractID
        if (initialAbstractIds.has(abstractID) && !initialStatesMap.has(abstractID)) {
            initialStatesMap.set(abstractID, {
                AbstractStateID: abstractID,
                ConcreteStateID: concreteID,
                InitialUrl: webHref,
                InitialPage: webTitle
            });
        }
    });

    // 2) Resolve widget trees SEQUENTIALLY (in order)
    for (const state of stateMap.values()) {
        const elements = await getWidgetTreeForState(state.CyElementId);
        const tree = buildWidgetTreeFromElements(elements);
        screenshotExports.push({
            SourceImageName: state.CyElementId + ".png",
            OutputImageName: state.ConcreteStateID + ".png"
        });

        const { CyElementId, ...cleanState } = {
            ...state,
            WidgetTree: tree
        };
        concreteStates.push(cleanState);
    }

    // Iterate over each ConcreteAction element in the graph
    cy.$(".ConcreteAction").forEach((ele) => {
        const abstractWidgetID = ele.data("AbstractID");
        const concreteWidgetID = ele.data("ConcreteID");
        const concreteActionID = ele.data("actionId");
        const abstractActionID = getHybridAbstractActionIdentifier(ele, abstractWidgetID, concreteActionID);
        const webHref = ele.data("WebHref");
        const webCssClasses = ele.data("WebCssClasses");
        const webTagName = ele.data("WebTagName");
        const desc = ele.data("Desc");

        // CLEAN WebOuterHTML (handles inline icons like <button>...<svg/>...</button>)
        const webOuterHTML = stripSvgAndPath(ele.data("WebOuterHTML"));

        const webCssSelector = ele.data("WebCssSelector");
        const inputText = ele.data("InputText");

        const sourceNode = ele.source();
        const targetNode = ele.target();
        // Concrete execution instance kept under one abstract action entry.
        const actionInstance = {
            ConcreteWidgetID: concreteWidgetID,
            ConcreteActionID: concreteActionID,
            SourceAbstractStateID: sourceNode ? sourceNode.data("AbstractID") : undefined,
            SourceConcreteStateID: sourceNode ? sourceNode.data("ConcreteID") : undefined,
            TargetAbstractStateID: targetNode ? targetNode.data("AbstractID") : undefined,
            TargetConcreteStateID: targetNode ? targetNode.data("ConcreteID") : undefined
        };

        if (!actionMap.has(abstractActionID)) {
            actionMap.set(abstractActionID, {
                AbstractActionID: abstractActionID,
                AbstractWidgetID: abstractWidgetID,
                // Representative concrete widget/action used to expose SUT-facing fields once.
                ConcreteWidgetID: concreteWidgetID,
                ConcreteActionID: concreteActionID,
                // Representative concrete action edge used to export one screenshot for this abstract action.
                RepresentativeCyElementId: ele.id(),
                WebHref: webHref,
                WebCssClasses: webCssClasses,
                WebTagName: webTagName,
                Desc: desc,
                WebOuterHTML: webOuterHTML,
                WebCssSelector: webCssSelector,
                InputText: inputText,
                ConcreteInstances: []
            });
        }

        // Check if the action instance already exists in the action map
        const action = actionMap.get(abstractActionID);
        const instanceExists = action.ConcreteInstances.some((instance) =>
            instance.ConcreteWidgetID === actionInstance.ConcreteWidgetID &&
            instance.ConcreteActionID === actionInstance.ConcreteActionID &&
            instance.SourceConcreteStateID === actionInstance.SourceConcreteStateID &&
            instance.TargetConcreteStateID === actionInstance.TargetConcreteStateID
        );

        if (!instanceExists) {
            action.ConcreteInstances.push(actionInstance);
        }
    });

    actionMap.forEach((action) => {
        screenshotExports.push({
            SourceImageName: action.RepresentativeCyElementId + ".png",
            OutputImageName: action.ConcreteWidgetID + ".png",
            ExportType: "action"
        });

        delete action.RepresentativeCyElementId;
        concreteActions.push(action);
    });

    // Iterate over each ConcreteAction element to handle transitions
    cy.$(".ConcreteAction").forEach((ele) => {
        const sourceNode = ele.source();
        const targetNode = ele.target();

        if (!sourceNode || !targetNode) {
            return;
        }

        const sourceAbstractID = sourceNode.data("AbstractID");
        const targetAbstractID = targetNode.data("AbstractID");
        const abstractWidgetID = ele.data("AbstractID");
        const concreteWidgetID = ele.data("ConcreteID");
        const concreteActionID = ele.data("actionId");
        const actionAbstractID = getHybridAbstractActionIdentifier(ele, abstractWidgetID, concreteActionID);
        // Export one abstract transition and attach all concrete occurrences below it.
        const transitionKey = [
            sourceAbstractID,
            targetAbstractID,
            actionAbstractID
        ].join("::");

        if (!transitionMap.has(transitionKey)) {
            transitionMap.set(transitionKey, {
                SourceAbstractStateID: sourceAbstractID,
                TargetAbstractStateID: targetAbstractID,
                AbstractActionID: actionAbstractID,
                AbstractWidgetID: abstractWidgetID,
                ConcreteInstances: []
            });
        }

        const transition = transitionMap.get(transitionKey);
        // Concrete provenance for one abstract transition occurrence.
        const concreteInstance = {
            SourceConcreteStateID: sourceNode.data("ConcreteID"),
            TargetConcreteStateID: targetNode.data("ConcreteID"),
            ConcreteWidgetID: concreteWidgetID,
            ConcreteActionID: concreteActionID
        };

        const transitionExists = transition.ConcreteInstances.some((instance) =>
            instance.SourceConcreteStateID === concreteInstance.SourceConcreteStateID &&
            instance.TargetConcreteStateID === concreteInstance.TargetConcreteStateID &&
            instance.ConcreteWidgetID === concreteInstance.ConcreteWidgetID &&
            instance.ConcreteActionID === concreteInstance.ConcreteActionID
        );

        if (!transitionExists) {
            transition.ConcreteInstances.push(concreteInstance);
        }
    });

    transitionMap.forEach((transition) => {
        concreteTransitions.push(transition);
    });

    return {
        jsonModel: {
            InitialStates: Array.from(initialStatesMap.values()),
            ConcreteState: concreteStates,
            ConcreteAction: concreteActions,
            ConcreteTransitions: concreteTransitions
        },
        screenshotExports: screenshotExports
    };
}

function getHybridAbstractActionIdentifier(concreteActionEdge, abstractWidgetID, concreteActionID) {
    const sourceNode = concreteActionEdge.source();
    const targetNode = concreteActionEdge.target();

    if (!sourceNode || !targetNode) {
        return abstractWidgetID;
    }

    const sourceAbstractStateID = sourceNode.data("AbstractID");
    const targetAbstractStateID = targetNode.data("AbstractID");
    let abstractActionID = "";

    cy.$(".AbstractAction").forEach((abstractActionEdge) => {
        if (abstractActionID) {
            return;
        }

        const hasMatchingTransition =
            abstractActionEdge.source().data("stateId") === sourceAbstractStateID &&
            abstractActionEdge.target().data("stateId") === targetAbstractStateID;
        const concreteActionIds = abstractActionEdge.data("concreteActionIds");
        const hasMatchingConcreteActionId = Array.isArray(concreteActionIds) ?
            concreteActionIds.includes(concreteActionID) :
            (typeof concreteActionIds === "string" && concreteActionIds.indexOf(concreteActionID) !== -1);

        if (hasMatchingTransition && hasMatchingConcreteActionId) {
            abstractActionID = getHybridAbstractActionEdgeIdentifier(abstractActionEdge);
        }
    });

    return abstractActionID || abstractWidgetID;
}

function getHybridAbstractActionEdgeIdentifier(abstractActionEdge) {
    const identifierCandidates = [
        abstractActionEdge.data("AbstractID"),
        abstractActionEdge.data("actionId"),
        abstractActionEdge.data("uid")
    ];

    for (const identifier of identifierCandidates) {
        if (identifier != null && identifier !== "") {
            return identifier;
        }
    }

    return abstractActionEdge.id();
}
