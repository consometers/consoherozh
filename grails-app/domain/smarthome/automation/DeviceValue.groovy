package smarthome.automation

import smarthome.security.User;
import grails.validation.Validateable;

/**
 * Toutes les valeurs historisées d'un device
 *  
 * @author gregory
 *
 */
@Validateable
class DeviceValue {
	static belongsTo = [device: Device]
	
	String value
	Date dateValue
	
	// permet d'avoir plusieurs types de valeur pour un device
	// si un seul type, ne rien mettre
	String name 
	
	// Infos sur la date pour effectuer des regroupempents en base
	Integer dayOfYear
	Integer weekOfYear
	Integer monthOfYear
	Integer hourOfDay
	
	
    static constraints = {
		name nullable: true
    }
	
	static mapping = {
		name index: "DeviceValue_DeviceName_Idx"
		device index: "DeviceValue_DeviceName_Idx"
		dateValue index: "DeviceValue_DeviceName_Idx"
		
		hourOfDay formula: 'extract(hour from date_value)'
		dayOfYear formula: 'extract(doy from date_value)'
		weekOfYear formula: 'extract(week from date_value)'
		monthOfYear formula: 'extract(month from date_value)'
	}
	
	
	/**
	 * Valeur numérique min par jour
	 *
	 * @param device
	 * @param metaName
	 */
	static def doubleValueMinByDay(Device device, String metaName = null) {
		def values = DeviceValue.valuesByDay(device, metaName)
		
		values?.min {
			it?.toDouble()
		}
	}
	
	
	/**
	 * Valeur numérique max par jour
	 *
	 * @param device
	 * @param metaName
	 */
	static def doubleValueMaxByDay(Device device, String metaName = null) {
		def values = DeviceValue.valuesByDay(device, metaName)
		
		values?.max {
			it?.toDouble()
		}
	}

	
	/**
	 * Valeur numérique min, max par jour
	 *
	 * @param device
	 * @param metaName
	 */
	static def doubleValueAggregategByDay(Device device, String metaName = null) {
		def values = DeviceValue.valuesByDay(device, metaName)
		def result = [:]
			
		result.min = values?.min {
			it?.toDouble()
		}
		result.max = values?.max {
			it?.toDouble()
		}
		result.count = values?.size()
		
		return result
	}
	
	
	/**
	 * Renvoit les valeurs du jour d'un device
	 * 
	 * @param device
	 * @param metaName
	 */
	static def valuesByDay(Device device, String metaName = null) {
		DeviceValue.createCriteria().list {
			eq "device", device
			
			if (metaName) {
				eq "name", metaName
			} else {
				isNull "name"
			}
			
			ge "dateValue", new Date().clearTime()
			
			projections {
				property "value"
			}
		}
	}
	
	
	/**
	 * La 1ere valeur du jour
	 * 
	 * @param device
	 * @param metaName
	 * @param last
	 */
	static def firstValueByDay(Device device, String metaName = null) {
		def values = DeviceValue.createCriteria().list(max: 1) {
			eq "device", device
			
			if (metaName) {
				eq "name", metaName
			} else {
				isNull "name"
			}
			
			ge "dateValue", new Date().clearTime()
			
			order "dateValue", "asc"
		}
		
		return values ? values[0] : null
	}

	
	/**
	 * La dernière valeur du jour
	 * 
	 * @param device
	 * @param metaName
	 * @param last
	 */
	static def lastValueByDay(Device device, String metaName = null) {
		def values = DeviceValue.createCriteria().list(max: 1) {
			eq "device", device
			
			if (metaName) {
				eq "name", metaName
			} else {
				isNull "name"
			}
			
			ge "dateValue", new Date().clearTime()
			
			order "dateValue", "desc"
		}
		
		return values ? values[0] : null
	}
	
}
