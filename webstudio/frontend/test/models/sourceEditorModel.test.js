// Verifies WS-FUNC-WORKSPACE-SOURCE-EDITOR-001, WS-UX-SOURCE-EDITOR-001, WS-FUNC-COMPOSITION-FLOW-001, and WS-FUNC-POLICIES-001:
// source class and reference helpers used by Java composition and policies.
import test from "node:test";
import assert from "node:assert/strict";
import {
    inferPolicyPropertyKey,
    policyInterfaceSimpleName,
    sourceClassName,
    sourceFilesForReferencedClasses,
    sourceFilesNotInReferenceSet,
    updatedPolicyPropertiesContent
} from "../../src/models/sourceEditorModel.js";

test("extracts source class names", () => {
    assert.equal(sourceClassName({ name: "MyPolicy.java" }), "MyPolicy");
    assert.equal(sourceClassName({ name: "MyPolicy" }), "MyPolicy");
    assert.equal(sourceClassName(null), "");
});

test("splits source files by configured references", () => {
    const sourceFiles = [
        { name: "OnePolicy.java" },
        { name: "TwoPolicy.java" }
    ];
    const workspaceDocument = {
        references: {
            policies: ["OnePolicy"]
        }
    };

    assert.deepEqual(sourceFilesForReferencedClasses(sourceFiles, workspaceDocument, "policies"), [
        { name: "OnePolicy.java" }
    ]);
    assert.deepEqual(sourceFilesNotInReferenceSet(sourceFiles, workspaceDocument, "policies"), [
        { name: "TwoPolicy.java" }
    ]);
});

test("maps policy property keys to interface names", () => {
    assert.equal(policyInterfaceSimpleName("clickablePolicies"), "ClickablePolicy");
    assert.equal(policyInterfaceSimpleName("visiblePolicies"), "VisiblePolicy");
    assert.equal(policyInterfaceSimpleName("unknownPolicies"), "");
});

test("infers policy property key from configured classes or implemented interface", () => {
    const policyDefinitions = [
        {
            propertyKey: "clickablePolicies",
            configuredClassNames: ["ConfiguredClickablePolicy"]
        },
        {
            propertyKey: "visiblePolicies",
            configuredClassNames: []
        }
    ];

    assert.equal(
        inferPolicyPropertyKey({ name: "ConfiguredClickablePolicy.java", content: "" }, policyDefinitions),
        "clickablePolicies"
    );
    assert.equal(
        inferPolicyPropertyKey({ name: "CustomVisiblePolicy.java", content: "class CustomVisiblePolicy implements VisiblePolicy" }, policyDefinitions),
        "visiblePolicies"
    );
});

test("updates policy properties content values", () => {
    assert.deepEqual(
        updatedPolicyPropertiesContent(
            { clickablePolicies: "OldPolicy; CustomPolicy" },
            "clickablePolicies",
            "CustomPolicy",
            false
        ),
        { clickablePolicies: "OldPolicy" }
    );
    assert.deepEqual(
        updatedPolicyPropertiesContent(
            { clickablePolicies: "OldPolicy" },
            "clickablePolicies",
            "CustomPolicy",
            true
        ),
        { clickablePolicies: "OldPolicy; CustomPolicy" }
    );
});
