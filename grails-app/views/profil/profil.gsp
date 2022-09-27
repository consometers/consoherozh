<html>
<head>
<meta name='layout' content='authenticated' />
</head>

<body>
	<g:applyLayout name="applicationConfigure">

		<g:form name="profil-form" controller="profil" method="post" class="aui ${ mobileAgent ? 'top-label' : '' }">
			<g:hiddenField name="user.id" value="${user.id}" />
	
			<h4>Général</h4>
			
			<fieldset>
				<div class="field-group">
					<label>Email<span class="aui-icon icon-required"> required</span></label>
					<g:textField name="user.username" value="${user.username}" type="email"
						class="text long-field" required="true" disabled="true" />
				</div>
				<div class="field-group">
					<label>Prénom<span class="aui-icon icon-required"> required</span></label>
					<g:textField name="user.prenom" value="${user.prenom}" required="true" 
						class="text long-field"  />
				</div>
				<div class="field-group">
					<label>Nom<span class="aui-icon icon-required">required</span></label>
					<g:textField name="user.nom" value="${user.nom}" class="text long-field"
						required="true" />
				</div>
				<div class="field-group">
					<label >N° téléphone mobile</label>
					<g:textField name="user.telephoneMobile" value="${user.telephoneMobile}" class="text medium-field" />
					<div class="description">Utile pour l'envoi de notifications SMS. Au format +336...</div>
				</div>
			</fieldset>
			<fieldset class="group">
		        <legend><span>Social</span></legend>
		        <div class="checkbox">
		        	<g:checkBox name="user.profilPublic" value="${ user.profilPublic }" class="checkbox"/>
		            <label for="user.profilPublic" class="label">J'autorise les autres utilisateurs <g:meta name="app.code"/> à pouvoir m'envoyer des invitations
		            dans le but de partager les statistiques de ma maison. Vous pouvez ainsi suivre d'autres utilisateurs et comparer vos consommations.
		            <br/>
		            Dans un souci de confidentialité, vos données ne seront visibles à vos amis que si vous acceptez leurs invitations.
		            </label>
		        </div>
		    </fieldset>

			
			<h4>Maison principale</h4>
			<g:include action="templateEditByUser" controller="house" params="[userId: user.id]"/>

			<div id="ajaxModes">
				<g:include action="templateEditByUser" controller="mode" params="[userId: user.id]"/>
			</div>
			
			<h4>Applications</h4>
			
			<fieldset>
				<div class="field-group">
					<label>Application ID<span class="aui-icon icon-required"> required</span></label>
					<h5>${ user.applicationKey }</h5>
					<div class="description">Utilisez cet ID pour connecter des agents à l'application <g:meta name="app.code"/>. Il vous sera demandé dans les identifiants de connexion.</div>
				</div>
			</fieldset>
	
			<div class="buttons-container">
				<div class="buttons">
					<g:actionSubmit value="Enregistrer" action="saveProfil" class="aui-button aui-button-primary" />
				</div>
			</div>
			
		</g:form>

	</g:applyLayout>
</body>
</html>