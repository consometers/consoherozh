/**
 * 
 */
package smarthome.security

import smarthome.security.SmartHomeSecurityUtils;
import grails.validation.Validateable


/**
 * Formulaire pour créer un nouveau compte
 * 
 * @author gregory
 *
 */
class AccountCommand
implements Validateable {
	String prenom
	String nom
	String username
	String newPassword
	String confirmPassword
	boolean profilPublic

	
	static constraints = {
		newPassword blank: false, validator: SmartHomeSecurityUtils.passwordValidator
		confirmPassword blank: false, validator: SmartHomeSecurityUtils.passwordConfirmValidator
	}
	
	
	static {
		grails.converters.JSON.registerObjectMarshaller(AccountCommand) {
			[prenom: it.prenom, nom: it.nom, username: it.username]
		}
	}
}
