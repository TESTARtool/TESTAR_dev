<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Available models</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/all.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container">
    <div class="row">
        <div class="col-3">
            <img src="img/testar-logo.png" class="logo" alt="Testar Logo">
        </div>
    </div>
    <div class="row">
        <div class="col-6"><h1>Available Models</h1></div>
    </div>
    <div class="row">
        <div class="col-3"><strong>Identifier</strong></div>
        <div class="col-2"><strong>Application name</strong></div>
        <div class="col-2"><strong>Application version</strong></div>
        <div class="col-5"></div>
    </div>
    <%
        int i = 1;
    %>
    <c:forEach var="model" items="${models}">
        <div class="row">
            <div class="col-3"><a data-toggle="collapse" href="#sequenceRow<%= i%>"><span class="fas fa-angle-down"></span></a>&nbsp;${model.modelIdentifier}</div>
            <div class="col-2">${model.applicationName}</div>
            <div class="col-2">${model.applicationVersion}</div>
            <div class="col1"><a data-toggle="modal" data-target="#sequenceModal<%= i %>"><span class="fas fa-layer-group"></span></a></div>
            <div class="col-4"></div>
        </div>
        <!-- Collapse block -->
        <div class="collapse" id="sequenceRow<%= i %>">
            <div class="row">
                <div class="col-6"><h5>Sequences</h5></div>
            </div>
            <div class="row">
                <div class="col-1"></div>
                <div class="col-3"><strong>Start date/time</strong></div>
                <div class="col-2"><strong>Nr of steps</strong></div>
            </div>
            <c:forEach var="seq" items="${model.sequences}">
                <div class="row">
                    <div class="col-1"></div>
                    <div class="col-3">${seq.startDateTime}</div>
                    <div class="col-2">${seq.numberOfSteps}</div>
                </div>
            </c:forEach>
        </div>

        <!-- Modal block -->
        <div class="modal fade" id="sequenceModal<%= i%>" tabindex="-1" role="dialog" aria-labelledby="sequenceModalLabel<%= i %>" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="sequenceModalLabel<%= i++ %>">Select the model layers</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form target="_blank" method="post" action="graph">
                        <input type="hidden" value="${model.modelIdentifier}" name="modelIdentifier">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-12">
                                What layers do you want to visualize for this model?
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-4"><label for="abstractoption"><input id="abstractoption"
                                                                                  name="abstractoption" type="checkbox">
                                Abstract Layer</label></div>
                            <div class="col-4"><label for="concreteoption"><input id="concreteoption"
                                                                                  name="concreteoption" type="checkbox">
                                Concrete layer</label></div>
                            <div class="col-4"><label for="sequenceoption"><input id="sequenceoption"
                                                                                  name="sequenceoption" type="checkbox">
                                Sequence Layer</label></div>
                        </div>

                        <div class="row">
                            <div class="col-12">
                                Options:
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-6">
                                <label for="compoundoption"><input name="compoundoption" id="compoundoption" type="checkbox">
                                Compound graph</label>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Generate graph</button>
                    </div>
                </form>
                </div>
            </div>
        </div>

    </c:forEach>
</div>

<script src="js/jquery-3.2.1.slim.min.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>