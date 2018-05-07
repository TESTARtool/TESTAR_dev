var getStateTreeTestar = function (ignoredTags) {
    var body = document.body;
    var bodyWrapped = wrapElementTestar(body, undefined, undefined);
    traverseElementTestar(bodyWrapped, body, ignoredTags);
    return bodyWrapped;
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
        if (ignoredTags.includes(childElement.nodeName.toLowerCase()) ||
            childElement.nodeType !== 1) {
            continue
        }

        var childWrapped = wrapElementTestar(childElement);
        traverseElementTestar(childWrapped, rootElement, ignoredTags);
        parentWrapped.wrappedChildren.push(childWrapped);
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

        id: element.id,
        name: element.name,
        tagName: element.tagName.toLowerCase(),
        textContent: "",
        title: element.title,
        value: element.value,
        href: element.href,
        cssClasses: element.getAttribute("class"),
        display: computedStyle.getPropertyValue('display'),
        type: element.getAttribute("type"),

        zIndex: getZIndexTestar(element),
        rect: getRectTestar(element),
        dimensions: getDimensionsTestar(element),

        isClickable: isClickableTestar(element),
        hasKeyboardFocus: document.activeElement === element,

        wrappedChildren: []
    };
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
    return [parseInt(rect.x), parseInt(rect.y),
        parseInt(rect.width), parseInt(rect.height)];
}

function getDimensionsTestar(element) {
    if (element === document.body) {
        scrollLeft = document.documentElement.scrollLeft;
        scrollTop = document.documentElement.scrollTop;
    }
    else {
        scrollLeft = element.scrollLeft;
        scrollTop = element.scrollTop;
    }

    return {
        overflow: element.style.overflow,
        innerWidth: window.innerWidth,
        innerHeight: window.innerHeight,
        clientWidth: element.clientWidth,
        clientHeight: element.clientHeight,
        scrollWidth: element.scrollWidth,
        scrollHeight: element.scrollHeight,
        scrollLeft: scrollLeft,
        scrollTop: scrollTop
    };
}

function isClickableTestar(element) {
    if (element.onclick !== null) {
        return true;
    }
    var arr = element.getEventListeners('click');
    return arr !== undefined && arr.length > 0;
}
