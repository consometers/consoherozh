<html>
<head>
	<meta name='layout' content='anonymous'/>
</head>

<body data-page-size="aui-page-size-medium">
	<g:applyLayout name="applicationContent">
    
    <g:if test="${ command.error }">
    	<g:render template="/templates/messageError" model="[message: command.error]"/>
    </g:if>
    
    <h3 class="separator"><g:meta name="app.code"/></h3>
    
	<g:form action="authenticate" controller="googleAction" class="aui ${ mobileAgent ? 'top-label' : '' }" autocomplete="off">
	
		<g:hiddenField name="client_id" value="${ command.client_id }"/>
		<g:hiddenField name="redirect_uri" value="${ command.redirect_uri }"/>
		<g:hiddenField name="state" value="${ command.state }"/>
		<g:hiddenField name="response_type" value="${ command.response_type }"/>
		<g:hiddenField name="scope" value="${ command.scope }"/>
	
		<fieldset>
	        <div class="field-group" style="padding-bottom:15px">
	            <label></label>
	            <div>
	            	<h5>Google requiert votre autorisation</h5>
	            	<ul>
				    	<g:each var="scope" in="${ command.scope?.split(' ') }">
				    		<li>${ scope }</li>
				    	</g:each>
				    </ul>
	            </div>
	        </div>
	        <div class="field-group">
	            <label for="username">Adresse mail<span class="aui-icon icon-required"> required</span></label>
	            <input class="text" type="email" id="username" name="username" placeholder="yourmail@example.com" autofocus="true">
	        </div>
	        <div class="field-group">
	            <label for="password1" accesskey="p">Mot de passe<span class="aui-icon icon-required"> required</span></label>
	            <input class="password" type="password" id="password" name="password">
	        </div>
	     </fieldset>
	    
	    <div class="buttons-container">
	        <div class="buttons">
	            <input class="aui-button aui-button-primary" type="submit" value="S'authentifier" id="connexion">
	            <g:link class="cancel" controller="register" action="forgotPassword">J'ai oublié mon mot de passe</g:link>
	        </div>
	    </div>
	</g:form>
	</g:applyLayout>
	
</body>
</html>