// Credits to Nigel
document.addEventListener('DOMContentLoaded', function() {
    let ctx = document.getElementById('issueMetricChart').getContext('2d');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: issueIndices,
            datasets: [{
                backgroundColor: '#ff553344',
                borderColor: '#ff442277',
                borderWidth: 1,
                data: issueValues
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    scaleLabel: {
                        labelString: "# of Issues",
                        display: true
                    },
                    ticks: {
                        beginAtZero: true,
                        stepSize: 1,
                        suggestedMax: Math.max(...issueValues) + 1
                    }
                }],
                xAxes: [{
                    scaleLabel: {
                        labelString: "Iteration #",
                        display: true
                    },
                    ticks: {
                        beginAtZero: true,
                        stepSize: 1
                    }
                }]
            },
            legend: {
                display: false
            },
            animation: {
                duration: 1000,
                easing: 'easeOutQuad'
            },
            tooltips: {
                callbacks: {
                    label: (tooltipItem) => {
                        let value = 'value' in tooltipItem ? tooltipItem.value : 0;

                        return ` ${value} issues`;
                    },
                    title: (tooltipItem) => {
                        let value = tooltipItem[0].label || 0;

                        return `Iteration ${value}`;
                    }
                }
            }
        }
    });
}, false);
