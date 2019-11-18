/*
 * Adds the TESTAR canvas to the page under test, if needed
 * The canvas is resized to the viewport dimensions
 *
 * @return {type} the type
 */
function addCanvasTestar() {
    // Only add when needed
    if (typeof testar_canvas === 'object') {
        ensureCanvasOnTop();
        return typeof testar_canvas;
    }

    // Create canvas, get reference to context
    testar_canvas = document.createElement('canvas');
    testar_canvas.id = 'testar_canvas';
    document.body.appendChild(testar_canvas);
    testarCtx = testar_canvas.getContext('2d');

    // Set canvas to complete viewport
    resizeCanvasTestar();

    // Make sure canvas keeps size of viewport on resize or scroll
    window.addEventListener('resize', resizeCanvasTestar, true);
    window.addEventListener('scroll', resizeCanvasTestar, true);

    ensureCanvasOnTop();
    return typeof testar_canvas;
}

/*
 * This ensures the canvas is always on top, as some scripts
 * will try to get their element the highest z-index
 */
function ensureCanvasOnTop() {
    var lengths = Array.from(document.querySelectorAll('body *'))
        .map(a => parseFloat(window.getComputedStyle(a).zIndex))
        .filter(a => !isNaN(a));
    var maxIndex = Math.max.apply(null, lengths);
    if (testar_canvas.style.zIndex < maxIndex) {
        testar_canvas.style.zIndex = maxIndex + 1;
    }
}

/*
 * Return the width/height dimensions of the page
 *
 * @return {Array} the array with the dimensions
 */
function canvasDimensionsTestar() {
    return [window.screenX,
        window.screenY + window.outerHeight - window.innerHeight,
        document.documentElement.clientWidth,
        document.documentElement.clientHeight,
        window.innerWidth,
        window.innerHeight
    ];
}

/*
 * Resizes the canvas to the same dimensions as the viewport
 */
function resizeCanvasTestar() {
    testar_canvas.width = document.documentElement.clientWidth;
    testar_canvas.height = document.documentElement.clientHeight;
    var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
    testar_canvas.style.left = scrollLeft + "px";
    var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    testar_canvas.style.top = scrollTop + "px";
}

/*
 * Clears a region from the canvas
 * @param {Array} args : x, y, width, height
 */
function clearCanvasTestar(args) {
    testarCtx.clearRect(args[0], args[1], args[2], args[3]);
}

/*
 * Write text on the Canvas with font and color
 * @param {Array} args : color, fontSize, font, text, x, y
 */
function drawTextTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.font = args[1] + 'px ' + args[2];
    testarCtx.fillText(args[3], args[4], args[5]);
}

/*
 * Draw a line on the canvas from (x1, y1) to (x2, y2)
 * @param {Array} args : color, width, x1, y1, x2, y2
 */
function drawLineTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.beginPath();
    testarCtx.moveTo(args[2], args[3]);
    testarCtx.lineTo(args[4], args[5]);
    testarCtx.stroke();
}

/*
 * Draw an open or filled triangle from (x1, y1) to (x2, y2) to (x3, y3)
 * @param {Array} args : color, width, fill, x1, y1, x2, y2, x3, y3
 */
function drawTriangleTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.strokeStyle = args[1];
    testarCtx.lineWidth = args[1];
    testarCtx.beginPath();
    testarCtx.moveTo(args[3], args[4]);
    testarCtx.lineTo(args[5], args[6]);
    testarCtx.lineTo(args[7], args[8]);
    testarCtx.lineTo(args[3], args[4]);
    testarCtx.lineTo(args[5], args[6]);

    if (args[2] === "fill") {
        testarCtx.fill();
    } else if (args[2] === "stroke") {
        testarCtx.stroke();
    }
}

/*
 * Draw an open or closed ellipse
 * @param {Array} args : color, width, fill, x, y, radiusX, radiusY
 */
function drawEllipseTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.strokeStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.beginPath();
    testarCtx.ellipse(args[3], args[4], args[5], args[6], 0, 0, 2 * Math.PI);

    if (args[2] === "fill") {
        testarCtx.fill();
    } else if (args[2] === "stroke") {
        testarCtx.stroke();
    }
}

/*
 * Draw a open or closed rectangle
 * @param {Array} args : color, width, fill, x, y, width, height
 */
function drawRectTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.strokeStyle = args[0];

    if (args[2] === 'fillRect') {
        testarCtx.fillRect(args[3], args[4], args[5], args[6]);
    } else if (args[2] === 'strokeRect') {
        testarCtx.strokeRect(args[3], args[4], args[5], args[6]);
    }
}
