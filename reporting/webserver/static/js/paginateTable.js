// Credits to Nigel
var testIterations = document.getElementsByClassName("iteration-overview")[0];

var ti_maxElements = 10;
var ti_index = 0;

function getIterationRecords() {
    return [...testIterations.getElementsByClassName("value")].filter(it => !it.className.includes("arrow"));
}

function renderTestIterations() {
    let values = getIterationRecords();

    let show = [];
    for (let i = 0; i < ti_maxElements; i++)
        show.push(ti_index * ti_maxElements + i);

    values.forEach(it => {
        let found = false;

        for (let i = 0; i < show.length; i++) {
            let button = it.getElementsByTagName("button")[0]
            let itId = button.id.replace("iteration_id_", "")

            if (itId == show[i]) {
                found = true;
                break;
            }
        }

        if (found)
            it.style.display = "";
        else
            it.style.display = "none";
    });

    let caption = document.getElementById("captionPaginationIndicator");
    caption.innerHTML = `${ti_index + 1} / ${Math.ceil(values.length / ti_maxElements)}`;
}

function nextIndex() {
    var length = getIterationRecords().length;
    ti_index++;

    if (ti_index * ti_maxElements >= length)
        ti_index = 0;
    renderTestIterations();
}

function previousIndex() {
    var length = getIterationRecords().length;
    ti_index--;

    if (ti_index < 0)
        ti_index = Math.ceil(length / ti_maxElements - 1);
    renderTestIterations();
}

renderTestIterations();
