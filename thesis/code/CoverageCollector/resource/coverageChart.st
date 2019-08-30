coverageChart(title, subTitle, minutes, actions, sequences, lines, branches) ::= <<
<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>NMT $title$</title>

        <style type="text/css">
#container {
    min-width: 310px;
    max-width: 1200px;
    height: 800px;
    margin: 0 auto
}
        </style>
    </head>
    <body>

<script src="highcharts/code/highcharts.js"></script>
<script src="highcharts/code/modules/series-label.js"></script>
<script src="highcharts/code/modules/exporting.js"></script>
<script src="highcharts/code/modules/export-data.js"></script>

<div id="container"></div>

<script type="text/javascript">
Highcharts.chart('container',
{
    title: {
        text: '$title$'
    },
    subtitle: {
        text: '$subTitle$'
    },
    yAxis: [
        { // Primary yAxis
            labels: {
                format: '{value}%',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            title: {
                text: 'Percentage coverage',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            opposite: true
        },
        { // Secondary yAxis
            labels: {
                format: '{value}',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            title: {
                text: 'Number of actions',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            visible: true,
            gridLineWidth: 0
        }
    ],

    legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle'
    },
    tooltip: {
        shared: true
    },

    plotOptions: {
        line : {
          dataLabels : {
            enabled : true,
            allowOverlap: true,
            formatter: function() {
                    if(this.series.name == 'Number of actions') {
                            $sequences: {seq|if (this.point.category === $seq.first$) return 'sequence $seq.second$';$\n$}$
                    }
              return "";
            }
          }
        },
        marker : {
            enabled: false
        }
    },
    xAxis: {
        title: {
            text: 'Test duration (min)'
        },
        categories: [$minutes;separator=","$]
    },
    series: [
        {
            name: 'Number of actions',
            visible: true,
            showInLegend: true,
            dataLabels: {
                enabled: true
            },        
            yAxis: 1,
            data: [$actions;separator=","$]
        },
        {
            name: 'Line coverage',
            tooltip: {
                pointFormatter: function() {
                   var point = this;
                        return '<span style="color:' + point.color + '">\u25CF</span> ' + point.series.name + ': <b>' + point.y  +  '%</b><br/>';
                }
            },        
            yAxis: 0,
             data: [$lines;separator=","$]
        },
        {
            name: 'Branch coverage',
            tooltip: {
            dataLabels: {
              enabled: false
            },        
                pointFormatter: function() {
                   var point = this;
                        return '<span style="color:' + point.color + '">\u25CF</span> ' + point.series.name + ': <b>' + point.y  +  '%</b><br/>';
                }
            },        
            yAxis: 0,
            data: [$branches;separator=","$]
        }
    ],

    responsive: {
        rules: [{
            condition: {
                maxWidth: 500
            },
            chartOptions: {
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom'
                }
            }
        }]
    }

});
        </script>
    </body>
</html>

>>