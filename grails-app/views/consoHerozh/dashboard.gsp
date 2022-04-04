<html>
<head>
    <meta name='layout' content='consoherozh-app' />
</head>
<body>
<h1 class="mb-4 mt-4">Tableau de bord</h1>

<div class="dashboard" style="position: relative; max-width: 1000px">

<counter-connected
  class="draggable"
  title="Linky"
  connect-url="${createLink(controller: "dataChallenge", action: "personalData")}"
  <g:if test="${linky}">
      connected
      view-entries-url="${createLink(controller: "device", action: "deviceChart", params:['device.id': linky.device.id])}"
      <g:if test="${!linky.isConnected()}">expired</g:if>
      idle-power-usage-url="${createLink(controller: "device", action: "idlePowerUsage", params:['device.id': linky.device.id])}"
  </g:if>>
    <ion-icon slot="icon" name="flash-outline"></ion-icon>
    <ion-icon slot="handle" name="reorder-three" class="handle"></ion-icon>
</counter-connected>

<g:if test="${linky}">
<script type="text/javascript">
    querySelectorLast(document, 'counter-connected').entries = new CounterEntries("kWh", "kWh", counterEntriesFromDailyConsumption(
        <%= linkyDays %>
    ));
</script>
</g:if>

<g:if test="${!linky || !linky.isConnected()}">
<counter-manual class="draggable" title="Élec manuel">
    <ion-icon slot="icon" name="flash-outline"></ion-icon>
    <ion-icon slot="handle" name="reorder-three" class="handle"></ion-icon>
</counter-manual>
</g:if>

<script type="text/javascript">
    {
        let entries = new CounterEntries("kWh", "kWh", <%= electricityIndices %>);
        entries.recordUrl = "<%= createLink(controller: "consoHerozh", action: "recordIndex", params:['type': 'Électricité']) %>";
        entries.removeUrl = "<%= createLink(controller: "consoHerozh", action: "removeIndex", params:['type': 'Électricité']) %>";
        querySelectorLast(document, 'counter-manual').entries = entries;
    }
</script>

<counter-manual class="draggable" title="Eau">
    <ion-icon slot="icon" name="water-outline"></ion-icon>
    <ion-icon slot="handle" name="reorder-three" class="handle"></ion-icon>
</counter-manual>

<script type="text/javascript">
    {
        let entries = new CounterEntries("m³", "L", <%= waterIndices %>);
        entries.recordUrl = "<%= createLink(controller: "consoHerozh", action: "recordIndex", params:['type': 'Eau']) %>";
        entries.removeUrl = "<%= createLink(controller: "consoHerozh", action: "removeIndex", params:['type': 'Eau']) %>";
        querySelectorLast(document, 'counter-manual').entries = entries;
    }
</script>

<counter-manual class="draggable" title="Gaz">
    <ion-icon slot="icon" name="flame-outline"></ion-icon>
    <ion-icon slot="handle" name="reorder-three" class="handle"></ion-icon>
</counter-manual>

<script type="text/javascript">
    {
        let entries = new CounterEntries("m³", "m³", <%= gasIndices %>);
        entries.recordUrl = "<%= createLink(controller: "consoHerozh", action: "recordIndex", params:['type': 'Gaz']) %>";
        entries.removeUrl = "<%= createLink(controller: "consoHerozh", action: "removeIndex", params:['type': 'Gaz']) %>";
        querySelectorLast(document, 'counter-manual').entries = entries;
    }
</script>

<div class="card draggable">
    <asset:image src="join-group.jpg" class="card-img-top handle" alt="Bannière ConsoHerozh"/>
    <div class="card-body">
        <p class="card-text">Les ConsoHerozh : Consommateurs d’énergie responsables et informés</p>
    </div>
    <div class="card-footer" style="text-align: right;">
        <a href="https://www.facebook.com/groups/422285815424568/" target="_blank" rel="noopener noreferrer" class="btn btn-outline-secondary" role="button" >
            <ion-icon name="logo-facebook"></ion-icon>
            Rejoignez-nous !
        </a>
    </div>
</div>

</div>

</body>
</html>