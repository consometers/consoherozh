<html>
<head>
    <meta name='layout' content='authenticated-chart' />
</head>

<body class="aui-page-focused">

<g:applyLayout name="applicationContent">

	<g:if test="${ linky && linky.isConnected() }">

		<h3>Vous avez autorisé le transfert de vos données Enedis</h3>

		<p>Le transfert de vos consommations électrique vers Breizh ALEC est activé. Ce site est autorisé à collecter les données Enedis pour que vous puissiez disposer d’informations sur votre consommation d’électricité.</p>
		<p>Vous pouvez à tout moment annuler votre consentement sur votre <a href="http://espace-client-particuliers.enedis.fr/">espace client Enedis</a>.</p>

	</g:if>
	<g:else>

		<h2><g:meta name="app.code"/> souhaite accéder à vos données de consommation</h2>

		<p>
		Pour vous permettre de visualiser ces données et vous aider à réduire votre consommation énergétique,
		nous souhaitons accéder à votre consommation électrique des deux dernières années.
		</p>

		<p>
			Ces données mesurées par votre compteur communiquant nous sont fournies par Enedis,
			après recueil de votre consentement, au pas minimal de 10 minutes.
		</p>

		<h3>Autoriser le transfert de vos données Enedis</h3>

		<p>
			<strong>Breizh ALEC</strong> est l’éditeur du site <g:meta name="app.code"/>.
		</p>
		<p>
			<strong>Enedis</strong> gère le réseau d’électricité jusqu’au compteur d’électricité.
		</p>
		<p>
			En cliquant sur ce bouton, vous allez accéder à votre compte personnel
			Enedis où vous pourrez donner votre accord pour qu’Enedis transmette vos données à Breizh ALEC.
		</p>
		<p style="text-align: center; margin-top: 40px;">
			<g:link action="authorize" controller="dataConnect">
				<asset:image src="bleu-enedis.png" width="200px"/>
			</g:link>
		</p>

	</g:else>

</g:applyLayout>

</body>
</html>