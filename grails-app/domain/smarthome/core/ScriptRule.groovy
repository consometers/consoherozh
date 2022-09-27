package smarthome.core
/**
 * Une règle métier enregistrée en base dans un script Groovy
 * 
 * @author gregory
 *
 */
class ScriptRule {
	String ruleName
	String description
	String script
	
	// Automatic Timestamping
	// http://grails.org/doc/latest/guide/GORM.html#eventsAutoTimestamping
	Date dateCreated
	Date lastUpdated
	
    static constraints = {
		ruleName unique: true
		dateCreated nullable: true
		lastUpdated nullable: true
    }
	
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		sort 'ruleName'
		script type: 'text'
	}
}
