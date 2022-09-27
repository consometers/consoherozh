package smarthome.automation

import groovy.time.TimeCategory
import smarthome.core.SmartHomeCoreConstantes

/**
 * Token de connexion pour un agent
 *  
 * @author gregory
 *
 */
class AgentToken implements Serializable {
	static belongsTo = [agent: Agent]
	static transients = ['websocketUrl']
	
	Date dateExpiration
	String token
	String websocketKey
	String websocketUrl
	String serverId // identifie le serveur dans un cluster
	
    static constraints = {
		token unique: true
		websocketKey nullable: true
		serverId nullable: true
		websocketUrl nullable: true, bindable: true
    }
	
	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		token index: "AgentToken_Token_Idx"
	}
	
	
	static {
		grails.converters.JSON.registerObjectMarshaller(AgentToken) {
			[dateExpiration: it.dateExpiration, token: it.token, websocketUrl: it.websocketUrl]
		}
	}
	
	
	/**
	 * Token est expiré
	 * 
	 * @return
	 */
	boolean hasExpired() {
		!dateExpiration || dateExpiration < new Date()
	}
	
	
	/**
	 * Calcul un nouveau token avec sa date d'expiration
	 * 
	 * @return
	 */
	def refreshToken() {
		use(TimeCategory) {
			dateExpiration = new Date() + 7.days
		}
		
		token = UUID.randomUUID()
	}
}
