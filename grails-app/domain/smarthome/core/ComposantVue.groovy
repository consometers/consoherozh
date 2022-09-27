package smarthome.core
/**
 * Enregistrement état d'un composant vue pour un utilisateur donné
 *  
 * @author gregory
 *
 */
class ComposantVue {
	String name
	String page
	long userId
	String data
	
    static constraints = {
		name unique: ['page','userId']
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		name index: "ComposantVue_NamePageUser_Idx"
		page index: "ComposantVue_NamePageUser_Idx"
		userId index: "ComposantVue_NamePageUser_Idx"
	}
}
