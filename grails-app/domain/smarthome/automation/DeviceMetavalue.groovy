package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Les valeurs spécifiques à un type device
 *  
 * @author gregory
 *
 */
class DeviceMetavalue implements Serializable {
	static belongsTo = [device: Device]
	
	String name
	String label
	String value
	String type
	String unite
	
	/*
	 * sa valeur sera la valeur principale du device
	 */
	boolean main = false
	/*
	 *  la valeur doit être historisée à chaque changement 
	 */
	boolean trace = false
	/*
	 * la valeur devient un device virtuel (enregistrée dans un autre device sous son nom)
	 */
	boolean virtualDevice = false
		
	
	
    static constraints = {
		value nullable: true;
		label nullable: true;
		type nullable: true;
		unite nullable: true;
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		device index: "DeviceMetavalue_Device_Idx"
		unite length: 32
		version false
	}
}
