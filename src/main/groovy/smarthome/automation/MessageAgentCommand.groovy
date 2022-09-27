package smarthome.automation

import grails.validation.Validateable;


class MessageAgentCommand implements Validateable {
	
	String username
	String mac
	String applicationKey
	String privateIp
	String publicIp
	String agentModel
	Map data = [:]
	
	
	static constraints = {
		publicIp nullable: true
	}
}
