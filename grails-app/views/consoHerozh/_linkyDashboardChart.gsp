
<div class="container">

    <h1>Dashboard</h1>

    <div sytle="height: 400px;">
        <canvas id="chart_loadcurve_suspend" width="400" height="400"></canvas>
    </div>

    <script>

        // TODO use Ajax XHTMLRequest
        // injected data from controller model.
        var builtcurve = ${ raw(chart.toChartjsCurve().toString(false))};
        // build is added by JsonBuilder while using loadcurve was a quick way to return a hashlist
        var curves = builtcurve.build;
        // in javascript asset consoherozh/linky.js
        linkyChart(curves)

    </script>

</div>
