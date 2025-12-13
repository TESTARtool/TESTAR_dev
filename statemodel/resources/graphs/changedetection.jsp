<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Change Detection</title>
    <script src="js/babel.min.js"></script>
    <script src="js/cytoscape.min.js"></script>
    <script src="js/cola.min.js"></script>
    <script src="js/cytoscape-cose-bilkent.js"></script>
    <script src="js/cytoscape-cola.js"></script>
    <script src="js/cytoscape-euler.js"></script>
    <script src="js/dagre.js"></script>
    <script src="js/cytoscape-dagre.js"></script>
    <script src="js/klay.js"></script>
    <script src="js/cytoscape-klay.js"></script>
    <script src="js/jquery-3.2.1.min.js"></script>
    <script src="js/jquery.magnific-popup.min.js"></script>
    <script src="https://bundle.run/pixelmatch@5.2.0"></script>
    <script src="pixelmatchInterop.js"></script>
    <link rel="stylesheet" href="css/magnific-popup.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
</head>
<body>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="topbar">
    <img src="img/testar-logo.png" class="logo">
    <div class="layout">
        <div class="column">
            <div><label for="layout-control">Layout:
                <select name="layout-control" id="layout-control">
                    <option selected value="cola">Cola</option>
                    <option value="cose">Cose</option>
                    <option value="circle">Circle</option>
                    <option value="concentric">Concentric</option>
                    <option value="grid">Grid</option>
                    <option value="dagre">Dagre</option>
                    <option value="klay">Klay</option>
                </select></label></div>
        </div>

        <div class="column">
            <div class="extra-margin-left">
                <div><label for="oldModel">Old model:
                    <select name="oldModel" id="oldModel">
                        <c:forEach var="model" items="${models}">
                            <option value="${model.modelIdentifier}"
                                    data-appname="${model.applicationName}"
                                    data-appversion="${model.applicationVersion}"
                                    data-abstraction="${model.abstractionAttributes}"
                                    <c:if test="${oldModelIdentifier == model.modelIdentifier}">selected</c:if>>
                                ${model.applicationName} ${model.applicationVersion} (${model.modelIdentifier})
                            </option>
                        </c:forEach>
                    </select></label></div>
                <div><label for="newModel">New model:
                    <select name="newModel" id="newModel">
                        <c:forEach var="model" items="${models}">
                            <option value="${model.modelIdentifier}"
                                    data-appname="${model.applicationName}"
                                    data-appversion="${model.applicationVersion}"
                                    data-abstraction="${model.abstractionAttributes}"
                                    <c:if test="${newModelIdentifier == model.modelIdentifier}">selected</c:if>>
                                ${model.applicationName} ${model.applicationVersion} (${model.modelIdentifier})
                            </option>
                        </c:forEach>
                    </select></label></div>
                <div>
                    <button id="compareBtn" type="button" class="button_custom">Compare</button>
                </div>
            </div>
        </div>

        <div class="column">
            <div class="extra-margin-left">
                <div class="stats-text"><strong>BETA</strong>: Results depend strongly on the abstraction attributes used to infer each model.</div>
            </div>
        </div>
    </div>
</div>

<div class="viewpane" id="cy"></div>

<div class="cd-panel cd-panel--from-right js-cd-panel-main">
    <div class="cd-panel__container">
        <div class="panel-header" id="content-panel-header"></div>
        <div class="cd-panel__content" id="cd-content-panel"></div>
    </div>
</div>

<!-- Screenshot modal reused -->
<div class="modal fade" id="screenshotModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="screenshotTitle">Screenshot</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body text-center">
                <img id="screenshotImg" class="img-fluid" alt="Screenshot">
            </div>
        </div>
    </div>
</div>

<script>
    let appStatus = {};
    appStatus.graph = {};
    let cy = null;
    let changeResult = null;

    $(document).ready(function() {
        $('#compareBtn').on('click', function() {
            const oldId = $('#oldModel').val();
            const newId = $('#newModel').val();
            if (!oldId || !newId) {
                alert('Please select both models to compare');
                return;
            }
            $.post({
                url: 'changedetection',
                data: { oldModelIdentifier: oldId, newModelIdentifier: newId },
                success: function(resultText) {
                    $('#compareResult').show();
                    changeResult = typeof resultText === 'string' ? JSON.parse(resultText) : resultText;
                    renderResult(changeResult);
                    loadMergedGraph(oldId, newId);
                },
                error: function() {
                    alert('Comparison failed');
                }
            });
        });

        // choose defaults if not provided: first option as old, second as new
        const preOld = $('#oldModel').val();
        const preNew = $('#newModel').val();
        if (!preOld) {
            const first = $('#oldModel option').eq(0).val();
            $('#oldModel').val(first);
        }
        if (!preNew) {
            const second = $('#newModel option').eq(1).val() || $('#newModel option').eq(0).val();
            $('#newModel').val(second);
        }

        // auto-trigger compare
        $('#compareBtn').click();
    });

    function renderResult(result) {
        const addedStates = result.addedStates || [];
        const removedStates = result.removedStates || [];
        const changedStatesMap = result.changedStates || {};

        const oldIds = new Set((result.oldStates || []).map(s => s.stateId));
        if (oldIds.size === 0) {
            Object.keys(changedStatesMap).forEach(id => oldIds.add(id));
            removedStates.forEach(s => oldIds.add(s.stateId));
            addedStates.forEach(s => oldIds.add(s.stateId));
        }
        const unchanged = [];
        oldIds.forEach(id => {
            if (!changedStatesMap[id] && !removedStates.find(s => s.stateId === id) && !addedStates.find(s => s.stateId === id)) {
                unchanged.push(id);
            }
        });

        $('#badgeUnchanged').text(unchanged.length);
        $('#badgeChanged').text(Object.keys(changedStatesMap).length);
        $('#badgeAdded').text(addedStates.length);
        $('#badgeRemoved').text(removedStates.length);

        $('#addedStates').empty();
        addedStates.forEach(function(state) {
            $('#addedStates').append('<li class="list-group-item">' + state.stateId + '</li>');
        });

        $('#removedStates').empty();
        removedStates.forEach(function(state) {
            $('#removedStates').append('<li class="list-group-item">' + state.stateId + '</li>');
        });

        $('#changedStates').empty();
        Object.keys(changedStatesMap).forEach(function(stateId) {
            const diff = changedStatesMap[stateId];
            const added = (diff.added || []).map(d => d.propertyName + ': ' + (d.newValue || '')).join('<br>');
            const removed = (diff.removed || []).map(d => d.propertyName + ': ' + (d.oldValue || '')).join('<br>');
            const changedProps = (diff.changed || []).map(d => d.propertyName + ': ' + (d.oldValue || '') + ' -> ' + (d.newValue || '')).join('<br>');
            $('#changedStates').append(
                '<div class="mb-2"><strong>' + stateId + '</strong><br>' +
                '<em>Added:</em><br>' + (added || '<span class="text-muted">none</span>') + '<br>' +
                '<em>Removed:</em><br>' + (removed || '<span class="text-muted">none</span>') + '<br>' +
                '<em>Changed:</em><br>' + (changedProps || '<span class="text-muted">none</span>') +
                '</div>'
            );
        });

        $('#unchangedStates').empty();
        unchanged.forEach(function(stateId) {
            $('#unchangedStates').append('<li class="list-group-item">' + stateId + '</li>');
        });

        $('#rawJson').text(JSON.stringify(result, null, 2));
    }

    function loadMergedGraph(oldId, newId) {
        fetch('changedetection-graph?oldModelIdentifier=' + encodeURIComponent(oldId) + '&newModelIdentifier=' + encodeURIComponent(newId))
            .then(r => r.json())
            .then(function(elements) {
                const blackholeIds = new Set();
                elements.forEach(function(el) {
                    const classes = Array.isArray(el.classes) ? el.classes : [];
                    if (el.group === 'nodes' && classes.indexOf('BlackHole') >= 0 && el.data && el.data.id) {
                        blackholeIds.add(el.data.id);
                    }
                });

                elements.forEach(function(el) {
                    const classes = Array.isArray(el.classes) ? el.classes : (el.classes ? [el.classes] : []);
                    if (el.group === 'nodes' && el.data) {
                        if (!el.data.label) {
                            el.data.label = el.data.id;
                        }
                    }
                    if (el.group === 'edges' && el.data && !el.data.label) {
                        el.data.label = el.data.Desc || el.data.primaryKey || el.data.abstractId || el.data.actionId || el.data.id || '';
                    }
                    // hide labels for any edge into black holes
                    if (el.group === 'edges' && el.data) {
                        const tgt = el.data.target;
                        if (tgt && blackholeIds.has(tgt)) {
                            el.data.label = '';
                            el.data.hideLabel = true;
                            if (classes.indexOf('no-label') < 0) {
                                classes.push('no-label');
                            }
                            el.classes = classes;
                        }
                    }
                });

                if (cy) {
                    cy.json({ elements: elements });
                    cy.layout({ name: 'cola' }).run();
                } else {
                    initCy(elements);
                }
            })
            .catch(() => alert('Failed to load merged graph'));
    }

    function initCy(elements) {
        cy = cytoscape({
            container: document.getElementById("cy"),
            elements: elements,
            style: getStyleSheet(),
            layout: { name: 'cola' },
            wheelSensitivity: 0.5
        });

        initToggles();
        initHoverHighlights();

        // node click -> details panel
        cy.on('tap', 'node', function(evt) {
            openDetailsPanel(evt.target);
        });

        // action click -> details panel
        cy.on('tap', 'edge', function(evt) {
            openEdgeDetails(evt.target);
        });
    }

    function getStyleSheet() {
        const base = [
            { selector: 'node', style: { 'background-color': '#F6EFF7', 'border-width': "1px", 'border-color': '#000000', 'label': '', 'color': '#5d574d', 'font-size': '0.4em' } },
            { selector: 'node[counter]', style: { 'label': 'data(counter)' } },
            { selector: ':parent', style: { 'background-opacity': 0.9, 'border-style' : 'dashed', 'label': 'data(id)' } },
            { selector: 'edge', style: { 'width': 1, 'line-color': '#ccc', 'target-arrow-color': '#ccc', 'target-arrow-shape': 'triangle', 'curve-style': 'unbundled-bezier', 'text-rotation' : 'autorotate', 'label': 'data(label)', 'color': '#5d574d', 'font-size': '0.4em' } },
            { selector: 'edge[counter]', style: { 'label': 'data(counter)' } },
            { selector: '.AbstractAction', style: { 'line-color': '#1c9099', 'target-arrow-color': '#1c9099', 'label' : 'data(label)' } },
            { selector: '.AbstractState', style: { 'background-color': '#1c9099', 'label' : 'data(label)', 'background-image': 'data(screenshot)', 'background-fit': 'cover', 'background-opacity': 1 } },
            { selector: '.isInitial', style: { 'background-color': '#1c9099', 'width': '60px', 'height': '60px', 'border-color': '#000000' } },
            { selector: '.isAbstractedBy', style: { 'line-color': '#bdc9e1', 'target-arrow-color': '#bdc9e1', 'line-style': 'dashed', 'arrow-scale': 0.5, 'width': 0.5 } },
            { selector: '.BlackHole', style: { 'background-color': '#bdc9e1', 'label' : 'data(label)', 'background-image': 'url(img/blackhole-bg.jpg)', 'background-fit': 'cover', 'background-opacity': 1 } },
            { selector: '.Widget', style: { 'background-color': '#9ECAE1', 'label' : 'data(label)' } },
            { selector: '.no-label', style: { 'label': '' } },
            { selector: 'edge[hideLabel]', style: { 'label': '' } },

            // layer parents
            { selector: '.Layer', style: { 'shape': 'roundrectangle', 'padding': 20, 'background-color': '#f3ecf5', 'border-style': 'dashed', 'border-width': 2, 'border-color': '#444', 'label': 'data(label)', 'text-valign': 'top', 'text-halign': 'center', 'font-size': '0.6em', 'color': '#555' } },
            { selector: '.AbstractLayer', style: { 'background-color': '#f3ecf5' } },

            // change detection overlays
            { selector: 'node[status = "unchanged"]', style: { 'border-style': 'solid', 'border-width': 2 } },
            { selector: 'node[status = "changed"]', style: { 'border-style': 'dashed', 'border-width': 6 } },
            { selector: 'node[status = "added"]', style: { 'shape': 'star', 'border-width': 4, 'border-color': '#4CAF50', 'background-color': '#4CAF50' } },
            { selector: 'node[status = "removed"]', style: { 'shape': 'triangle', 'border-width': 4, 'border-color': '#E41A1C', 'background-color': '#E41A1C' } },
            { selector: 'edge[status = "added"]', style: { 'line-color': '#4CAF50', 'target-arrow-color': '#4CAF50' } },
            { selector: 'edge[status = "removed"]', style: { 'line-color': '#E41A1C', 'target-arrow-color': '#E41A1C', 'line-style': 'dashed' } },
            { selector: 'edge[status = "changed"]', style: { 'line-color': '#FFD92F', 'target-arrow-color': '#FFD92F', 'line-style': 'dotted' } },
            // inter-layer edges neutral style
            { selector: 'edge.isAbstractedBy', style: { 'line-color': '#bdc9e1', 'target-arrow-color': '#bdc9e1', 'line-style': 'dashed', 'arrow-scale': 0.6, 'width': 1.2 } }
        ];
        return base;
    }

    function initToggles() {
        let layoutControl = document.getElementById("layout-control");
        layoutControl.addEventListener("change", function () {
            let selectedLayout = layoutControl.value;
            cy.layout({ name : selectedLayout, animate: 'end', animationDuration: 800 }).run();
        });
    }

    function initHoverHighlights() {
        cy.$('node').on('mouseover', (event) => {
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.is(':parent')) return;
            event.target.addClass(event.target.hasClass('isInitial') ? 'selected-initial-node' : 'selected-node');
        });
        cy.$('node').on('mouseout', (event) => {
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.is(':parent')) return;
            if (event.target.hasClass('selected-node')) {
                event.target.removeClass('selected-node');
            }
            if (event.target.hasClass('selected-initial-node')) {
                event.target.removeClass('selected-initial-node');
            }
        });
        cy.$('edge').on('mouseover', (event) => {
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.hasClass('isAbstractedBy')) return;
            event.target.addClass('selected-edge');
        });
        cy.$('edge').on('mouseout', (event) => {
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.hasClass('isAbstractedBy')) return;
            event.target.removeClass('selected-edge');
        });
    }

    function openDetailsPanel(node) {
        const panel = document.getElementsByClassName("cd-panel")[0];
        const contentPanel = document.getElementById("cd-content-panel");
        const contentPanelHeader = document.getElementById("content-panel-header");
        panel.classList.add("cd-panel--is-visible");

        while (contentPanel.firstChild) contentPanel.removeChild(contentPanel.firstChild);
        while (contentPanelHeader.firstChild) contentPanelHeader.removeChild(contentPanelHeader.firstChild);

        let closeButton = document.createElement("button");
        closeButton.id = "close-panel";
        closeButton.classList.add("skip");
        closeButton.appendChild(document.createTextNode("Close"));
        closeButton.addEventListener("click", function () {
            panel.classList.remove("cd-panel--is-visible");
            appStatus.graph.selectedNode = null;
        });
        contentPanelHeader.appendChild(closeButton);

        appStatus.graph.selectedNode = node;

        // fill details using changeResult
        const stateId = node.id();
        const changedStates = (changeResult && changeResult.changedStates) || {};
        const changedActions = (changeResult && changeResult.changedActions) || {};
        const added = (changeResult && changeResult.addedStates || []).find(s => s.stateId === stateId);
        const removed = (changeResult && changeResult.removedStates || []).find(s => s.stateId === stateId);
        const diff = changedStates[stateId];
        const actionDiff = changedActions[stateId];

        let html = '<h5>' + stateId + '</h5>';
        if (added) html += '<p><span class="badge badge-success">Added</span></p>';
        else if (removed) html += '<p><span class="badge badge-danger">Removed</span></p>';
        else if (diff) html += '<p><span class="badge badge-warning">Changed</span></p>';
        else html += '<p><span class="badge badge-secondary">Unchanged</span></p>';

        const data = node.data();
        const oldShot = data.oldScreenshot;
        const newShot = data.newScreenshot;
        const fallbackShot = data.screenshot;

        if (oldShot && newShot) {
            html += '<div class="row mb-2">' +
                    '<div class="col-6"><img class="img-fluid" style="max-height:300px; cursor:pointer;" src="' + oldShot + '" alt="Old screenshot" onclick="openScreenshot(\'' + oldShot + '\', \'Old screenshot\')"></div>' +
                    '<div class="col-6"><img class="img-fluid" style="max-height:300px; cursor:pointer;" src="' + newShot + '" alt="New screenshot" onclick="openScreenshot(\'' + newShot + '\', \'New screenshot\')"></div>' +
                    '</div>';
            html += '<div class="mb-2"><small>Diff</small><br>' +
                    '<div id="cd-diff-status" class="text-muted">Computing diff…</div>' +
                    '<img id="cd-diff-img" class="img-fluid" style="max-height:300px; display:none; cursor:pointer;" alt="Diff screenshot">' +
                    '</div>';
        } else if (oldShot || newShot || fallbackShot) {
            const shot = newShot || oldShot || fallbackShot;
            html += '<div class="mb-2"><img class="img-fluid" style="max-height:300px; cursor:pointer;" src="' + shot + '" alt="Screenshot" onclick="openScreenshot(\'' + shot + '\', \'Screenshot\')"></div>';
        }

        if (diff) {
            html += '<strong>Properties</strong><br>';
            html += '<em>Added:</em><br>' + renderList(diff.added, d => d.propertyName + ': ' + (d.newValue || '')) + '<br>';
            html += '<em>Removed:</em><br>' + renderList(diff.removed, d => d.propertyName + ': ' + (d.oldValue || '')) + '<br>';
            html += '<em>Changed:</em><br>' + renderList(diff.changed, d => d.propertyName + ': ' + (d.oldValue || '') + ' -> ' + (d.newValue || '')) + '<br>';
        }

        if (actionDiff) {
            html += '<strong>Actions</strong><br>';
            html += '<em>Added outgoing:</em><br>' + renderList(actionDiff.addedOutgoing, d => d.actionId + ' (' + d.description + ')') + '<br>';
            html += '<em>Removed outgoing:</em><br>' + renderList(actionDiff.removedOutgoing, d => d.actionId + ' (' + d.description + ')') + '<br>';
            html += '<em>Added incoming:</em><br>' + renderList(actionDiff.addedIncoming, d => d.actionId + ' (' + d.description + ')') + '<br>';
            html += '<em>Removed incoming:</em><br>' + renderList(actionDiff.removedIncoming, d => d.actionId + ' (' + d.description + ')') + '<br>';
        }

        // attributes of this node
        const attrs = extractAttributes(data);
        if (Object.keys(attrs).length > 0) {
            html += '<br><strong>Attributes</strong><br>' + buildAttributesTable(attrs);
        }

        contentPanel.innerHTML = html;

        if (oldShot && newShot) {
            computeAndRenderDiff(oldShot, newShot, 0.3);
        }
    }

    let diffJobCounter = 0;

    async function computeAndRenderDiff(oldUrl, newUrl, threshold) {
        const jobId = ++diffJobCounter;
        const statusEl = document.getElementById('cd-diff-status');
        const imgEl = document.getElementById('cd-diff-img');
        if (!statusEl || !imgEl) {
            return;
        }

        try {
            if (!window.pixelmatchInterop || typeof window.pixelmatchInterop.compareImages !== 'function' || typeof window.pixelmatch !== 'function') {
                statusEl.textContent = 'Diff unavailable (pixelmatch not loaded).';
                return;
            }

            const [oldB64, newB64] = await Promise.all([fetchAsBase64(oldUrl), fetchAsBase64(newUrl)]);
            const diffB64 = await window.pixelmatchInterop.compareImages(newB64, oldB64, threshold);
            if (jobId !== diffJobCounter) {
                return;
            }

            imgEl.src = 'data:image/png;base64,' + diffB64;
            imgEl.style.display = 'block';
            imgEl.onclick = function () {
                openScreenshot(imgEl.src, 'Diff screenshot');
            };
            statusEl.remove();
        } catch (e) {
            if (jobId !== diffJobCounter) {
                return;
            }
            statusEl.textContent = 'Diff failed: ' + (e && e.message ? e.message : e);
        }
    }

    function fetchAsBase64(url) {
        return fetch(url)
            .then(function (res) {
                if (!res.ok) {
                    throw new Error('HTTP ' + res.status + ' for ' + url);
                }
                return res.blob();
            })
            .then(function (blob) {
                return new Promise(function (resolve, reject) {
                    const reader = new FileReader();
                    reader.onloadend = function () {
                        const result = reader.result || '';
                        const parts = String(result).split(',');
                        resolve(parts.length > 1 ? parts[1] : parts[0]);
                    };
                    reader.onerror = function () {
                        reject(reader.error || new Error('Failed to read image'));
                    };
                    reader.readAsDataURL(blob);
                });
            });
    }

    function openEdgeDetails(edge) {
        const panel = document.getElementsByClassName("cd-panel")[0];
        const contentPanel = document.getElementById("cd-content-panel");
        const contentPanelHeader = document.getElementById("content-panel-header");
        panel.classList.add("cd-panel--is-visible");

        while (contentPanel.firstChild) contentPanel.removeChild(contentPanel.firstChild);
        while (contentPanelHeader.firstChild) contentPanelHeader.removeChild(contentPanelHeader.firstChild);

        let closeButton = document.createElement("button");
        closeButton.id = "close-panel";
        closeButton.classList.add("skip");
        closeButton.appendChild(document.createTextNode("Close"));
        closeButton.addEventListener("click", function () {
            panel.classList.remove("cd-panel--is-visible");
            appStatus.graph.selectedEdge = null;
        });
        contentPanelHeader.appendChild(closeButton);

        appStatus.graph.selectedEdge = edge;

        const data = edge.data() || {};
        const status = data.status || 'unchanged';
        const label = data.label || data.Desc || data.primaryKey || data.abstractId || data.actionId || data.id;
        const source = edge.source();
        const target = edge.target();
        let html = '<h5>Action</h5>';
        html += '<p><span class="badge badge-light">' + status + '</span></p>';
        html += '<p><strong>Label:</strong> ' + (label || '(none)') + '</p>';
        html += '<p><strong>From:</strong> ' + (source.data('label') || source.id()) + '  <strong>To:</strong> ' + (target.data('label') || target.id()) + '</p>';

        // screenshots of source / target if available
        const sourceShot = source.data('newScreenshot') || source.data('oldScreenshot') || source.data('screenshot');
        const targetShot = target.data('newScreenshot') || target.data('oldScreenshot') || target.data('screenshot');
        if (sourceShot || targetShot) {
            html += '<div class="row mb-2">';
            if (sourceShot) {
                html += '<div class="col-6 text-center"><small>From</small><br><img class="img-fluid" style="max-height:220px; cursor:pointer;" src="' + sourceShot + '" alt="Source screenshot" onclick="openScreenshot(\'' + sourceShot + '\', \'Source screenshot\')"></div>';
            }
            if (targetShot) {
                html += '<div class="col-6 text-center"><small>To</small><br><img class="img-fluid" style="max-height:220px; cursor:pointer;" src="' + targetShot + '" alt="Target screenshot" onclick="openScreenshot(\'' + targetShot + '\', \'Target screenshot\')"></div>';
            }
            html += '</div>';
        }

        const attrs = extractAttributes(data);
        if (Object.keys(attrs).length > 0) {
            html += '<strong>Attributes</strong><br>' + buildAttributesTable(attrs);
        }

        contentPanel.innerHTML = html;
    }

    function renderList(items, formatter) {
        if (!items || items.length === 0) {
            return '<span class="text-muted">none</span>';
        }
        return items.map(formatter).join('<br>');
    }

    function buildAttributesTable(attrs) {
        const keys = Object.keys(attrs || {}).sort();
        if (keys.length === 0) {
            return '<span class="text-muted">none</span>';
        }
        let rows = keys.map(k => '<tr><td>' + k + '</td><td>' + attrs[k] + '</td></tr>').join('');
        return '<table class="table-data"><thead><tr><th>Attribute name</th><th>Attribute value</th></tr></thead><tbody>' + rows + '</tbody></table>';
    }

    function openScreenshot(url, title) {
        $('#screenshotImg').attr('src', url);
        $('#screenshotTitle').text(title || 'Screenshot');
        $('#screenshotModal').modal('show');
    }

    // extract attributes excluding internal fields
    function extractAttributes(data) {
        const skip = new Set(['id','label','parent','screenshot','oldScreenshot','newScreenshot','status','rawId','stateId','actionId','abstractId','Desc','description','source','target','primaryKey']);
        const attrs = {};
        Object.keys(data || {}).forEach(k => {
            if (!skip.has(k)) {
                attrs[k] = data[k];
            }
        });
        return attrs;
    }
</script>
</body>
</html>
