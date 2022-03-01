/**
 * 
 */
package smarthome.security

import grails.validation.Validateable


/**
 * 
 * 
 * @author gregory
 *
 */
class UserCommand
implements Validateable {
	String search
	Boolean profilPublic
	List<Long> notInIds = []

	
	static constraints = {
		search nullable: true	
		profilPublic nullable: true	
	}
}
