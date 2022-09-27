package smarthome.automation.notification

import smarthome.automation.Notification;
import smarthome.core.SmartHomeException;

/**
 * Configuration et envoi de notification
 * 
 * @author Gregory
 *
 */
interface NotificationSender {

	/**
	 * Envoi de notification
	 * 
	 * @throws SmartHomeException
	 */
	void send(Notification notification, Map context) throws SmartHomeException
	
	
	/**
	 * Nom complet du sender
	 * 
	 * @return
	 */
	String getName()
	
	
	/**
	 * Nom simple du sender
	 * 
	 * @return
	 */
	String getSimpleName()
}
