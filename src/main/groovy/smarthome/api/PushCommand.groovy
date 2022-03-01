/**
 * 
 */
package smarthome.api

import grails.validation.Validateable;

/**
 * Réception d'un message push : envoi de données
 * 
 * @see https://github.com/gelleouet/smarthome-application/wiki/API
 * @author gregory.elleouet@gmail.com<Grégory Elléoouet>
 *
 */
class PushCommand
implements Validateable {
	String application
	String name
	String unite
	String metaname
	List<Map> datas = []
	
	
	static constraints = {
		metaname nullable: true	
		unite nullable: true	
	}
}
