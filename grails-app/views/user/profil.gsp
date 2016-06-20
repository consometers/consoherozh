<%@ page import="smarthome.automation.notification.NotificationAccountEnum" %>

<html>
<head>
<meta name='layout' content='authenticated' />
</head>

<body>
	<g:applyLayout name="applicationHeader">
		<div class="aui-page-header-image">
			<div class="aui-avatar aui-avatar-medium">
				<div class="aui-avatar-inner">
					<g:link controller="user" action="profil">
						<asset:image src="ico_add_avatar.png" />
					</g:link>
				</div>
			</div>
		</div>
		<div class="aui-page-header-main">
			<h3>
				<g:link controller="user" action="profil">${user.prenom} ${user.nom}</g:link>
			</h3>
		</div>
		<div class="aui-page-header-actions">
			<g:form controller="user" >
				<div class="aui-buttons">
					<g:actionSubmit class="aui-button" value="Changer mon mot de passe" action="password"/>
				</div>
			</g:form>
		</div>
	</g:applyLayout>

	<g:applyLayout name="applicationContent">
		<g:form controller="user" method="post" class="aui">
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
			</fieldset>

			
			<h4>Description maison</h4>
			
			<g:if test="${ house?.id }">
				<g:hiddenField name="house.id" value="${house.id}" />
			</g:if>
			
			<fieldset>
				<div class="field-group">
					<label>Surface (en m²)</label>
					<g:field name="house.surface" type="number" value="${house?.surface}" class="text medium-field" />
					<div class="description">Utilisé pour le calcul du classement énergétique</div>
				</div>
				<div class="field-group">
					<label>Compteur principal</label>
					<g:select name="house.compteur.id" value="${house?.compteur?.id}" from="${ compteurs }" optionKey="id" 
						optionValue="label" class="select" noSelection="[null: '']"/>
					<div class="description">Utilisé pour le calcul du classement énergétique</div>
				</div>
			</fieldset>
			
			
			<h4>Notifications SMS</h4>
			
			<fieldset>
				<div class="field-group">
					<label >N° téléphone mobile</label>
					<g:textField name="user.telephoneMobile" value="${user.telephoneMobile}" class="text medium-field" />
					<div class="description">Ne sert exclusiment qu'aux notifications SMS</div>
				</div>
			</fieldset>

			<fieldset>
				<div class="field-group">
					<label >Compte</label>
					<div>
						<g:if test="${ smsAccount }">
							${ smsAccount.senderInstance.description } 
							<g:remoteLink url="[controller: 'notificationAccount', action: 'dialogNotificationAccount', id: smsAccount.id]"
								update="ajaxDialog" onComplete="showNotificationAccountDialog()" class="aui-button aui-button-link">Modifier le compte</g:remoteLink>
						</g:if>
						<g:else>
							Vous n'avez pas encore configuré de compte pour l'envoi des SMS. Veuillez cliquer sur le bouton suivant pour en créer un.
							<g:remoteLink url="[controller: 'notificationAccount', action: 'dialogNotificationAccount', params: [typeNotification: NotificationAccountEnum.sms]]"
								update="ajaxDialog" onComplete="showNotificationAccountDialog()" class="aui-button aui-button-link">Créer un compte</g:remoteLink> 
						</g:else>
					</div>
				</div>
			</fieldset>
			
			
			
			<h4>Applications</h4>
			
			<fieldset>
				<div class="field-group">
					<label>Application ID<span class="aui-icon icon-required"> required</span></label>
					<h5>${ user.applicationKey }</h5>
					<div class="description">Utilisez cet ID pour connecter des agents à l'application SmartHome. Il vous sera demandé dans les identifiants de connexion.</div>
				</div>
			</fieldset>
	
			<div class="buttons-container">
				<div class="buttons">
					<g:actionSubmit value="Enregistrer" action="saveProfil" class="aui-button aui-button-primary" />
					<g:link uri="/" class="cancel">Annuler</g:link>
				</div>
			</div>
			
		</g:form>
	</g:applyLayout>
</body>
</html>