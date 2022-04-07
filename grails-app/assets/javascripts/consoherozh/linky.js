
function computeIdleData(loadcurve) {
    moment.locale('fr')

    if ( loadcurve.length > 1 ) {
        var loadcurve_step_ms = moment(loadcurve[1].x) - moment(loadcurve[0].x);
        var idle_consumption = null;

        for (point of loadcurve) {
            // computes min(point.y)
            if (idle_consumption == null || point.y < idle_consumption) {
                idle_consumption = point.y;
            }
            point.label = point.y + " Wh";
            var time = moment(point.x)
            point.title = 'de ' + time.format('HH:mm') + ' à ' + time.add(loadcurve_step_ms, 'milliseconds').format('HH:mm');
            // TODO(cyril) did not manage to display bars after the associated x value
            // (currently centered on the x value)
            point.x = time.subtract(loadcurve_step_ms / 2, 'milliseconds');
        }

        //var suspend = people.map(({ point }) => {...point, ...{y: suspend_consumption}});
        //var suspend = loadcurve.map(point => Object.assign(point, {y: suspend_consumption}));
        var idle = loadcurve.map(point => Object.assign(Object.assign({}, point), {y: idle_consumption}));

        return idle;
    }
    return [];
}

class LinkyChart {

    getLinkyChart() {
        if (typeof this.linkyChart === 'undefined') {
            var item = document.getElementById('chart_loadcurve_suspend');
            this.linkyChart = new Chart(item, {
                type: 'bar',
                data: {
                    //labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                    labels: [],
                    datasets: [{
                        label: "Consommation",
                        // data: loadcurve,
                        borderWidth: 1,
                        categoryPercentage: 1.0,
                        barPercentage: 1.0,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        //stepped: true,
                        //fill: true
                    }, {
                        label: "Veille",
                        // data: idle,
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
                                // let jchart magic occur here, work automatically for hours, days, month
                                // unit: 'hour',
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
        return this.linkyChart;
    }

    setLoadCurve(loadcurve, idlecurve = null)
    {
        if (Array.isArray(loadcurve) && loadcurve.length > 0) {

            const thisChart = this.getLinkyChart();

            // update chart datas
            thisChart.data.datasets[0].data = loadcurve;
            thisChart.data.datasets[1].data = idlecurve ? idlecurve : computeIdleData(loadcurve);
            thisChart.update();
        }
    }

    /**
     * built curve injected data from controller model
     *
     * Only for first creation, next will be done with Ajax/XMLHttpRequest
     * upon form actions with requestChartUpdate
     *
     * Current usage : see template _linkyDashboardChart
     * var builtcurve = ${ raw(chart.toChartjsCurve().toString(false))};
     * var loadcurve = builtcurve.build.loadCurve;
     *
     */
    fillLinkyChart(builtcurve) {
        this.setLoadCurve(builtcurve.loadCurve);
        // next updates will be aynchronous
        this.prepare();
    }

    requestChartUpdate(formData) {
        if (this.chartDataUrl) {
            let url = this.chartDataUrl
            let req = new XMLHttpRequest();
            let thisNested = this;
            // This will be called after the response is received
            req.onload = () => {
                if (req.status != 200) { // analyze HTTP status of the response
                    alert(`Error ${req.status}: ${req.statusText}`); // e.g. 404: Not Found
                } else { // show the result
                    console.log(`Done Got chart data, got ${req.response.length} bytes`); // response is the server response
                    // responseType === 'json' does not work, responseType is "" ...
                    if ( req.responseText.startsWith('{"loadCurve"') )
                    {
                        var json = JSON.parse(req.responseText);
                        thisNested.setLoadCurve(json.loadCurve, json.idleCurve );

                        if ( json.command ) {
                            // update date on navigation action.
                            // command ~ form
                            const dateChart = document.getElementById('dateChart');
                            const formattedDate = moment(json.command.dateChart).format('yyyy-MM-DD');
                            console.log('dateChart ' + json.command.dateChart + ' was ' + dateChart.value + ' formatted=' + formattedDate);
                            dateChart.value = formattedDate;
                            // reset navigation
                            const navigation = document.getElementById('navigation');
                            navigation.value = '';
                        }
                    }
                    else
                    {
                        thisNested.setLoadCurve([]);
                    }
                }
            };
            req.open("post", url, true);
            // not a multipart ( rejected by tomcat since no multipart boundary found )
            // req.setRequestHeader('Content-Type', 'multipart/form-data');
            req.setRequestHeader('Accept', 'application/json');
            req.send(formData);
        }
    }

    prepare() {
        const navigationCharForm = document.getElementById('navigation-chart-form');
        const thisLinkyChart = this;
        // relative Url
        thisLinkyChart.chartDataUrl='/device/deviceChartJson?';
        navigationCharForm.addEventListener( "submit", function ( event ) {
            event.preventDefault();
            let formData = new FormData(navigationCharForm);
            thisLinkyChart.requestChartUpdate(formData);
        } );
    }
}