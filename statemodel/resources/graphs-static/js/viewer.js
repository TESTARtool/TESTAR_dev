(() => {
  const MODEL_DIR = "./model";
  const elementsUrl = `${MODEL_DIR}/elements.json`;

  const infoContent = document.getElementById("info-content");
  const layoutControl = document.getElementById("layout-control");
  const showLabels = document.getElementById("show-labels");
  const fitButton = document.getElementById("fit-graph");

  if (window.__TESTAR_RUN__) {
    const run = window.__TESTAR_RUN__;
    document.getElementById("run-summary").textContent = `${run.runId} | Model ${run.modelIdentifier}. ${run.modelScope}`;
  }

  function setInfo(html) {
    infoContent.innerHTML = html;
  }

  function renderDataTable(data) {
    const keys = Object.keys(data).sort();
    if (keys.length === 0) return "<div class=\"muted\">No data fields.</div>";
    const rows = keys.map((k) => {
      const v = data[k];
      return `<tr><td>${escapeHtml(k)}</td><td>${escapeHtml(String(v))}</td></tr>`;
    }).join("");
    return `<table class="data-table"><thead><tr><th>Attribute</th><th>Value</th></tr></thead><tbody>${rows}</tbody></table>`;
  }

  function escapeHtml(str) {
    const s = String(str);
    return s
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  function updateStats(cy) {
    const set = (id, text) => {
      const el = document.getElementById(id);
      if (el) el.textContent = text;
    };
    set("stats-abstract-states", `Abstract states: ${cy.$("node.AbstractState").size()}`);
    set("stats-abstract-actions", `Abstract actions: ${cy.$("edge.AbstractAction").size()}`);
    set("stats-concrete-states", `Concrete states: ${cy.$("node.ConcreteState").size()}`);
    set("stats-concrete-actions", `Concrete actions: ${cy.$("edge.ConcreteAction").size()}`);
    set("stats-sequence-nodes", `Sequence nodes: ${cy.$("node.SequenceNode").size()}`);
    set("stats-sequence-steps", `Sequence steps: ${cy.$("edge.SequenceStep").size()}`);
  }

  function initCy(elements) {
    const cy = cytoscape({
      container: document.getElementById("cy"),
      // Viewer labels belong to presentation, not the embedded export source.
      elements: elements.map(element => ({...element, data: {...element.data}})),
      style: [
        {
          selector: "node",
          style: {
            "background-color": "#F6EFF7",
            "border-width": 1,
            "border-color": "#000000",
            "label": "",
            "color": "#5d574d",
            "font-size": "0.4em"
          }
        },
        {
          selector: "node[counter]",
          style: {
            "label": "data(counter)"
          }
        },
        {
          selector: ":parent",
          style: {
            "background-opacity": 0.9,
            "border-style": "dashed",
            "label": "data(id)"
          }
        },
        {
          selector: "edge",
          style: {
            "width": 1,
            "line-color": "#ccc",
            "target-arrow-color": "#ccc",
            "target-arrow-shape": "triangle",
            "curve-style": "unbundled-bezier",
            "text-rotation": "autorotate",
            "label": "",
            "color": "#5d574d",
            "font-size": "0.3em"
          }
        },
        {
          selector: "edge[counter]",
          style: {
            "label": "data(counter)"
          }
        },
        {
          selector: ".AbstractAction",
          style: {
            "line-color": "#1c9099",
            "target-arrow-color": "#1c9099"
          }
        },
        {
          selector: ".AbstractState",
          style: {
            "background-color": "#1c9099",
            "label": "data(customLabel)"
          }
        },
        {
          selector: ".isInitial",
          style: {
            "background-color": "#1c9099",
            "width": "60px",
            "height": "60px",
            "border-color": "#000000"
          }
        },
        {
          selector: ".ConcreteState",
          style: {
            "background-color": "#67A9CF",
            "background-image": (ele) => {
              const id = ele.data("id");
              if (window.__TESTAR_IMAGES__ && window.__TESTAR_IMAGES__[id]) {
                return window.__TESTAR_IMAGES__[id];
              }
              return undefined;
            },
            "background-fit": "contain",
            "label": "data(customLabel)"
          }
        },
        {
          selector: ".ConcreteAction",
          style: {
            "line-color": "#67A9CF",
            "target-arrow-color": "#67A9CF"
          }
        },
        {
          selector: ".isAbstractedBy",
          style: {
            "line-color": "#bdc9e1",
            "target-arrow-color": "#bdc9e1",
            "line-style": "dashed",
            "arrow-scale": 0.5,
            "width": 0.5
          }
        },
        {
          selector: ".SequenceStep",
          style: {
            "line-color": "#016450",
            "target-arrow-color": "#016450"
          }
        },
        {
          selector: ".SequenceNode",
          style: {
            "background-color": "#016450",
            "label": "data(customLabel)"
          }
        },
        {
          selector: ".TestSequence",
          style: {
            "background-color": "#014636",
            "label": "data(customLabel)"
          }
        },
        {
          selector: ".Accessed",
          style: {
            "line-color": "#d0d1e6",
            "target-arrow-color": "#d0d1e6",
            "line-style": "dashed",
            "arrow-scale": 0.5,
            "width": 0.5
          }
        },
        {
          selector: ".Widget",
          style: {
            "background-color": "#e7298a",
            "background-opacity": 0.8,
            "label": "data(customLabel)"
          }
        },
        {
          selector: ".isChildOf",
          style: {
            "line-color": "#df65b0",
            "target-arrow-color": "#df65b0"
          }
        },
        {
          selector: ".BlackHole",
          style: {
            "background-color": "#000000",
            "label": "data(id)"
          }
        },
        {
          selector: ".UnvisitedAbstractAction",
          style: {
            "line-color": "#1c9099",
            "target-arrow-color": "#1c9099",
            "line-style": "dashed",
            "width": 1
          }
        },
        {
          selector: ".no-label",
          style: {
            "label": ""
          }
        },
        {
          selector: ".errorState",
          style: {
            "border-color": "#FF0000",
            "line-color": "#FF0000"
          }
        }
      ],
      layout: {
        name: "grid"
      },
      wheelSensitivity: 0.5
    });

    cy.ready(() => {
      cy.$(".Widget").forEach((w) => w.data("customLabel", `${w.data("Role") || "W"}-${w.data("counter") || ""}`));

      cy.$(".ConcreteState").forEach((w) => {
        const c = w.data("counter");
        if (c != null) w.data("customLabel", `CS-${c}`);
        const verdict = w.data("oracleVerdictCode");
        if (verdict && ["2", "3"].includes(String(verdict))) {
          w.addClass("errorState");
        }
      });

      cy.$(".AbstractState").forEach((w) => {
        const c = w.data("counter");
        if (c != null) w.data("customLabel", `AS-${c}`);
      });

      cy.$(".SequenceNode").forEach((w) => {
        const c = w.data("counter");
        if (c != null) w.data("customLabel", `SN-${c}`);
      });

      cy.$(".TestSequence").forEach((w) => {
        const c = w.data("counter");
        if (c != null) w.data("customLabel", `TS-${c}`);
      });

      updateStats(cy);
      cy.fit(undefined, 30);
    });

    layoutControl.addEventListener("change", () => {
      const selectedLayout = layoutControl.value;
      cy.layout({
        name: selectedLayout,
        animate: "end",
        animationEasing: "ease-out",
        animationDuration: 800
      }).run();
    });

    showLabels.addEventListener("change", () => {
      if (showLabels.checked) {
        cy.$(".no-label").removeClass("no-label");
      } else {
        cy.$("node").addClass("no-label");
        cy.$("edge").addClass("no-label");
      }
    });

    fitButton.addEventListener("click", () => {
      cy.fit(undefined, 30);
    });

    cy.on("tap", "node, edge", (evt) => {
      const el = evt.target;
      const isNode = el.isNode();
      const kind = isNode ? "Node" : "Edge";
      const classes = el.classes();
      const data = el.data() || {};
      const id = data.id || el.id();

      let imageHtml = "";
      let imageSrc = "";
      let treeHtml = "";
      const tree = (window.__TESTAR_WIDGET_TREES__ || {})[id];
      if (el.hasClass("ConcreteState")) {
        treeHtml = tree?.length
          ? `<div class="info-section"><a class="inspect-widget-tree" href="widget-tree.html?state=${escapeHtml(encodeURIComponent(id))}" target="_blank" rel="noopener noreferrer">Inspect Widget Tree</a></div>`
          : `<p class="muted">${window.__TESTAR_RUN__?.widgetTreesCaptured === false
            ? "Widget trees were not captured in this snapshot." : "Widget tree unavailable in this snapshot."}</p>`;
      }
      if (el.hasClass("ConcreteState") || el.hasClass("ConcreteAction")) {
        imageSrc = (window.__TESTAR_IMAGES__ && window.__TESTAR_IMAGES__[id]) || "";
        imageHtml = imageSrc
          ? `<div class="info-section"><img class="info-image" src="${imageSrc}" alt="Screenshot ${escapeHtml(id)}" /></div>`
          : `<p class="muted">${isNode ? "State" : "Action"} screenshot unavailable.</p>`;
      }

      const content = [
        `<div class="info-section info-card">`,
        `  <div class="info-row"><span class="info-badge">${escapeHtml(kind)}</span><span class="info-value">${escapeHtml(id)}</span></div>`,
        `  <div class="info-row"><span class="info-label">Classes</span><span class="info-value">${escapeHtml(classes)}</span></div>`,
        `</div>`,
        imageHtml,
        treeHtml,
        `<div class="info-section">${renderDataTable(data)}</div>`
      ].join("");
      setInfo(content);

      if (imageSrc) {
        const img = infoContent.querySelector(".info-image");
        if (img) {
          img.addEventListener("click", () => showZoom(imageSrc));
        }
      }
    });

    return cy;
  }

  function ensureZoomOverlay() {
    let overlay = document.getElementById("zoom-overlay");
    if (overlay) return overlay;
    overlay = document.createElement("div");
    overlay.id = "zoom-overlay";
    overlay.className = "zoom-overlay";
    overlay.innerHTML = "<img alt=\"Zoomed screenshot\" />";
    overlay.addEventListener("click", () => {
      overlay.style.display = "none";
    });
    document.body.appendChild(overlay);
    return overlay;
  }

  function showZoom(src) {
    const overlay = ensureZoomOverlay();
    const img = overlay.querySelector("img");
    img.src = src;
    overlay.style.display = "flex";
  }

  if (window.__TESTAR_ELEMENTS__) {
    initCy(window.__TESTAR_ELEMENTS__);
  } else {
    fetch(elementsUrl)
      .then((response) => {
        if (!response.ok) throw new Error(`Failed to load ${elementsUrl}`);
        return response.json();
      })
      .then((elements) => {
        initCy(elements);
      })
      .catch((err) => {
        setInfo(`<div class="muted">Error loading model: ${escapeHtml(err.message)}</div>`);
      });
  }
})();
