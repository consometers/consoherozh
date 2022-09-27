package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Fournissuer pour un type de device
 * 
 * Ex : edf, eau, gaz, etc..
 *  
 * @author gregory
 *
 */
class DeviceTypeProvider implements Serializable {
	String libelle
	DeviceType deviceType
	
	
    static constraints = {
		libelle unique: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		sort 'libelle'
	}
}
