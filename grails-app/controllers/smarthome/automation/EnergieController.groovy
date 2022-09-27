package smarthome.automation

import org.springframework.security.access.annotation.Secured;
import smarthome.core.AbstractController;
import smarthome.security.User;
import smarthome.core.DateUtils
import groovy.time.TimeCategory


@Secured("isAuthenticated()")
class EnergieController extends AbstractController {

	DeviceService deviceService
	HouseService houseService
	
	
	/**
	 * Affichage du watmetre pour le compteur
	 * 
	 * @param device
	 * @return
	 */
	def watmetre(Device device) {
		deviceService.edit(device)
		render(view: '/deviceType/teleInformation/watmetre', model: [device: device])	
	}
	
	
	/**
	 * Widget compteur par défaut
	 * 
	 * @return
	 */
	def widget() {
		def user = authenticatedUser
		def house = houseService.findDefaultByUser(user)
		render(template: '/deviceType/teleInformation/widget', model: [house: house])
	}

	private def meanConsumption(Device device, Date fromDay, Date toDay) {
		def values = DeviceValueDay.values(device, fromDay,  toDay, "basesum")
		if (values.size() == 0) {
			return null
		}
		return values.sum { it.value } / values.size()
	}

	def briefWidget() {
		def user = authenticatedUser

		// TODO cyril view should directly take the list of values to manage how to display dates
		Date today = new Date().clearTime()
		Date thisMonth = DateUtils.firstDayInMonth(today)
		Date previousMonth
		use(TimeCategory) { previousMonth = thisMonth - 1.month }

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

		DeviceType linkyDevice = DeviceType.findByLibelle(grailsApplication.config.enedis.compteurLabel)
		Device device = Device.createCriteria().get {
			eq 'user', user
			eq 'deviceType', linkyDevice
		}

		def meanThisMonth
		def meanPreviousMonth

		if (device) {
			meanThisMonth = meanConsumption(device, thisMonth,  today)
			meanPreviousMonth = meanConsumption(device, previousMonth, DateUtils.lastDayInMonth(previousMonth))
		} else {
			meanThisMonth = null
			meanPreviousMonth = null
		}

		[
				device: device,
		 		previousMonth: previousMonth,
		 		today: today,
				dayOfMonth: dayOfMonth,
				meanThisMonth: meanThisMonth,
				meanPreviousMonth: meanPreviousMonth
		]
	}
}
