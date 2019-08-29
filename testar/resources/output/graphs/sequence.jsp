<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="content">
    <c:forEach var="viz" items="${visualizations}">
        <div class="edge-popup mfp-hide">
            <div class="screenshot"><img src="${contentFolder}/${viz.screenshotSource}.png" alt="Source screenshot"><div class="top-left-text">${viz.counterSource}</div></div>
            <div class="action">${viz.actionDescription}</div>
            <div class="screenshot"><img src="${contentFolder}/${viz.screenshotTarget}.png" alt="Target screenshot"><div class="top-left-text">${viz.counterTarget}</div></div>
        </div>
    </c:forEach></div>