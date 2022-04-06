/**
 * built curve injected data from controller model
 *
 * TODO should populates with Ajax/XHTMLRequest
 *
 * Cuurent usage : see template _linkyDashboardChart
 * var builtcurve = ${ raw(chart.toChartjsCurve().toString(false))};
 * var loadcurve = builtcurve.build.loadCurve;
 *
 */
function linkyChart(builtcurve)
{
    moment.locale('fr')

    var loadcurve = builtcurve.loadCurve;

    if (Array.isArray(loadcurve) && loadcurve.length > 0) {

        var loadcurve_step_ms = moment(loadcurve[1].x) - moment(loadcurve[0].x);
        var suspend_consumption = null;

        for (point of loadcurve) {
            // computes min(point.y)
            if (suspend_consumption == null || point.y < suspend_consumption) {
                suspend_consumption = point.y;
            }
            point.label = point.y + " Wh";
            time = moment(point.x)
            point.title = 'de ' + time.format('HH:mm') + ' à ' + time.add(loadcurve_step_ms, 'milliseconds').format('HH:mm');
            // TODO(cyril) did not manage to display bars after the associated x value
            // (currently centered on the x value)
            point.x = time.subtract(loadcurve_step_ms / 2, 'milliseconds');
        }

        //var suspend = people.map(({ point }) => {...point, ...{y: suspend_consumption}});
        //var suspend = loadcurve.map(point => Object.assign(point, {y: suspend_consumption}));
        var suspend = loadcurve.map(point => Object.assign(Object.assign({}, point), {y: suspend_consumption}));


        var item = document.getElementById('chart_loadcurve_suspend');
        var myChart = new Chart(item, {
            type: 'bar',
            data: {
                //labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                labels: [],
                datasets: [{
                    label: "Consommation",
                    data: loadcurve,
                    borderWidth: 1,
                    categoryPercentage: 1.0,
                    barPercentage: 1.0,
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    //stepped: true,
                    //fill: true
                }, {
                    label: "Veille",
                    data: suspend,
                    borderWidth: 1,
                    categoryPercentage: 1.0,
                    barPercentage: 1.0,
                    // backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    //stepped: true,
                    //fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            // Include a dollar sign in the ticks
                            callback: function (value, index, values) {
                                return value + ' Wh';
                            }
                        }
                    },
                    x: {
                        type: 'time',
                        stacked: true,
                        grid: {
                            offset: false
                        },
                        time: {
                            unit: 'hour',
                            unitStepSize: 1,
                            displayFormats: {
                                'hour': 'HH:mm',
                            }
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            title: function (tooltipItems, data) {
                                return tooltipItems[0].raw.title;
                            },
                            label: function (context) {
                                return context.dataset.label + ' : ' + context.raw.label;
                            }
                        }
                    }
                }
            }

        });
    }
}
