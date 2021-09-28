class StateViewer {
  ctx = undefined;
  canvas = undefined;
  actions = undefined;

  constructor (canvas) {
    this.canvas = canvas;
    this.ctx = this.canvas.getContext("2d");
  }

  getJSON(url, callback) {
    const request = new XMLHttpRequest();
    request.onreadystatechange = () => {
      if (request.readyState == 4 && request.status == 200) {
        callback(JSON.parse(request.responseText));
      }
    };
    request.open("GET", url);
    request.send();
  }

  drawAction(action) {
    this.ctx.beginPath();
    this.ctx.lineWidth = "2";
    this.ctx.strokeStyle = action["toOracle"] ? "#D6965E" : "#5E9ED5";
    this.ctx.rect(...action.shape);
    this.ctx.stroke();
  }

  /**
   * Find all actions which intersect with the given cords.
   * @param {int} px Point x
   * @param {int} py Point y
   * @returns A list with found objects
   */
  getActionByCords(px, py) {

    // Return an empty array when there are no actions on the current cords
    if (this.actions == null) return [];
    
    let foundActions = [];
    for (const action of this.actions) {
      const x1 = action.shape[0];
      const y1 = action.shape[1];
      const x2 = x1 + action.shape[2];
      const y2 = y1 + action.shape[3];
      if (px > x1 && px < x2 && py > y1 && py < y2) {
        foundActions.push(action);
      }
    }
    return foundActions;
  }

  /**
   * Create link to redirect to the next state
   * @param {action} action
   * @returns Option link html object
   */
  actionOptionCode(action) {
    const optionClass = action["toOracle"] ? "oracle-link" : "no-oracle-link";
    const url = `/concrete/${action.toState}`;
    const name = action.toState;
    return `<a href="${url}" class="${optionClass}">${name}</a> `;
  }

  /**
   * Register event listener for the canvas
   */
  registerClickEvent() {
    const rect = this.canvas.getBoundingClientRect();
    this.canvas.addEventListener("click", (e) => {

      // Retrieve click locations
      const px = e.clientX - rect.left;
      const py = e.clientY - rect.top;

      const clickedActions = this.getActionByCords(px, py);

      // Handle click event when there is one action
      if (clickedActions.length === 1) {
        document.location = `/concrete/${clickedActions[0].toState}`;

      // Handle click event when there are more than one actions.
      } else if (clickedActions.length > 1) {
        const options = document.getElementById("options");
        document.getElementById("options-menu").style.display = "block";
        options.innerHTML = "";
        for (const clickedAction of clickedActions) {
          options.innerHTML += this.actionOptionCode(clickedAction);
        }
      }
    });
  }

  drawState(state) {
    this.actions = state.edges_out;
    this.canvas.height = state.shape[3];
    this.canvas.width = state.shape[2];
    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

    let screenshot = new Image();
    screenshot.onload = () => {
      // Draw screenshot.
      this.ctx.drawImage(screenshot, 0, 0);

      // Draw actions ontop of the screenshot.
      for (const action of state.edges_out) {
        this.drawAction(action);
      }
    };
    screenshot.src = state.screenshot;
  }

  requestState(concreteId) {
    this.getJSON(`/api/concrete/${concreteId}`, state => {this.drawState(state)});
  }
}
