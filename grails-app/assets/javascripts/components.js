
// Uses slot for handle because Draggabilly cannot select them in shadowDom
// FIXME make dumb component and a presenter / view model?
class CardEnergyTrend extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <style>
      .card-header, .card-footer {
        background-color: rgba(0,0,0,.02);
      }
      .card-body h5 {
        text-align: center;
        margin-bottom: 1rem;
        font-size: 1.5rem;
      }
      table {
        width: 100%;
      }
      #header-icon {
        font-size: 48px;
        height: 48px;
      }
      .trend-good .trend-colorize {
        color: seagreen;
      }
      .trend-bad .trend-colorize {
        color: orangered;
      }
      td {
        padding: 5px 0;
      }
      #handle {
        text-align: right;
        font-size: 32px;
        color:#ddd;
      }
      </style>
      <div class="card">
        <div class="card-header">
          <div class="row g-0">
            <div class="col">
            </div>
            <div class="col-6" style="display: flex; align-items: center; justify-content: center;">
              <h5 class="trend-colorize" id="title">Énergie</h5>
            </div>
            <div class="col" id="handle">
              <slot name="handle"></slot>
            </div>
          </div>
          <p class="text-center trend-colorize" id="header-icon">
            <slot name="icon"></slot>
          </p>
        </div>
        <div class="card-body">
          <h5 id="trend-text" class="trend-colorize"></h5>
          <table>
            <tbody></tbody>
          </table>
          <div id="no-data-placeholder" class="text-muted">
            <slot name="no-data-placeholder"><p>Aucune donnée disponible.</p></slot>
          </div>
        </div>
        <slot name="footer"></slot>
      </div>`);
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));
        this._entriesObserver = new Observer();
        this._entriesObserver.onSet = () => { this._render() };
        this._entriesObserver.onUpdate = () => { this._render() };
    }

    get entries() {
        return this._entriesObserver.observed;
    }

    set entries(newEntries) {
        return this._entriesObserver.observed = newEntries;
    }

    connectedCallback() {}

    disconnectedCallback() {}

    static get observedAttributes() {
        return ["title"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === "title") {
            this.shadowRoot.querySelector('#title').innerText = newValue;
        }
    }

    _renderTrendClass(trendClass) {
        const element = this.shadowRoot.querySelector('div.card');
        for (let className of element.classList) {
            if (className.startsWith('trend-')) {
                element.classList.remove(className);
            }
        }
        if (trendClass) {
            element.classList.add(trendClass);
        }
    }

    _renderTrendText(text) {
        const element = this.shadowRoot.querySelector('#trend-text');
        if (text) {
            element.innerHTML = text;
            element.style.display = "block";
        } else {
            element.innerHTML = '';
            element.style.display = "none";
        }
    }

    _render() {
        // Compute trend based on the two most recent ranges

        // TODO do no use private api
        let latestEntries = this.entries._entries.slice(this.entries.length-2);
        latestEntries.reverse();

        let latestConsumption = null;
        let trendPercent = null;

        for (const entry of latestEntries) {
            if (entry.meanConsumption !== null) {
                if (latestConsumption == null) {
                    latestConsumption = entry.meanConsumption;
                } else {
                    trendPercent = 100.0 * (latestConsumption - entry.meanConsumption) / entry.meanConsumption;
                    break;
                }
            }
        }

        // Render trend

        let text = null;
        let className = null;

        if (trendPercent != null) {
            text = formatValue(trendPercent, {unit: '%', maximumFractionDigits: 1})
            if (trendPercent > 0) {
                className = "trend-bad";
                text = `<ion-icon name="trending-up"></ion-icon> +${text}`;
            } else if (trendPercent < 0) {
                className = "trend-good";
                text = `<ion-icon name="trending-down"></ion-icon> ${text}`;
            }
            let emoji = null;
            if (trendPercent <= -50) {
                emoji = "🦄";
            } else if (trendPercent < 0) {
                emoji = "👍";
            } else if (trendPercent < 2) {
                emoji = "😇";
            } else if (trendPercent < 100) {
                emoji = "🤔";
            } else {
                emoji = "😲";
            }
            text = `${text} ${emoji}`;
        }

        this._renderTrendClass(className);
        this._renderTrendText(text);

        // Render entries

        const table = this.shadowRoot.querySelector('table');
        const tbody = this.shadowRoot.querySelector('tbody');
        const noDataPlaceHolder = this.shadowRoot.getElementById('no-data-placeholder');
        tbody.innerHTML = '';

        let firstLine = true;

        if (latestEntries.length > 1) {
            table.style.display = 'table';
            noDataPlaceHolder.style.display = 'none';
            for (const entry of latestEntries) {
                if (entry.meanConsumption === null) {
                    break;
                }
                const tr = document.createElement('tr');
                tbody.appendChild(tr)
                if (firstLine) {
                    tr.classList.add("trend-colorize");
                    firstLine = false;
                } else {
                    tr.classList.add("text-muted");
                }

                const date_td = document.createElement('td');
                date_td.innerText = formatDateRange(new Date(entry.datePrev.getTime() + 86400000), entry.date);
                tr.appendChild(date_td)

                const value_td = document.createElement('td');
                value_td.style.textAlign = 'right';
                if (entry.meanConsumption != null) {
                    value_td.innerText = formatValue(entry.meanConsumption, {
                        unit: this.entries.humanUnit + '/j',
                        minimumFractionDigits: 1,
                        maximumFractionDigits: 1});
                } else {
                    value_td.innerText = "non disponible";
                }
                tr.appendChild(value_td);
            }
        } else {
            table.style.display = 'none';
            noDataPlaceHolder.style.display = 'block';
        }
    }
}

window.customElements.define('card-energy-trend', CardEnergyTrend);

class CounterManual extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <card-energy-trend>
        <div slot="icon"><slot name="icon"></slot></div>
        <div slot="handle"><slot name="handle"></slot></div>
        <p slot="no-data-placeholder"></p>
        <div slot="footer" class="card-footer text-secondary">
          <div class="row g-0">
            <div class="col" style="text-align: left; height: 32px;">
              <a id="view-entries" role="button"><ion-icon name="bar-chart-outline" style="font-size: 32px;"></ion-icon></a>
            </div>
            <div class="col-9" style="text-align: right;">
              <button type="button" class="btn btn-outline-secondary" id="add-entry">Nouveau relevé</button>
            </slot>
            </div>
          </div>
        </div>
      </card-energy-trend>`
        );
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));

        this.trendCard = this.shadowRoot.querySelector('card-energy-trend');

        let addEntryButton = this.shadowRoot.getElementById('add-entry');
        addEntryButton.addEventListener("click", () => {
            this.entryFormModal.show();
        });

        let viewEntriesButton = this.shadowRoot.getElementById('view-entries');
        viewEntriesButton.addEventListener("click", () => {
            this.entriesModal.show();
        });
        this._entriesObserver = new Observer();
        this._entriesObserver.onSet = (newEntries) => {
            this.trendCard.entries = newEntries;
            if (this._entriesModal !== undefined) {
                this._entriesModal.component.entries = newEntries;
            }
            this._render();
        };
        this._entriesObserver.onUpdate = () => { this._render() };
    }

    get entries() {
        return this._entriesObserver.observed;
    }

    set entries(newEntries) {
        return this._entriesObserver.observed = newEntries;
    }

    static get observedAttributes() {
        return ["title"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        this.trendCard.setAttribute(name, newValue);
    }

    get entryFormModal() {
        if (this._entryFormModal=== undefined) {
            // create the modal with the `modal-page` component
            this._entryFormModal = document.createElement('bootstrap-modal');
            this._entryFormModal.componentName = 'counter-manual-entry-form';
            this._entryFormModal.setAttribute("title", `Nouveau relevé « ${this.getAttribute("title")} »`)

            if (this.entries.length > 0) {
                this._entryFormModal.componentProps['previousValue'] = this.entries.last.value;
            }
            this._entryFormModal.componentProps['unit'] = this.entries.indexUnit;

            document.body.appendChild(this._entryFormModal);

            this._entryFormModal.component.addEventListener('cancel', () => {
                this._entryFormModal.hide();
            });
            this._entryFormModal.component.form.addEventListener('submit', (e) => {
                e.preventDefault();
                var date = this._entryFormModal.component.form.querySelector('#date').value;
                var value = this._entryFormModal.component.form.querySelector('#index').value;
                this.entries.add({date: date, value: value});
                this._entryFormModal.dispose();
                this._entryFormModal = undefined;
                return false;
            });
        }
        return this._entryFormModal;
    }

    get entriesModal() {
        if (this._entriesModal === undefined) {
            // create the modal with the `modal-page` component
            this._entriesModal = document.createElement('bootstrap-modal');
            this._entriesModal.componentName = 'counter-manual-entries-table';
            this._entriesModal.setAttribute("title", `Conso du compteur « ${this.getAttribute("title")} »`)

            document.body.appendChild(this._entriesModal);

            this._entriesModal.component.entries = this.entries;
            this._entriesModal.setAttribute("modal-class", "modal-fullscreen-md-down");
        }
        return this._entriesModal;
    }

    _render() {
        const detailsIcon = this.shadowRoot.querySelector('.card-footer ion-icon');
        if (this.entries.length < 1) {
            detailsIcon.style.display = 'none';
        } else {
            detailsIcon.style.display = 'block';
        }
        let noDataPlaceholder = this.shadowRoot.querySelector('p');
        if (this.entries.length == 0) {
            noDataPlaceholder.innerText = `Aucun relevé manuel.`;
            if (this._entriesModal) {
                this._entriesModal.hide();
            }
        } else if (this.entries.length == 1) {
            noDataPlaceholder.innerText = `Premier relevé le ${formatDate(this.entries.first.date, {year:'ifDifferent', month:'long', day: true})}.`;
        }
    }
}

window.customElements.define('counter-manual', CounterManual);

class CounterManualEntryForm extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <style>
        .footer * {
          margin: 0.25rem;
        }
        .footer {
          display: flex;
          flex-wrap: wrap;
          flex-shrink: 0;
          align-items: center;
          justify-content: flex-end;
        }
      </style>
      <form>
        <div class="mb-3">
          <label for="date" class="form-label">Date du relevé</label>
          <input type="date" class="form-control" id="date" required>
        </div>
        <div class="mb-3">
          <label for="index" class="form-label"></label>
          <input type="number" class="form-control" id="index" aria-describedby="indexHelp" required>
          <div id="indexHelp" class="form-text"></div>
        </div>
        <div class="mb-3 text-right footer">
          <button type="button" class="btn btn-secondary" id="dismiss-btn">Annuler</button>
          <button type="submit" class="btn btn-primary">Valider</button>
        </div>
      </form>`
        );
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));
        this.shadowRoot.getElementById('dismiss-btn').addEventListener("click", () => {
            this.dispatchEvent(new Event('cancel'));
        })
        this.form = this.shadowRoot.querySelector('form');
    }

    connectedCallback() {
        this._render();
    }

    _render() {

        const dateInput = this.shadowRoot.getElementById('date');
        if (!dateInput.value) {
            dateInput.value = formatIsoDate(new Date());
        }

        const unit = this.getAttribute("unit");

        this.shadowRoot.querySelector("label[for='index']").innerText =
            `Index de mon compteur (${unit})`;

        if (this.hasAttribute("previousValue")) {
            const previousValue = formatValue(parseInt(this.getAttribute("previousValue"), 10));
            const help = `Dernier relevé ${previousValue} ${unit}`;
            this.shadowRoot.getElementById("indexHelp").innerText = help;
        }
    }

    static get observedAttributes() {
        return ["previousValue"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        this._render();
    }
}

window.customElements.define('counter-manual-entry-form', CounterManualEntryForm);

class CounterEntries {
    constructor(indexUnit, humanUnit, entries) {
        this.observers = new Observers();
        this.indexUnit = indexUnit;
        this.humanUnit = humanUnit;
        this.entries = entries;
    }

    _computeEntries() {
        for (let entry of this._entries) {
            entry.date = parseIsoDate(entry.date);
        }

        this._entries.sort((a, b) => (a.date > b.date) ? 1 : -1)

        let previousEntry = null;

        for (let entry of this._entries) {
            if (previousEntry) {
                entry.datePrev = previousEntry.date;
                entry.consumption = entry.value - previousEntry.value;

                if ((this.humanUnit === "L") && (this.indexUnit === "m³")) {
                    entry.consumption = entry.consumption * 1000;
                }

                const oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds
                const days = Math.round(Math.abs((entry.date - entry.datePrev) / oneDay));
                entry.meanConsumption = entry.consumption / days;
            } else {
                entry.datePrev = null;
                entry.consumption = null;
                entry.meanConsumption = null;
            }
            previousEntry = entry;
        }
    }

    set entries(entries) {
        this._entries = entries;
        this._computeEntries();
        this.observers.notifyAll(this);
    }

    add(entry) {
        this._entries.push(entry);
        this._computeEntries();
        if (this.recordUrl) {
            let url = `${this.recordUrl}&date=${formatIsoDate(entry.date)}&index=${entry.value}`;
            let req = new XMLHttpRequest();
            req.onload = () => { console.log("Saved entry") };
            req.open("get", url, true);
            req.send();
        }
        this.observers.notifyAll(this);
    }

    remove(entry) {
        let index = this._entries.indexOf(entry);
        if (index !== -1) {
            this._entries.splice(index, 1);
        }
        this._computeEntries();
        if (this.removeUrl) {
            let url = `${this.removeUrl}&date=${formatIsoDate(entry.date)}`;
            let req = new XMLHttpRequest();
            req.onload = () => { console.log("Saved entry") };
            req.open("get", url, true);
            req.send();
        }
        this.observers.notifyAll(this);
    }

    get length() {
        return this._entries.length;
    }

    get first() {
        return this._entries[0];
    }

    get last() {
        return this._entries[this._entries.length-1];
    }

    aslist() {
        return this._entries.slice();
    }

    [Symbol.iterator]() { return this._entries.values(); }
}

class CounterManualEntriesTable extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <style>
        a ion-icon {
          font-size: 24px;
        }
        #chart-container {
          position: relative;
          width: 100%;
          height: 200px;
        }
        td, th {
            text-align: center;
        }
      </style>

      <div style="display: block;" id="chart-container">
        <canvas id="myChart" width="400" height="200"></canvas>
      </div>

      <table class="table table-hover">
        <thead>
          <tr>
            <th>Date</th>
            <th>Index (<span id="index-unit"></span>)</th>
            <th>Conso</th>
            <th>Moyenne</th>
            <th></th>
          <tr>
        </thead>
        <tbody></tbody>
      </table>`
        );
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));
        var ctx = this.shadowRoot.getElementById('myChart').getContext('2d');
        this.chart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [],
                datasets: [{
                    label: 'Moyenne journalière',
                    data: [],
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                legend: {
                    display: false
                },
                tooltips: {
                    callbacks: {
                        label: (tooltipItem) => {
                            let value = formatValue(tooltipItem.yLabel, {
                                unit: this.entries.humanUnit,
                                minimumFractionDigits: 0,
                                maximumFractionDigits: 2});
                            return `Moyenne: ${value}/jour`;
                        }
                    }
                },
                scales: {
                    xAxes: [{
                        offset: true,
                        gridLines: {
                            offsetGridLines: false
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            beginAtZero: true,
                        }
                    }]
                }
            }
        });
        this._entriesObserver = new Observer();
        this._entriesObserver.onSet = () => { this._render() };
        this._entriesObserver.onUpdate = () => { this._render() };
    }

    get entries() {
        return this._entriesObserver.observed;
    }

    set entries(newEntries) {
        return this._entriesObserver.observed = newEntries;
    }

    _render() {
        this.shadowRoot.getElementById('index-unit').innerText = this.entries.indexUnit;

        const tbody = this.shadowRoot.querySelector('tbody');
        tbody.innerHTML = '';

        for (let entry of this.entries.aslist().reverse()) {
            const tr = document.createElement('tr');
            tbody.appendChild(tr);

            const tdDate = document.createElement('td');
            tdDate.innerText = formatDate(entry.date, {day: true, month: 'short'});
            tr.appendChild(tdDate);

            const tdIndex = document.createElement('td');
            tdIndex.innerText = formatValue(entry.value, {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0});
            tr.appendChild(tdIndex);

            const tdConsumption = document.createElement('td');
            if (entry.consumption !== null) {
                tdConsumption.innerText = formatValue(entry.consumption, {
                    unit: this.entries.humanUnit,
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 0});
            }
            tr.appendChild(tdConsumption);

            const tdMean = document.createElement('td');
            if (entry.meanConsumption !== null) {
                tdMean.innerText = formatValue(entry.meanConsumption, {
                    unit: this.entries.humanUnit + '/jour',
                    minimumFractionDigits: 1,
                    maximumFractionDigits: 1});
            }
            tr.appendChild(tdMean);

            const tdDelete = document.createElement('td');
            const a = document.createElement('a');
            a.setAttribute("role", "button");
            a.classList.add("text-secondary");
            a.innerHTML = '<ion-icon name="trash-outline"></ion-icon>';
            a.addEventListener('click', () => {
                this.entries.remove(entry);
            })
            tdDelete.appendChild(a);
            tr.appendChild(tdDelete);
        }

        const chartContainer = this.shadowRoot.getElementById('chart-container');

        if (this.entries.length > 2) {
            chartContainer.style.display = 'block';

            this.chart.data.labels = [];
            this.chart.data.datasets[0].data = [];

            for (let entry of this.entries) {
                if (entry.meanConsumption !== null) {
                    const date = formatDate(entry.date, {day: true, month: 'short', year: true});
                    this.chart.data.labels.push(date);
                    this.chart.data.datasets[0].data.push(entry.meanConsumption);
                }
            }
            this.chart.update();
        } else {
            chartContainer.style.display = 'none';
        }
    }
}

window.customElements.define('counter-manual-entries-table', CounterManualEntriesTable);

class CounterConnected extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <card-energy-trend>
        <div slot="icon"><slot name="icon"></slot></div>
        <div slot="handle"><slot name="handle"></slot></div>
        <p slot="no-data-placeholder" id="no-data-placeholder"></p>
        <div slot="footer">
          <div class="card-body alert-warning" id="alert-warning"></div>
          <div class="card-footer text-secondary">
            <div class="row g-0">
              <div class="col" style="text-align: left; height: 32px;">
                <a href="#" role="button" id="view-entries-btn" class="text-secondary">
                  <ion-icon name="bar-chart-outline" style="font-size: 32px;"></ion-icon>
                </a>
              </div>
              <div class="col-9" style="text-align: right;">
                <a href="#" role="button" id="connect-btn" class="btn btn-outline-secondary"></a>
              </div>
            </div>
          </div>
        </div>
      </card-energy-trend>`
        );
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));

        this.trendCard = this.shadowRoot.querySelector('card-energy-trend');
        this._entriesObserver = new Observer();

        this._entriesObserver.onSet = (newEntries) => {
            this.trendCard.entries = newEntries;
            this._render();
        };
        this._entriesObserver.onUpdate = () => { this._render() };

        this._render();
    }

    get entries() {
        return this._entriesObserver.observed;
    }

    set entries(newEntries) {
        return this._entriesObserver.observed = newEntries;
    }

    static get observedAttributes() {
        return ["title", "connected", "expired", "connect-url", "view-entries-url"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === "title") {
            this.trendCard.setAttribute(name, newValue);
        } else if (name === "connected" || name === "expired") {
            this._render();
        } else if (name === "connect-url") {
            this.shadowRoot.getElementById("connect-btn").setAttribute("href", newValue);
        } else if (name === "view-entries-url") {
            this.shadowRoot.getElementById("view-entries-btn").setAttribute("href", newValue);
        }
    }

    _render() {
        const viewEntries = this.shadowRoot.getElementById('view-entries-btn');
        const connect = this.shadowRoot.getElementById('connect-btn');
        const noDataPlaceholder = this.shadowRoot.getElementById('no-data-placeholder');
        const alertWarning = this.shadowRoot.getElementById('alert-warning');

        if (!this.hasAttribute("connected")) {
            connect.style.display = 'initial';
            connect.innerText = 'Connecter';
            noDataPlaceholder.innerText = `Compteur communicant non connecté à l’application.`;
        } else {
            connect.style.display = 'none';
            noDataPlaceholder.innerText = `Données en cours de récupération…`;
        }

        if (this.hasAttribute("expired")) {
            connect.style.display = 'initial';
            connect.innerText = 'Reconnecter';
            alertWarning.innerText = `La connexion avec le compteur communicant a expiré.`;
            alertWarning.style.display = 'block';
        } else {
            alertWarning.style.display = 'none';
        }

        if (this.entries) {
            viewEntries.style.display = 'initial';
        } else {
            viewEntries.style.display = 'none';
        }
    }
}

window.customElements.define('counter-connected', CounterConnected);