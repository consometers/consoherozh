/**
 * 
 */
package smarthome.security

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grails.core.GrailsApplication;

import grails.plugin.springsecurity.SpringSecurityUtils



/**
 * @author gregory
 *
 */
class SmartHomeSecurityUtils {

	// TODO validators should return message ids instead of messages

	private static final Pattern EMAIL_PATTERN = Pattern.compile("(.+)@(.+)\\.(.+)")
	
	/**
	 * Validator email
	 * Le EmailValidator de commons-validator bloque certains noms de domaine
	 */
	static final emailValidator = { String email, command ->
		Matcher emailMatcher = EMAIL_PATTERN.matcher(email)
		
		if (!emailMatcher.matches()) {
			return "Adresse email non valide !"
		}
		
		if (emailMatcher.group(1).length() < 2) {
			return "Utilisateur email non valide !"
		}
		
		if (emailMatcher.group(2).length() < 4) {
			return "Nom de domaine email non valide !"
		}
		
		if (emailMatcher.group(3).length() < 2) {
			return "Extension email non valide !"
		}
	}
	
	
	/**
	 * Validator pour le mot de passe
	 */
	static final passwordValidator = { String password, command ->
		if (command.username && command.username.equals(password)) {
			return 'securityUtils.password.mustNotMatchMail'
		}

		def conf = SpringSecurityUtils.securityConfig

		int minLength = conf.ui.password.minLength instanceof Number ? conf.ui.password.minLength : 8

		if (!password || password.length() < minLength) {
			return ['securityUtils.password.tooShort', minLength]
		}

		int maxLength = conf.ui.password.maxLength instanceof Number ? conf.ui.password.maxLength : 64

		if (password.length() > maxLength) {
			return ['securityUtils.password.tooLong', maxLength]
		}

		if (conf.ui.password.validationRegex) {
			if (!password.matches(passValidationRegex)) {
				return 'securityUtils.password.notSecure'
			}
		} else {
			String passValidationRegex = '^.*(?=.*\\d)(?=.*[a-zA-Z]).*$'
			if (!password.matches(passValidationRegex)) {
				return 'securityUtils.password.mustContainLettersAndDigits'
			}
		}
	}


	/**
	 * Validator pour la confirmation d'un mot de passe
	 */
	static final passwordConfirmValidator = { value, command ->
		if (command.newPassword != command.confirmPassword) {
			return 'securityUtils.password.doesNotMatchConfirmation'
		}
	}

}
