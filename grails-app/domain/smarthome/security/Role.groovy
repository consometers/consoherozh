package smarthome.security

import smarthome.core.SmartHomeCoreConstantes

class Role {

	static final String ROLE_PREFIX = "ROLE_"
	
	String authority

	static mapping = {
		table schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA
		sort 'authority'
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
