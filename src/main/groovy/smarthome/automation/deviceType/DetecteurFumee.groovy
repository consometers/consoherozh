package smarthome.automation.deviceType

import smarthome.automation.ChartTypeEnum;

/**
 * Périphérique Détecteur fumée
 * Aucune action particulière car capteur
 * 
 * @author gregory
 *
 */
class DetecteurFumee extends AbstractDeviceType {
	
	/**
	 * Le détecteur est-il en alerte fumée
	 * @return
	 */
	boolean isSmoke() {
		(device.value?.isDouble() && device.value?.toDouble() > 0) || "true".equals(device.value)		
	}
}
