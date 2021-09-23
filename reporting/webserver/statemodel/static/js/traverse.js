// Global variables
let stepCount = 0;
let nextState = "";
let stateCollection = [];
let stateviewer;

function traverse(fromState, toState) {
  // Set global variables
  canvas = document.getElementById("screen");
  stateviewer = new StateViewer(canvas);

  // Overwrite action drawer
  stateviewer.drawAction = drawAction;

  stateviewer.getJSON(
    `/api/concrete/traverse/${fromState}/${toState}`,
    parseTraverse
  );
}

// Overwrite action drawer
function drawAction(action) {
  if (action.toState != targetState) return;
  this.ctx.beginPath();
  this.ctx.lineWidth = "3";
  this.ctx.strokeStyle = "#9ed55e"; // Greenish

  this.ctx.rect(...action.shape);
  this.ctx.stroke();
}

function setStep() {
  stepCount++;
  if (stateCollection.length > stepCount) {
    targetState = stateCollection[stepCount].cid;
    setTimeout(drawNextState, 2500);
  } else {
    console.log("Finished");
  }
}

function drawNextState() {
  stateviewer.drawState(stateCollection[stepCount]);
  setStep();
}

function parseTraverse(states) {
  stateCollection = states;
  stateviewer.drawState(stateCollection[0]);

  stepCount = 0;
  setStep();
}
