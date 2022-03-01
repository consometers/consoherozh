/**
 * 
 */
package smarthome.security

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
class ProfilCommand
implements Validateable { 
	User user
	House house
	List<Mode> modes = []
}
