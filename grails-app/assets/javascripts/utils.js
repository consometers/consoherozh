function parseIsoDate(dateStr) {
    if (dateStr instanceof Date) {
        return dateStr;
    }
    const parts = dateStr.split('-').map(x => parseInt(x, 10));
    return new Date(parts[0], parts[1]-1, parts[2], 0, 0, 0);
}

function formatIsoDate(date) {
    var dd = String(date.getDate()).padStart(2, '0');
    var mm = String(date.getMonth() + 1).padStart(2, '0');
    var yyyy = date.getFullYear();
    return `${yyyy}-${mm}-${dd}`;
}

function formatDate(dateStr, options={year:'ifDifferent', month:'long', day: true}) {
    const date = parseIsoDate(dateStr);
    // const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    // return date.toLocaleDateString('fr-FR', options);
    let parts = [];

    if (('day' in options) && options['day']) {
        parts.push((date.getDate() == 1) ? '1er' : date.getDate().toString());
    }

    if ('month' in options) {
        if (options['month'] == 'short') {
            const monthsShort = [
                "janv.", "févr.", "mars", "avr.", "mai", "juin",
                "juill.", "août", "sept.", "oct.", "nov.", "déc."
            ];
            parts.push(monthsShort[date.getMonth()]);
        } else {
            const monthsLong = [
                "janvier", "février", "mars", "avril", "mai", "juin",
                "juillet", "août", "septembre", "octobre", "novembre", "décembre"
            ];
            parts.push(monthsLong[date.getMonth()]);
        }
    }

    if ('year' in options) {
        if ((options['year'] === true) || ((options['year'] === 'ifDifferent') && (date.getYear() !== (new Date()).getYear()))) {
            parts.push(date.getFullYear());
        }
    }

    return parts.join(' ');
}

function isLastDayOfMonth(date) {
    var test = new Date(date);
    month = test.getMonth();
    test.setDate(test.getDate() + 1);
    return test.getMonth() !== month;
}

function formatDateRange(from, to) {
    if (from.getYear() == to.getYear()) {
        if (from.getMonth() == to.getMonth()) {
            if (from.getDate() == to.getDate()) {
                return `le ${formatDate(from, {day: true, month: 'long'})}`;
            } else {
                if (isLastDayOfMonth(to)) {
                    return formatDate(from, {month: 'long'});
                } else {
                    return `du ${formatDate(from, {day: true})} au ${formatDate(to, {day: true, month: 'long'})}`;
                }
            }
        }
    }
    return `du ${formatDate(from, {day: true, month: 'short'})} au ${formatDate(to, {day: true, month: 'short'})}`;
}

formatDateRangeTests = [
    {from: "2020-11-02", to: "2020-11-02", expected: "le 2 novembre"},
    {from: "2020-11-01", to: "2020-11-29", expected: "du 1er au 29 novembre"},
    {from: "2020-12-01", to: "2020-12-31", expected: "décembre"},
    {from: "2020-10-17", to: "2020-11-12", expected: "du 17 oct. au 12 nov."}
];

for (let test of formatDateRangeTests) {
    let from = parseIsoDate(test.from);
    let to = parseIsoDate(test.to);
    let formatted = formatDateRange(from, to);
    console.assert(formatted == test.expected, formatted, test.expected);
}

function formatValue(value, options={}) {
    var toLocaleOptions = ['minimumFractionDigits', 'maximumFractionDigits'];
    toLocaleOptions = Object.keys(options)
        .filter(key => toLocaleOptions.includes(key))
        .reduce((obj, key) => {
            obj[key] = options[key];
            return obj;
        }, {});
    value = value.toLocaleString('fr', toLocaleOptions);
    if ('unit' in options) {
        value = value + ' ' + options.unit;
    }
    return value;
}

function querySelectorLast(element, selector) {
    let elements = element.querySelectorAll(selector);
    return elements[elements.length - 1];
}

class CachedTemplates {

    constructor() {
        this.cache = new WeakMap();
    }

    makeOrGet(element, innerHTML) {
        const classId = element.constructor;
        if (!(classId in this.cache)) {
            const template = document.createElement('template');
            template.innerHTML = innerHTML;
            this.cache[classId] = template;
        }
        return this.cache[classId];
    }
}

const cachedTemplates = new CachedTemplates();

class Observers {

    constructor() {
        this._callbacks = new Set();
    }

    add(callback) {
        this._callbacks.add(callback);
    }

    remove(observer) {
        this._callbacks.delete(callback);
    }

    notifyAll(data) {
        for (let callback of this._callbacks) {
            callback(data);
        }
    }
}

// Takes care of subscribing / unsubscribing when another observed / callback is set
class Observer {

    constructor() {
        this._observed = null;
        this._onUpdateCallback = null;
        this._onSetCallback = null;
    }

    set observed(newObserved) {
        if (this._observed === newObserved) {
            return;
        }

        if (this._onUpdateCallback !== null) {
            if (this._observed !== null) {
                this._observed.observers.remove(this._onUpdateCallback);
            }

            if (newObserved !== null ) {
                newObserved.observers.add(this._onUpdateCallback);
            }
        }

        this._observed = newObserved;

        if (this._onSetCallback !== null) {
            this._onSetCallback(this._observed);
        }
    }

    get observed() {
        return this._observed;
    }

    set onUpdate(newUpdateCallback) {
        if (this._onUpdateCallback === newUpdateCallback) {
            return;
        }

        if (this._observed !== null) {
            if (this._onUpdateCallback !== null) {
                this._observed.observers.remove(this._onUpdateCallback);
            }

            if (newUpdateCallback !== null ) {
                this._observed.observers.remove(newUpdateCallback);
            }
        }

        this._onUpdateCallback = newUpdateCallback;
    }

    set onSet(callback) {
        this._onSetCallback = callback;
    }
}