<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta charset="UTF-8" />

    <title><g:layoutTitle default="${g.meta(name: 'app.code') }"/></title>
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">

    <script type="module" src="https://unpkg.com/ionicons@5.2.3/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule="" src="https://unpkg.com/ionicons@5.2.3/dist/ionicons/ionicons.js"></script>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js" integrity="sha384-ygbV9kiqUc6oa4msXn9868pTtWMgiQaeYH7/t7LECLbyPA2x65Kgf80OJFdroafW" crossorigin="anonymous"></script>

    <script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js" integrity="sha256-t9UJPrESBeG2ojKTIcFLPGF7nHi2vEc7f5A2KpH/UBU=" crossorigin="anonymous"></script>

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <script src="https://unpkg.com/packery@2/dist/packery.pkgd.min.js"></script>
    <script src="https://unpkg.com/draggabilly@2/dist/draggabilly.pkgd.min.js"></script>


    <asset:stylesheet src="consoherozh.css"/>

    <asset:javascript src="consoherozh.js"/>
    <asset:javascript src="utils.js"/>
    <asset:javascript src="bootstrap-components.js"/>
    <asset:javascript src="components.js"/>
    <asset:javascript src="consoherozh.js"/>
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
                <li><g:link controller="public" action="legal" class="link-secondary">Mentions Légales</g:link></li>
                <li><g:link controller="public" action="privacy" class="link-secondary">Données personnelles</g:link></li>
            </ul>
        </div>
        <div class="col-6 col-md">
            <h5>Proposé par</h5>
            <div class="logo-block">
                <a href="https://www.aloen.fr/">
                    <asset:image src="aloen-logo.png" width="80"/>
                </a>
            </div>
            <div class="logo-block">
                <a href="https://www.consometers.org/">
                    <asset:image src="consometers-logo.png" width="140"/>
                </a>
            </div>
        </div>
        <div class="col-6 col-md">
            <h5>Avec le soutien de</h5>
            <div class="logo-block">
                <a href="https://www.bretagne.bzh/">
                    <asset:image src="bretagne-logo.svg" alt="Empower" width="70"/>
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