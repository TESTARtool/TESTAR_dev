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
    <script src="js/jquery-3.2.1.slim.min.js"></script>
    <script src="js/jquery.magnific-popup.min.js"></script>
    <link rel="stylesheet" href="css/magnific-popup.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="topbar">
    <img src="img/testar-logo.png" class="logo">
    <div class="layout">
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
        </select></label></div>
        <div>
            <label for="show-labels"><input type="checkbox" id="show-labels" checked> Show node labels</label>
        </div>
    </div>
</div>

<div class="viewpane" id="cy">
</div>

<div class="cd-panel cd-panel--from-right js-cd-panel-main">
    <div class="cd-panel__container">
        <div class="panel-header">
            <button id="close-panel" type="button">Close</button>
        </div>
        <div class="cd-panel__content" id="cd-content-panel">

        </div> <!-- cd-panel__content -->
    </div> <!-- cd-panel__container -->
</div>


<script>

    let cy = cytoscape({
        container: document.getElementById("cy"),

        elements: fetch("${graphContentFile}").then(function(response) {
            return response.json();
        })


        ,

        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': '#ad1a66',
                    'label': 'data(id)'
                }
            },

            {
                selector: ':parent',
                style: {
                    'background-opacity': 0.2
                }
            },

            {
                selector: 'edge',
                style: {
                    'width': 2,
                    'line-color': '#ccc',
                    'target-arrow-color': '#ccc',
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'unbundled-bezier'
                }
            },
            {
                selector: '.AbstractAction',
                style: {
                    'line-color': '#ad1a66',
                    'target-arrow-color': '#ad1a66'
                }

            },

            {
                selector: '.AbstractState',
                style: {
                    'background-color': '#ad1a66'
                }
            },

            {
                selector: '.isInitial',
                style: {
                    'background-color': '#ad1a66',
                    'width': '60px',
                    'height': '60px',
                    'border-width': '2px',
                    'border-color': '#000000'
                }
            },

            {
                selector: '.ConcreteState',
                style: {
                    'background-color': '#ffa44b',
                    'background-image': function (ele) {
                        return "${modelIdentifier}/" + ele.data('id') + ".png"
                    },
                    'background-fit': 'contain'
                }
            },

            {
                selector: '.ConcreteAction',
                style: {
                    'line-color': '#ffa44b',
                    'target-arrow-color': '#ffa44b'
                }
            },

            {
                selector: '.isAbstractedBy',
                style: {
                    'line-color': '#bdbf8f',
                    'target-arrow-color': '#bdbf8f'
                }
            },

            {
                selector: '.SequenceStep',
                style: {
                    'line-color': '#ff7492'
                }
            },

            {
                selector: '.SequenceNode',
                style: {
                    'background-color': '#ff7492'
                }
            },

            {
                selector: '.FirstNode',
                style: {
                    'line-color': '#ffe192'
                }
            },

            {
                selector: '.TestSequence',
                style: {
                    'background-color': '#ffe192'
                }
            },


            {
                selector: '.BlackHole',
                style: {
                    'background-color': '#000000',
                    'label': 'data(id)',
                    'background-image' : "img/blackhole-bg.jpg",
                    'background-fit': 'contain'
                }
            },

            {
                selector: '.UnvisitedAbstractAction',
                style: {
                    'line-color': "#999989",
                    'target-arrow-color': "#999989",
                    'width': 1
                }
            },

            {
                selector: '.no-label',
                style: {
                    'label': ''
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

    // document.getElementById("myBtn").addEventListener("mousedown", function () {
    //     let cdPanel = document.getElementsByClassName("cd-panel")[0];
    //     cdPanel.classList.add("cd-panel--is-visible");
    // });

    document.getElementById("close-panel").addEventListener("click", function () {
        let cdPanel = document.getElementsByClassName("cd-panel")[0];
        cdPanel.classList.remove("cd-panel--is-visible");
    });

    let showLabels = document.getElementById("show-labels");
    showLabels.addEventListener("change", function () {
        if (showLabels.checked) {
            cy.$('.no-label').removeClass('no-label');
        }
        else {
            cy.$('node').addClass('no-label');
        }
    });

    // when nodes get clicked, we need to open the side bar
    cy.on('tap', 'node', function(evt){
        let targetNode = evt.target;
        let sidePanel = document.getElementsByClassName("cd-panel")[0];
        let contentPanel = document.getElementById("cd-content-panel");
        let child = contentPanel.lastChild;
        while (child) {
            contentPanel.removeChild(child);
            child = contentPanel.lastChild;
        }
        // create a popup anchor
        let popupAnchor = document.createElement("a");
        popupAnchor.href = "${modelIdentifier}/" + targetNode.id() + ".png";
        $(popupAnchor).magnificPopup(
            {type: "image"}
        );

        if (targetNode.hasClass("ConcreteState")) { // add the screenshot full image
            let nodeImage = document.createElement("img");
            nodeImage.alt = "Image for node " + targetNode.id();
            nodeImage.src = "${modelIdentifier}/" + targetNode.id() + ".png";
            nodeImage.classList.add("node-img-full");
            popupAnchor.appendChild(nodeImage);
            contentPanel.appendChild(popupAnchor);
        }

        let data = targetNode.data();
        for (let item in data) {
            if (data.hasOwnProperty(item)) {

                console.log(item);
                let nodeContent = document.createElement("div");
                let textContent = document.createTextNode(item + " : " + data[item]);
                nodeContent.appendChild(textContent);
                contentPanel.appendChild(nodeContent);
            }
        }

        sidePanel.classList.add("cd-panel--is-visible");
        // console.log( 'tapped ' + node.id() );
        console.log(targetNode.data());
    });

</script>
</body>
</html>