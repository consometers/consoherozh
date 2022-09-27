package smarthome.core

/**
 * Gestion des workflows métier
 * 
 *  
 * @author gregory
 *
 */
class Workflow {
	String libelle
	String description
	byte[] data
	
	
    static constraints = {
		
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		sort 'libelle'
	}
	
	
	/**
	 * Retourne la clé BPMN
	 * 
	 * @return
	 */
	String bpmnKey() {
		return "${libelle}.bpmn20.xml"	
	}
}
