
document.addEventListener('DOMContentLoaded', function() {
    let ctx = document.getElementById('oracleMetricChart').getContext('2d');

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: oracleIndices,
            datasets: [{
                backgroundColor: ['#ff441177', '#44ff1177'],
                borderColor: ['#aa221177', '#22aa1177'],
                borderWidth: 1,
                data: oracleValues
            }]
        },
        options: {
            legend: {
                display: true
            },
            animation: {
                duration: 1000,
                easing: 'easeOutQuad'
            }
        }
    });
}, false);