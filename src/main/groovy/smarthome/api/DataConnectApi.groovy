package smarthome.api

import java.text.SimpleDateFormat

import grails.core.GrailsApplication
import org.grails.web.json.JSONElement

import groovy.time.TimeCategory
import smarthome.core.DateUtils
import smarthome.core.SmartHomeException
import smarthome.core.http.Http
import smarthome.core.http.transformer.JsonResponseTransformer
import smarthome.security.User

import java.util.regex.Pattern

/**
 * API DataConnect Enedis
 * 
 * @author gelleouet <gregory.elleouet@gmail.com>
 *
 */
class DataConnectApi {
	private static final Map URLS = [
		"dev": [
			authorize: "https://gw.hml.api.enedis.fr",
			token: "https://gw.hml.api.enedis.fr",
			metric: "https://gw.hml.api.enedis.fr"
		],
		"prod": [
			authorize: "https://mon-compte-particulier.enedis.fr",
			token: "https://gw.prd.api.enedis.fr",
			metric: "https://gw.prd.api.enedis.fr"
		]
	]

	private static final SimpleDateFormat DATA_POINT_TIME_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

	private static final SimpleDateFormat DATA_POINT_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd")

	private static final Pattern DURATION_REGEX = ~/^PT(\d+)M$/

	GrailsApplication grailsApplication


	/**
	 * Appel de la page de consentement
	 * 
	 * Quand vous serez en production, ce lien dirigera le client vers la page 
	 * d’authentification à son compte client Enedis ; une fois connecté, il y 
	 * trouvera une page avec votre logo, votre nom, les données dont vous avez 
	 * besoin qui lui permettra d’autoriser ou pas le partage de ses données Linky.
	 * 
	 * https://datahub-enedis.fr/data-connect/documentation/authorize/
	 * 
	 * @return
	 */
	String authorize_uri() {
		String url = "${URLS[(grailsApplication.config.enedis.env)].authorize}/dataconnect/v1/oauth2/authorize"
		url += "?client_id=${grailsApplication.config.enedis.client_id}"
		url += "&duration=P6M"
		url += "&response_type=code"
		url += "&state=${grailsApplication.config.enedis.state}"
		return url
	}


	/**
	 *  Obtention des jetons
	 *  https://datahub-enedis.fr/data-connect/documentation/authorize/
	 *  
	 *  En retour, bingo, vous récupérez deux jetons ! Un jeton d’accès (access_token)
	 *  qui est la clé d’entrée auprès d’Enedis pour les données auxquelles le
	 *  client vient de vous donner accès. Il a une durée de validité de 3h30.
	 *  Vous disposez également d’un jeton de rafraîchissement (refresh_token) 
	 *  qui a une durée de validité d’un an et permet de renouveler le jeton d’accès.
	 * 
	 * @param code
	 * @return
	 * @throws SmartHomeException
	 */
	JSONElement authorization_code(String code) throws SmartHomeException {
		String url = "${URLS[(grailsApplication.config.enedis.env)].token}/v1/oauth2/token"

		Http httpRequest = Http.Post(url)
				.queryParam("redirect_uri", grailsApplication.config.enedis.redirect_uri)
				.formField("client_id", grailsApplication.config.enedis.client_id)
				.formField("client_secret", grailsApplication.config.enedis.client_secret)
				.formField("grant_type", GrantTypeEnum.authorization_code.toString())
				.formField("code", code)


		JSONElement result = httpRequest.execute(new JsonResponseTransformer("error_description"))?.content

		if (!result) {
			throw new SmartHomeException("Token response empty !")
		}

		return result
	}


	/**
	 *  Rafraîchissement du jeton d’accès
	 *  https://datahub-enedis.fr/data-connect/documentation/authorize/
	 *
	 *  Lorsque votre jeton d’accès est expiré, vous ne pouvez plus récupérer de
	 *  données et les API vous retournent un code erreur HTTP 401 (Unauthorized).
	 *  Pas de panique, il vous est possible d’en générer un nouveau sans
	 *  demander à l’utilisateur de se ré-authentifier.
	 *  C’est le rôle du jeton de rafraîchissement.
	 *
	 * @param refreshToken
	 * @return
	 * @throws SmartHomeException
	 */
	JSONElement refresh_token(String refreshToken) throws SmartHomeException {
		String url = "${URLS[(grailsApplication.config.enedis.env)].token}/v1/oauth2/token"

		Http httpRequest = Http.Post(url)
				.queryParam("redirect_uri", grailsApplication.config.enedis.redirect_uri)
				.formField("client_id", grailsApplication.config.enedis.client_id)
				.formField("client_secret", grailsApplication.config.enedis.client_secret)
				.formField("grant_type", GrantTypeEnum.refresh_token.toString())
				.formField("refresh_token", refreshToken)

		JSONElement result = httpRequest.execute(new JsonResponseTransformer("error_description"))?.content

		if (!result) {
			throw new SmartHomeException("Token response empty !")
		}

		return result
	}



	/**
	 * Récupération de la puissance moyenne consommée quotidiennement,
	 * par tranche d'une demi-heure
	 * 
	 * Cette sous ressource renvoie les valeurs correspondant à des journées de
	 * mesure de la courbe de charge de consommation d’un client pour chaque
	 * jour de la période demandée. Les valeurs retournées sont des puissances
	 * moyennes de consommation sur des tranches de 30 minutes.
	 * 
	 * 
	 * Chaque valeur
	 * est associée à un numéro, la valeur portant le numéro 1 correspond à la
	 * puissance moyenne mesurée entre minuit et minuit trente le premier jour
	 * de la période demandée. La valeur portant le numéro le plus élevé
	 * correspond à la puissance moyenne mesurée entre 23h30 et minuit, la
	 * veille du dernier jour demandé. Les éventuelles périodes de données
	 * absentes se manifesteront par un saut dans la numérotation.
	 * 
	 * La courbe de
	 * charge s’obtient sur des journées complètes de minuit à minuit du jour
	 * suivant en heures locales. Un appel peut porter au maximum sur 7 jours
	 * consécutifs. Un appel peut porter sur des données datant au maximum de 24
	 * mois et 15 jours avant la date d’appel.
	 * 
	 * https://datahub-enedis.fr/data-connect/documentation/metering-data-v4/
	 * 
	 * @param start
	 * @param end
	 * @param usagePointId
	 * @param token
	 * @return
	 * @throws SmartHomeException
	 */
	List<JSONElement> consumption_load_curve(Date start, Date end, String usagePointId, String token) throws SmartHomeException {
		String url = "${URLS[(grailsApplication.config.enedis.env)].metric}/v4/metering_data/consumption_load_curve"

		JSONElement response = Http.Get(url)
				.queryParam("start", DateUtils.formatDateIso(start))
				.queryParam("end", DateUtils.formatDateIso(end))
				.queryParam("usage_point_id", usagePointId)
				.header("Authorization", "Bearer ${token}")
				.header("Accept", "application/json")
				.execute(new JsonResponseTransformer("error_description"))?.content

		if (!response || !response.meter_reading) {
			throw new SmartHomeException("consumptionLoadCurve response empty !")
		}

		List<JSONElement> datapoints = response.meter_reading.interval_reading

		datapoints.each { datapoint ->
			def matcher = datapoint.interval_length =~ DURATION_REGEX
			if (!matcher.matches()) {
				throw new SmartHomeException("Interval length not implemented: ${datapoint.interval_length}")
			}
			double intervalHours = Integer.parseInt(matcher.group(1)) / 60.0
			int meanPower = Integer.parseInt(datapoint.value)
			// FIXME(cyril) was rounded(0) by greg, why ?
			// round by 2 to avoid values like 484.00000009679997
			datapoint.wh = (intervalHours * meanPower).round(2)
			datapoint.timestamp = DATA_POINT_TIME_FORMAT.parse(datapoint.date)
		}

		return datapoints
	}


	/**
	 * Récupération de la consommation quotidienne
	 * 
	 * Cette sous ressource renvoie les valeurs correspondant à la consommation
	 * quotidienne sur chaque jour de la période demandée. Chaque valeur est
	 * associée à un numéro, la valeur portant le numéro 1 correspond à la
	 * consommation du premier jour de la période demandée. La valeur portant le
	 * dernier numéro correspond à la consommation de la veille du jour de fin
	 * de la période demandée. Les éventuelles périodes de données absentes se
	 * manifesteront par un saut dans la numérotation. Un appel peut porter au
	 * maximum sur 365 jours consécutifs. Un appel peut porter sur des données
	 * datant au maximum de 36 mois et 15 jours avant la date d’appel.
	 *
	 * https://datahub-enedis.fr/data-connect/documentation/metering-data/
	 *
	 * @param start
	 * @param end
	 * @param usagePointId
	 * @param token
	 * @return
	 * @throws SmartHomeException
	 */
	List<JSONElement> daily_consumption(Date start, Date end, String usagePointId, String token) throws SmartHomeException {
		String url = "${URLS[(grailsApplication.config.enedis.env)].metric}/v4/metering_data/daily_consumption"

		JSONElement response = Http.Get(url)
				.queryParam("start", DateUtils.formatDateIso(start))
				.queryParam("end", DateUtils.formatDateIso(end))
				.queryParam("usage_point_id", usagePointId)
				.header("Authorization", "Bearer ${token}")
				.header("Accept", "application/json")
				.execute(new JsonResponseTransformer("error_description"))?.content

		if (!response || !response.meter_reading) {
			throw new SmartHomeException("daily_consumption response empty !")
		}

		List<JSONElement> datapoints = response.meter_reading.interval_reading

		datapoints.each { datapoint ->
			datapoint.timestamp = DATA_POINT_DATE_FORMAT.parse(datapoint.date)
		}

		return datapoints
	}


	/**
	 * Récupération de la puissance maximale de consommation atteinte quotidiennement
	 *
	 * Cette sous ressource renvoie les valeurs correspondant à des puissances
	 * maximales atteintes quotidiennement pour chaque jour de la période demandée.
	 * Chaque valeur est associée à un numéro, la valeur portant le numéro 1
	 * correspond à la puissance maximale atteinte sur le premier jour de la
	 * période demandée. La valeur portant le dernier numéro correspond à la
	 * puissance maximale atteinte la veille du jour de fin de la période demandée.
	 * Les éventuelles périodes de données absentes se manifesteront par un saut
	 * dans la numérotation. La courbe de charge s’obtient sur des journées
	 * complètes de minuit à minuit du jour suivant en heures locales. Un appel
	 * peut porter au maximum sur 365 jours consécutifs. Un appel peut porter
	 * sur des données datant au maximum de 36 mois et 15 jours avant la date d’appel.
	 *
	 * https://datahub-enedis.fr/data-connect/documentation/metering-data/
	 *
	 * @param start
	 * @param end
	 * @param usagePointId
	 * @param token
	 * @return
	 * @throws SmartHomeException
	 */
	List<JSONElement> consumption_max_power(Date start, Date end, String usagePointId, String token) throws SmartHomeException {
		String url = "${URLS[(grailsApplication.config.enedis.env)].metric}/v4/metering_data/daily_consumption_max_power"

		JSONElement response = Http.Get(url)
				.queryParam("start", DateUtils.formatDateIso(start))
				.queryParam("end", DateUtils.formatDateIso(end))
				.queryParam("usage_point_id", usagePointId)
				.header("Authorization", "Bearer ${token}")
				.header("Accept", "application/json")
				.execute(new JsonResponseTransformer("error_description"))?.content

		if (!response || !response.meter_reading) {
			throw new SmartHomeException("daily_consumption response empty !")
		}

		List<JSONElement> datapoints = response.meter_reading.interval_reading

		datapoints.each { datapoint ->
			datapoint.timestamp = DATA_POINT_DATE_FORMAT.parse(datapoint.date)
		}

		return datapoints
	}



	public void setGrailsApplication(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication
	}
}
