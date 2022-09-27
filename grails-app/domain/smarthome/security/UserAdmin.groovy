package smarthome.security

import smarthome.core.SmartHomeCoreConstantes

/**
 * Un administrateur de plusieurs utilisateurs
 * 
 * @author gregory
 *
 */
class UserAdmin implements Serializable {

	User user
	User admin
	
	
	static constraints = {
		
	}

	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		admin index: "UserAdmin_Admin_Idx"
		id composite: ['user', 'admin']
		version false
	}

}
