# Compteur Linky enedis

Les données sont récupérées de manière asynchrone de façon automatique et à intervalle régulier à condition que
l'enregistrement du consentement et l'échange d'autorisation avec enedis a eu lieu, une configuration initiale
est nécessaire ( voir **Installation** ).

Libellé unique identifiant ce périphérique : 'Compteur électrique (Linky)' 

    smarthome=# select * from smarthome.device_type where impl_class='smarthome.automation.deviceType.Linky';
    id   | version |           libelle           |              impl_class               | qualitatif | planning
    -------+---------+-----------------------------+---------------------------------------+------------+----------
    17836 |       1 | Compteur électrique (Linky) | smarthome.automation.deviceType.Linky | t          | f
    (1 ligne)

classe groovy de persistence **smarthome.automation.DeviceType**

classe groovy d'implémentation **smarthome.automation.deviceType.Linky**

## DataConnectService

### authorization_code

Nécessaire à la première connection pour enregister l'autorisaiton du compte de délégation client via
cette application auprès d'enedis . 

### consumptionLoadCurve

Le champ **last_consumption_load_curve** de la configuration json stockée dans notificationAccount sert
de référence pour savoir la date de l'entrée la plus récente de la courbe de consommation a déjà été obtenue.

Jusqu'à 7 jours de compteurs sont  demandés.
délègue à DataConnectApi la récupération de la courbe de consommation en json et mets à jour last_consumption_load_curve.

### dailyConsumption

Le champ **last_daily_consumption** de la configuration json stockée dans notificationAccount sert
de référence pour savoir la date de l'entrée la plus récente de la consommation par jour a déjà été obtenue.

Jusqu'à une année de compteurs sont demandés
délègue à DataConnectApi la récupération de la consommation par jour.

### consumptionMaxPower

Le champ **consumption_max_power** de la configuration json stockée dans notificationAccount sert
de référence pour savoir la date de l'entrée la plus récente de la puissance maximale utilisée sur une journée a déjà été obtenue.

## DataConnectApi

l'utilisation du protocole data-connect est discutée ici : https://github.com/consometers/data-connect

# Installation

Il est nécessaire d'avoir obtenu auprès d'enedis un compte pour l'application installée, ceci n'est possible
que pour les entreprises ou les auto-entrepreneurs disposant d'un n° de SIRET. 
Les paramètres de ce compte seront à renseigner dans client_id, client_secret.

Pour initier le projet il faut créer un NotificationAccountSender pour enedis avec les paramètres de nommages 
correspondant à ceux de la configuration.

Les paramètres de nommage lus au lancement de la webapp sont définis dans la configuration grails sous 'enedis'

smarthome-config.groovy ( actuel grails 2.4.x)

    enedis {
        appName = "Enedis DataConnect"
        compteurLabel = "Compteur électrique (Linky)"
        env = "prod"
        client_id = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
        client_secret = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
        redirect_uri = "<url du portail>/dataconnect-proxy/redirect"
        state = "bmhs"
    }

Pour se faire il faut se connecter avec un utilisateur ayant les droits ROLE_ADMIN sur 
`<url du portail>/notificationAccountSender/notificationAccountSenders`.

[Ajouter] 
* Libellé : celui de enedis.appName ex `Enedis DataConnect`
* Implémentation : `smarthome.automation.datasource.DataConnectDataSourceProvider`
* Rôle : laisser vide 
* Cron: respectant une cronExpression ( org.quartz.CronExpression  ) valide, ex `0 0/10 * * * ?` pour effectuer la 
récupération des compteurs toutes les 10 heures.

C'est le framework Quartz qui gère cela via  DataSourceProviderCronPaginateSubJob

		List<Map> providers = notificationAccountService.listIdsWithCron([offset: offset, max: max])

## NotificationAccountSender

libelle : 'Enedis DataConnect' ( libelle configurable dans enedis.appName )

cron : spécifie quand les appels vont être effectués.

implementation : smarthome.automation.datasource.DataConnectDataSourceProvider

    smarthome=# select * from smarthome.notification_account_sender;
    id   | version |                          impl_class                           |       libelle       | role |      cron      
    --------+---------+---------------------------------------------------------------+---------------------+------+----------------
    4 |       0 | smarthome.automation.datasource.DataConnectDataSourceProvider | Enedis DataConnect  |      | 0 0/10 * * * ?


## NotificationAccount

Créé DataConnectService.authorization_code, nécessite que le NotificationAccountSender existe.

    id   | version |                                                                                                                                                                        config                                                                                                                                                                         | notification_account_sender_id | user_id
    --------+---------+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------+---------
    351125 |   56436 | {"access_token":"....","refresh_token":"...","last_consumption_max_power":1647471600000,"expired":false,"last_token":1647617405318,"last_daily_consumption":1647471600000,"usage_point_id":"00000000000000","last_consumption_load_curve":1647558000000} |                              4 |       2
    (1 ligne)

