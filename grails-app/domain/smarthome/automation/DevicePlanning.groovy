package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Planning activés sur un device
 *  
 * @author gregory
 *
 */
class DevicePlanning implements Serializable  {
	Device device
	Planning planning
	
	// propriétés utilisateur
	Integer status
	
	
	static transients = ['status']
	
	static belongsTo = [device: Device]
	
	static constraints = {
		status nullable: true, bindable: true
	}
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		device index: "DevicePlanning_Idx"
		version false
	}
}
