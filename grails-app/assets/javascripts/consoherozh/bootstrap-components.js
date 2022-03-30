const bootstrapStyleTag = `<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1" crossorigin="anonymous">`

// FIXME does not populate slots
// Switch to something like ion modals, append one to the body when needed
// Don’t know why it is done like this but tired of testing broken things

class BootstrapModal extends HTMLElement {

    get template() {
        return cachedTemplates.makeOrGet(this, `
      ${bootstrapStyleTag}
      <div class="modal" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title"></h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <slot name="content"><p>Modal body text goes here, slot does not work.</p></slot>
            </div>
          </div>
        </div>
      </div>`
        );
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.shadowRoot.appendChild(this.template.content.cloneNode(true));
        this.componentProps = {};
    }

    connectedCallback() {
        const el = this.shadowRoot.querySelector('.modal');
        this.instance = new bootstrap.Modal(el, {});
    }

    static get observedAttributes() {
        return ["title", "modal-class"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === "title") {
            this.shadowRoot.querySelector('.modal-title').innerText = newValue;
        } else if (name === "modal-class") {
            this.shadowRoot.querySelector('.modal-dialog').classList.add(newValue);
        }
    }

    show() {
        this.makeComponentIfNeeded();
        this.instance.show();
    }

    hide() {
        this.instance.hide();
    }

    dispose() {
        this.instance.hide();
        this.instance.dispose();
        this.remove();
    }

    set componentName(elementName) {
        this._componentName = elementName;
        this._component = undefined;
    }

    get component() {
        this.makeComponentIfNeeded();
        return this._component;
    }

    makeComponentIfNeeded() {
        if (this._component === undefined) {
            this._component = document.createElement(this._componentName);
            for (const prop in this.componentProps) {
                this._component.setAttribute(prop, this.componentProps[prop]);
            }
            const body = this.shadowRoot.querySelector('.modal-body');
            body.innerHTML = '';
            body.appendChild(this._component);
        }
    }
}

window.customElements.define('bootstrap-modal', BootstrapModal);