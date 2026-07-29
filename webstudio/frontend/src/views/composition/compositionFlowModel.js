// Implements WS-FUNC-COMPOSITION-FLOW-001 and WS-UX-COMPOSITION-FLOW-001:
// derives visual composition nodes from composition.properties and workspace Java sources.
import { parsePropertiesContent } from "../../models/workspaceSettingsModel.js";
import { sourceClassName } from "../../models/sourceEditorModel.js";

export const COMPOSITION_FLOW_NODE_DEFINITIONS = [
    { id: "settings", title: "SettingsCapability", propertyKey: "settingsCapabilityClass", kind: "capability" },
    { id: "test-session", title: "TestSessionCapability", propertyKey: "testSessionCapabilityClass", kind: "capability" },
    { id: "test-sequence", title: "TestSequenceCapability", propertyKey: "testSequenceCapabilityClass", kind: "capability" },
    { id: "system", title: "SystemService", propertyKey: "systemServiceClass", kind: "service" },
    { id: "stop-criteria", title: "StopCriteriaCapability", propertyKey: "stopCriteriaCapabilityClass", kind: "capability" },
    { id: "state", title: "StateService", propertyKey: "stateServiceClass", kind: "service" },
    { id: "state-identifier", title: "StateIdentifierService", propertyKey: "stateIdentifierServiceClass", kind: "service" },
    { id: "oracle-evaluation", title: "OracleEvaluationService", propertyKey: "", kind: "service" },
    { id: "oracle-services", title: "Custom Oracle Services", propertyKey: "oracleComposerClass", kind: "oracle" },
    { id: "action-derivation", title: "ActionDerivationService", propertyKey: "actionDerivationServiceClass", kind: "service" },
    { id: "action-identifier", title: "ActionIdentifierService", propertyKey: "actionIdentifierServiceClass", kind: "service" },
    { id: "action-selector", title: "ActionSelectorService", propertyKey: "actionSelectorServiceClass", kind: "service" },
    { id: "action-execution", title: "ActionExecutionService", propertyKey: "actionExecutionServiceClass", kind: "service" }
];

export function policySourceFilesFromWorkspace(workspaceDocument) {
    return (workspaceDocument?.sourceFiles || []).filter((sourceFile) => sourceFile.category === "policy");
}

export function compositionSourceFilesFromWorkspace(workspaceDocument) {
    return (workspaceDocument?.sourceFiles || []).filter((sourceFile) =>
        sourceFile.category === "service" || sourceFile.category === "capability"
    );
}

export function sourceFileByClassName(sourceFiles, className) {
    if (!className) {
        return null;
    }

    return (sourceFiles || []).find((sourceFile) => sourceClassName(sourceFile) === className) || null;
}

export function createCompositionFlowNode(nodeDefinition, properties, sourceFiles) {
    const configuredClassName = nodeDefinition.propertyKey ? (properties[nodeDefinition.propertyKey] || "") : "";
    const sourceFile = sourceFileByClassName(sourceFiles, configuredClassName);
    const isCustom = configuredClassName !== "";
    const color = nodeDefinition.kind === "oracle"
        ? "oracle"
        : (isCustom ? (sourceFile ? "custom" : "invalid") : "default");

    return {
        id: nodeDefinition.id,
        title: nodeDefinition.title,
        propertyKey: nodeDefinition.propertyKey,
        kind: nodeDefinition.kind,
        configuredClassName,
        sourceFile,
        color,
        description: isCustom
            ? configuredClassName
            : `Default ${nodeDefinition.kind} implementation`
    };
}

export function buildCompositionFlowNodes(workspaceDocument, sourceFiles) {
    if (!workspaceDocument?.compositionProperties?.content) {
        return [];
    }

    const compositionProperties = parsePropertiesContent(workspaceDocument.compositionProperties.content);
    return COMPOSITION_FLOW_NODE_DEFINITIONS.map((nodeDefinition) =>
        createCompositionFlowNode(nodeDefinition, compositionProperties, sourceFiles)
    );
}

export function refreshedSelectedCompositionFlowNode(compositionFlowNodes, selectedCompositionFlowNode) {
    if (!selectedCompositionFlowNode) {
        return null;
    }

    return compositionFlowNodes.find((flowNode) => flowNode.id === selectedCompositionFlowNode.id)
        || selectedCompositionFlowNode;
}
