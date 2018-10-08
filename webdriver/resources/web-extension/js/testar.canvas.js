function addCanvasTestar() {
    // Only add when needed
    if (typeof testar_canvas === 'object') {
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

    return typeof testar_canvas;
}

function canvasDimensionsTestar() {
    return [window.screenX,
            window.screenY + window.outerHeight - window.innerHeight,
            document.documentElement.clientWidth,
        document.documentElement.clientHeight,
        window.innerWidth,
        window.innerHeight
    ];
}

function resizeCanvasTestar() {
    testar_canvas.width = document.documentElement.clientWidth;
    testar_canvas.height = document.documentElement.clientHeight;
    var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
    testar_canvas.style.left = scrollLeft + "px";
    var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    testar_canvas.style.top = scrollTop + "px";
}

function clearCanvasTestar(args) {
    testarCtx.clearRect(args[0], args[1], args[2], args[3]);
}

function drawTextTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.font = args[1] + 'px ' + args[2];
    testarCtx.fillText(args[3], args[4], args[5]);
}

function drawLineTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.beginPath();
    testarCtx.moveTo(args[2], args[3]);
    testarCtx.lineTo(args[4], args[5]);
    testarCtx.stroke();
}

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
    }
    else if (args[2] === "stroke") {
        testarCtx.stroke();
    }
}

function drawEllipseTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.strokeStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.beginPath();
    testarCtx.ellipse(args[3], args[4], args[5], args[6], 0, 0, 2 * Math.PI);
    if (args[2] === "fill") {
        testarCtx.fill();
    }
    else if (args[2] === "stroke") {
        testarCtx.stroke();
    }
}

function drawRectTestar(args) {
    testarCtx.fillStyle = args[0];
    testarCtx.lineWidth = args[1];
    testarCtx.strokeStyle = args[0];
    if (args[2] === 'fillRect') {
        testarCtx.fillRect(args[3], args[4], args[5], args[6]);
    }
    else if (args[2] === 'strokeRect') {
        testarCtx.strokeRect(args[3], args[4], args[5], args[6]);
    }
}
