/**
 * 
 */
package smarthome.automation

import grails.validation.Validateable
import smarthome.automation.Mode

/**
 * Enregistement profil
 * 
 * @author gregory
 *
 */
class ModeCommand implements Validateable {
	List<Mode> modes = []
}
