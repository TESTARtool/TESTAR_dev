/*
 * Inject and run this code before anything from the page is loaded
 * If this were to be done via a <script> it might be after other
 * scripts or inline declarations
 */
var actualCode = '(' + function () {
    EventTarget.prototype._addEventListener = EventTarget.prototype.addEventListener;
    EventTarget.prototype.addEventListener = function (a, b, c) {
        if (c === undefined)
            c = false;
        this._addEventListener(a, b, c);
        if (!this.eventListenerList)
            this.eventListenerList = {};
        if (!this.eventListenerList[a])
            this.eventListenerList[a] = [];
        this.eventListenerList[a].push({listener: b, useCapture: c});
    };

    EventTarget.prototype.getEventListeners = function (a) {
        if (!this.eventListenerList)
            this.eventListenerList = {};
        if (a === undefined)
            return this.eventListenerList;
        return this.eventListenerList[a];
    };
    EventTarget.prototype.clearEventListeners = function (a) {
        if (!this.eventListenerList)
            this.eventListenerList = {};
        if (a === undefined) {
            for (var x in (this.getEventListeners())) this.clearEventListeners(x);
            return;
        }
        var el = this.getEventListeners(a);
        if (el === undefined)
            return;
        for (var i = el.length - 1; i >= 0; --i) {
            var ev = el[i];
            this.removeEventListener(a, ev.listener, ev.useCapture);
        }
    };

    EventTarget.prototype._removeEventListener = EventTarget.prototype.removeEventListener;
    EventTarget.prototype.removeEventListener = function (a, b, c) {
        if (c === undefined)
            c = false;
        this._removeEventListener(a, b, c);
        if (!this.eventListenerList)
            this.eventListenerList = {};
        if (!this.eventListenerList[a])
            this.eventListenerList[a] = [];

        // Find the event in the list
        for (var i = 0; i < this.eventListenerList[a].length; i++) {
            if (this.eventListenerList[a][i].listener === b &&
                this.eventListenerList[a][i].useCapture === c) {
                this.eventListenerList[a].splice(i, 1);
                break;
            }
        }
        if (this.eventListenerList[a].length === 0)
            delete this.eventListenerList[a];
    };

    // Disallow browser dialogs
    window.alert = function () {};
    window.confirm = function () {};
    window.print = function () {};
    navigator.geolocation.getCurrentPosition = function () {};
} + ')();';

// Inject into the document
var script = document.createElement('script');
script.textContent = actualCode;
(document.head || document.documentElement).appendChild(script);
script.remove();

// https://stackoverflow.com/questions/9515704/
// https://stackoverflow.com/questions/446892/
