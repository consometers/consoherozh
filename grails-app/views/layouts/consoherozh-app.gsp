<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta charset="UTF-8" />

    <title><g:layoutTitle default="${g.meta(name: 'app.code') }"/></title>
    <link rel="icon" href="${assetPath(src: 'consoherozh-196.png')}">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">

    <script type="module" src="https://unpkg.com/ionicons@5.2.3/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule="" src="https://unpkg.com/ionicons@5.2.3/dist/ionicons/ionicons.js"></script>

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <asset:stylesheet src="consoherozh-app.css"/>
    <asset:javascript src="consoherozh-app.js"/>

    <g:layoutHead/>
</head>
<body class="d-flex flex-column h-100" style="background: #f4f5f7;">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">ConsoHerozh</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <g:link controller="consoHerozh" action="dashboard" class="nav-link active" aria-current="page" href="#">Tableau de bord</g:link>
                </li>
                <li class="nav-item">
                    <g:link controller="dataChallenge" action="personalData" class="nav-link">Mes consentements</g:link>
                </li>
            </ul>
            <div class="d-flex nav-item dropdown navbar-nav">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <sec:username/>
                </a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <sec:ifSwitched>
                        <li><g:link action="exitSwitchUser" controller="user" class="dropdown-item">Revenir à votre session</g:link></li>
                    </sec:ifSwitched>
                    <li><g:link controller="logout" class="dropdown-item">Déconnexion</g:link></li>
                </ul>
            </div>
        </div>
    </div>
</nav>

<main class="flex-shrink-0">
    <div class="container">
        <g:layoutBody/>
    </div>
</main>

<footer class="footer mt-auto py-3" style="background: white; border-top: 1px solid rgb(223, 225, 230);">
    <div class="container">
    <div class="row">
        <div class="col-6 col-md">
            <h5>Contact</h5>
            <ul class="list-unstyled text-small">
                <li><a href="mailto:contact@consoherozh.fr" class="link-secondary">contact@consoherozh.fr</a></li>
                <li><a href="https://www.facebook.com/groups/422285815424568" class="link-secondary">Groupe Facebook</a></li>
                <li><g:link controller="public" action="faq" class="link-secondary">Foire aux Questions</g:link></li>
                <li><g:link controller="public" action="legal" class="link-secondary">Mentions Légales</g:link></li>
                <li><g:link controller="public" action="privacy" class="link-secondary">Données personnelles</g:link></li>
            </ul>
        </div>
        <div class="col-6 col-md">
            <h5>Proposé par</h5>
            <div class="logo-block">
                <a href="https://breizh-alec.bzh/">
                    <asset:image src="breizh-alec-logo.png" alt="Breizh ALEC" width="80"/>
                </a>
            </div>
            <div class="logo-block">
                <a href="https://www.consometers.org/">
                    <asset:image src="consometers-logo.png" alt="Consometers" width="140"/>
                </a>
            </div>
        </div>
        <div class="col-6 col-md">
            <h5>Avec le soutien de</h5>
            <div class="logo-block">
                <a href="https://www.bretagne.bzh/">
                    <asset:image src="bretagne-logo.svg" alt="Région Bretagne" width="70"/>
                </a>
            </div>
            <div class="logo-block">
                <a href="https://www.interregeurope.eu/empower/">
                    <asset:image src="empower-logo.png" alt="Empower" width="140"/>
                </a>
            </div>
        </div>
    </div></div>
</footer>
</body>
</html>