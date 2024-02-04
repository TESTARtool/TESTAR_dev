<!DOCTYPE html>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TESTAR View Mode</title>
	<link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="row_title">
        <img src="img/testar-view.png" class="logo" alt="Testar Logo">
    </div>
    <div class="resultListTitle">TESTAR HTML Results Summary</div>
	<div>
            <%
                List<String> htmlFiles = (List<String>) request.getAttribute("htmlReportsList");
                if (htmlFiles != null) {
                    Iterator<String> iterator = htmlFiles.iterator();
                    while (iterator.hasNext()) {
                        String file = iterator.next();
            %>
                            <button class="collapsible" onclick="toggleVisualization(this)" data-file='<%= file %>'><%= file %></button>
							<div class="content" style="display: none;">
                                <br>
								<div id="<%= file %>"></div>
								<br>
                            </div>
            <%
                    }
                } else {
            %>
                    <p>No HTML files available</p>
            <%
                }
            %>
    </div>

<script>
    function toggleVisualization(button) {
        var content = button.nextElementSibling;
        var allCollapsibles = document.querySelectorAll('.collapsible');
        
        // Close all other collapsibles
        allCollapsibles.forEach(function(collapsible) {
            var otherContent = collapsible.nextElementSibling;
            if (collapsible !== button && otherContent.style.display === "block") {
                otherContent.style.display = "none";
                clearHtmlContent(collapsible);  // Clear HTML content for closed collapsibles
            }
        });

        if (content.style.display === "block") {
            content.style.display = "none";
            clearHtmlContent(button);  // Clear HTML content when hiding
        } else {
            content.style.display = "block";
            loadAndShowHtmlContent(button);  // Load HTML content when showing
        }
    }

    function loadAndShowHtmlContent(button) {
        var filePath = button.getAttribute('data-file');
        var contentContainer = document.getElementById(filePath);

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                contentContainer.innerHTML = xhr.responseText;
            }
        };

        xhr.open("GET", "/viewSummary?selectedFile=" + encodeURIComponent(filePath), true);
        xhr.send("selectedFile=" + encodeURIComponent(filePath));
    }

    function clearHtmlContent(button) {
        var filePath = button.getAttribute('data-file');
        var contentContainer = document.getElementById(filePath);
        contentContainer.innerHTML = "";  // Clear HTML content
    }

    function reverse() {
        let direction = document.getElementById('main').style.flexDirection;
        if (direction === 'column') document.getElementById('main').style.flexDirection = 'column-reverse';
        else document.getElementById('main').style.flexDirection = 'column';
    }
</script>
</body>
</html>