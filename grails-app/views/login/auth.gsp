<html>
<head>
	<meta name='layout' content='anonymous'/>
</head>

<body style="background: #0747a6;">
	<g:applyLayout name="applicationContent">

		<asset:image src="banner.jpg" style="width: 100%;" alt="Bienvenue sur ConsoHeroes !"/>

		<div class="aui-group">
			<div class="aui-item">

			<h3 class="separator">1) Visualiser ma conso</h3>

			<g:if test='${flash.message}'>
				<div class="aui-message aui-message-error">
					<p>${flash.message}</p>
				</div>
			</g:if>

			<form action="${postUrl}" method="post" id="d" class="aui login ${ mobileAgent ? 'top-label' : '' }" autocomplete='off'>
				<fieldset>
					<div class="field-group">
						<input class="text" type="email" id="username" name="username" placeholder="Adresse mail" autofocus="true">
					</div>
					<div class="field-group">
						<input class="password" type="password" id="password" name="password" placeholder="Mot de passe" >
					</div>
				 </fieldset>
				 <fieldset class="group">
					<div class="checkbox">
						<input type="hidden" name="${rememberMeParameter}" id="remember_me" value='true'>
					</div>
				</fieldset>
				<div class="buttons-container">
					<div class="buttons">
						<input class="aui-button aui-button-primary" type="submit" value="Me connecter" id="connexion">
					</div>
					<div class="buttons" style="margin-top: 5px;">
						<g:link class="cancel" controller="register" action="forgotPassword">J'ai oublié mon mot de passe</g:link>
					</div>
				</div>
			</form>
				<h3 class="separator" style="margin-top: 10px;"></h3>
				<g:form controller="register" action="account" class="aui login">
					<div class="buttons-container">
					<button class="aui-button aui-button-primary highlight">Créer un compte</button>
					</div>
				</g:form>
			</div>
			<div class="aui-item">
				<h3 class="separator">2) Rejoindre notre communauté</h3>
				<p>Venez découvrir</p>
				<ul>
				<li>🌱 des éco gestes</li>
				<li>👍 des défis</li>
				<li>😀 une communauté</li>
				</ul>
				<p>
				<strong>Notre communauté n'attend plus que VOUS,<br/>
					<a href="https://www.facebook.com/groups/422285815424568">rejoignez-nous sur Facebook</a> !</strong>
				</p>
			</div>
		</div>
	</g:applyLayout>
	
</body>
</html>