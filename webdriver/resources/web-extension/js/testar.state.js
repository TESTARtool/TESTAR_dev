var TestarState = (function () {

/*
 * Keep a map with all labels client-side
 */
var labelMap;

/*
 * Get the widget tree (Chrome, Firefox) or flattened tree (Edge)
 * @param {Array} array of tags that can be skipped, like <style>, <script> etc.
 * @return {(Object | Array)}
 */
var getStateTreeTestar = function (ignoredTags) {
    var body = document.body;
    var bodyWrapped = wrapElementTestar(body, 0, 0);
    bodyWrapped['documentHasFocus'] = document.hasFocus();
    bodyWrapped['documentTitle'] = document.title;
    bodyWrapped['largestContentfulPaint'] = latestLCP;

    // Find all labels on the page
    getLabelMapTestar();

    // For Edge we return a flattened tree (as array), see comment in WdStateFetcher
    if (window.navigator.userAgent.indexOf("Edge") > -1) {
        var treeArray = [];
        traverseElementArrayTestar(treeArray, bodyWrapped, body, -1, ignoredTags);
        return treeArray;
    }
    else {
        traverseElementTestar(bodyWrapped, body, ignoredTags);
        return bodyWrapped;
    }
};

/*
 * Traverse the children from the parent element
 * @param {object} parentWrapped, the (wrapped) parent object
 * @param {node} rootElement, the body element
 * @param {object} ignoredTags, list of tags to skip, <style>, <script> etc.
 */
function traverseElementTestar(parentWrapped, rootElement, ignoredTags) {
    var childNodes = getChildNodesTestar(parentWrapped);
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            parentWrapped.textContent = parentWrapped.textContent.trim();
            continue;
        }
        if (childElement.nodeType !== 1 ||
            ignoredTags.includes(childElement.nodeName.toLowerCase())) {
            continue;
        }

        var childWrapped = wrapElementTestar(childElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
        traverseElementTestar(childWrapped, rootElement, ignoredTags);
        parentWrapped.wrappedChildren.push(childWrapped);
    }
	
	// Descend through Shadow DOM Web Elements
	if(parentWrapped.element.shadowRoot !== null){
		var shadowNodes = parentWrapped.element.shadowRoot.childNodes;
		for (var i = 0; i < shadowNodes.length; i++) {
			var childShadowElement = shadowNodes[i];
			
			// Filter ignored tags or non-element nodes
			if (childShadowElement.nodeType === 3) {
				parentWrapped.textContent += childShadowElement.textContent;
				parentWrapped.textContent = parentWrapped.textContent.trim();
				continue;
			}
			if (childShadowElement.nodeType !== 1 ||
				ignoredTags.includes(childShadowElement.nodeName.toLowerCase())) {
				continue;
			}
			
			var childShadowWrapped = wrapElementTestar(childShadowElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
			traverseElementTestar(childShadowWrapped, rootElement, ignoredTags);
			parentWrapped.wrappedChildren.push(childShadowWrapped);
		}
	}
	
    if (shouldDeleteElementForSerialization(parentWrapped.element)) {
        delete parentWrapped.element;
    }
}

function shouldDeleteElementForSerialization(element) {
    try {
        const frameElement = element.ownerDocument.defaultView?.frameElement;
        return frameElement && frameElement !== element;
    } catch (e) {
        return true;
    }
}

/*
 * Flattened version of traverseElementTestar for Edge
 * @param {Array} treeArray, array of wrapped elements
 * @param {object} parentWrapped, the (wrapped) parent object
 * @param {node} rootElement, the body element
 * @param {integer} parentId, index of the parent in the flattened array
 * @param {object} ignoredTags, array of tags to skip, <style>, <script> etc.
 */
function traverseElementArrayTestar(treeArray, parentWrapped, rootElement, parentId, ignoredTags) {
    parentWrapped['parentId'] = parentId;
    treeArray.push(parentWrapped);

    parentId = treeArray.length - 1;

    var childNodes = getChildNodesTestar(parentWrapped);
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            parentWrapped.textContent = parentWrapped.textContent.trim();
            continue;
        }
        if (childElement.nodeType !== 1 ||
            ignoredTags.includes(childElement.nodeName.toLowerCase())) {
            continue
        }

        var childWrapped = wrapElementTestar(childElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
        traverseElementArrayTestar(treeArray, childWrapped, rootElement, parentId, ignoredTags);
    }
}

/*
 * Get all the childnodes of an (wrapped) element
 * Anticipate the use of iFrames
 * @param {object} parentWrapped, the parent element
 * @return {Array} array of the child nodes
 */
function getChildNodesTestar(parentWrapped) {
    var childNodes = parentWrapped.element.childNodes;

    // Might be contained in an iFrame
    if (childNodes.length === 0 &&
        parentWrapped.element.contentDocument !== undefined &&
        parentWrapped.element.contentDocument !== null) {
        childNodes = parentWrapped.element.contentDocument.childNodes;

        var style = getComputedStyle(parentWrapped.element);
        var left = parseInt(style.getPropertyValue('border-left-width'));
        left += parseInt(style.getPropertyValue('padding-left'));
        parentWrapped["xOffset"] = parentWrapped['rect'][0] + left;
        var top = parseInt(style.getPropertyValue('border-top-width'));
        top += parseInt(style.getPropertyValue('padding-top'));
        parentWrapped["yOffset"] = parentWrapped['rect'][1] + top;
    }

    return childNodes;
}

/*
 * Wrapping all required properties in a struct
 * This minimizes the roundtrips between Java and webdriver
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @return {object} array element and attributes
 */
function wrapElementTestar(element, xOffset, yOffset) {
    var computedStyle = getComputedStyle(element);
    var clientRect = element.getBoundingClientRect();

	var shadowElement = false;
	if(element.shadowRoot !== null){
		shadowElement = true;
	}

    return {
        element: element,

        attributeMap: getAttributeMapTestar(element),

        name: getNameTestar(element),
        tagName: element.tagName.toLowerCase(),
        textContent: "",
        innerText: element.innerText,
        value: element.value,
        checked: element.checked,
        selected: element.selected,
        disabled: element.disabled,
        multiple: element.multiple,
        length: getElementLength(element),

        display: computedStyle.display,
        visibility: computedStyle.visibility,
        styleOverflow: computedStyle.overflow,
        styleOverflowX: computedStyle.overflowX,
        styleOverflowY: computedStyle.overflowY,
        stylePosition: computedStyle.position,
        styleOpacity: computedStyle.opacity,
        computedFontSize: computedStyle.fontSize,
        computedColor: computedStyle.color,
        computedBackgroundColor: getEffectiveBackgroundColor(element),
        innerHTML: element.innerHTML,
        outerHTML: element.outerHTML,

        zIndex: getZIndexTestar(element, computedStyle),
        rect: getRectTestar(element, xOffset, yOffset, clientRect),
        dimensions: getDimensionsTestar(element, computedStyle),
        naturalWidth: (element.naturalWidth !== undefined) ? element.naturalWidth : 0,
        naturalHeight: (element.naturalHeight !== undefined) ? element.naturalHeight : 0,
        displayedWidth: (element.width !== undefined) ? parseInt(element.width) : 0,
        displayedHeight: (element.height !== undefined) ? parseInt(element.height) : 0,
        isBlocked: getIsBlockedTestar(element, xOffset, yOffset, clientRect),
        isClickable: isClickableTestar(element, xOffset, yOffset),
        isShadowElement: shadowElement,
        hasKeyboardFocus: document.activeElement === element,
        xpath: getXPath(element),

        wrappedChildren: [],
        xOffset: xOffset,
        yOffset: yOffset
    };
}

function getEffectiveBackgroundColor(el) {
  while (el) {
    const bg = window.getComputedStyle(el).backgroundColor;

    // Check if background is NOT fully transparent
    if (bg && bg !== 'transparent' && !isFullyTransparent(bg)) {
      return bg;
    }

    el = el.parentElement;
  }

  // Fallback: if no background found, assume white (typical default)
  return 'rgb(255, 255, 255)';
}

function isFullyTransparent(color) {
  // Check for 'rgba(0, 0, 0, 0)' or any rgba where alpha = 0
  const match = color.match(/^rgba?\(\s*(\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d*\.?\d+))?\s*\)$/i);
  if (!match) return false;

  const alpha = match[4];
  return typeof alpha !== 'undefined' && parseFloat(alpha) === 0;
}

/*
 * Determine the name for the element, optionally taken from the label
 * @param {node} the HTML element
 * @return {string} the name
 */
function getNameTestar(element) {
    var name = element.getAttribute("name");
    var id = element.getAttribute("id");

    try {
        if (id && labelMap[id]) {
            return labelMap[id];
        }
    }
    catch (err) {
    }

    if (name) {
        return name;
    }
    if (id) {
        return id;
    }
    return "";
}

/*
 * Calculate the z-index of an element
 * @param {node} the HTML element
 * @param {object} the computed style of the element
 * @return {number} the z-index
 */
function getZIndexTestar(element, computedStyle) {
    if (element === document.body) {
        return 0;
    }

    if (element === null || element === undefined) {
        return 0;
    } else if (element.nodeType !== 1) {
        return getZIndexTestar(element.parentNode, computedStyle) + 1;
    }

    var zIndex = computedStyle.getPropertyValue('z-index');
    if (isNaN(zIndex)) {
        return getZIndexTestar(element.parentNode, computedStyle) + 1;
    }
    return zIndex * 1;
}

/*
 * Get the position and dimensions of the element
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @param {object} clientRect, the bounding client rect
 * @return {array} array with the position and dimensions
 */
function getRectTestar(element, xOffset, yOffset, clientRect) {
    if (element === document.body) {
        clientRect = document.documentElement.getBoundingClientRect();
    }

    return [
        parseInt(clientRect.left) + xOffset,
        parseInt(clientRect.top) + yOffset,
        parseInt(element === document.body ? window.innerWidth : clientRect.width),
        parseInt(element === document.body ? window.innerHeight : clientRect.height)
    ];
}

/*
 * Get all appropriate dimensions of the element
 * Used to determine if a scrollbar is present
 * @param {node} element, the parent HTML element
 * @param {object} the computed style of the element
 * @return {array} array with the dimensions
 */
function getDimensionsTestar(element, computedStyle) {
    if (element === document.body) {
        scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
        scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    } else {
        scrollLeft = element.scrollLeft;
        scrollTop = element.scrollTop;
    }

    return {
        overflowX: computedStyle.getPropertyValue('overflow-x'),
        overflowY: computedStyle.getPropertyValue('overflow-y'),
        clientWidth: element.clientWidth,
        clientHeight: element.clientHeight,
        offsetWidth: element.offsetWidth || 0,
        offsetHeight: element.offsetHeight || 0,
        scrollWidth: element.scrollWidth,
        scrollHeight: element.scrollHeight,
        scrollLeft: scrollLeft,
        scrollTop: scrollTop,
        borderWidth: parseInt(computedStyle.borderLeftWidth, 10) + parseInt(computedStyle.borderRightWidth, 10),
        borderHeight: parseInt(computedStyle.borderTopWidth, 10) + parseInt(computedStyle.borderBottomWidth, 10)
    };
}

/*
 * Determine if the document body exceeds the height of the browser window
 * Used to determine if the browser contains a vertical scrollbar
 * @return {bool} true if the document body is higher
 */
function isPageVerticalScrollable(){
	return (document.body.clientHeight > window.innerHeight);
}

/*
 * Determine if the document body exceeds the width of the browser window
 * Used to determine if the browser contains a horizontal scrollbar
 * @return {bool} true if the document body is wider
 */
function isPageHorizontalScrollable(){
	return (document.body.clientWidth > window.innerWidth);
}

/*
 * Determine if an element is blocked by another element,
 * but does not contain said element (e.g. <img> inside <a>)
 * @param {node} element, the parent HTML element
 * @param {object} xOffset, offset off the iFrame (if applicable)
 * @param {object} yOffset, offset off the iFrame (if applicable)
 * @param {object} clientRect, the bounding client rect
 * @return {bool} true if the element is blocked by another element
 */
function getIsBlockedTestar(element, xOffset, yOffset, clientRect) {
    // get element at element's (click) position
    var x = clientRect.left + clientRect.width / 2 + xOffset;
    var y = clientRect.top + clientRect.height / 2 + yOffset;
    var elem = document.elementFromPoint(x, y);

    // element is inside iframe(s)
    var outer = undefined;
    while (elem instanceof HTMLIFrameElement ||
           elem instanceof HTMLFrameElement ||
           (outer !== undefined && outer.contentWindow !== undefined &&
            elem instanceof outer.contentWindow.HTMLIFrameElement)) {
        outer = elem;

        // Adjust according to position of the iframe
        var tmp = elem.getBoundingClientRect();
        x -= tmp.x;
        y -= tmp.y;

        // Elements from a cross-origin frame are not reachable
        try {
            elem = elem.contentWindow.document.elementFromPoint(x, y);
        }
        catch(exception) {
            return true
        }
    }

    // elem can not be found, asssume the originating element is not blocked
    if (elem === null) {
        return false;
    }

    // Ignore encapsulated childs of <a>
    if (element.tagName === "A" && element.contains(elem)) {
        return false;
    }

    // Ignore "label for"
    if (elem.tagName === "LABEL") {
        return false;
    }

    // return whether obscured element has same parent node
    // (will also return false if element === elem)
    return elem.parentNode !== element.parentNode;
}

/*
 * Determine if an element is clickable
 * @param {node} element, the parent HTML element
 * @return {bool} true if the element is clickable
 */
function isClickableTestar(element) {
    // onClick defined as tag attribute
    if (element.getAttribute("onclick") !== null) {
        return true;
    }
    // No prototype patching within dynamically loaded iFrames
    if (element.getEventListeners === undefined) {
        return false;
    }
    // onClick added via JS
    var arr = element.getEventListeners('click');
    return arr !== undefined && arr.length > 0;
}

/*
 * Fill the labelMap for all HTML elements on the page that have labels
 */
function getLabelMapTestar() {
    labelMap = {};
    var labelList = document.getElementsByTagName("LABEL");
    for (var i = 0; i < labelList.length; i++) {
        var item = labelList[i];
        if (item.getAttribute("for")) {
            labelMap[item.getAttribute("for")] = item.textContent;
        }
    }
}

function getElementLength(element) {
    if (typeof element.length === 'number') {
        return element.length;
    }

    if (typeof element.value === 'string') {
        return element.value.length;
    }

    if (typeof element.textContent === 'string') {
        return element.textContent.trim().length;
    }

    return -1;
}

/*
 * Get all the attributes for an HTML element
 * @param {node} element, the parent HTML element
 * @return {array} array of name, value pairs
 */
function getAttributeMapTestar(element) {
    return Array.from(element.attributes).reduce(function (map, att) {
        map[att.name] = att.nodeValue;
        return map;
    }, {});
}

function getXPath(element) {
  try {
    // Create an array to store the path
    var path = [];

    // Iterate through the ancestors of the element
    while (element && element.nodeType === Node.ELEMENT_NODE) {
      var index = 1;
      var sibling = element.previousSibling;

      // Find the index of the element among its siblings
      while (sibling) {
        if (sibling.nodeType === Node.ELEMENT_NODE && sibling.nodeName === element.nodeName) {
          index++;
        }
        sibling = sibling.previousSibling;
      }

      // Build the XPath expression for the element
      var tagName = element.nodeName.toLowerCase();
      var pathSegment = tagName + '[' + index + ']';
      path.unshift(pathSegment);

      // Move up to the parent element
      element = element.parentNode;
    }

    // Join the path segments to form the XPath
    var xpath = path.length ? '/' + path.join('/') : '';

    return xpath;
  } catch (error) {
    // Handle any errors and return an empty string
    console.error('Error occurred while obtaining XPath:', error);
    return '';
  }
}

// Performance Observer for LCP
let latestLCP = null;
const observer = new PerformanceObserver((list) => {
    const entries = list.getEntries();
    const lastEntry = entries[entries.length - 1];
    if (lastEntry) {
        latestLCP = lastEntry.startTime;
    }
});
observer.observe({ type: "largest-contentful-paint", buffered: true });

return {
    getStateTreeTestar: getStateTreeTestar
};
})();