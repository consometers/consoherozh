package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Les valeurs aggrégées sur un mois
 *  
 * @author gregory
 *
 */
class DeviceValueMonth implements Serializable {
	Device device
	Double value
	Date dateValue
	String name 
	
	
	static belongsTo = [device: Device]
	
	
    static constraints = {
		
    }
	
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		dateValue index: "DeviceValueMonth_Idx"
		device index: "DeviceValueMonth_Idx"
		name index: "DeviceValueMonth_Idx", length: 64
		version false
	}
	
	
	static List values(Device device, Date dateDebut, Date dateFin, String metaName = null) {
		return DeviceValueMonth.createCriteria().list {
			eq 'device', device
			between 'dateValue', dateDebut, dateFin
			
			if (metaName) {
				or {
					for (String token : metaName.split(",")) {
						eq "name", token
					}
				}
			}
			
			order 'dateValue', 'name'
		}
	}
}
