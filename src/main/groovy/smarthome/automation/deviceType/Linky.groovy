package smarthome.automation.deviceType

import groovy.time.TimeCategory
import grails.core.GrailsApplication
import smarthome.automation.NotificationAccount
import smarthome.automation.NotificationAccountSender

import java.util.Date
import java.util.List
import java.util.Map

import smarthome.automation.ChartTypeEnum
import smarthome.automation.ChartViewEnum
import smarthome.automation.DataModifierEnum
import smarthome.automation.Device
import smarthome.automation.DeviceChartCommand
import smarthome.automation.DeviceMetadata
import smarthome.automation.DeviceTypeProvider
import smarthome.automation.DeviceTypeProviderPrix
import smarthome.automation.DeviceValue
import smarthome.automation.DeviceValueDay
import smarthome.automation.DeviceValueMonth
import smarthome.automation.HouseConso
import smarthome.core.DateUtils
import smarthome.core.SmartHomeException
import smarthome.core.chart.GoogleChart
import smarthome.core.chart.GoogleDataTableCol

/**
 * Périphérique Télé-info EDF
 * 
 * @author gregory
 *
 */
class Linky extends AbstractDeviceType {
	/**
	 * @see smarthome.automation.deviceType.AbstractDeviceType.chartDataTemplate()
	 */
	@Override
	def chartDataTemplate() {
		'/deviceType/linky/linkyChartDatas'
	}



	/** 
	 * (non-Javadoc)
	 * @see smarthome.automation.deviceType.AbstractDeviceType#viewChart()
	 */
	@Override
	String viewChart() {
		'/deviceType/linky/linkyChart'
	}


	/**
	 * (non-Javadoc)
	 * @see smarthome.automation.deviceType.AbstractDeviceType#values()
	 */
	@Override
	List values(DeviceChartCommand command) throws SmartHomeException {
		def values = []

		if (command.viewMode == ChartViewEnum.day) {
			values = DeviceValue.values(command.device, command.dateDebut(), command.dateFin(),
					command.metaName ?: "null,hcinst,hpinst,baseinst")
		} else {
			values = super.values(command)
		}

		return values
	}


	/**
	 * important de surcharger cette boite car le graphe teleinfo a son propre builder de chart
	 * sinon le graphe sera créé une 1ere fois dans la méthode parent
	 * 
	 * @see smarthome.automation.deviceType.AbstractDeviceType#googleChart(smarthome.automation.DeviceChartCommand, java.util.List)
	 */
	@Override
	GoogleChart googleChart(DeviceChartCommand command, List values) {
		GoogleChart chart = new GoogleChart()

		String label
		switch (command.viewMode) {
			case ChartViewEnum.year:
				label = "Consommation mensuelle"
				break
			case ChartViewEnum.month:
				label = "Consommation journalière"
				break
			case ChartViewEnum.day:
				label = "Consommation"
				break
		}

		if (command.viewMode == ChartViewEnum.day) {
			chart.values = values.groupBy { it.dateValue }

			chart.colonnes = []
			chart.colonnes << new GoogleDataTableCol(label: "Date", type: "datetime", property: "key")

			chart.colonnes << new GoogleDataTableCol(label: label, type: "number", value: { deviceValue, index, currentChart ->
				deviceValue.value.find{ it.name == "baseinst" }?.value
			})
		} else {
			chart.values = values

			chart.colonnes = []
			chart.colonnes << new GoogleDataTableCol(label: "Date", type: "date", property: "key")

			chart.colonnes << new GoogleDataTableCol(label: label, type: "number", value: { deviceValue, index, currentChart ->
				def value = deviceValue.value.find{ it.name == "basesum" }?.value
				if (value != null) {
					return (value / 1000d).round(1)
				} else {
					return null
				}
			})
		}

		return chart
	}



	/**
	 * @see smarthome.automation.deviceType.AbstractDeviceType.prepateMetaValuesForSave()
	 */
	@Override
	def prepareMetaValuesForSave(def datas) {
		Date dateInf

		use (TimeCategory) {
			dateInf = device.dateValue - 30.minutes
		}

		// si le device n'existe pas encore, il n'y a donc pas d'anciennes valeurs
		// pour calculer la dernière conso
		if (device.id) {
			// calcul conso heure creuse sur la période
			def hc = device.metavalue("hchc")

			// les metavalues sur la période sont désormais gérées par le controller (à cause du offline)
			// mais pour les anciennes versions, il faut les ajouter ici manuellement
			if (hc && !datas.metavalues?.hcinst) {
				device.addMetavalue("hcinst", [value: "0", label: "Période heures creuses",
					trace: true, unite: "Wh"])
				// récupère la dernière valeur hchc
				def lastHC = DeviceValue.lastValueInPeriod(device, dateInf, device.dateValue, "hchc")

				if (lastHC) {
					def conso = hc.value.toLong() - lastHC.value.toLong()
					device.addMetavalue("hcinst", [value: conso.toString()])
				}
			}

			// calcul conso heure pleine sur la période
			def hp = device.metavalue("hchp")

			if (hp && !datas.metavalues?.hpinst) {
				device.addMetavalue("hpinst", [value: "0", label: "Période heures pleines",
					trace: true, unite: "Wh"])
				// récupère la dernière valeur hchp
				def lastHP = DeviceValue.lastValueInPeriod(device, dateInf, device.dateValue, "hchp")

				if (lastHP) {
					def conso = hp.value.toLong() - lastHP.value.toLong()
					device.addMetavalue("hpinst", [value: conso.toString()])
				}
			}

			// calcul conso toute heure sur la période
			def base = device.metavalue("base")

			if (base && !datas.metavalues?.baseinst) {
				device.addMetavalue("baseinst", [value: "0", label: "Période toutes heures",
					trace: true, unite: "Wh"])
				// récupère la dernière valeur base
				def lastBase = DeviceValue.lastValueInPeriod(device, dateInf, device.dateValue, "base")

				if (lastBase) {
					def conso = base.value.toLong() - lastBase.value.toLong()
					device.addMetavalue("baseinst", [value: conso.toString()])
				}
			}
		}
	}


	/**
	 * Les consos du jour en map indexé par le type d'heure (HC, HP, BASE, etc.)
	 * 
	 * @return
	 */
	Map consosJour() {
		def consos = []
		def currentDate = new Date()
		def currentYear = currentDate[Calendar.YEAR]

		def first_base = DeviceValue.firstValueByDay(device, 'base')
		def last_base = DeviceValue.lastValueByDay(device, 'base')
		consos.base = first_base?.value && last_base?.value ? (last_base.value - first_base.value) / 1000.0 : 0.0
		consos.total = (consos.base as Double).round(1)

		return consos
	}


	/**
	 * (non-Javadoc)
	 * @see smarthome.automation.deviceType.AbstractDeviceType#aggregateValueDay(java.util.Date)
	 */
	@Override
	List aggregateValueDay(Date dateReference) {
		def values = []

		// traite d'abord l'intensité max
		values.addAll(DeviceValue.executeQuery("""\
			SELECT new map(date_trunc('day', deviceValue.dateValue) as dateValue, deviceValue.name as name,
			max(deviceValue.value) as max)
			FROM DeviceValue deviceValue
			WHERE deviceValue.device = :device
			AND deviceValue.dateValue BETWEEN :dateDebut AND :dateFin
			AND deviceValue.name is null
			GROUP BY deviceValue.name, date_trunc('day', deviceValue.dateValue)""", [device: device,
					dateDebut: DateUtils.firstTimeInDay(dateReference), dateFin: DateUtils.lastTimeInDay(dateReference)]))

		// traite ensuite les index
		values.addAll(DeviceValue.executeQuery("""\
			SELECT new map(date_trunc('day', deviceValue.dateValue) as dateValue, deviceValue.name as name,
			(max(deviceValue.value) - min(deviceValue.value)) as sum)
			FROM DeviceValue deviceValue
			WHERE deviceValue.device = :device
			AND deviceValue.dateValue BETWEEN :dateDebut AND :dateFin
			AND deviceValue.name in (:metaNames)
			GROUP BY deviceValue.name, date_trunc('day', deviceValue.dateValue)""", [device: device,
					dateDebut: DateUtils.firstTimeInDay(dateReference), dateFin: DateUtils.lastTimeInDay(dateReference),
					metaNames: ['hchp', 'hchc', 'base']]))

		return values
	}



	/**
	 * (non-Javadoc)
	 * @see smarthome.automation.deviceType.AbstractDeviceType#aggregateValueMonth(java.util.Date)
	 */
	@Override
	List aggregateValueMonth(Date dateReference) {
		def values = []

		// traite d'abord l'intensité max
		values.addAll(DeviceValue.executeQuery("""\
			SELECT new map(date_trunc('month', deviceValue.dateValue) as dateValue, deviceValue.name as name,
			max(deviceValue.value) as max)
			FROM DeviceValue deviceValue
			WHERE deviceValue.device = :device
			AND deviceValue.dateValue BETWEEN :dateDebut AND :dateFin
			AND deviceValue.name is null
			GROUP BY deviceValue.name, date_trunc('month', deviceValue.dateValue)""", [device: device,
					dateDebut: DateUtils.firstDayInMonth(dateReference), dateFin: DateUtils.lastTimeInDay(DateUtils.lastDayInMonth(dateReference))]))

		// traite ensuite les index
		values.addAll(DeviceValue.executeQuery("""\
			SELECT new map(date_trunc('month', deviceValue.dateValue) as dateValue, deviceValue.name as name,
			(max(deviceValue.value) - min(deviceValue.value)) as sum)
			FROM DeviceValue deviceValue
			WHERE deviceValue.device = :device
			AND deviceValue.dateValue BETWEEN :dateDebut AND :dateFin
			AND deviceValue.name in (:metaNames)
			GROUP BY deviceValue.name, date_trunc('month', deviceValue.dateValue)""", [device: device,
					dateDebut: DateUtils.firstDayInMonth(dateReference), dateFin: DateUtils.lastTimeInDay(DateUtils.lastDayInMonth(dateReference)),
					metaNames: ['hchp', 'hchc', 'base']]))

		return values
	}

	boolean isConnected() {
		// Assumes one Linky per user for now
		GrailsApplication grailsApplication = grails.util.Holders.grailsApplication
		NotificationAccountSender accountSender = NotificationAccountSender.findByLibelle(grailsApplication.config.enedis.appName)
		NotificationAccount account = NotificationAccount.createCriteria().get {
			eq 'user', device.user
			eq 'notificationAccountSender', accountSender
		}
		if (!account) {
			return false
		} else {
			account.configToJson()
			return !account.jsonConfig.expired
		}
	}
}
