// Verifies WS-FUNC-COMPOSITION-FLOW-001 and WS-UX-COMPOSITION-FLOW-001:
// composition source filtering, node derivation, and selected node refresh behavior.
import test from "node:test";
import assert from "node:assert/strict";
import {
    buildCompositionFlowNodes,
    compositionSourceFilesFromWorkspace,
    createCompositionFlowNode,
    policySourceFilesFromWorkspace,
    refreshedSelectedCompositionFlowNode,
    sourceFileByClassName
} from "../../../src/views/composition/compositionFlowModel.js";

const customSystemSource = {
    name: "CustomSystemService.java",
    category: "service",
    content: "public class CustomSystemService {}"
};

test("splits workspace source files by composition and policy categories", () => {
    const workspaceDocument = {
        sourceFiles: [
            customSystemSource,
            { name: "CustomCapability.java", category: "capability" },
            { name: "CustomPolicy.java", category: "policy" },
            { name: "Ignored.java", category: "other" }
        ]
    };

    assert.deepEqual(
        compositionSourceFilesFromWorkspace(workspaceDocument).map((sourceFile) => sourceFile.name),
        ["CustomSystemService.java", "CustomCapability.java"]
    );
    assert.deepEqual(
        policySourceFilesFromWorkspace(workspaceDocument).map((sourceFile) => sourceFile.name),
        ["CustomPolicy.java"]
    );
});

test("finds source files by declared class name", () => {
    assert.equal(sourceFileByClassName([customSystemSource], "CustomSystemService"), customSystemSource);
    assert.equal(sourceFileByClassName([customSystemSource], ""), null);
    assert.equal(sourceFileByClassName([customSystemSource], "Missing"), null);
});

test("creates composition flow node visual state from configured class", () => {
    const node = createCompositionFlowNode(
        { id: "system", title: "SystemService", propertyKey: "systemServiceClass", kind: "service" },
        { systemServiceClass: "CustomSystemService" },
        [customSystemSource]
    );

    assert.equal(node.id, "system");
    assert.equal(node.configuredClassName, "CustomSystemService");
    assert.equal(node.sourceFile, customSystemSource);
    assert.equal(node.color, "custom");
    assert.equal(node.description, "CustomSystemService");
});

test("marks missing configured source as invalid and default nodes as default", () => {
    const missingNode = createCompositionFlowNode(
        { id: "system", title: "SystemService", propertyKey: "systemServiceClass", kind: "service" },
        { systemServiceClass: "MissingSystemService" },
        []
    );
    const defaultNode = createCompositionFlowNode(
        { id: "state", title: "StateService", propertyKey: "stateServiceClass", kind: "service" },
        {},
        []
    );
    const oracleNode = createCompositionFlowNode(
        { id: "oracle-services", title: "Custom Oracle Services", propertyKey: "oracleComposerClass", kind: "oracle" },
        {},
        []
    );

    assert.equal(missingNode.color, "invalid");
    assert.equal(defaultNode.color, "default");
    assert.equal(defaultNode.description, "Default service implementation");
    assert.equal(oracleNode.color, "oracle");
});

test("builds composition flow nodes from workspace composition properties", () => {
    const workspaceDocument = {
        compositionProperties: {
            content: "systemServiceClass=CustomSystemService\n"
        }
    };

    const nodes = buildCompositionFlowNodes(workspaceDocument, [customSystemSource]);
    const systemNode = nodes.find((node) => node.id === "system");

    assert.equal(nodes.length, 13);
    assert.equal(systemNode.sourceFile, customSystemSource);
    assert.equal(systemNode.color, "custom");
});

test("refreshes selected composition node against rebuilt nodes", () => {
    const selectedNode = { id: "system", color: "old" };
    const refreshedNode = { id: "system", color: "custom" };

    assert.equal(refreshedSelectedCompositionFlowNode([refreshedNode], selectedNode), refreshedNode);
    assert.equal(refreshedSelectedCompositionFlowNode([], selectedNode), selectedNode);
    assert.equal(refreshedSelectedCompositionFlowNode([], null), null);
});
