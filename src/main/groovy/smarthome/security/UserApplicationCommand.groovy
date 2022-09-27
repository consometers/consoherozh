package smarthome.security

import grails.validation.Validateable


/**
 * 
 * 
 * @author gregory
 *
 */
class UserApplicationCommand
implements Validateable {
	String search
	User user

	
	static constraints = {
		search nullable: true	
	}
}
