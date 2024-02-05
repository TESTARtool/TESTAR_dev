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
	<label><input type="checkbox" id="filter-ok">Filter OK Sequences</label><br>
	<label><input type="checkbox" id="filter-duplicates">Filter Duplicated Erroneous Sequences</label><br>
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
    /** Functions to show (open and load HTML sequence) or hide (close and clear HTML sequence) the collapsible elements **/
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

    // Invoke the Java Servlet to load the HTML sequence
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

    /** This function comes from the HTML sequence report to be able to reverse the sequence order **/
    function reverse() {
        let direction = document.getElementById('main').style.flexDirection;
        if (direction === 'column') document.getElementById('main').style.flexDirection = 'column-reverse';
        else document.getElementById('main').style.flexDirection = 'column';
    }

    /** Functions to filter OK or erroneous duplicated sequences **/
    document.getElementById('filter-ok').addEventListener('change', function() {
        filterCollapsibles('OK.html', this.checked);
    });
	function filterCollapsibles(filterValue, hide) {
        var collapsibles = document.querySelectorAll('.collapsible');

        collapsibles.forEach(function(collapsible) {
            var dataFile = collapsible.getAttribute('data-file');
            if (dataFile.includes(filterValue)) {
                collapsible.style.display = hide ? 'none' : 'block';
                // hide the corresponding content as well
                if (hide) {
                    collapsible.nextElementSibling.style.display = 'none';
                    clearHtmlContent(collapsible);
                }
            }
        });
    }

    document.getElementById('filter-duplicates').addEventListener('change', function() {
        if (!this.checked) {
            showAllCollapsiblesExcept('OK.html');
        } else {
            sendFilterDuplicatesRequest();
        }
    });
	function showAllCollapsiblesExcept(filterValue) {
        var collapsibles = document.querySelectorAll('.collapsible');

        collapsibles.forEach(function(collapsible) {
            var dataFile = collapsible.getAttribute('data-file');
			if(!dataFile.includes(filterValue)) collapsible.style.display = 'block';
        });
    }
    function sendFilterDuplicatesRequest() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                 handleResponse(xhr.responseText);
            }
        };

        xhr.open("POST", "/viewSummary", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send("filter-duplicates=true");
    }
	function handleResponse(responseText) {
        // Remove '\r' from each line and split into an array, then filter out empty strings
        var uniqueReports = responseText.replace(/\r/g, '').split('\n').filter(function(report) {
            return report.trim() !== '';
        });

        hideDuplicatedReportsNotInList(uniqueReports);
    }
	function hideDuplicatedReportsNotInList(uniqueReports) {
        var collapsibles = document.querySelectorAll('.collapsible');

        collapsibles.forEach(function(collapsible) {
            var dataFile = collapsible.getAttribute('data-file');
            if (!dataFile.includes('OK.html') && !uniqueReports.includes(dataFile)) {
                collapsible.style.display = 'none';
            }
        });
    }
</script>
</body>
</html>