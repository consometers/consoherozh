package smarthome.automation

import smarthome.core.SmartHomeCoreConstantes

/**
 * Grille tarif des fournisseurs énergie / eau
 * 
 * @author gregory
 *
 */
class DeviceTypeProviderPrix implements Serializable {
	DeviceTypeProvider deviceTypeProvider
	int annee
	double prixUnitaire
	String contrat
	String period
	
	
    static constraints = {
		
    }
	
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		deviceTypeProvider index: 'DeviceTypeProviderPrix_Idx'
		annee index: 'DeviceTypeProviderPrix_Idx'
		contrat length: 16
		period length: 16
	}
}
