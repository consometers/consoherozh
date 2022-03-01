package smarthome.automation

import grails.validation.Validateable


/**
 * Recherche alertes
 * 
 * @author gregory
 *
 */
class DeviceAlertCommand
implements Validateable {
	Long id
	Boolean open
	Long userId
}
