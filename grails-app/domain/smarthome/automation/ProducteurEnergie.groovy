package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Producteurs d'énergie
 *  
 * @author gregory
 *
 */
class ProducteurEnergie implements Serializable {
	String libelle
	Double surface
	Double investissement
	Integer nbaction


	static constraints = {
	}


	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
	}
}
