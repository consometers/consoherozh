package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Modes activés sur une maison
 *  
 * @author gregory
 *
 */
class HouseMode implements Serializable  {
	House house
	Mode mode
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		id composite: ['house', 'mode']
		version false
	}
}
