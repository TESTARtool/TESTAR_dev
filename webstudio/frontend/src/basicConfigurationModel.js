export const ORACLE_COMPOSITION_NODE_ID = "oracle-services";

export function oracleCompositionNodes(compositionFlowNodes = []) {
    return compositionFlowNodes.filter((flowNode) => flowNode?.id === ORACLE_COMPOSITION_NODE_ID);
}

export function isOracleCompositionSelected(selectedEditor, selectedCompositionFlowNode) {
    return selectedEditor === "java-composition"
        && (!selectedCompositionFlowNode || selectedCompositionFlowNode.id === ORACLE_COMPOSITION_NODE_ID);
}
