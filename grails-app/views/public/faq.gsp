<html>
<head>
  <meta name='layout' content='anonymous'/>
</head>

<body>
<g:applyLayout name="applicationContent">

  <h1>Foire aux Questions</h1>

  <h2>Comment me connecter à mon linky/ à mes données ?</h2>
  <p>
    La récupération des données issues de votre compteur Linky dans Consoherozh se fait en deux étapes : l'activation chez Enedis, puis la connexion entre Enedis et Consoherozh.
  </p>

  <h4>L'activation de mes données chez Enedis</h4>
  <p>
  Il faut tout d'abord disposer d'un compte (gratuit) auprès du service Enedis. Vous pouvez créer et gérer votre espace à l'adresse suivante : <a href="https://mon-compte-particulier.enedis.fr/">https://mon-compte-particulier.enedis.fr/</a> . Vous aurez besoin pour le créer de votre N° de PDL (point de livraison) indiqué sur votre facture d'électricité.
  Dans cet espace client, vous devez activer la collecte : rendez-vous dans le menu "Gérer l'accès à mes données", puis cliquez sur les boutons d'activations présents, dans les cadres "Enregistrement de la consommation horaire" puis "Collecte de la consommation horaire",
  </p>

  <h4>La connexion entre Enedis et ConsoHerozh</h4>
  <p>
  Une fois votre espace client créé, et la collecte activée, vous pouvez vous rendre sur Consoherozh pour établir la connexion.
  Sur la page d'accueil, utilisez le bouton du cadre "Linky" : celui-ci vous permet d'accéder au processus de consentement de partage de vos données, qui se conclut par la transmission des données d'Enedis vers Consoherozh.
  </p>

  <h2>Au bout de combien de temps je peux visualiser mes données ?</h2>
  <p>
  Les données sont récupérées chaque nuit. Une fois le consentement donné, attendez le lendemain pour voir votre première courbe ;)
  </p>

  <h2>Aucune données ne s'affiche, pourquoi ?</h2>
  <p>
  Trois possibilités :
  </p>
  <ul>
  <li>soit vous n'avez pas suivi entièrement le processus d'activation, auquel cas reportez-vous à la question ci-dessus.</li>
  <li>soit votre consentement a expiré et le widget affiche un message "La connexion avec le compteur communicant a expiré." Vous devez alors utiliser le bouton "Reconnecter" pour reprendre la transmission de données.</li>
    <li>soit le service d'Enedis rencontre des difficultés, auquel cas vous pouvez attendre, ou bien faire un message à l'administrateur du site (<a href="mailto:contact@consoherozh.fr">contact@consoherozh.fr</a>).</li>
  </ul>

  <h2>Quelles données sont disponibles ?</h2>
  <p>
  Le service ConsoHerozh collecte uniquement les données de consommation électrique globales du foyer (à l'échelle du compteur Linky), au pas horaire.
  Ces données sont ensuites "agrégées" pour vous présenter les courbes de consommations mensuelles et annuelles.
  Enfin, chaque compteur calcule automatiquement une tendance d'évolution des consommations (voir question ci-dessous).
  </p>

  <h2>Comment est calculée la Tendance ?</h2>
  <p>
  Pour chaque compteur manuel, la tendance d'évolution des consommations est exprimée entre deux relevés.
  Le calcul correspond au ratio "Consommation de la dernière période" ÷ "Consommation de l'avant-dernière période".
  Pour le compteur Linky, il s'agit du même calcul, avec une période fixée à 7 jours et 14 jours.
  </p>


  <h2>Comment afficher l'histogramme de mes consommations ?</h2>
  <p>
  Pour les compteurs manuels, l'histogramme s'affiche directement quand vous cliquez sur le bouton "courbes" en bas à gauche de chaque tuile.
  Pour le compteur Linky, l'histogramme s'ouvre sur une nouvelle page.
  </p>

  <h2>Comment changer de période d'affichage de mes consommations ?</h2>
  <p>
  Pour les compteurs manuels, l'ensemble de la période est affichée, sans réglage possible.
  Pour le compteur Linky, sur la page d'affichage des données, vous pouvez faire varier la période et la durée affichée à l'aide des boutons "Jour - Mois - Année" et du calendrier, situés en haut du graphique.
  </p>

  <h2>Quelles sont les prochaines évolutions de Consoherozh ?</h2>
  <p>
  En lien avec les utilisateurs et les animateurs du service, nous réfléchissons à de nombreuses évolutions.
  </p>
  <p>
  Voilà les prochaines chosent qui pourraient voir le jour...
  </p>
  <ul>
    <li>Ajouter un label ou une bulle d'aide à "Tendance" indiquant par rapport à quoi est faite la comparaison.</li>
    <li>Si possible, de créer un fil facebook du groupe ConsoHerozh directement sur la page plutôt qu'un lien vers le groupe ?</li>
    <li>Remplacer le bloc "Vos données sont disponibles" par un graphique sparkline de la journée, des 30 derniers jours ou du mois.</li>
  </ul>
  <p>
  Est-ce que d'autres vous seraient utiles, et seriez-vous prêts à passer du temps avec nous pour les mettre au point ? Alors contactez-nous pour en échanger ;)
  </p>
  
</g:applyLayout>

</body>
</html>