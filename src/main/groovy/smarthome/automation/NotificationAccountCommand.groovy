package smarthome.automation

import smarthome.security.User;
import grails.validation.Validateable;


class NotificationAccountCommand
implements Validateable
{
	String libelle
	User user
	
	
	static constraints = {
		
	}
}
