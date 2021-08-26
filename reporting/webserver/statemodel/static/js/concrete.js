// Global variables
var ctx, canvas, actions;

function getJSON(url, callback) {
  const request = new XMLHttpRequest();
  request.onreadystatechange = function () {
    if (request.readyState == 4 && request.status == 200) {
      callback(JSON.parse(request.responseText));
    }
  };
  request.open("GET", url);
  request.send();
}

function drawAction(action) {
  ctx.beginPath();
  ctx.lineWidth = "2    ";
  ctx.strokeStyle = "blue";
  ctx.rect(...action.shape);
  ctx.stroke();
}

function getActionByCords(px, py) {
  if (actions == null) return [];
  let foundActions = [];
  for (const action of actions) {
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

function registerClickEvent() {
  canvas.addEventListener("click", (e) => {
    const rect = canvas.getBoundingClientRect();
    const px = e.clientX - rect.left;
    const py = e.clientY - rect.top;
    clickedActions = getActionByCords(px, py);
    if (clickedActions.length === 1) {
      document.location = `/concrete/state/${clickedActions[0].toState}`;
    } else if (clickedActions.length > 1) {
        const options = document.getElementById('options');
        document.getElementById('options-menu').style.display = 'block';
        options.innerHTML = '';
        for(clickedAction of clickedActions) {
            options.innerHTML += `<a href="/concrete/state/${clickedAction.toState}">${clickedAction.toState}</a> `
        }
    }
  });
}

function drawState(state) {
  actions = state.edges_out;

  ctx.clearRect(0, 0, canvas.width, canvas.height);

  let screenshot = new Image();
  screenshot.onload = () => {
    ctx.drawImage(screenshot, 0, 0);

    // Draw actions
    for (const action of state.edges_out) {
      drawAction(action);
    }
  };
  screenshot.src = state.screenshot;
}

function requestState(concreteId) {
  getJSON(`/api/concrete/state/${concreteId}`, drawState);
}
