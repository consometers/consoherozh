package smarthome.automation


import smarthome.core.SmartHomeCoreConstantes
import smarthome.security.User

/**
 * Les graphiques personnalisés
 *  
 * @author gregory
 *
 */
class Chart {
	static belongsTo = [user: User]
	static hasMany = [devices: ChartDevice]
	
	String label
	String chartType
	String groupe
	String ylegend
	
	
    static constraints = {
		ylegend nullable: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		user index: "Chart_User_Idx"
		devices cascade: 'all-delete-orphan'
	}
	
}
