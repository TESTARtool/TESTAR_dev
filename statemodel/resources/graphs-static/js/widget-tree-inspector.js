(function (root) {
    "use strict";

    function render(container, elements, environment = root) {
        const document = environment.document;
        container.replaceChildren();
        container.className = "widget-tree-inspector";
        function element(tag, text, parent) {
            const node = document.createElement(tag);
            if (text !== undefined) {
                node.textContent = String(text);
            }
            parent.appendChild(node);
            return node;
        }
        try {
            const roots = environment.TestarModelExport.buildWidgetTree(elements);
            if (!roots.length) {
                element("p", "Widget tree unavailable in this snapshot.", container);
                return;
            }
            const hierarchy = element("section", undefined, container);
            hierarchy.className = "widget-tree-hierarchy";
            element("h2", "Widget Hierarchy", hierarchy);
            const list = element("ul", undefined, hierarchy);
            list.className = "widget-tree-nodes";
            const properties = element("section", undefined, container);
            properties.className = "widget-tree-properties";
            let selection;
            function select(widget, button) {
                if (selection) {
                    selection.setAttribute("aria-pressed", "false");
                }
                selection = button;
                button.setAttribute("aria-pressed", "true");
                properties.replaceChildren();
                element("h2", "Widget Attributes", properties);
                const attributes = element("div", undefined, properties);
                attributes.className = "widget-tree-attributes";
                const table = element("table", undefined, attributes);
                table.className = "data-table";
                const data = {AbstractWidgetID: widget.AbstractWidgetID, ConcreteWidgetID: widget.ConcreteWidgetID, ...widget.Properties};
                for (const name of Object.keys(data).sort()) {
                    const row = element("tr", undefined, table);
                    element("td", name, row);
                    element("td", data[name] ?? "", row);
                }
            }
            function append(widget, parent) {
                const item = element("li", undefined, parent);
                const row = element("div", undefined, item);
                row.className = "widget-tree-row";
                const toggle = element("button", widget.Children.length ? "+" : "", row);
                toggle.type = "button";
                toggle.disabled = !widget.Children.length;
                toggle.setAttribute("aria-label", "Expand child widgets");
                toggle.setAttribute("aria-expanded", "false");
                const data = widget.Properties;
                const role = data.Role || data.WebTagName || data.AndroidClassName || "Widget";
                const text = data.WebTextContent || data.WebInnerText || data.AndroidText || data.UIAName || data.Title || data.WebName || data.Name || "";
                const label = `${role}${text ? ": " + String(text).trim().slice(0, 100) : ""}`;
                const button = element("button", label, row);
                button.type = "button";
                button.title = widget.ConcreteWidgetID || widget.AbstractWidgetID || label;
                button.className = "widget-tree-select";
                button.setAttribute("aria-pressed", "false");
                button.addEventListener("click", () => select(widget, button));
                let children;
                toggle.addEventListener("click", () => {
                    if (!children) {
                        children = element("ul", undefined, item);
                        widget.Children.forEach(child => append(child, children));
                    } else {
                        children.hidden = !children.hidden;
                    }
                    toggle.textContent = children.hidden ? "+" : "-";
                    toggle.setAttribute("aria-expanded", String(!children.hidden));
                });
                return {widget, button};
            }
            const first = roots.map(widget => append(widget, list))[0];
            select(first.widget, first.button);
        } catch (error) {
            container.replaceChildren();
            element("p", `Unable to inspect the captured tree: ${error.message}`, container);
        }
    }

    function initializePage(environment = root) {
        const document = environment.document;
        const container = document.getElementById("widget-tree-content");
        const stateId = new environment.URLSearchParams(environment.location.search).get("state");
        const state = (environment.__TESTAR_ELEMENTS__ || []).find(record => {
            const classes = Array.isArray(record.classes) ? record.classes : String(record.classes || "").split(/\s+/);
            return record.data?.id === stateId && classes.includes("ConcreteState");
        });
        const metadata = environment.__TESTAR_RUN__ || {};
        document.getElementById("snapshot-summary").textContent =
            `Snapshot: ${metadata.runId || "unknown"} | Model: ${metadata.modelIdentifier || "unknown"}`;
        container.replaceChildren();
        container.className = "";
        document.title = "Captured Widget Tree";
        if (!state) {
            document.getElementById("state-summary").textContent = "No captured concrete state selected.";
            container.textContent = "Select a concrete state in the graph and open Inspect Widget Tree.";
            return;
        }
        const concreteId = state.data.ConcreteID || state.data.stateId || stateId;
        document.title = `Widget Tree - ${concreteId}`;
        document.getElementById("state-summary").textContent = `Concrete state: ${concreteId} | Graph ID: ${stateId}`;
        const trees = environment.__TESTAR_WIDGET_TREES__ || {};
        const tree = Object.prototype.hasOwnProperty.call(trees, stateId) ? trees[stateId] : undefined;
        if (!tree?.length) {
            container.textContent = metadata.widgetTreesCaptured === false
                ? "Widget trees were not captured in this snapshot." : "Widget tree unavailable in this snapshot.";
            return;
        }
        render(container, tree, environment);
    }

    const api = {render, initializePage};
    root.TestarWidgetTreeInspector = api;
    if (typeof module !== "undefined" && module.exports) {
        module.exports = api;
    }
})(globalThis);
