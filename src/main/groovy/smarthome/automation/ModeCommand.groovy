/**
 * 
 */
package smarthome.automation

import smarthome.automation.House;
import smarthome.automation.Mode;
import smarthome.security.SmartHomeSecurityUtils;
import grails.validation.Validateable


/**
 * Enregistement profil
 * 
 * @author gregory
 *
 */
class ModeCommand implements Validateable {
	List<Mode> modes = []
}
