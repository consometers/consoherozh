
# Créer un compte

Pour utiliser l'application il faut au préalable se créer un compte.

L'url relative est register/account, sur le site de référence : https://www.consoherozh.fr/register/account.

Il est nécessaire de fournir une adresse mail.

## Screenshot

[screenshot creation de compte](screenshots/register.account.png)

## Nom / Pseudo

Choix du nom affiché

## Adresse Mail

l'adresse mail est l'adresse électronique associée à votre compte ConsoHerozh.
C'est cette adresse vous utiliserez pour vous connecter à votre compte ConsoHerozh.

### Adresse Mail validée

Le format valide doit comporter un nom d'utilisateur texte suivi de @ et un nom de domaine comportant au moins un point (.)  .

_code_  utils/smarthome/security/SmartHomeSecurityUtils.groovy

## Mot de passe

Mot de passe à entrer une seconde fois dans le champ Confirmation fois pour éviter les typos.

Le mot de passe doit comporter **Minimum 8 caractères dont 1 chiffre**.

Le mot de passe doit être saisi deux fois, si le contenu du champs Confirmation n'est pas le même que le champ Mot de passe alors l'erreur suivante apparait :

    Erreur validation du formulaire

        La confirmation ne correspond pas au mot de passe

## Données personnelles

Pour créer un compte il faut cocher l'option d'acceptation de l'utilisation des données personnelles.

(x) En créant votre compte, vous nous autorisez à utiliser vos données pour les finalités décrites sur la page relative aux données personnelles.

_code_ : controller public action privacy.


## Bugs

Si vous êtes déjà connecté au site et que vous accèdez directement à la page register/account et tentez de créer un nouveau compte alors l'erreur suivante apparait :

    Erreur générale. Impossible d'exécuter la requête demandée. 

[ce bug est suivi sur github](https://github.com/consometers/consoherozh/issues/5)