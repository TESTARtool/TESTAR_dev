var labelMap;

var getStateTreeTestar = function (ignoredTags) {
    var body = document.body;
    var bodyWrapped = wrapElementTestar(body, 0, 0);
    bodyWrapped['documentHasFocus'] = document.hasFocus();
    bodyWrapped['documentTitle'] = document.title;

    // Find all labels on the page
    getLabelMapTestar();

    // For Edge we return a flattened tree (as list), see comment in WdStateFetcher
    if (window.navigator.userAgent.indexOf("Edge") > -1) {
        var treeList = [];
        traverseElementListTestar(treeList, bodyWrapped, body, -1, ignoredTags);
        return treeList;
    } 
    else {
        traverseElementTestar(bodyWrapped, body, ignoredTags);
        return bodyWrapped;
    }
};

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
            continue
        }

        var childWrapped = wrapElementTestar(childElement, parentWrapped["xOffset"], parentWrapped["yOffset"]);
        traverseElementTestar(childWrapped, rootElement, ignoredTags);
        parentWrapped.wrappedChildren.push(childWrapped);
    }
}

function traverseElementListTestar(treeList, parentWrapped, rootElement, parentId, ignoredTags) {
    parentWrapped['parentId'] = parentId;
    treeList.push(parentWrapped);

    parentId = treeList.length - 1;

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
        traverseElementListTestar(treeList, childWrapped, rootElement, parentId, ignoredTags);
    }
}

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
 */
function wrapElementTestar(element, xOffset, yOffset) {
    var computedStyle = getComputedStyle(element);

    return {
        element: element,

        attributeMap: getAttributeMapTestar(element),

        name: getNameTestar(element),
        tagName: element.tagName.toLowerCase(),
        textContent: "",
        value: element.value,
        display: computedStyle.getPropertyValue('display'),

        zIndex: getZIndexTestar(element),
        rect: getRectTestar(element, xOffset, yOffset),
        dimensions: getDimensionsTestar(element),
        isBlocked: getIsBlockedTestar(element, xOffset, yOffset),
        isClickable: isClickableTestar(element, xOffset, yOffset),
        hasKeyboardFocus: document.activeElement === element,

        wrappedChildren: [],
        xOffset: xOffset,
        yOffset: yOffset
    };
}

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

function getZIndexTestar(element) {
    if (element === document.body) {
        return 0;
    }

    if (element === null || element === undefined) {
        return 0;
    } else if (element.nodeType !== 1) {
        return getZIndexTestar(element.parentNode) + 1;
    }

    var zIndex = getComputedStyle(element).getPropertyValue('z-index');
    if (isNaN(zIndex)) {
        return getZIndexTestar(element.parentNode) + 1;
    }
    return zIndex * 1;
}

function getRectTestar(element, xOffset, yOffset) {
    var rect = element.getBoundingClientRect();
    if (element === document.body) {
        rect = document.documentElement.getBoundingClientRect();
    }

    return [
        parseInt(rect.left) + xOffset,
        parseInt(rect.top) + yOffset,
        parseInt(element === document.body ? window.innerWidth : rect.width),
        parseInt(element === document.body ? window.innerHeight : rect.height)
    ];
}

function getDimensionsTestar(element) {
    if (element === document.body) {
        scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
        scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    } else {
        scrollLeft = element.scrollLeft;
        scrollTop = element.scrollTop;
    }

    var style = window.getComputedStyle(element);

    return {
        overflowX: style.getPropertyValue('overflow-x'),
        overflowY: style.getPropertyValue('overflow-y'),
        clientWidth: element.clientWidth,
        clientHeight: element.clientHeight,
        offsetWidth: element.offsetWidth || 0,
        offsetHeight: element.offsetHeight || 0,
        scrollWidth: element.scrollWidth,
        scrollHeight: element.scrollHeight,
        scrollLeft: scrollLeft,
        scrollTop: scrollTop,
        borderWidth: parseInt(style.borderLeftWidth, 10) + parseInt(style.borderRightWidth, 10),
        borderHeight: parseInt(style.borderTopWidth, 10) + parseInt(style.borderBottomWidth, 10)
    };
}

function getIsBlockedTestar(element, xOffset, yOffset) {
    // get element at element's (click) position
    var rect = element.getBoundingClientRect();
    var x = rect.left + rect.width / 2 + xOffset;
    var y = rect.top + rect.height / 2 + yOffset;
    var elem = document.elementFromPoint(x, y);

    // element is inside iframe(s)
    var outer = undefined;
    while (elem instanceof HTMLIFrameElement ||
            (outer !== undefined && outer.contentWindow !== undefined &&
             elem instanceof outer.contentWindow.HTMLIFrameElement)) {
        outer = elem;

        var tmp = elem.getBoundingClientRect();
        x -= tmp.x;
        y -= tmp.y;
        elem = elem.contentWindow.document.elementFromPoint(x, y);
    }

    // return whether obscured element has same parent node
    // (will also return false if element === elem)
    if (elem === null) {
        return false;
    }

    // Ignore encapsulated childs of <a>
    if (element.tagName === "A" && element.contains(elem)) {
        return false;
    }

    return elem.parentNode !== element.parentNode;
}

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

function getAttributeMapTestar(element) {
    return Array.from(element.attributes).reduce(function (map, att) {
        map[att.name] = att.nodeValue;
        return map;
    }, {});
}
