
<div class="container">

    <h1>Dashboard</h1>

    <div sytle="height: 400px;">
        <canvas id="chart_loadcurve_day" width="400" height="400"></canvas>
    </div>

    <div sytle="height: 400px;">
        <canvas id="chart_loadcurve_month" width="400" height="400"></canvas>
    </div>

    <div sytle="height: 400px;">
        <canvas id="chart_loadcurve_year" width="400" height="400"></canvas>
    </div>

    <script>

        const linkyChart = new LinkyChart();
        // TODO use Ajax XMLHttpRequest
        // injected data from controller model.
        var builtcurve = ${ raw(chart.toChartjsCurve().toString(false))};
        // build is added by JsonBuilder while using loadcurve was a quick way to return a hashlist
        var curves = builtcurve.build;
        // in javascript asset consoherozh/linky.js
        linkyChart.fillLinkyChart(curves)

    </script>

</div>
