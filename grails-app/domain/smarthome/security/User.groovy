package smarthome.security

import smarthome.core.SmartHomeCoreConstantes;

/**
 * @see resources.groovy pour la personnalisation des renderer json et xml
 * 
 * @author gregory
 *
 */
class User implements Serializable {

	static hasMany = [friends: UserFriend]
	
	String username	// sert aussi d'email qui sera la clé unique
	String password // see UserPasswordEncoderListener for encoding at insert and update.
	String nom
	String prenom
	String applicationKey
	String telephoneMobile
	
	Date lastActivation
	Date lastConnexion
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	boolean profilPublic
	
	
	// la liste des roles (utilisé pour le binding mais n'est pas mappé en base)
	def roles = []

	static transients = ['springSecurityService', 'roles']

	static constraints = {
		username blank: false, unique: true, validator: SmartHomeSecurityUtils.emailValidator
		password blank: false, validator: SmartHomeSecurityUtils.passwordValidator
		nom blank: false
		prenom blank: true
		roles bindable: true
		telephoneMobile nullable: true
		lastConnexion nullable: true
	}

	static mapping = {
		table name: 'utilisateur', schema: SmartHomeCoreConstantes.DEFAULT_SCHEMA // conflit sur certaines bases avec "user"
		password column: '`password`'
		friends cascade: 'all-delete-orphan'
		username index: 'UserApplication_Username_Idx'
		sort 'nom'
	}
	

	static {
		grails.converters.JSON.registerObjectMarshaller(User) {
			[id: it.id, username: it.username, nom: it.nom, prenom: it.prenom]
		}
	}
	

	Set<Role> getAuthorities() {
		def roles = UserRole.createCriteria().list {
			eq 'user', this
			join 'role'
		}
		
		return roles.collect { it.role }
	}
	
	
	boolean hasRole(String role) {
		this.getAuthorities().find {
			it.authority == role
		}
	}

	String getPrenomNom() {
		return "$prenom $nom"
	}
	
	String getNomPrenom() {
		return "$nom $prenom"
	}
	
	String getInitiale() {
		String initiale = prenom[0].toUpperCase()
		String[] tokens = nom.split(" ")
		
		if (tokens.length > 1) {
			initiale += tokens[0][0].toUpperCase() + tokens[1][0].toUpperCase()
		} else if (tokens) {
			initiale += tokens[0][0].toUpperCase()
		}
		
		return initiale
	}
}
