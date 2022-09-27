package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Devices associés à l'event
 *  
 * @author gregory
 *
 */
class EventDevice implements Serializable  {
	static belongsTo = [event: Event, device: Device]
	
	Event event
	Device device
	
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		event index: "EventDevice_Event_Idx"
		device index: "EventDevice_Device_Idx"
		version false
	}
}
