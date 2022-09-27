package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Les infos spécifiques à un type device
 *  
 * @author gregory
 *
 */
class DeviceMetadata implements Serializable {
	static belongsTo = [device: Device]
	
	String name
	String label
	String value
	String type
	String values
	
	
    static constraints = {
		label nullable: true
		value nullable: true
		type nullable: true
		values nullable: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		device index: "DeviceMetadata_Device_Idx"
		values type: 'text'
		version false
	}
}
