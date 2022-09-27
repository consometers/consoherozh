package smarthome.automation

import smarthome.core.DateUtils
import grails.validation.Validateable
import groovy.time.TimeCategory


abstract class AbstractChartCommand<T> {
	Date dateChart
	Date dateDebutUser
	ChartViewEnum viewMode
	ChartNavigationEnum navigation
	boolean comparePreviousYear


	/**
	 * Defaut constructor
	 * 
	 */
	AbstractChartCommand() {
		dateChart = new Date().clearTime()
		// Use yesterday’s date by default, gives more sense for Linky which updates date for a whole day
		// TODO Cyril: find a Linky specific place to do this
		use(TimeCategory) {
			dateChart = dateChart - 1.day
		}
		viewMode = ChartViewEnum.month
	}


	/**
	 * Date début en fonction view mode
	 * 
	 * @return
	 */
	Date dateDebut() {
		if (dateDebutUser) {
			return dateDebutUser
		} else {
			Date date = dateChart.clearTime()

			switch (viewMode) {
				case ChartViewEnum.year:
					use(TimeCategory) { date = date - 1.year }
					return date
				case ChartViewEnum.month:
					use(TimeCategory) { date = date - 1.month }
					return date
				default: return date
			}
		}
	}

	/**
	 * Date fin en fonction view mode
	 * 
	 * @return
	 */
	Date dateFin() {
		Date date = dateChart.clearTime()

		use(TimeCategory) {
			date = date + 23.hours + 59.minutes + 59.seconds
		}

		return date
	}


	/**
	 * Gère la navigation
	 */
	void navigation() {
		use(TimeCategory) {
			if (navigation == ChartNavigationEnum.prev) {
				switch (viewMode) {
					case ChartViewEnum.day:
						dateChart = dateChart - 1.days
						break
					case ChartViewEnum.month:
						dateChart = dateChart - 1.months
						break
					case ChartViewEnum.year:
						dateChart = dateChart - 1.years
						break
				}
			} else if (navigation == ChartNavigationEnum.next) {
				switch (viewMode) {
					case ChartViewEnum.day:
						dateChart = dateChart + 1.days
						break
					case ChartViewEnum.month:
						dateChart = dateChart + 1.months
						break
					case ChartViewEnum.year:
						dateChart = dateChart + 1.years
						break
				}
			}
		}
	}


	/**
	 * Clone l'objet mais pour l'année précédente
	 * 
	 * @return
	 */
	T cloneForLastYear() {
		T clone = this.clone()
		use(TimeCategory) {
			clone.dateChart = this.dateChart - 1.years
		}
		return clone
	}


	/**
	 * Clone l'objet
	 *
	 * @return
	 */
	T clone() {
		T clone = this.getClass().newInstance()
		clone.viewMode = this.viewMode
		clone.dateChart = this.dateChart
		clone.dateDebutUser = this.dateDebutUser
		return clone
	}


	/**
	 * Clone en un autre objet
	 *
	 * @return
	 */
	def clone(Class cloneClass) {
		def clone = cloneClass.newInstance()
		clone.viewMode = this.viewMode
		clone.dateChart = this.dateChart
		clone.dateDebutUser = this.dateDebutUser
		return clone
	}

	/**
	 * Retourne l'année précédente
	 * 
	 * @return
	 */
	int lastYear() {
		return dateChart[Calendar.YEAR] - 1
	}
}
