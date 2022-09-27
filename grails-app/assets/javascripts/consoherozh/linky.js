
function computeIdleData(loadcurve) {
    moment.locale('fr')

    if ( loadcurve.length > 1 ) {
        var idle_consumption = null;

        for (point of loadcurve) {
            // computes min(point.y)
            if (idle_consumption == null || point.y < idle_consumption) {
                idle_consumption = point.y;
            }
        }
        var idle = loadcurve.map(point => Object.assign(Object.assign({}, point), {y: idle_consumption}));
        return fillTitleLabel(idle,false);
    }
    return [];
}

function fillTitleLabel(curve,align=true) {
    moment.locale('fr')

    const days_in_ms = (24 * 3600 * 1000);
    if ( curve.length > 1 ) {
        var loadcurve_step_ms = moment(curve[1].x) - moment(curve[0].x);
        for (point of curve) {

            var time = moment(point.x)
            if ( loadcurve_step_ms <  days_in_ms ) {
                point.label = point.y + " Wh";
                point.title = time.format('dddd DD MMM') + ' de ' + time.format('HH:mm') + ' à ' + time.add(loadcurve_step_ms, 'milliseconds').format('HH:mm');
            }
            else
            {
                point.label = point.y + " kWh";
                point.title = 'du ' + time.format('dddd DD MMM') + ' au ' + time.add(loadcurve_step_ms, 'milliseconds').format('dddd DD MMM');
            }
            // TODO(cyril) did not manage to display bars after the associated x value
            // TODO(philippe) didn't find out either
            // (currently centered on the x value)
            if (align) {
                point.x = time.subtract(loadcurve_step_ms / 2, 'milliseconds');
            }
        }
    }
    return curve
}

function getViewModeString(viewMode) {
    const mode = ( typeof viewMode === 'string' ) ? viewMode : viewMode.name;
    return mode;
}

function addDrillDown(thisChart) {
    thisChart.options.onClick = ((e) => {
        const canvasPosition = Chart.helpers.getRelativePosition(e, thisChart);
        const dataX = thisChart.scales.x.getValueForPixel(canvasPosition.x);
        // const dataY = thisChart.scales.y.getValueForPixel(canvasPosition.y);

        if (dataX) {
            const navigationCharForm = document.getElementById('navigation-chart-form');
            const dateChart = document.getElementById('dateChart');
            const formattedDate = moment(dataX).format('yyyy-MM-DD');
            dateChart.value = formattedDate;
            const viewMode = document.getElementById('viewMode');
            switch (viewMode.value) {
                case 'year':
                    viewMode.value = 'month';
                    break;
                case 'month':
                    viewMode.value = 'day';
                    break;
            }
            console.log('click on ' + dataX + ' drill-down');
            // emulate submit by clicking button
            // navigationCharForm.submit();
            const button = document.getElementById('navigation-chart-' + viewMode.value + '-button');
            if (button) {
                button.click();
            }
        }
    });
}

function activateGraphForPeriod(period)
{
    const periods = [ 'day', 'month', 'year'];
    periods.forEach( function (p) {
        const chart = document.getElementById( 'chart_loadcurve_' + p);
        if ( chart ) {
            chart.hidden = ! (p === period);
        }
    });
}

class LinkyChart {

    getDailyChart() {
        if (typeof this.dailyChart === 'undefined') {
            var item = document.getElementById('chart_loadcurve_day');
            this.dailyChart = new Chart(item, {
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
                                // let jchart magic occur here
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
        return this.dailyChart;
    }

    getMonthlyChart() {
        if (typeof this.monthlyChart === 'undefined') {
            var item = document.getElementById('chart_loadcurve_month');
            this.monthlyChart = new Chart(item, {
                type: 'bar',
                data: {
                    //labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                    labels: [],
                    datasets: [{
                        label: "Consommation journalière",
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
                    }, {
                        type: 'line',
                        label: "Puissance Max",
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
                                callback: function (value, index, values) {
                                    return value + ' kWh';
                                }
                            }
                        },
                        x: {
                            type: 'time',
                            stacked: true,
                            grid: {
                                offset: true
                            },
                            time: {
                                unit: 'day',
                                unitStepSize: 1,
                                displayFormats: {
                                    'day': 'ddd D MMM',
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
            addDrillDown(this.monthlyChart);
        }
        return this.monthlyChart;
    }

    getYearlyChart() {
        if (typeof this.yearlyChart === 'undefined') {
            var item = document.getElementById('chart_loadcurve_year');
            this.yearlyChart = new Chart(item, {
                type: 'bar',
                data: {
                    //labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
                    labels: [],
                    datasets: [{
                        label: "Consommation mensuelle",
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
                                    return value + ' kWh';
                                }
                            }
                        },
                        x: {
                            type: 'time',
                            stacked: true,
                            grid: {
                                offset: true
                            },
                            time: {
                                unit: 'month',
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
            addDrillDown(this.yearlyChart);
        }
        return this.yearlyChart;
    }

    // get chart for 'day' 'month' or year
    getLinkyChart(viewMode) {
        // ugly to fix parameter type ( can be command , htmlitem , string )
        const mode = getViewModeString(viewMode)
        switch (mode) {
            case 'month' :
                return this.getMonthlyChart();
                break;
            case 'year' :
                return this.getYearlyChart();
                break;
            default: // day
                return this.getDailyChart();
                break;
        }
    }

    setLoadCurve(viewMode, loadcurve, idlecurve = null, maxcurve = null)
    {
        const thisChart = this.getLinkyChart(viewMode);
        if (Array.isArray(loadcurve) && loadcurve.length > 0) {
            const align = getViewModeString(viewMode) === 'day';
            fillTitleLabel(loadcurve, align);
            // update chart datas
            thisChart.data.datasets[0].data = loadcurve;
            thisChart.data.datasets[1].data = idlecurve ? fillTitleLabel(idlecurve, align) : computeIdleData(loadcurve, align);
            if ((maxcurve) && ( thisChart.data.datasets.length > 2))
            {
                thisChart.data.datasets[2].data = fillTitleLabel(maxcurve,false);
            }
        }
        else
        {
            // resets data ( and no date ).
            thisChart.data.datasets[0].data = [];
            thisChart.data.datasets[1].data = [];
        }
        thisChart.update();
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
        // next updates will be aynchronous
        this.prepare();
        const viewMode = document.getElementById('viewMode');
        this.setLoadCurve(viewMode.value, builtcurve.loadCurve);
        if (viewMode) {
            activateGraphForPeriod(viewMode.value);
        }
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
                        thisNested.setLoadCurve(json.command.viewMode, json.loadCurve, json.idleCurve, json.maxCurve );

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
                            // rebind buildIdle checkbox.
                            const buildIdleCheckBox = document.getElementById( 'navigation-chart-build-idle-checkbox');
                            const buildIdle = document.getElementById('buildIdle');
                            if ( buildIdleCheckBox && buildIdle ) {
                                // buildIdle sent as value, received as checked.
                                buildIdleCheckBox.checked = buildIdle.checked;
                                buildIdle.value = buildIdle.checked ? "true" : "false"
                            }
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

// extracted from  user/chart.js user/device.js for chart support deviceChart.gsp
// works without jQuery
function onLoadChart() {

    const navigationCharForm = document.getElementById('navigation-chart-form');
    // bind navigation-chart-XXX-buttons to viewNode and navigation form values
    // select right chart for period to display
    if ( navigationCharForm ) {
        const viewMode = document.getElementById('viewMode');
        if (viewMode) {
            const periods = [ 'day', 'month', 'year'];
            periods.forEach( function(period)
            {
                const chart = document.getElementById( 'chart_loadcurve_' + period);
                // chart.hidden = true;
                const button = document.getElementById('navigation-chart-' + period + '-button')
                if ( button )
                {
                    button.onclick = function () {
                        viewMode.value = period;
                        activateGraphForPeriod(period);
                    };
                }
            })
        }
        const navigation = document.getElementById('navigation');
        if (navigation) {
            const ways = [ 'prev','next']
            ways.forEach( function(way) {
                const button = document.getElementById('navigation-chart-' + way + '-button')
                if (button) {
                    button.onclick = function () {
                        navigation.value = way;
                    };
                }
            })
        }
        // frontend bindings between checkbox and buildIdle hidden field for DeviceChartCommand
        const buildIdle = document.getElementById('buildIdle');
        if (buildIdle)
        {
            buildIdle.value = false;
        }
        const buildIdleCheckBox =  document.getElementById( 'navigation-chart-build-idle-checkbox')
        if (buildIdleCheckBox) {
            buildIdleCheckBox.checked = false;
            buildIdleCheckBox.onchange = function () {
                // this is the way
                buildIdle.value = this.checked ? "true" : "false"
            };
        }
    }
}

function onLoadDeviceChart() {
    onLoadChart()
}
