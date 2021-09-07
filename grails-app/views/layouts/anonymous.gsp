<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="${g.meta(name: 'app.code') }"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<link rel="icon" href="${assetPath(src: 'consoherozh-196.png')}">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
		
		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
	</head>
	
	
	<g:if test="${ mobileAgent }">
		<g:set var="bodyClass" value=""/>
	</g:if>
	<g:else>
		<g:set var="bodyClass" value="aui-page-focused ${pageProperty(name: 'body.data-page-size') ?: 'aui-page-size-large'}"/>
	</g:else>
	
	
	<body class="${ bodyClass }" onload="${pageProperty(name: 'body.onload')}" style="${pageProperty(name: 'body.style')}">
		<g:include view="/layouts/headerAnonymous.gsp"/>
		<section id="content" role="main">
			<g:layoutBody/>
		</section>
		<g:include view="/layouts/footer.gsp"/>
	</body>
</html>
