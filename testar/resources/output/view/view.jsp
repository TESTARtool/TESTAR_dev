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
    <div class="resultListTitle">TESTAR Output Results folders</div>
	<form method="post">
		<div style="white-space: pre">The caracter '*' is a wildcard</div>
		<div style="white-space: pre">To filter all 'notepad' folders from 2024 you can use: '2024*notepad'</div>
		<br>
		<label for="filterInput">Filter Folders: </label><input type="text" id="filterInput" name="filterInput" oninput="filterFolders(this.value)">	

		<div class="resultListContainer">
            <%
                List<String> folderList = (List<String>) request.getAttribute("folderList");
                if (folderList != null) {
                    Iterator<String> iterator = folderList.iterator();
                    while (iterator.hasNext()) {
                        String folder = iterator.next();
            %>
                            <div class="folderItem">
                                <label class="custom-checkbox"> <%= folder %> <input type="checkbox" name="selectedFolders" value="<%= folder %>" checked><span class="checkmark"></span></label>
                            </div>
            <%
                    }
                } else {
            %>
                    <p>No folders available</p>
            <%
                }
            %>
        </div>

        <button type="submit">View Results Summary</button>
    </form>
<script>
    // Function to filter folders based on input value with wildcard support
    function filterFolders(filterValue) {
        var folderItems = document.querySelectorAll('.folderItem');
        
        // Iterate through folder items and show/hide based on the filter
        folderItems.forEach(function(item) {
            var folderName = item.textContent.trim();
            var filterParts = filterValue.toLowerCase().split('*');
            var matchesFilter = true;

            // Check if each part of the filter appears in the folder name
            filterParts.forEach(function(filterPart) {
                if (filterPart !== '' && !folderName.toLowerCase().includes(filterPart)) {
                    matchesFilter = false;
                }
            });

            if (matchesFilter) {
                item.style.display = 'block';
            } else {
                item.style.display = 'none';
            }
        });
    }
</script>
</body>
</html>