<%@ page import="java.util.List" %>
<%@ page import="org.testar.statemodel.analysis.representation.ActionViz" %>
<%
    List<ActionViz> visualizations = (List<ActionViz>) request.getAttribute("visualizations");
    String contentFolder = (String) request.getAttribute("contentFolder");
%>
<div id="content">
    <% for (ActionViz viz : visualizations) { %>
        <div class="edge-popup mfp-hide">
            <div class="screenshot"><img src="<%= contentFolder %>/<%= viz.getScreenshotSource() %>.png" alt="Source screenshot"><div class="top-left-text"><%= viz.getCounterSource() %></div></div>
            <div class="action"><% if (!viz.isDeterministic()) { %>Non-deterministic! - <% } %><%= viz.getActionDescription() %></div>
            <div class="screenshot"><img src="<%= contentFolder %>/<%= viz.getScreenshotTarget() %>.png" alt="Target screenshot"><div class="top-left-text"><%= viz.getCounterTarget() %></div></div>
        </div>
    <% } %>
</div>
