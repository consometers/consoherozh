package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Mode de chauffage
 * 
 * @author gregory
 *
 */
class Chauffage implements Serializable {
	String libelle
	
	
    static constraints = {
		libelle unique: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		sort 'libelle'
	}
}
