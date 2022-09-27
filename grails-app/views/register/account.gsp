<html>
<head>
	<meta name='layout' content='anonymous'/>
</head>

<body>
	<g:applyLayout name="applicationContent">
    
    <h3 class="separator">Création d'un compte <g:meta name="app.code"/></h3>
          
	<g:form action="createAccount" class="aui ${ mobileAgent ? 'top-label' : '' }" autocomplete='off'>
		<fieldset>
	        <div class="field-group">
	            <label for="nom">Nom / pseudo<span class="aui-icon icon-required"> required</span></label>
	            <g:field class="text medium-field" type="text" name="nom" required="true" value="${ command.nom }"/>
	        </div>
	        <div class="field-group">
	            <label for="username">Adresse mail<span class="aui-icon icon-required"> required</span></label>
	            <g:field class="text long-field" type="email" name="username" required="true" value="${ command.username }"/>
	        </div>
	        <div class="field-group">
	            <label for="username">Mot de passe<span class="aui-icon icon-required"> required</span></label>
	            <g:passwordField name="newPassword" class="text medim-field" required="true" pattern=".{8,64}"/>
	            <div class="description">Minimum 8 caractères dont 1 chiffre</div>
	        </div>
	        <div class="field-group">
	            <label for="username">Confirmation<span class="aui-icon icon-required"> required</span></label>
	            <g:passwordField name="confirmPassword" class="text medim-field" required="true" pattern=".{8,64}"/>
	            <div class="description">Minimum 8 caractères dont 1 chiffre</div>
	        </div>
	     </fieldset>
		<fieldset class="group">
			<div class="checkbox">
				<input type="checkbox" name="privacy" value='false' required="required"> En créant votre compte, vous nous autorisez à utiliser vos données pour les finalités décrites sur la <g:link controller="public" action="privacy">page relative aux données personnelles</g:link>.
			</div>
		</fieldset>
		%{-- FIXME cyril do not ask for real name and data sharing at signup --}%
		<g:hiddenField name="prenom" value=" " />
		<g:hiddenField name="profilPublic" value="false" />
	     <br/>

	    <div class="buttons-container">
	        <div class="buttons">
	            <input class="aui-button aui-button-primary" type="submit" value="Envoyer">
	            <g:link uri="/" class="cancel">Retourner à la page d’accueil</g:link>
	        </div>
	    </div>
	</g:form>
	
	</g:applyLayout>
	
</body>
</html>


