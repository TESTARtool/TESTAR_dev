var labelMap;

var getStateTreeTestar = function (ignoredTags) {
    var body = document.body;
    var bodyWrapped = wrapElementTestar(body);
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
    var childNodes = parentWrapped.element.childNodes;
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            continue;
        }
        if (ignoredTags.includes(childElement.nodeName.toLowerCase()) || childElement.nodeType !== 1) {
            continue
        }

        var childWrapped = wrapElementTestar(childElement);
        traverseElementTestar(childWrapped, rootElement, ignoredTags);
        parentWrapped.wrappedChildren.push(childWrapped);
    }
}

function traverseElementListTestar(treeList, parentWrapped, rootElement, parentId, ignoredTags) {
    parentWrapped['parentId'] = parentId;
    treeList.push(parentWrapped);

    parentId = treeList.length - 1;

    var childNodes = parentWrapped.element.childNodes;
    for (var i = 0; i < childNodes.length; i++) {
        var childElement = childNodes[i];

        // Filter ignored tags or non-element nodes
        if (childElement.nodeType === 3) {
            parentWrapped.textContent += childElement.textContent;
            parentWrapped.textContent = parentWrapped.textContent.trim();
            continue;
        }
        if (ignoredTags.includes(childElement.nodeName.toLowerCase()) || childElement.nodeType !== 1) {
            continue
        }

        var childWrapped = wrapElementTestar(childElement);
        traverseElementListTestar(treeList, childWrapped, rootElement, parentId, ignoredTags);
    }
}

/*
 * Wrapping all required properties in a struct
 * This minimizes the roundtrips between Java and webdriver
 */
function wrapElementTestar(element) {
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
        rect: getRectTestar(element),
        dimensions: getDimensionsTestar(element),
        isBlocked: getIsBlockedTestar(element),
        isClickable: isClickableTestar(element),
        hasKeyboardFocus: document.activeElement === element,

        wrappedChildren: []
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

    var zIndex = getComputedStyle(element).getPropertyValue('z-index');
    if (isNaN(zIndex)) {
        return getZIndexTestar(element.parentNode) + 1;
    }
    return zIndex * 1;
}

function getRectTestar(element) {
    var rect = element.getBoundingClientRect();
    if (element === document.body) {
        rect = document.documentElement.getBoundingClientRect();
    }

    return [
        parseInt(rect.left),
        parseInt(rect.top),
        parseInt(element === document.body ? window.innerWidth : rect.width),
        parseInt(element === document.body ? window.innerHeight : rect.height)
    ];
}

function getDimensionsTestar(element) {
    if (element === document.body) {
        scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
        scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
    }
    else {
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

function getIsBlockedTestar(element) {
    // get element at element's (click) position
    var rect = element.getBoundingClientRect();
    var x = rect.left + rect.width / 2;
    var y = rect.top + rect.height / 2;
    var elem = document.elementFromPoint(x, y);

    // return whether obscured element has same parent node
    // (will also return false if element === elem)
    if (elem === null) {
        return false;
    }
    return elem.parentNode !== element.parentNode;
}

function isClickableTestar(element) {
    // onClick defined as tag attribute
    if (element.onclick !== null) {
        return true;
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
    return Array.from(element.attributes).reduce(function(map, att) {
        map[att.name] = att.nodeValue;
        return map;
    }, {});
}
