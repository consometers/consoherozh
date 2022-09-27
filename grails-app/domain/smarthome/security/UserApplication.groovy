package smarthome.security

import org.apache.commons.lang.StringUtils
import smarthome.core.SmartHomeCoreConstantes

/**
 * Les applications third-party de l'utilisateur (facebook, twitter, google, etc...)
 * Généralement des application OAuth
 * 
 * @author gregory
 *
 */
class UserApplication implements Serializable {

	static belongsTo = [user: User]
	
	String applicationId
	String token
	String name
	Date dateAuth
	
	
	static constraints = {
		user unique: 'applicationId'
		token unique: true	
	}
	

	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		user index: "UserApplication_User_Idx"
		token index: "UserApplication_Token_Idx"
	}

	
	/**
	 * Template impl
	 * 
	 * @return
	 */
	String view() {
		String templateName = StringUtils.uncapitalize(name.replace(" ", ""))
		"/userApplication/impl/${templateName}"	
	}
}
