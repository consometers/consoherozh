<html>
<head>
    <meta name='layout' content='authenticated' />
</head>

<body>

<g:applyLayout name="applicationConfigure">

<g:form >
    <fieldset>
    <div class="field-group">
        <label for="libelle">Libelle<span
                class="aui-icon icon-required"> required</span></label>
        <g:textField class="text long-field" name="libelle" value=""/>
    </div>
    </fieldset>
    <g:actionSubmit class="aui-button" value="Ajouter ce chauffage" action="add"/>
</g:form>

    <div style="overflow-x:auto;">
        <app:datatable datatableId="datatable" recordsTotal="${ recordsTotal }">
            <thead>
            <tr>
                <th>Libelle</th>
            </tr>
            </thead>
            <tbody>
            <g:each var="chauffage" in="${ chauffages }">
                <tr>
                    <td>${chauffage.libelle}</td>
                </tr>
            </g:each>
            </tbody>
        </app:datatable>
    </div>

</g:applyLayout>

</body>