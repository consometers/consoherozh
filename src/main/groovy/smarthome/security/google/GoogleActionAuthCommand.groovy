/**
 * 
 */
package smarthome.security.google

import org.apache.commons.lang.StringUtils;

import smarthome.core.SmartHomeException;
import grails.validation.Validateable


/**
 * OAuth request params for google actions
 * 
 * @author gregory
 *
 */
class GoogleActionAuthCommand
implements Validateable {
	String applicationId
	String applicationName
	
	String client_id
	String redirect_uri
	String state
	String response_type
	String username
	String password
	String error
	String scope
	
	
	/**
	 * L'url de redirection complétée
	 * 
	 * @return
	 */
	String redirectUrl(String token) {
		return "${redirect_uri}#access_token=${token}&token_type=bearer&state=${state}"	
	}
}
