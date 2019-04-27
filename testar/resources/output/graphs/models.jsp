<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Available models</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css" integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf" crossorigin="anonymous">
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

<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</body>
</html>