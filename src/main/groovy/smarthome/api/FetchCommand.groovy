/**
 * 
 */
package smarthome.api

import grails.databinding.BindingFormat

import grails.validation.Validateable

/**
 * Réception d'un message fetch
 * 
 * @see https://github.com/gelleouet/smarthome-application/wiki/API
 * @author gregory.elleouet@gmail.com <Grégory Elléoouet>
 *
 */
class FetchCommand
implements Validateable {
	String application
	String name
	String metaname
	@BindingFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	Date start
	@BindingFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	Date end
	long offset = 0
	Long limit
	String order = "asc"


	static constraints = {
		start nullable: true
		end nullable: true
		limit nullable: true
	}
}
