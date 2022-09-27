package smarthome.automation

import smarthome.security.User;
import grails.validation.Validateable;

class NotificationCommand implements Validateable {
	String description
	User user
	
	
	static constraints = {
		
	}
}
