function exportAbstractJSON() {
    if (!beginExport()) return;

    exportAbstractJSONInternal()
        .then((exportResult) => {
            showModelExportResult(exportResult, "Abstract JSON export finished.");
        })
        .catch((err) => {
            console.error("Abstract JSON model generation failed:", err);
        })
        .finally(() => {
            endExport();
        });
}

async function exportAbstractJSONInternal() {
    const initialAbstractIds = getAbstractInitialAbstractStateIds();
    const abstractExport = await generateAbstractModelJSON(initialAbstractIds);
    return persistModelExports(
        null,
        null,
        "model_abstract.json",
        abstractExport.jsonModel,
        null,
        abstractExport.screenshotExports
    );
}

function getAbstractInitialAbstractStateIds() {
    const initialAbstractIds = new Set();
    cy.$(".AbstractState")
        .filter((ele) => ele.data("isInitial") === "true")
        .forEach((ele) => {
            initialAbstractIds.add(ele.data("stateId"));
        });
    return initialAbstractIds;
}

async function generateAbstractModelJSON(initialAbstractIds) {
    // Extract all initial abstract states and keep one representative concrete artifact per abstract entity.
    const initialStates = [];
    const abstractStates = [];
    const abstractActions = [];
    const abstractTransitions = [];
    const screenshotExports = [];
    const exportedActionIds = new Set();

    // 1) Collect abstract states directly from the abstract layer.
    for (const abstractStateNode of cy.$(".AbstractState").toArray()) {
        const abstractID = abstractStateNode.data("stateId");
        const representativeConcreteState = findRepresentativeConcreteStateNode(abstractStateNode);
        const representativeCyElementId = representativeConcreteState ? representativeConcreteState.id() : null;
        const webTitle = representativeConcreteState ? representativeConcreteState.data("WebTitle") : "";
        const webHref = representativeConcreteState ? representativeConcreteState.data("WebHref") : "";
        let widgetTree = [];

        // 2) Resolve one representative concrete widget tree and one state screenshot for this abstract state.
        if (representativeCyElementId) {
            const elements = await getWidgetTreeForState(representativeCyElementId);
            widgetTree = buildWidgetTreeFromElements(elements);
            screenshotExports.push({
                SourceImageName: representativeCyElementId + ".png",
                OutputImageName: abstractID + ".png"
            });
        }

        abstractStates.push({
            AbstractStateID: abstractID,
            WebTitle: webTitle,
            WebHref: webHref,
            WidgetTree: widgetTree
        });

        if (initialAbstractIds.has(abstractID)) {
            initialStates.push({
                AbstractStateID: abstractID,
                InitialUrl: webHref,
                InitialPage: webTitle
            });
        }
    }

    // 3) Collect abstract actions from the abstract layer and use one representative concrete action for SUT-facing fields.
    cy.$(".AbstractAction").forEach((abstractActionEdge) => {
        const representativeConcreteAction = findRepresentativeConcreteActionEdge(abstractActionEdge);
        const abstractActionID = getAbstractActionIdentifier(abstractActionEdge, representativeConcreteAction);
        const abstractWidgetID = getAbstractWidgetIdentifier(representativeConcreteAction);

        if (!exportedActionIds.has(abstractActionID)) {
            abstractActions.push({
                AbstractActionID: abstractActionID,
                AbstractWidgetID: abstractWidgetID,
                WebHref: representativeConcreteAction ? representativeConcreteAction.data("WebHref") : "",
                WebCssClasses: representativeConcreteAction ? representativeConcreteAction.data("WebCssClasses") : "",
                WebTagName: representativeConcreteAction ? representativeConcreteAction.data("WebTagName") : "",
                Desc: representativeConcreteAction ? representativeConcreteAction.data("Desc") : "",
                WebOuterHTML: representativeConcreteAction ? stripSvgAndPath(representativeConcreteAction.data("WebOuterHTML")) : "",
                WebCssSelector: representativeConcreteAction ? representativeConcreteAction.data("WebCssSelector") : "",
                InputText: representativeConcreteAction ? representativeConcreteAction.data("InputText") : ""
            });

            // Export one representative concrete action screenshot under the abstract action identifier.
            if (representativeConcreteAction) {
                screenshotExports.push({
                    SourceImageName: representativeConcreteAction.id() + ".png",
                    OutputImageName: abstractActionID + ".png",
                    ExportType: "action"
                });
            }

            exportedActionIds.add(abstractActionID);
        }

        // 4) Export abstract transitions directly from the abstract action edges.
        const sourceAbstractID = abstractActionEdge.source().data("stateId");
        const targetAbstractID = abstractActionEdge.target().data("stateId");
        abstractTransitions.push({
            SourceAbstractStateID: sourceAbstractID,
            TargetAbstractStateID: targetAbstractID,
            AbstractActionID: abstractActionID
        });
    });

    return {
        jsonModel: {
            InitialStates: initialStates,
            ConcreteState: abstractStates,
            ConcreteAction: abstractActions,
            ConcreteTransitions: abstractTransitions
        },
        screenshotExports: screenshotExports
    };
}

function findRepresentativeConcreteStateNode(abstractStateNode) {
    const concreteStateNodes = abstractStateNode.incomers("node.ConcreteState");
    if (concreteStateNodes.size() === 0) {
        return null;
    }
    return concreteStateNodes.first();
}

function findRepresentativeConcreteActionEdge(abstractActionEdge) {
    const concreteActionIds = abstractActionEdge.data("concreteActionIds");
    const abstractActionID = getAbstractActionIdentifier(abstractActionEdge, null);
    const sourceAbstractID = abstractActionEdge.source().data("stateId");
    const targetAbstractID = abstractActionEdge.target().data("stateId");
    let representativeConcreteAction = null;

    cy.$(".ConcreteAction").forEach((concreteActionEdge) => {
        if (representativeConcreteAction) {
            return;
        }

        const sourceNode = concreteActionEdge.source();
        const targetNode = concreteActionEdge.target();
        const hasMatchingTransition = sourceNode &&
            targetNode &&
            sourceNode.data("AbstractID") === sourceAbstractID &&
            targetNode.data("AbstractID") === targetAbstractID;
        const hasMatchingAbstractID = concreteActionEdge.data("AbstractID") === abstractActionID;
        const concreteActionId = concreteActionEdge.data("actionId");
        const hasMatchingConcreteActionId = Array.isArray(concreteActionIds) ?
            concreteActionIds.includes(concreteActionId) :
            (typeof concreteActionIds === "string" && concreteActionIds.indexOf(concreteActionId) !== -1);

        if (hasMatchingAbstractID || (hasMatchingTransition && hasMatchingConcreteActionId)) {
            representativeConcreteAction = concreteActionEdge;
        }
    });

    return representativeConcreteAction;
}

function getAbstractActionIdentifier(abstractActionEdge, representativeConcreteAction) {
    const identifierCandidates = [
        abstractActionEdge.data("AbstractID"),
        abstractActionEdge.data("actionId"),
        abstractActionEdge.data("uid"),
        representativeConcreteAction ? representativeConcreteAction.data("AbstractID") : null
    ];

    for (const identifier of identifierCandidates) {
        if (identifier != null && identifier !== "") {
            return identifier;
        }
    }

    return abstractActionEdge.id();
}

function getAbstractWidgetIdentifier(representativeConcreteAction) {
    if (!representativeConcreteAction) {
        return "";
    }

    const abstractWidgetID = representativeConcreteAction.data("AbstractID");
    return abstractWidgetID == null ? "" : abstractWidgetID;
}
