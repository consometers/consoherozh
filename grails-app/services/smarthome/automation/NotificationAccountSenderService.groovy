package smarthome.automation

import grails.gorm.transactions.Transactional
import smarthome.core.AbstractService
import smarthome.core.SmartHomeException


class NotificationAccountSenderService extends AbstractService {

	NotificationAccountSender findByImplClass(String implClass) {
		NotificationAccountSender.findByImplClass(implClass)
	}

	NotificationAccountSender findByLibelle(String libelle) {
		NotificationAccountSender.findByLibelle(libelle)
	}
}
