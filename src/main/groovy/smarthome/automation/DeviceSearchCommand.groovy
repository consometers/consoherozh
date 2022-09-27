package smarthome.automation

import java.util.Map;

import grails.validation.Validateable;

class DeviceSearchCommand
implements Validateable
{
	Map pagination = [:]
	String search
	String deviceTypeClass
	long userId
	long adminId
	String searchGroupe
	String tableauBord
	boolean favori
	Long userSharedId
	Date dateDebut = new Date()
	Date dateFin = new Date()
	
	
	static constraints = {
		search nullable: true
		searchGroupe nullable: true
		deviceTypeClass nullable: true
		tableauBord nullable: true
		userSharedId nullable: true
	}
}
