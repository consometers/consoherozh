package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes
import smarthome.security.User

/**
 * Description des maisons d'un user
 *  
 * @author gregory
 *
 */
class Mode implements Serializable {
	static belongsTo = [user: User]
	
	String name
	Integer status
	
	
	static transients = ['status']
	
	
    static constraints = {
		status bindable: true, nullable: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		user index: "Mode_User_Idx"
		name length:32
	}
}
