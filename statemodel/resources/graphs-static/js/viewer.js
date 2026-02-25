(() => {
  const MODEL_DIR = "./model";
  const elementsUrl = `${MODEL_DIR}/elements.json`;

  const infoContent = document.getElementById("info-content");
  const layoutControl = document.getElementById("layout-control");
  const showLabels = document.getElementById("show-labels");
  const fitButton = document.getElementById("fit-graph");
  const multiQueryRun = document.getElementById("multi-query-run");
  const multiQueryReset = document.getElementById("multi-query-reset");
  const multiQueryBox = document.getElementById("multiQueryJson");
  const multiQueryError = document.getElementById("multiQueryError");

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
      elements,
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
              return `${MODEL_DIR}/${id}.png`;
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
      if (isNode && el.hasClass("ConcreteState")) {
        imageSrc = (window.__TESTAR_IMAGES__ && window.__TESTAR_IMAGES__[id])
          ? window.__TESTAR_IMAGES__[id]
          : `${MODEL_DIR}/${id}.png`;
        imageHtml = `<div class="info-section"><img class="info-image" data-zoom-src="${imageSrc}" src="${imageSrc}" alt="Screenshot ${escapeHtml(id)}" /></div>`;
      }

      const content = [
        `<div class="info-section info-card">`,
        `  <div class="info-row"><span class="info-badge">${escapeHtml(kind)}</span><span class="info-value">${escapeHtml(id)}</span></div>`,
        `  <div class="info-row"><span class="info-label">Classes</span><span class="info-value">${escapeHtml(classes)}</span></div>`,
        `</div>`,
        imageHtml,
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

  function storeOriginalColorOnce(ele) {
    if (ele.isNode()) {
      if (ele.data("_multiOrigBg") == null) {
        ele.data("_multiOrigBg", ele.style("background-color"));
      }
    } else if (ele.isEdge()) {
      if (ele.data("_multiOrigLine") == null) {
        ele.data("_multiOrigLine", ele.style("line-color"));
      }
      if (ele.data("_multiOrigArrow") == null) {
        ele.data("_multiOrigArrow", ele.style("target-arrow-color"));
      }
    }
  }

  function highlightElementMulti(ele) {
    storeOriginalColorOnce(ele);
    if (ele.isNode()) {
      ele.style("background-color", "yellow");
    } else if (ele.isEdge()) {
      ele.style("line-color", "yellow");
      ele.style("target-arrow-color", "yellow");
    }
  }

  function matchesAny(fieldValue, wantedValues) {
    if (fieldValue === undefined || fieldValue === null) return false;
    const hay = String(fieldValue);
    for (const w of wantedValues) {
      if (w == null) continue;
      const needle = String(w);
      if (needle.length === 0) continue;
      if (hay.includes(needle)) return true;
    }
    return false;
  }

  function runMultiQuery(cy) {
    if (multiQueryError) multiQueryError.textContent = "";
    const raw = (multiQueryBox && multiQueryBox.value || "").trim();
    if (!raw) {
      if (multiQueryError) multiQueryError.textContent = "Paste a JSON object first.";
      return;
    }

    let queryObj;
    try {
      queryObj = JSON.parse(raw);
    } catch (e) {
      if (multiQueryError) multiQueryError.textContent = "Invalid JSON: " + e.message;
      return;
    }

    if (!queryObj || typeof queryObj !== "object" || Array.isArray(queryObj)) {
      if (multiQueryError) multiQueryError.textContent = "JSON must be an object like { \"Key\": [\"v1\", \"v2\"] }.";
      return;
    }

    const queries = [];
    for (const [key, arr] of Object.entries(queryObj)) {
      if (!Array.isArray(arr)) {
        if (multiQueryError) multiQueryError.textContent = `Value for "${key}" must be an array of strings.`;
        return;
      }
      const values = arr.map(x => String(x).trim()).filter(Boolean);
      if (values.length > 0) {
        queries.push({ key, values });
      }
    }

    if (queries.length === 0) {
      if (multiQueryError) multiQueryError.textContent = "No non-empty arrays found.";
      return;
    }

    const elements = cy.$("node, edge");
    let hitCount = 0;
    elements.forEach(ele => {
      for (const q of queries) {
        const fieldVal = ele.data(q.key);
        if (matchesAny(fieldVal, q.values)) {
          highlightElementMulti(ele);
          hitCount++;
          break;
        }
      }
    });

    if (multiQueryError) multiQueryError.textContent = `Highlighted ${hitCount} element(s).`;
  }

  function resetMultiQuery(cy) {
    cy.elements().forEach(ele => {
      if (ele.isNode()) {
        const orig = ele.data("_multiOrigBg");
        if (orig != null) {
          ele.style("background-color", orig);
          ele.removeData("_multiOrigBg");
        }
      } else if (ele.isEdge()) {
        const origLine = ele.data("_multiOrigLine");
        const origArrow = ele.data("_multiOrigArrow");
        if (origLine != null) {
          ele.style("line-color", origLine);
          ele.removeData("_multiOrigLine");
        }
        if (origArrow != null) {
          ele.style("target-arrow-color", origArrow);
          ele.removeData("_multiOrigArrow");
        }
      }
    });
    if (multiQueryError) multiQueryError.textContent = "";
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
    const cy = initCy(window.__TESTAR_ELEMENTS__);
    if (multiQueryRun) multiQueryRun.addEventListener("click", () => runMultiQuery(cy));
    if (multiQueryReset) multiQueryReset.addEventListener("click", () => resetMultiQuery(cy));
  } else {
    fetch(elementsUrl)
      .then((response) => {
        if (!response.ok) throw new Error(`Failed to load ${elementsUrl}`);
        return response.json();
      })
      .then((elements) => {
        const cy = initCy(elements);
        if (multiQueryRun) multiQueryRun.addEventListener("click", () => runMultiQuery(cy));
        if (multiQueryReset) multiQueryReset.addEventListener("click", () => resetMultiQuery(cy));
      })
      .catch((err) => {
        setInfo(`<div class="muted">Error loading model: ${escapeHtml(err.message)}</div>`);
      });
  }
})();
