package smarthome.security

import smarthome.core.SmartHomeCoreConstantes

/**
 * Les amis d'un utilisateur
 * 
 * @author gregory
 *
 */
class UserFriend implements Serializable {

	User user
	User friend
	boolean confirm
	
	
	static belongsTo = [user: User]
	
	
	static constraints = {
		friend unique: 'user'
	}

	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		user index: "UserFriend_User_Idx"
		friend index: "UserFriend_Friend_Idx"
	}

}
