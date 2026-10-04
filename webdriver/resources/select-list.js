/* SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

try {
    const target = arguments[0];
    const capturedElement = arguments[1];
    const value = arguments[2];
    const method = arguments[3];
    let field = capturedElement && capturedElement.isConnected ? capturedElement : null;

    if (!field) {
        let candidates = [];
        switch (method) {
            case "ID":
                const identifiedElement = document.getElementById(target);
                candidates = identifiedElement ? [identifiedElement] : [];
                break;
            case "NAME":
                candidates = Array.from(document.getElementsByName(target));
                break;
            case "CSS":
                candidates = Array.from(document.querySelectorAll(target));
                break;
        }
        if (candidates.length > 1) {
            return "Multiple elements match the select target";
        }
        field = candidates[0];
    }

    if (!field || field.tagName.toLowerCase() !== "select") {
        return "Unable to locate the captured select field";
    }
    const matches = Array.from(field.options).filter(option => option.value === value);
    if (matches.length !== 1) {
        return "Select option value is missing or ambiguous";
    }

    field.value = value;
    const EventConstructor = field.ownerDocument.defaultView.Event;
    field.dispatchEvent(new EventConstructor("input", { bubbles: true }));
    field.dispatchEvent(new EventConstructor("change", { bubbles: true }));
    return true;
} catch (error) {
    return "Select interaction failed: " + error.message;
}
