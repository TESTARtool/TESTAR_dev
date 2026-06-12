function generateAMP() {
    if (!beginExport()) return;

    generateAMPInternal()
        .catch((err) => {
            console.error("AMP generation failed:", err);
        })
        .finally(() => {
            endExport();
        });
}

async function generateAMPInternal() {
    // Extract the information of the initial abstract state
    let initialNodes = cy.$(".AbstractState").filter((ele) => ele.data("isInitial") === "true");
    let initialAbstractId = initialNodes[0].data("stateId");
    let initialUrl = "";
    let initialPage = "";
    let initialIdentifier = "";
    let concreteStates = [];
    let concreteActions = [];
    let concreteTransitions = [];

    // Iterate over each ConcreteState element in the graph
    cy.$(".ConcreteState").forEach((ele) => {
        const abstractID = ele.data("AbstractID");
        const webTitle = ele.data("WebTitle");

        // Check if the state already exists in concreteStates
        if (!concreteStates.some((state) => state.AbstractID === abstractID)) {
            concreteStates.push({
                AbstractID: abstractID,
                WebTitle: webTitle
            });
        }

        // If this concrete state is the initial state, save initial data
        if (abstractID === initialAbstractId) {
            initialUrl = ele.data("WebHref");
            initialPage = ele.data("WebTitle");
            initialIdentifier = initialAbstractId;
        }
    });

    // Iterate over each ConcreteAction element in the graph
    cy.$(".ConcreteAction").forEach((ele) => {
        const abstractID = ele.data("AbstractID");
        const desc = ele.data("Desc");
        const webCssSelector = ele.data("WebCssSelector");
        const inputText = ele.data("InputText");

        // Check if the action already exists in concreteActions
        if (!concreteActions.some((action) => action.AbstractID === abstractID)) {
            concreteActions.push({
                AbstractID: abstractID,
                Desc: desc,
                WebCssSelector: webCssSelector,
                InputText: inputText
            });
        }
    });

    // Iterate over each ConcreteAction element to handle transitions
    cy.$(".ConcreteAction").forEach((ele) => {
        const sourceNode = ele.source();
        const targetNode = ele.target();

        if (sourceNode && targetNode) {
            const transition = {
                Source: sourceNode.data("AbstractID"),
                Target: targetNode.data("AbstractID"),
                Action: ele.data("AbstractID")
            };

            // Check if the transition already exists
            if (!concreteTransitions.some((existingTransition) =>
                existingTransition.Source === transition.Source &&
                existingTransition.Target === transition.Target &&
                existingTransition.Action === transition.Action
            )) {
                concreteTransitions.push(transition);
            }
        }
    });

    // Construct the final JSON object
    const jsonResult = {
        InitialUrl: initialUrl,
        InitialPage: initialPage,
        InitialIdentifier: initialIdentifier,
        ConcreteState: concreteStates,
        ConcreteAction: concreteActions,
        ConcreteTransitions: concreteTransitions
    };

    // Invoke the TESTAR-AXINI transformer
    const res = await fetch("http://localhost:8090/generate-amp", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(jsonResult)
    });

    const ampCode = await res.text();
    console.log("Original JSON:", JSON.stringify(jsonResult));
    console.log("Generated AMP code:", ampCode);
    downloadAmpFile(ampCode);
}

function downloadAmpFile(content, filename = "model.amp") {
    const blob = new Blob([content], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
}
