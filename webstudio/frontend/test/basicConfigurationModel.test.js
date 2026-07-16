import test from "node:test";
import assert from "node:assert/strict";

import {
    ORACLE_COMPOSITION_NODE_ID,
    isOracleCompositionSelected,
    oracleCompositionNodes
} from "../src/basicConfigurationModel.js";

test("oracleCompositionNodes keeps only the Custom Oracle Services node", () => {
    const nodes = [
        { id: "state", title: "StateService" },
        { id: ORACLE_COMPOSITION_NODE_ID, title: "Custom Oracle Services" },
        { id: "action-derivation", title: "ActionDerivationService" }
    ];

    assert.deepEqual(oracleCompositionNodes(nodes), [
        { id: ORACLE_COMPOSITION_NODE_ID, title: "Custom Oracle Services" }
    ]);
});

test("isOracleCompositionSelected only matches the Java composition oracle node", () => {
    assert.equal(isOracleCompositionSelected("java-composition", { id: ORACLE_COMPOSITION_NODE_ID }), true);
    assert.equal(isOracleCompositionSelected("java-composition", null), true);
    assert.equal(isOracleCompositionSelected("java-composition", { id: "state" }), false);
    assert.equal(isOracleCompositionSelected("settings-form", { id: ORACLE_COMPOSITION_NODE_ID }), false);
});
