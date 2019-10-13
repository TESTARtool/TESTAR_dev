<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Graph</title>
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
    <script src="js/jquery-3.2.1.slim.min.js"></script>
    <script src="js/jquery.magnific-popup.min.js"></script>
    <link rel="stylesheet" href="css/magnific-popup.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="topbar">
    <img src="img/testar-logo.png" class="logo">
    <div class="layout">
        <div class="column">
            <div><label for="layout-control">Layout: <select name="layout-control" id="layout-control">
                <option selected disabled></option>
                <option value="random">Random</option>
                <option value="grid">Grid</option>
                <option value="circle">Circle</option>
                <option value="concentric">Concentric</option>
                <option value="breadthfirst">Breadthfirst</option>
                <option value="cose">Cose</option>
                <option value="cose-bilkent">Cose-bilkent</option>
                <option value="cola">Cola</option>
                <option value="euler">Euler</option>
                <option value="dagre">Dagre</option>
                <option value="klay">Klay</option>
            </select></label></div>
            <div>
                <label for="show-labels" class="custom-checkbox">Show labels<input type="checkbox" id="show-labels" checked><span class="checkmark"></span></label>
            </div>
            <div>
                <button id="show-all" type="button" class="button_custom">Show all nodes</button>
            </div>
        </div>

        <div class="column">
            <div class="extra-margin-left"><label for="toggle-abstract-layer" class="custom-checkbox">Show abstract layer<input type="checkbox" id="toggle-abstract-layer" checked><span class="checkmark"></span></label></div>
            <div class="extra-margin-left"><label for="toggle-concrete-layer" class="custom-checkbox">Show concrete layer<input type="checkbox" id="toggle-concrete-layer" checked><span class="checkmark"></span></label></div>
            <div class="extra-margin-left"><label for="toggle-sequence-layer" class="custom-checkbox">Show sequence layer<input type="checkbox" id="toggle-sequence-layer" checked><span class="checkmark"></span></label></div>
            <div class="extra-margin-left"><label for="toggle-layer-transitions" class="custom-checkbox">Show inter-layer edges<input type="checkbox" id="toggle-layer-transitions" checked><span class="checkmark"></span></label></div>
        </div>

        <div class="column">
            <div class="extra-margin-left"><span class="legend">Legend:</span></div>
        </div>

        <div class="column">
            <div class="legend-box abstract-state" id="legend-abstract-state"></div>
            <div class="legend-box concrete-state" id="legend-concrete-state"></div>
            <div class="legend-box sequence-node"></div>
        </div>

        <div class="column">
            <div class="legend-text">Abstract state</div>
            <div class="legend-text">Concrete State</div>
            <div class="legend-text">Sequence Node</div>
        </div>

        <div class="column">
            <div class="legend-box first-node"></div>
            <div class="legend-box widget"></div>
            <div class="legend-box blackhole"></div>
        </div>

        <div class="column">
            <div class="legend-text">First Sequence Node</div>
            <div class="legend-text">Widget</div>
            <div class="legend-text">Black hole</div>
        </div>

        <div class="column">
            <div class="extra-margin-left">
                <div class="stats-text" id="stats-abstract-states"></div>
                <div class="stats-text" id="stats-abstract-actions"></div>
                <div class="stats-text" id="stats-concrete-states"></div>
                <div class="stats-text" id="stats-concrete-actions"></div>
            </div>
        </div>
    </div>
</div>

<div class="viewpane" id="cy">
</div>

<div class="cd-panel cd-panel--from-right js-cd-panel-main">
    <div class="cd-panel__container">
        <div class="panel-header" id="content-panel-header">
            <%--<button id="close-panel" type="button">Close</button>--%>
        </div>
        <div class="cd-panel__content" id="cd-content-panel">

        </div> <!-- cd-panel__content -->
    </div> <!-- cd-panel__container -->
</div>


<script>

    // global object that will hold some config values
    let appStatus = {};
    appStatus.graph = {};
    let cy = cytoscape({
        container: document.getElementById("cy"),

        elements: fetch("${contentFolder}/${graphContentFile}").then(function(response) {
            return response.json();
        }),

        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': '#F6EFF7',
                    'border-width': "1px",
                    'border-color': '#000000',
                    'label': '',
                    'color': '#5d574d',
                    'font-size': '0.4em'
                }
            },

            {
                selector: 'node[counter]',
                style: {
                    'label': 'data(counter)',
                }
            },

            {
                selector: ':parent',
                style: {
                    'background-opacity': 0.9,
                    'border-style' : 'dashed',
                    'label': 'data(id)'
                }
            },

            {
                selector: 'edge',
                style: {
                    'width': 1,
                    'line-color': '#ccc',
                    'target-arrow-color': '#ccc',
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'unbundled-bezier',
                    'text-rotation' : 'autorotate',
                    'label': '',
                    'color': '#5d574d',
                    'font-size': '0.3em'
                }
            },
            {
                selector: 'edge[counter]',
                style: {
                    'label': 'data(counter)',
                }
            },
            {
                selector: '.AbstractAction',
                style: {
                    'line-color': '#1c9099',
                    'target-arrow-color': '#1c9099'
                }

            },

            {
                selector: '.AbstractState',
                style: {
                    'background-color': '#1c9099',
                    'label' : 'data(customLabel)'
                }
            },

            {
                selector: '.isInitial',
                style: {
                    'background-color': '#1c9099',
                    'width': '60px',
                    'height': '60px',
                    'border-color': '#000000'
                }
            },

            {
                selector: '.ConcreteState',
                style: {
                    'background-color': '#67A9CF',
                    'background-image': function (ele) {
                        return "${contentFolder}/" + ele.data('id') + ".png"
                    },
                    'background-fit': 'contain',
                    'label' : 'data(customLabel)'
                }
            },

            {
                selector: '.ConcreteAction',
                style: {
                    'line-color': '#67A9CF',
                    'target-arrow-color': '#67A9CF'
                }
            },

            {
                selector: '.isAbstractedBy',
                style: {
                    'line-color': '#bdc9e1',
                    'target-arrow-color': '#bdc9e1',
                    'line-style': 'dashed',
                    'arrow-scale': 0.5,
                    'width': 0.5
                }
            },

            {
                selector: '.SequenceStep',
                style: {
                    'line-color': '#016450',
                    'target-arrow-color': '#016450'
                }
            },

            {
                selector: '.SequenceNode',
                style: {
                    'background-color': '#016450',
                    'label' : 'data(customLabel)'
                }
            },

            {
                selector: '.FirstNode',
                style: {
                    'line-color': '#014636',
                    'target-arrow-color': '#014636'
                }
            },

            {
                selector: '.TestSequence',
                style: {
                    'background-color': '#014636',
                    'label' : 'data(customLabel)'
                }
            },
            {
                selector: '.Accessed',
                style: {
                    'line-color': '#d0d1e6',
                    'target-arrow-color': '#d0d1e6',
                    'line-style': 'dashed',
                    'arrow-scale': 0.5,
                    'width': 0.5
                }
            },

            {
                selector: '.Widget',
                style: {
                    'background-color': '#e7298a',
                    'background-opacity': 0.8,
                    'label' : 'data(customLabel)'
                }
            },

            {
                selector: '.isChildOf',
                style: {
                    'line-color': "#df65b0",
                    'target-arrow-color': '#df65b0'
                }
            },


            {
                selector: '.BlackHole',
                style: {
                    'background-color': '#000000',
                    'label': 'data(id)',
                    'background-image' : "img/blackhole-bg.jpg",
                    'background-fit': 'contain',
                    'label': 'BlackHole'
                }
            },

            {
                selector: '.UnvisitedAbstractAction',
                style: {
                    'line-color': "#1c9099",
                    'target-arrow-color': "#1c9099",
                    'line-style': 'dashed',
                    'width': 1
                }
            },

            {
                selector: '.no-label',
                style: {
                    'label': ''
                }
            },

            {
                selector: '.invisible',
                style: {
                    'display' : 'none'
                }
            },

            {
                selector: '.dim',
                style: {
                    'line-color': "#FFFFFF",
                    'target-arrow-color': "#FFFFFF",
                    'background-color': '#FFFFFF',
                    'border-color': '#FFFFFF',
                    'background-image-opacity': 0.05
                }
            },
            {
                selector: '.leaves',
                style: {
                    'width': '60px',
                    'height': '60px',
                    'border-width': '2px'
                }
            },
            {
                selector: '.errorState',
                style: {
                    'border-color': '#FF0000',
                    'line-color': '#FF0000'
                }
            },
            {
                selector: '.selected-node',
                style: {
                    'width': '50px',
                    'height': '50px',
                    'border-color': '#4be2ff'
                }
            },
            {
                selector: '.selected-node-animated',
                style: {
                    'width': '50px',
                    'height': '50px',
                    'border-color': '#4be2ff',
                    'transition-property' : 'width height border-color',
                    'transition-duration' : '0.5s',
                    'transition-timing-function': 'ease-out-sine'
                }
            },
            {
                selector: '.selected-initial-node',
                style : {
                    'width': '60px',
                    'height': '60px',
                    'border-color': '#4be2ff'
                }
            },
            {
                selector: '.selected-initial-node-animated',
                style : {
                    'width': '60px',
                    'height': '60px',
                    'border-color': '#4be2ff',
                    'transition-property' : 'width height border-color',
                    'transition-duration' : '0.5s',
                    'transition-timing-function': 'ease-out-sine'
                }
            },
            {
                selector: '.connected-concrete-state-node',
                style: {
                    'width': '50px',
                    'height': '50px',
                    'border-color': '#3255ff'
                }
            },
            {
                selector: '.selected-edge',
                style: {
                    'line-color': "#4be2ff",
                    'target-arrow-color': "#4be2ff",
                    'line-style': 'solid',
                    'width': 2,
                    'font-size': '8px',
                    'font-weight': 'bold'
                }
            },
            {
                selector: '.mouse-over-concrete-action',
                style: {
                    'label' : 'data(Desc)'
                }
            }
        ],

        layout: {
            name: 'grid'
        },

        wheelSensitivity: 0.5


    });

    let layoutControl = document.getElementById("layout-control");
    layoutControl.addEventListener("change", function () {
        let selectedLayout = layoutControl.value;
        cy.layout({
            name : selectedLayout,
            animate: 'end',
            animationEasing: 'ease-out',
            animationDuration: 1000
        }).run();
    });

    let showLabels = document.getElementById("show-labels");
    showLabels.addEventListener("change", function () {
        if (showLabels.checked) {
            cy.$('.no-label').removeClass('no-label');
        }
        else {
            cy.$('node').addClass('no-label');
            cy.$('edge').addClass('no-label');
        }
    });

    // when nodes get clicked, we need to open the side bar
    cy.on('tap', 'node', function(evt) {
        let targetNode = evt.target;
        let sidePanel = document.getElementsByClassName("cd-panel")[0];
        let contentPanel = document.getElementById("cd-content-panel");
        let contentPanelHeader = document.getElementById("content-panel-header");
        appStatus.graph.selectedNode = targetNode;

        // highlight the selected node
        cy.$('edge.selected-edge').removeClass('selected-edge');
        cy.$('node.selected-node').union(cy.$('node.isInitial')).removeClass('selected-node').removeClass('selected-initial-node');
        cy.$('node.connected-concrete-state-node').removeClass('connected-concrete-state-node');
        targetNode.addClass(targetNode.hasClass('isInitial') ? 'selected-initial-node' : 'selected-node');

        // if the selected node is a sequence node, we also highlight the concrete state it accessed
        if (targetNode.hasClass('SequenceNode')) {
            targetNode.outgoers('.Accessed').target('.ConcreteState').addClass('connected-concrete-state-node');
        }

        // remove all the current child elements for both panel and panel header
        let child = contentPanel.lastChild;
        while (child) {
            contentPanel.removeChild(child);
            child = contentPanel.lastChild;
        }

        child = contentPanelHeader.lastChild;
        while (child) {
            contentPanelHeader.removeChild(child);
            child = contentPanelHeader.lastChild;
        }

        ///////////// add button section ///////////////////////////
        let closeButton = document.createElement("button");
        closeButton.id = "close-panel";
        closeButton.classList.add("skip");
        closeButton.appendChild(document.createTextNode("Close"));
        closeButton.addEventListener("click", function () {
            let cdPanel = document.getElementsByClassName("cd-panel")[0];
            cdPanel.classList.remove("cd-panel--is-visible");
            // remove the highlight from the selected node
            cy.$('node.selected-node').union(cy.$('node.isInitial')).removeClass('selected-node').removeClass('selected-initial-node');
            if (targetNode.hasClass('SequenceNode')) {
                targetNode.outgoers('.Accessed').target('.ConcreteState').removeClass('connected-concrete-state-node');
            }
            // remove the node selection
            appStatus.graph.selectedNode = null;
        });
        contentPanelHeader.appendChild(closeButton);

        // for concrete states we provide a button to retrieve the widget tree
        if (targetNode.hasClass("ConcreteState")) {
            let form = document.createElement("form");
            let input = document.createElement("input");
            input.type = "hidden";
            input.value = targetNode.id();
            input.name = "concrete_state_id";
            form.appendChild(input);

            form.method = "POST";
            form.action = "graph";
            form.target = "_blank";
            contentPanel.appendChild(form);

            let widgetTreeButton = document.createElement("button");
            widgetTreeButton.id ="widget-tree-button";
            widgetTreeButton.classList.add("skip");
            widgetTreeButton.appendChild(document.createTextNode("Inspect widget tree"));
            widgetTreeButton.addEventListener("click", function () {
                form.submit();
            });
            contentPanelHeader.appendChild(widgetTreeButton);
        }

        // add a visibility button
        let visibilityButton = document.createElement("button");
        visibilityButton.id = "toggle-visible";
        visibilityButton.classList.add("skip");
        visibilityButton.appendChild(document.createTextNode("Make invisible"));
        visibilityButton.addEventListener("click", function () {
            targetNode.addClass("invisible");
        });
        contentPanelHeader.appendChild(visibilityButton);

        // add a highlight button
        let highlightButton = document.createElement("button");
        highlightButton.id = "highlight";
        highlightButton.classList.add("skip");
        highlightButton.appendChild(document.createTextNode("Highlight"));
        highlightButton.addEventListener("click", function () {
            // we want to select the clicked node and its neighborhood
            let allNodes = cy.$(targetNode).closedNeighborhood();

            // next, in the case of a concrete action or an abstract action, we want to also fetch their concrete and
            // abstract counterparts for the neighborhood
            if (targetNode.hasClass('ConcreteState')) {
                // get the connect abstract states and their connected abstract actions
                let abstractStateNodes = allNodes.nodes('.ConcreteState').outgoers('.AbstractState');
                let abstractionEdges = allNodes.nodes('.ConcreteState').outgoers('.isAbstractedBy');
                let abstractActionNodes = abstractStateNodes.connectedEdges('.AbstractAction');

                // next, for each concrete action in the neighborhoor, attempt to fetch the corresponding abstract action
                let selectedAbstractActionNodes = [];
                allNodes.edges('.ConcreteAction').forEach(
                    (edge) => abstractActionNodes.forEach(
                        (abstractActionNode) => {
                            if (abstractActionNode.data('concreteActionIds').indexOf(edge.data('actionId')) != -1) {
                                selectedAbstractActionNodes.push(abstractActionNode);
                            }
                        }
                    )
                );

                // now join the nodes
                allNodes = allNodes.union(abstractStateNodes).union(selectedAbstractActionNodes).union(abstractionEdges);
            }

            // we also need to select the parent nodes in case of a compound graph.
            allNodes = allNodes.union(allNodes.parent());
            cy.$("*").difference(allNodes).addClass("invisible");
        });
        contentPanelHeader.appendChild(highlightButton);

        // for concrete states, we offer a button that will trace the paths leading to that state
        if (targetNode.hasClass("ConcreteState") && appStatus.sequenceLayerPresent) {
            let traceButton = document.createElement("button");
            traceButton.id = "trace-path-button";
            traceButton.classList.add("skip");
            traceButton.appendChild(document.createTextNode("Trace Path"));
            traceButton.addEventListener("click", () => {
                // get the sequence nodes that accessed this node
                let sequenceNodes = cy.$(targetNode).incomers(".SequenceNode, .Accessed");
                // get the complete list of sequence nodes that let to them
                let predecessorNodes = sequenceNodes.predecessors();
                // next get the concrete state nodes that were accessed by all these sequence nodes
                let concreteStateNodes = predecessorNodes.outgoers();
                // then, we have to get the concrete action nodes that correspond with the sequence steps
                let concreteActionUids = predecessorNodes.filter((element) => element.hasClass('SequenceStep')).map((element) =>
                    element.data('concreteActionUid'));
                // now fetch the edges matching the collected ids
                let concreteActions = concreteStateNodes.connectedEdges('.ConcreteAction').filter((element) =>
                    concreteActionUids.includes(element.data('uid')));
                let allElements = sequenceNodes.union(predecessorNodes).union(concreteStateNodes).union(targetNode).union(concreteActions);
                // add the parent nodes, if there are any
                allElements = allElements.union(cy.$(allElements).parent());
                cy.$("*").difference(allElements).addClass("invisible");
            });
            contentPanelHeader.appendChild(traceButton);
        }

        // if the clicked node is a test sequence, we show a button that will show just the nodes of that test sequence
        if (targetNode.hasClass('TestSequence') && appStatus.concreteLayerPresent) {
            let traceButton = document.createElement('button');
            traceButton.id = 'trace-sequence-button';
            traceButton.classList.add('skip');
            traceButton.appendChild(document.createTextNode('Trace Sequence'));
            traceButton.addEventListener('click', () => {
                // first, get all the nodes and edges in the sequence
                let sequenceElements = targetNode.successors('.SequenceNode, .SequenceStep, .FirstNode');
                // add the targetnode itself and the parents
                sequenceElements = sequenceElements.union(targetNode);

                // now get all the corresponding elements on the concrete layer
                let concreteStateNodes = sequenceElements.nodes('.SequenceNode').outgoers('.ConcreteState');
                let accessedEdges = sequenceElements.nodes('.SequenceNode').outgoers('.Accessed');
                let concreteActionNodes = concreteStateNodes.connectedEdges('.ConcreteAction');

                // for each sequence step, fetch the corresponding concrete action
                let selectedConcreteActionNodes = [];
                sequenceElements.edges('.SequenceStep').forEach(
                    (edge) => concreteActionNodes.forEach(
                        (concreteActionNode) => {
                            if (concreteActionNode.data('uid') == edge.data('concreteActionUid')) {
                                selectedConcreteActionNodes.push(concreteActionNode);
                            }
                        }
                    )
                );

                sequenceElements = sequenceElements.union(concreteStateNodes).union(accessedEdges).union(selectedConcreteActionNodes);

                // get all the corresponding elements on the abstract layer
                let abstractStateNodes = sequenceElements.nodes('.ConcreteState').outgoers('.AbstractState');
                let abstractionEdges = sequenceElements.nodes('.ConcreteState').outgoers('.isAbstractedBy');
                let abstractActionNodes = abstractStateNodes.connectedEdges('.AbstractAction');

                // next, for each concrete action in the neighborhoor, attempt to fetch the corresponding abstract action
                let selectedAbstractActionNodes = [];
                sequenceElements.edges('.ConcreteAction').forEach(
                    (edge) => abstractActionNodes.forEach(
                        (abstractActionNode) => {
                            if (abstractActionNode.data('concreteActionIds').indexOf(edge.data('actionId')) != -1) {
                                selectedAbstractActionNodes.push(abstractActionNode);
                            }
                        }
                    )
                );

                // now join the nodes
                sequenceElements = sequenceElements.union(abstractStateNodes).union(selectedAbstractActionNodes).union(abstractionEdges);

                // add the parents
                sequenceElements = sequenceElements.union(sequenceElements.parent());
                cy.$("*").difference(sequenceElements).addClass("invisible");
            });

            contentPanelHeader.appendChild(traceButton);
        }

        //////////////// end add button section //////////////////

        /////////// screenshot segment //////////

        // add the screenshot image if the node is a concrete state
        if (targetNode.hasClass("ConcreteState")) {
            // create a popup anchor
            let popupAnchor = document.createElement("a");
            popupAnchor.href = "${contentFolder}/" + targetNode.id() + ".png";
            $(popupAnchor).magnificPopup(
                {type: "image"}
            );

            // add the screenshot full image
            let nodeImage = document.createElement("img");
            nodeImage.alt = "Image for node " + targetNode.id();
            nodeImage.src = "${contentFolder}/" + targetNode.id() + ".png";
            nodeImage.classList.add("node-img-full");
            popupAnchor.appendChild(nodeImage);
            contentPanel.appendChild(popupAnchor);
        }

        // add a series of screenshots if the node is an abstract state
        if (targetNode.hasClass("AbstractState")) {
            // first retrieve all the concrete states that are abstracted by this abstract state
            let concreteNodes = targetNode.incomers(".ConcreteState");
            if (concreteNodes.size() > 0) {
                let div =  document.createElement('div');
                div.classList.add('popup-gallery');
                concreteNodes.forEach(
                    (element) => {
                        // create an anchor element for each screenshot
                        let popupAnchor = document.createElement("a");
                        popupAnchor.href = "${contentFolder}/" + element.id() + ".png";

                        // add the image thumbnail
                        // add the screenshot full image
                        let nodeImage = document.createElement("img");
                        nodeImage.alt = "Image for node " + element.id();
                        nodeImage.src = "${contentFolder}/" + element.id() + ".png";
                        nodeImage.classList.add("node-img-thumb");
                        popupAnchor.appendChild(nodeImage);
                        div.appendChild(popupAnchor);
                    }
                );

                // now add the popup code
                $(div).magnificPopup({
                    delegate: 'a',
                    type: 'image',
                    tLoading: 'Loading image #%curr%...',
                    mainClass: 'mfp-img-mobile',
                    gallery: {
                        enabled: true,
                        navigateByImgClick: true,
                        preload: [0,1] // Will preload 0 - before current, and 1 after the current image
                    },
                    image: {
                        tError: 'The image could not be loaded.',
                        titleSrc: function(item) {
                            return item.el.attr('title');
                        }
                    }
                });

                contentPanel.appendChild(div);
            }
        }

        /////////// end screenshot segment /////////////

        ////////// data segment   //////////////
        let paragraph = document.createElement("p");
        paragraph.classList.add("paragraph-data");
        let h3 = document.createElement("h3");
        h3.appendChild(document.createTextNode("Element data:"));
        paragraph.appendChild(h3);

        let filterBox = document.createElement("input");
        filterBox.type = "text";
        filterBox.id = "attribute-filter-box";
        filterBox.classList.add("attribute-filter");
        filterBox.placeholder = "Filter atribute values";
        filterBox.addEventListener("keyup", function(event) {
            filterDataFields(event.target.value);
        });
        paragraph.appendChild(filterBox);

        // add the data into a table
        let dataTable = document.createElement("table");
        dataTable.classList.add("table-data");
        dataTable.id = "attribute-data-table";
        let tableHeaderRow = document.createElement("tr");
        let th1 = document.createElement("th");
        th1.appendChild(document.createTextNode("Attribute name"));
        let th2 = document.createElement("th");
        th2.appendChild(document.createTextNode("Attribute value"));
        tableHeaderRow.appendChild(th1);
        tableHeaderRow.appendChild(th2);
        let thead = document.createElement("thead");
        thead.appendChild(tableHeaderRow);
        dataTable.appendChild(thead);
        let tbody = document.createElement("tbody");

        let data = targetNode.data();
        // we want to sort the data first
        const orderedData = {};
        Object.keys(data).sort().forEach(function (key) {
            orderedData[key] = data[key];
        });

        for (let item in orderedData) {
            if (data.hasOwnProperty(item)) {
                let tr = document.createElement("tr");
                let td1 = document.createElement("td");
                td1.appendChild(document.createTextNode(item));
                let td2 = document.createElement("td");
                td2.appendChild(document.createTextNode(data[item]));
                tr.appendChild(td1);
                tr.appendChild(td2);
                tbody.appendChild(tr);
            }
        }
        dataTable.appendChild(tbody);
        paragraph.appendChild(dataTable);
        contentPanel.appendChild(paragraph);
        ////////// end data segment //////////

        sidePanel.classList.add("cd-panel--is-visible");
        // console.log( 'tapped ' + node.id() );
    });

    // when edges get selected, we also open the side panel, but show just the close button and the data
    cy.on('tap', 'edge.ConcreteAction,edge.AbstractAction,edge.SequenceStep', function(evt) {
        let targetEdge = evt.target;
        let sidePanel = document.getElementsByClassName("cd-panel")[0];
        let contentPanel = document.getElementById("cd-content-panel");
        let contentPanelHeader = document.getElementById("content-panel-header");
        appStatus.graph.selectedEdge = targetEdge;

        // highlight the selected node
        cy.$('edge.selected-edge').removeClass('selected-edge');
        cy.$('node.selected-node').union(cy.$('node.isInitial')).removeClass('selected-node').removeClass('selected-initial-node');
        cy.$('node.connected-concrete-state-node').removeClass('connected-concrete-state-node');
        targetEdge.addClass('selected-edge');
        // if it's a sequence step, also highlight the corresponding concrete action
        if (targetEdge.hasClass('SequenceStep')) {
            targetEdge.source().outgoers('.ConcreteState').connectedEdges('.ConcreteAction').filter((element) =>
                element.data('uid') == targetEdge.data('concreteActionUid')).addClass('selected-edge');
        }

        // remove all the current child elements for both panel and panel header
        let child = contentPanel.lastChild;
        while (child) {
            contentPanel.removeChild(child);
            child = contentPanel.lastChild;
        }

        child = contentPanelHeader.lastChild;
        while (child) {
            contentPanelHeader.removeChild(child);
            child = contentPanelHeader.lastChild;
        }

        ///////////// add button section ///////////////////////////
        let closeButton = document.createElement("button");
        closeButton.id = "close-panel";
        closeButton.classList.add("skip");
        closeButton.appendChild(document.createTextNode("Close"));
        closeButton.addEventListener("click", function () {
            let cdPanel = document.getElementsByClassName("cd-panel")[0];
            cdPanel.classList.remove("cd-panel--is-visible");
            // remove the highlight from the selected node
            cy.$('edge.selected-edge').removeClass('selected-edge');
            // remove the edge selection
            appStatus.graph.selectedEdge = null;
        });
        contentPanelHeader.appendChild(closeButton);

        if (targetEdge.hasClass('ConcreteAction')) {
            // if it is a concrete action edge, we add a popup
            // first the content
            let popupContent = document.createElement("div");
            popupContent.id = 'popup-content';
            popupContent.classList.add('edge-popup', 'mfp-hide');
            // create divs for the source and target screenshots
            let sourceDiv = document.createElement("div");
            let sourceImg = document.createElement("img");
            sourceImg.src = "${contentFolder}/" + targetEdge.source().id() + ".png";
            sourceDiv.classList.add('screenshot');
            sourceDiv.appendChild(sourceImg);

            let targetDiv = document.createElement("div");
            let targetImg = document.createElement("img");
            targetImg.src = "${contentFolder}/" + targetEdge.target().id() + ".png";
            targetDiv.classList.add('screenshot');
            targetDiv.appendChild(targetImg);

            // add the edge text
            let descDiv = document.createElement("div");
            descDiv.appendChild(document.createTextNode(targetEdge.data('Desc')));
            descDiv.classList.add('action');

            // add the divs in order
            popupContent.appendChild(sourceDiv);
            popupContent.appendChild(descDiv);
            popupContent.appendChild(targetDiv);
            contentPanelHeader.appendChild(popupContent);

            // then a button to initialize it
            let popupButton = document.createElement("button");
            popupButton.id = "popup-edge";
            popupButton.classList.add("skip");
            popupButton.appendChild(document.createTextNode("Show"));
            contentPanelHeader.appendChild(popupButton);
            $('#popup-edge').magnificPopup({
                items: {
                    src: '#popup-content',
                    type: 'inline'
                }
            });
        }

        //////////// end button section ///////////////////////////

        ////////// data segment   //////////////
        let paragraph = document.createElement("p");
        paragraph.classList.add("paragraph-data");
        let h3 = document.createElement("h3");
        h3.appendChild(document.createTextNode("Element data:"));
        paragraph.appendChild(h3);

        let filterBox = document.createElement("input");
        filterBox.type = "text";
        filterBox.id = "attribute-filter-box";
        filterBox.classList.add("attribute-filter");
        filterBox.placeholder = "Filter atribute values";
        filterBox.addEventListener("keyup", function(event) {
            filterDataFields(event.target.value);
        });
        paragraph.appendChild(filterBox);

        // add the data into a table
        let dataTable = document.createElement("table");
        dataTable.classList.add("table-data");
        dataTable.id = "attribute-data-table";
        let tableHeaderRow = document.createElement("tr");
        let th1 = document.createElement("th");
        th1.appendChild(document.createTextNode("Attribute name"));
        let th2 = document.createElement("th");
        th2.appendChild(document.createTextNode("Attribute value"));
        tableHeaderRow.appendChild(th1);
        tableHeaderRow.appendChild(th2);
        let thead = document.createElement("thead");
        thead.appendChild(tableHeaderRow);
        dataTable.appendChild(thead);
        let tbody = document.createElement("tbody");

        let data = targetEdge.data();
        // we want to sort the data first
        const orderedData = {};
        Object.keys(data).sort().forEach(function (key) {
            orderedData[key] = data[key];
        });

        for (let item in orderedData) {
            if (data.hasOwnProperty(item)) {
                let tr = document.createElement("tr");
                let td1 = document.createElement("td");
                td1.appendChild(document.createTextNode(item));
                let td2 = document.createElement("td");
                td2.appendChild(document.createTextNode(data[item]));
                tr.appendChild(td1);
                tr.appendChild(td2);
                tbody.appendChild(tr);
            }
        }
        dataTable.appendChild(tbody);
        paragraph.appendChild(dataTable);
        contentPanel.appendChild(paragraph);
        ////////// end data segment //////////

        sidePanel.classList.add("cd-panel--is-visible");
    });

    let showAllButton = document.getElementById("show-all");
    showAllButton.addEventListener("click", function () {
        cy.$('.invisible').removeClass('invisible');
        cy.$('.dim').removeClass('dim');
        initLayers();
    });

    function initLayers() {
        appStatus.nrOfAbstractStates =  cy.$('node.AbstractState').size();
        appStatus.nrOfConcreteStates = cy.$('node.ConcreteState').size();
        appStatus.nrOfSequenceNodes = cy.$('node.SequenceNode').size();
        appStatus.nrOfAbstractActions = cy.$('edge.AbstractAction').size();
        appStatus.nrOfConcreteActions = cy.$('edge.ConcreteAction').size();
        appStatus.nrOfUnvisitedAbstractActions = cy.$('edge.UnvisitedAbstractAction').size();
        appStatus.abstractLayerPresent = appStatus.nrOfAbstractStates > 0;
        appStatus.concreteLayerPresent = appStatus.nrOfConcreteStates > 0;
        appStatus.sequenceLayerPresent = appStatus.nrOfSequenceNodes > 0;
        appStatus.nrOfLayersPresent = 0;
        console.log(appStatus);

        // ready several toggle buttons
        // abstract layer toggle
        let abstractLayerToggle = document.getElementById("toggle-abstract-layer");
        if (appStatus.abstractLayerPresent) {
            appStatus.nrOfLayersPresent++;
            abstractLayerToggle.checked = true;
            abstractLayerToggle.addEventListener("change", (e) => {
                if (abstractLayerToggle.checked) {
                    cy.$('node.AbstractState').union(cy.$('node.BlackHole')).union(cy.$('node.AbstractState').parent()).removeClass("invisible");
                }
                else {
                    cy.$('node.AbstractState').union(cy.$('node.BlackHole')).union(cy.$('node.AbstractState').parent()).addClass("invisible");
                }
            });
        }
        else {
            abstractLayerToggle.checked = false;
            abstractLayerToggle.disabled = true;
        }

        // concrete layer toggle
        let concreteLayerToggle = document.getElementById("toggle-concrete-layer");
        if (appStatus.concreteLayerPresent) {
            appStatus.nrOfLayersPresent++;
            concreteLayerToggle.checked = true;
            concreteLayerToggle.addEventListener("change", (e) => {
                if (concreteLayerToggle.checked) {
                    cy.$('node.ConcreteState').union(cy.$('node.ConcreteState').parent()).removeClass("invisible");
                }
                else {
                    cy.$('node.ConcreteState').union(cy.$('node.ConcreteState').parent()).addClass("invisible");
                }
            });
        }
        else {
            concreteLayerToggle.checked = false;
            concreteLayerToggle.disabled = true;
        }

        // sequence layer toggle
        let sequenceLayerToggle = document.getElementById("toggle-sequence-layer");
        if (appStatus.sequenceLayerPresent) {
            appStatus.nrOfLayersPresent++;
            sequenceLayerToggle.checked = true;
            sequenceLayerToggle.addEventListener("change", (e) => {
                if (sequenceLayerToggle.checked) {
                    cy.$('node.SequenceNode').union(cy.$('node.SequenceNode').parent()).removeClass("invisible");
                }
                else {
                    cy.$('node.SequenceNode').union(cy.$('node.SequenceNode').parent()).addClass("invisible");
                }
            });
        }
        else {
            sequenceLayerToggle.checked = false;
            sequenceLayerToggle.disabled = true;
        }

        // toggle for edges between the layers
        let interLayerEdgesToggle = document.getElementById("toggle-layer-transitions");
        if (appStatus.nrOfLayersPresent > 1 && appStatus.concreteLayerPresent) {
            interLayerEdgesToggle.checked = true;
            interLayerEdgesToggle.addEventListener("change", (e) => {
                if (interLayerEdgesToggle.checked) {
                    cy.$('edge.isAbstractedBy').union(cy.$('edge.Accessed')).removeClass("invisible");
                }
                else {
                    cy.$('edge.isAbstractedBy').union(cy.$('edge.Accessed')).addClass("invisible");
                }
            });
        }
        else {
            interLayerEdgesToggle.checked = false;
            interLayerEdgesToggle.disabled = true;
        }
    }

    function initStats() {
        let div = document.getElementById('stats-abstract-states');
        let text = document.createTextNode("Nr of abstract states: " + appStatus.nrOfAbstractStates);
        div.append(text);

        div = document.getElementById('stats-abstract-actions');
        text = document.createTextNode("Nr of abstract actions: " + appStatus.nrOfAbstractActions);
        div.append(text);

        div = document.getElementById('stats-concrete-states');
        text = document.createTextNode('Nr of concrete states:  ' + appStatus.nrOfConcreteStates);
        div.append(text);

        div = document.getElementById('stats-concrete-actions');
        text = document.createTextNode('Nr of concrete actions: ' + appStatus.nrOfConcreteActions);
        div.append(text);
    }

    function filterDataFields(filterValue) {
        let dataTable = document.getElementById("attribute-data-table");
        let dataTableBody = dataTable.getElementsByTagName("tbody")[0];
        let tableRows = dataTableBody.getElementsByTagName("tr");
        filterValue = filterValue.toLowerCase();
        for (i = 0; i < tableRows.length; i++) {
            if(filterValue == '') {
                tableRows[i].style.display = "";
            }
            else {
                // check if the attribute name matches
                let tableCell = tableRows[i].getElementsByTagName("td")[0];
                if (tableCell) {
                    let cellContent = tableCell.textContent || tableCell.innerText;
                    cellContent = cellContent.toLowerCase();
                    if (cellContent.indexOf(filterValue) > -1) {
                        tableRows[i].style.display = "";
                    }
                    else {
                        tableRows[i].style.display = "none";
                    }
                }
            }
        }
    }

    cy.ready(function (event) {
        initLayers();
        initStats();

        // highlight the leaves, which in this case will be the root of the widget tree
        cy.$(".Widget").leaves().addClass("leaves");
        cy.$(".Widget").forEach(
            (w) => w.data("customLabel", w.data("Role") + "-" + w.data("counter"))
        );

        // create custom labels for several classes
        // concrete state:
        cy.$(".ConcreteState").forEach(
            (w) => {
                w.data("customLabel", "CS-" + w.data("counter"));
                if (w.data('oracleVerdictCode') && ["2", "3"].includes(w.data('oracleVerdictCode'))) {
                    w.addClass('errorState');
                }
            }
        );

        // abstract state
        cy.$(".AbstractState").forEach(
            (w) => w.data("customLabel", "AS-" + w.data("counter"))
        );

        // sequence node
        cy.$(".SequenceNode").forEach(
            (w) => w.data("customLabel", "SN-" + w.data("counter"))
        );

        // test sequence
        cy.$(".TestSequence").forEach(
            (w) => w.data("customLabel", "TS-" + w.data("counter"))
        );

        // add a mouseover event to the concrete actions
        cy.$(".ConcreteAction").on('mouseover', function(event) {
            event.target.addClass("mouse-over-concrete-action");
        }).
        on('mouseout', function (event) {
            event.target.removeClass("mouse-over-concrete-action");
        });

        // add a highlight when mousing over nodes
        cy.$('node').on('mouseover', (event) => {
            // if there is currently a selected node or edge, we don't highlight, as there will already be a highlight in place
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.is(':parent')) return;
            event.target.addClass(event.target.hasClass('isInitial') ? 'selected-initial-node' : 'selected-node');

            // if the node is a sequence node, we want to also highlight the corresponding concrete action node
            if (event.target.hasClass('SequenceNode')) {
                cy.$(event.target).outgoers('.ConcreteState').addClass('selected-node');
            }
        });
        cy.$('node').on('mouseout', (event) => {
            // no action taken if there is a selected node or edge
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;
            if (event.target.is(':parent')) return;

            if (event.target.hasClass('selected-node')) {
                event.target.removeClass('selected-node');
            }
            if (event.target.hasClass('selected-initial-node')) {
                event.target.removeClass('selected-initial-node');
            }

            if (event.target.hasClass('SequenceNode')) {
                cy.$(event.target).outgoers('.ConcreteState').removeClass('selected-node');
            }
        });

        cy.$('edge').on('mouseover', (event) => {
            // if there is currently a selected node or edge, we don't highlight, as there will already be a highlight in place
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;

            if (event.target.hasClass('isAbstractedBy') || event.target.hasClass('Accessed')) return;
            event.target.addClass('selected-edge');

            // we also want to highlight the concrete action if
            if (event.target.hasClass('SequenceStep')) {
                event.target.source().outgoers('.ConcreteState').connectedEdges('.ConcreteAction').filter((element) =>
                    element.data('uid') == event.target.data('concreteActionUid')).addClass('selected-edge').addClass('mouse-over-concrete-action');
            }
        });
        cy.$('edge').on('mouseout', (event) => {
            // no action taken if there is a selected node or edge
            if ("selectedNode" in appStatus.graph && appStatus.graph.selectedNode != null) return;
            if ("selectedEdge" in appStatus.graph && appStatus.graph.selectedEdge != null) return;

            if (event.target.hasClass('isAbstractedBy') || event.target.hasClass('Accessed')) return;
            event.target.removeClass('selected-edge');

            if (event.target.hasClass('SequenceStep')) {
                event.target.source().outgoers('.ConcreteState').connectedEdges('.ConcreteAction').filter((element) =>
                    element.data('uid') == event.target.data('concreteActionUid')).removeClass('selected-edge').removeClass('mouse-over-concrete-action');
            }
        });

        // legend boxes
        let abstractStateLegendBox = document.getElementById("legend-abstract-state");
        abstractStateLegendBox.addEventListener('click', () => {
            let initialNodes = cy.$('node.isInitial');
            let abstractStateNodes = cy.$('node.AbstractState').difference(initialNodes);
            abstractStateNodes.addClass('selected-node-animated');
            initialNodes.addClass('selected-initial-node-animated');
            setTimeout(() => {
                abstractStateNodes.removeClass('selected-node-animated');
                initialNodes.removeClass('selected-initial-node-animated');
            }, 1000);
        });

        let concreteStateLegendBox = document.getElementById("legend-concrete-state");
        concreteStateLegendBox.addEventListener('click', () => {
            let  concreteStates = cy.$('node.ConcreteState');
            concreteStates.addClass('selected-node-animated');
            setTimeout(() => concreteStates.removeClass('selected-node-animated'), 1000);
        });

    });


</script>
</body>
</html>