<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Available models</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/all.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/magnific-popup.css">
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
        <div class="col-3"><strong>Abstraction Attributes</strong></div>
        <div class="col-2"></div>
    </div>
    <%
        int i = 1;
    %>
    <c:forEach var="model" items="${models}">
        <div class="row">
            <div class="col-3"><a data-toggle="collapse" href="#sequenceRow<%= i%>"><span class="fas fa-angle-down"></span></a>&nbsp;${model.modelIdentifier}</div>
            <div class="col-2">${model.applicationName}</div>
            <div class="col-2">${model.applicationVersion}</div>
            <div class="col-3">${model.abstractionAttributesAsString}</div>
            <div class="col1"><a data-toggle="modal" data-target="#sequenceModal<%= i %>"><span class="fas fa-layer-group"></span></a></div>
            <div class="col-1"></div>
        </div>
        <!-- Collapse block -->
        <div class="collapse" id="sequenceRow<%= i %>">
            <div class="row">
                <div class="col-6 mt-1"><h5>Sequences</h5></div>
            </div>
            <div class="row">
                <div class="col-1"></div>
                <div class="col-3"><strong>Start date/time</strong></div>
                <div class="col-2"><strong>Nr of steps</strong></div>
                <div class="col-2"><strong>Quirks</strong> &nbsp; <span class="fas fa-info-circle" title="Strange results encountered during test execution."></span></div>
                <div class="col-2"><strong>Deterministic run</strong></div>
                <div class="col-1"></div>
            </div>
            <c:forEach var="seq" items="${model.sequences}">
                <div class="row">
                    <div class="col-1 text-right"><span class="fas ${seq.verdictIcon}" title="${seq.verdictTooltip}"></span></div>
                    <div class="col-3">${seq.startDateTime}</div>
                    <div class="col-2">${seq.numberOfSteps}</div>
                    <div class="col-2">${seq.nrOfErrors}</div>
                    <div class="col-2"><span class="fas ${seq.deterministicIcon}"></span></div>
                    <div class="col-1"><span class="fas fa-eye" data-sequenceId="${seq.sequenceId}"></span></div>
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

<script src="js/jquery-3.2.1.min.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/jquery.magnific-popup.min.js"></script>

<script>
    // add an ajax call to the eye-ball icons
    $(document).ready(function () {
        $('.fa-eye').on('click', function (event) {
            // first make sure there is no active content area
            $('#content').remove();

            // fetch the slide show from the server
            let sequenceId = $(this).attr('data-sequenceId');
            $.post({
                url     : 'models',
                data     : {'sequenceId' : sequenceId},
                success    : function(resultText){
                    $('.container').append(resultText);
                    var divs = [];
                    $('#content').children().each(function(index, element) {
                        var obj = {};
                        obj.src = element;
                        obj.type = 'inline';
                        divs.push(obj);
                    });
                    $.magnificPopup.open({
                        items: divs,
                        gallery: {
                            enabled: true,
                            navigateByImgClick: true,
                            preload: [0,1] // Will preload 0 - before current, and 1 after the current image
                        },
                        type: "inline"
                    });
                },
                error : function(jqXHR, exception){
                    console.log('An error occurred while loading the slideshow!');
                }
            });
        })
    });
</script>
</body>
</html>