(function (root) {
    "use strict";

    function attach(loadSnapshot, environment = root) {
        const document = environment.document;
        const open = document.getElementById("export-model");
        const status = document.getElementById("model-export-status");
        let busy = false;
        let inventory = null;
        let requestVersion = 0;
        const propertyControls = new Map();

        function element(tag, text, parent) {
            const node = document.createElement(tag);
            if (text) {
                node.textContent = text;
            }
            if (parent) {
                parent.appendChild(node);
            }
            return node;
        }

        const dialog = element("dialog", null, document.body);
        dialog.className = "model-export-dialog";
        dialog.setAttribute("aria-labelledby", "model-export-title");
        element("h2", "Export Model", dialog).id = "model-export-title";
        const formatLabel = element("label", "Format", dialog);
        const format = element("select", null, formatLabel);
        format.id = "model-export-format";
        for (const [value, label] of [["abstract", "Abstract"], ["hybrid", "Hybrid"],
            ["concrete", "Concrete"], ["traces", "Sequence Traces"]]) {
            const option = element("option", label, format);
            option.value = value;
        }
        format.value = "hybrid";

        function checkbox(label, id) {
            const row = element("label", null, dialog);
            const input = element("input", null, row);
            input.type = "checkbox";
            input.id = id;
            element("span", label, row);
            return input;
        }

        const trees = checkbox("Include widget trees", "model-export-trees");
        const images = checkbox("Include screenshots", "model-export-images");
        images.checked = true;
        const availability = element("p", null, dialog);
        availability.id = "model-export-availability";
        element("p", "Identity fields, relationships, and widget hierarchy are always preserved.", dialog);
        element("p", "Semantic defaults include text, names, identifiers, roles, URLs, and interaction status. Visual measurements and colors are optional.", dialog);
        const advanced = element("details", null, dialog);
        advanced.id = "model-export-advanced";
        advanced.className = "model-export-advanced";
        const advancedSummary = element("summary", "Advanced Properties", advanced);
        const groups = element("div", null, advanced);
        groups.className = "model-export-property-groups";
        const progressBar = element("progress", null, dialog);
        progressBar.id = "model-export-progress";
        progressBar.hidden = true;
        progressBar.setAttribute("aria-label", "Current export phase progress");
        const feedback = element("p", null, dialog);
        feedback.setAttribute("role", "status");
        feedback.setAttribute("aria-live", "polite");
        feedback.id = "model-export-feedback";
        const actions = element("div", null, dialog);
        actions.className = "model-export-dialog-actions";
        const cancel = element("button", "Cancel", actions);
        cancel.type = "button";
        cancel.id = "model-export-cancel";
        const submit = element("button", "Export", actions);
        submit.type = "button";
        submit.id = "model-export-submit";

        function updatePropertyControls() {
            const widgetGroup = propertyControls.get("widgets");
            if (widgetGroup) {
                widgetGroup.fieldset.disabled = !trees.checked;
            }
            const sequenceGroup = propertyControls.get("sequences");
            if (sequenceGroup) {
                sequenceGroup.fieldset.hidden = format.value !== "traces";
                sequenceGroup.fieldset.disabled = format.value !== "traces";
            }
        }
        trees.addEventListener("change", updatePropertyControls);
        format.addEventListener("change", updatePropertyControls);

        function showProperties(snapshot) {
            groups.replaceChildren();
            propertyControls.clear();
            const properties = environment.TestarModelExport.propertyInventory(snapshot);
            const defaults = environment.TestarModelExport.defaultProperties(properties);
            for (const [key, label] of [["states", "States"], ["actions", "Actions"],
                ["transitions", "Transitions"], ["widgets", "Widgets"], ["sequences", "Sequences"]]) {
                const fieldset = element("fieldset", null, groups);
                fieldset.dataset.group = key;
                element("legend", label, fieldset);
                const search = element("input", null, fieldset);
                search.type = "search";
                search.placeholder = "Search properties";
                search.setAttribute("aria-label", `Search ${label.toLowerCase()} properties`);
                const selectAll = element("button", "Select all", fieldset);
                selectAll.type = "button";
                const clear = element("button", "Clear selection", fieldset);
                clear.type = "button";
                const restore = element("button", "Use semantic defaults", fieldset);
                restore.type = "button";
                const list = element("div", null, fieldset);
                list.className = "model-export-property-list";
                const controls = (properties[key] || []).map(name => {
                    const row = element("label", null, list);
                    const input = element("input", null, row);
                    input.type = "checkbox";
                    input.checked = defaults[key].includes(name);
                    element("span", name, row);
                    return {name, input, row};
                });
                if (!controls.length) {
                    element("p", "No optional properties available.", list);
                }
                search.addEventListener("input", () => {
                    const query = search.value.trim().toLowerCase();
                    controls.forEach(control => { control.row.hidden = !control.name.toLowerCase().includes(query); });
                });
                selectAll.addEventListener("click", () => controls.forEach(control => { control.input.checked = true; }));
                clear.addEventListener("click", () => controls.forEach(control => { control.input.checked = false; }));
                restore.addEventListener("click", () => controls.forEach(control => { control.input.checked = defaults[key].includes(control.name); }));
                propertyControls.set(key, {fieldset, controls});
            }
            const selected = Object.values(defaults).reduce((count, names) => count + names.length, 0);
            advancedSummary.textContent = `Advanced Properties (${selected} selected by default)`;
            updatePropertyControls();
        }

        cancel.addEventListener("click", () => {
            if (!busy) {
                requestVersion++;
                dialog.close();
            }
        });
        dialog.addEventListener("cancel", event => {
            if (busy) {
                event.preventDefault();
            } else {
                requestVersion++;
            }
        });

        open.addEventListener("click", async () => {
            if (busy || dialog.open) {
                return;
            }
            const version = ++requestVersion;
            inventory = null;
            groups.replaceChildren();
            advanced.open = false;
            format.value = "hybrid";
            trees.checked = false;
            images.checked = true;
            availability.textContent = "";
            feedback.textContent = "Loading export options...";
            submit.disabled = true;
            dialog.showModal();
            try {
                const snapshot = await loadSnapshot({format: "inventory", includeWidgetTrees: false, includeScreenshots: false});
                if (version !== requestVersion) {
                    return;
                }
                inventory = snapshot;
                trees.disabled = snapshot.metadata?.widgetTreesCaptured === false && !snapshot.metadata?.live;
                images.disabled = snapshot.metadata?.screenshotsCaptured === false && !snapshot.metadata?.live;
                images.checked = !images.disabled;
                availability.textContent = [trees.disabled ? "Widget trees were not captured in this snapshot." : "",
                    images.disabled ? "Screenshots were not captured in this snapshot." : ""].filter(Boolean).join(" ");
                showProperties(snapshot);
                feedback.textContent = "Choose the data to include in this download.";
                submit.disabled = false;
            } catch (error) {
                if (version === requestVersion) {
                    feedback.textContent = `Export failed: ${error.message}`;
                }
            }
        });

        submit.addEventListener("click", async () => {
            if (busy || !inventory) {
                return;
            }
            busy = true;
            advanced.open = false;
            open.disabled = true;
            submit.disabled = true;
            cancel.disabled = true;
            format.disabled = true;
            trees.disabled = true;
            images.disabled = true;
            propertyControls.forEach(group => { group.fieldset.disabled = true; });
            progressBar.hidden = false;
            const started = Date.now();
            let currentProgress = {phase: "graph"};
            const phases = {graph: "Reading model graph", trees: "Collecting widget trees", screenshots: "Collecting screenshots",
                transfer: "Receiving model data", parse: "Reading captured model data", transform: "Preparing model relationships",
                states: "Preparing state data", json: "Building JSON", checksums: "Preparing ZIP files", zip: "Packaging ZIP"};
            function renderProgress() {
                const {phase, completed, total, bytes} = currentProgress;
                const counts = total > 0 ? `: ${completed || 0}/${total}` : bytes > 0
                    ? `: ${(bytes / 1048576).toFixed(1)} MB received` : completed > 0 ? `: ${completed} processed` : "";
                feedback.textContent = `${phases[phase] || "Preparing export"}${counts} - ${Math.floor((Date.now() - started) / 1000)}s elapsed`;
                status.textContent = feedback.textContent;
                if (total > 0) {
                    progressBar.max = total;
                    progressBar.value = completed || 0;
                } else {
                    progressBar.removeAttribute("value");
                }
            }
            const reportProgress = update => {
                currentProgress = update;
                renderProgress();
            };
            renderProgress();
            const timer = environment.setInterval(renderProgress, 1000);
            const options = {format: format.value, includeWidgetTrees: trees.checked, includeScreenshots: images.checked,
                properties: Object.fromEntries([...propertyControls].map(([key, group]) =>
                    [key, group.controls.filter(control => control.input.checked).map(control => control.name)]))};
            try {
                const snapshot = await loadSnapshot(options, reportProgress);
                const {archive, warningCount} = await environment.TestarModelExportRuntime.prepare(
                    snapshot, options.format, options, reportProgress, environment);
                const blob = new environment.Blob([archive], {type: "application/zip"});
                const url = environment.URL.createObjectURL(blob);
                try {
                    const link = element("a", null, document.body);
                    link.href = url;
                    link.download = `model_${options.format}.zip`;
                    try {
                        link.click();
                    } finally {
                        link.remove();
                    }
                } finally {
                    environment.setTimeout(() => environment.URL.revokeObjectURL(url), 1000);
                }
                status.textContent = `Downloaded model_${options.format}.zip.`
                    + (warningCount ? ` ${warningCount} warnings listed in JSON.` : "");
                dialog.close();
            } catch (error) {
                feedback.textContent = `Export failed: ${error.message}`;
                status.textContent = feedback.textContent;
            } finally {
                environment.clearInterval(timer);
                progressBar.hidden = true;
                busy = false;
                open.disabled = false;
                submit.disabled = false;
                cancel.disabled = false;
                format.disabled = false;
                trees.disabled = inventory.metadata?.widgetTreesCaptured === false && !inventory.metadata?.live;
                images.disabled = inventory.metadata?.screenshotsCaptured === false && !inventory.metadata?.live;
                propertyControls.forEach(group => { group.fieldset.disabled = false; });
                updatePropertyControls();
            }
        });
    }

    root.TestarModelExportControls = {attach};
    if (typeof module !== "undefined" && module.exports) {
        module.exports = {attach};
    }
})(globalThis);
