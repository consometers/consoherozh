package smarthome.automation

import grails.converters.JSON;
import grails.plugin.cache.CachePut;
import grails.plugin.cache.Cacheable;

import grails.web.mapping.LinkGenerator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import grails.gorm.transactions.Transactional;

import smarthome.core.AbstractService;
import smarthome.core.AsynchronousMessage;
import smarthome.core.ClassUtils;
import smarthome.core.EndPointUtils;
import smarthome.core.ExchangeType;
import smarthome.core.QueryUtils;
import smarthome.core.SmartHomeCoreConstantes;
import smarthome.core.SmartHomeException;
import smarthome.endpoint.AgentEndPoint;
import smarthome.endpoint.AgentEndPointMessage;
import smarthome.endpoint.ShellEndPoint;
import smarthome.endpoint.TeleinfoEndPoint;
import smarthome.security.User;
import smarthome.security.UserService;


class AgentService extends AbstractService {

	// auto inject
	LinkGenerator grailsLinkGenerator
	
	// auto inject
	def grailsApplication
	
	UserService userService
	
	
	/**
	 * Enregistrement d'un domain
	 *
	 * @param domain
	 *
	 * @return domain
	 */
	@PreAuthorize("hasPermission(#agent, 'OWNER')")
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	Agent save(Agent agent) throws SmartHomeException {
		return super.save(agent)
	}
	
	
	/**
	 * Les agents d'un user
	 * 
	 * @param agentSearch
	 * @param userId
	 * @param pagination
	 * @return
	 */
	List<Agent> listByUser(String agentSearch, Long userId, Map pagination) {
		return Agent.createCriteria().list(pagination) {
			user {
				idEq(userId)
			}
			
			if (agentSearch) {
				ilike 'agentModel', QueryUtils.decorateMatchAll(agentSearch)
			}
		}
	}
	
	
	/**
	 * Activation d'un agent pour qu'il se puisse se connecter au websocket
	 * 
	 * @param agent
	 * @param actif
	 * @return
	 * 
	 * @throws SmartHomeException
	 */
	@PreAuthorize("hasPermission(#agent, 'OWNER')")
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	def activer(Agent agent, boolean actif) throws SmartHomeException {
		log.info("agent ${agent.mac} activation : ${actif}")
		agent.locked = !actif
		
		if (!agent.save()) {
			throw new SmartHomeException("Erreur activation agent !", agent)
		}
	}
	
	
	/**
	 * Edition d'un agent
	 * 
	 * @param agent
	 * @return
	 */
	@PreAuthorize("hasPermission(#agent, 'OWNER')")
	Agent edit(Agent agent) {
		return agent
	}
	
	
	/**
	 * Charge un agent pour un utilisateur et vérifie autorisation
	 * 
	 * @param userId
	 * @param agentId
	 * @return
	 * @throws SmartHomeException
	 */
	Agent authorize(Long userId, Long agentId) throws SmartHomeException {
		Agent agent	= Agent.read(agentId)
		
		if (agent.user.id != userId) {
			throw new SmartHomeException("Droits insuffisants pour cet agent !")
		}
		
		return agent
	}
	
	
	/**
	 * Démarre l'association automatique (inclusion ou exclusion) de nouveaux devices sur un agent
	 * 
	 * @param agent
	 * @param inclusion
	 * 
	 * @return
	 * @throws SmartHomeException
	 */
	@PreAuthorize("hasPermission(#agent, 'OWNER')")
	def startAssociation(Agent agent, boolean inclusion) throws SmartHomeException {
		if (agent.locked) {
			throw new SmartHomeException("L'agent ${agent.libelle} n'est pas activé !", agent)
		}
		
		if (!agent.online) {
			throw new SmartHomeException("L'agent ${agent.libelle} n'est pas connecté !", agent)
		}
		
		this.sendMessage(agent, [header: inclusion ? 'startInclusion' : 'startExclusion'])
	}
	
	
	/**
	 * Reset complet de la config de l'agent
	 * 
	 * @param agent
	 * @param inclusion
	 * 
	 * @return
	 * @throws SmartHomeException
	 */
	@PreAuthorize("hasPermission(#agent, 'OWNER')")
	def resetConfig(Agent agent) throws SmartHomeException {
		if (agent.locked) {
			throw new SmartHomeException("L'agent ${agent.libelle} n'est pas activé !", agent)
		}
		
		if (!agent.online) {
			throw new SmartHomeException("L'agent ${agent.libelle} n'est pas connecté !", agent)
		}
		
		this.sendMessage(agent, [header: 'resetConfig'])
	}
	
	
	/**
	 * Demande connexion au websocket
	 * 
	 * @param command
	 * @return
	 * @throws SmartHomeException
	 */
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	def subscribe(MessageAgentCommand command) throws SmartHomeException {
		log.info("agent subscribe for user ${command.username}")
		
		// suppression des erreurs d'un 1er binding car certaines propriétées sont injectée manuel
		command.clearErrors()
		
		if (!command.validate()) {
			throw new SmartHomeException("Subscribe message incomplete !")
		}
		
		def user = userService.authenticateApplication(command.username, command.applicationKey)
		
		// recherche d'un agent en fonction mac
		Agent domainAgent = Agent.findByMacAndUser(command.mac, user, [lock: true])
		def agentToken = null
		
		if (domainAgent) {
			// l'agent doit être activé
			if (domainAgent.locked) {
				throw new SmartHomeException("Agent not activated !")
			}
			
			// recherche d'un token non expiré
			if (! domainAgent.tokens.empty) {
				agentToken = domainAgent.tokens[0]
			} else {
				agentToken = new AgentToken(agent: domainAgent)
			}
			
			if (agentToken.hasExpired()) {
				agentToken.refreshToken()
				
				if (!agentToken.save()) {
					throw new SmartHomeException("Erreur refresh expired token !")
				}
			}
			
			// mise à jour dernière connexion et des IP
			domainAgent.lastConnexion = new Date()
			domainAgent.privateIp = command.privateIp
			domainAgent.publicIp = command.publicIp
			
			if (!domainAgent.save()) {
				throw new SmartHomeException("Erreur subscribe agent !")
			}
		} else {
			// pas d'agent mais les identifiants sont bons donc on le créé auto mais 
			//en mode bloqué le temps de l'activation par le user
			domainAgent = new Agent()
			domainAgent.mac = command.mac
			domainAgent.agentModel = command.agentModel
			domainAgent.privateIp = command.privateIp
			domainAgent.publicIp = command.publicIp
			domainAgent.lastConnexion = new Date()
			domainAgent.locked = true
			domainAgent.user = user
			
			if (!domainAgent.save()) {
				throw new SmartHomeException("Auto-created agent not activated !")
			}
			
			return null
		}
		
		// on y glisse l'url du websocket en gérant le SSL ou pas
		def urlApplication = grailsLinkGenerator.link(uri: AgentEndPoint.URL, absolute: true)
		agentToken.websocketUrl = EndPointUtils.httpToWs(urlApplication)
		
		return agentToken
	}
	
	
	/**
	 * Associe une session websocket à un token d'agent
	 * 
	 * @param websocketId
	 * @param message
	 * @return
	 * @throws SmartHomeException
	 */
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	AgentToken bindWebsocket(String websocketId, AgentEndPointMessage message) throws SmartHomeException {
		// recherche utilisateur avec paire username, applicationKey
		User user = User.findByUsernameAndApplicationKey(message.username, message.applicationKey)
		
		if (!user) {
			throw new SmartHomeException("Utilisateur non valide !")
		}
		
		Agent agent = Agent.findByMacAndUser(message.mac, user)
		
		if (!agent) {
			throw new SmartHomeException("Agent non valide !")
		}
		
		if (agent.locked) {
			throw new SmartHomeException("Agent verrouillé !")
		}
		
		// recherche token
		AgentToken agentToken = AgentToken.findByTokenAndAgent(message.token, agent)
		
		if (!agentToken) {
			throw new SmartHomeException("Token non valide !")
		}
		
		if (agentToken.hasExpired()) {
			throw new SmartHomeException("Token has expired !")
		}
		
		// associe le websocket au token et rend l'agent online
		agentToken.websocketKey = websocketId
		def serverId = grailsApplication.config.smarthome.cluster.serverId
		
		if (!serverId) {
			throw new SmartHomeException("smarthome.cluster.serverId property must be set !")
		}
		
		agentToken.serverId = serverId
		
		if (!agentToken.save()) {
			throw new SmartHomeException("Can't bind websocket : ${agentToken.errors}")
		}
		
		agent.online = true
		
		if (!agent.save()) {
			throw new SmartHomeException("Can't bind websocket : ${agent.errors}")
		}
		
		log.info "Bind websocket ${message.username}"
		
		return agentToken
	}
	
	
	/**
	 * 
	 * @param websocketId
	 * @param token
	 * @return
	 * @throws SmartHomeException
	 */
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	void unbindWebsocket(String token) throws SmartHomeException {
		log.info "Unbind websocket ${token}"
		
		AgentToken agentToken = this.findAgentToken(token) 
		
		if (!agentToken) {
			throw new SmartHomeException("Token not found !")
		}
		
		Agent agent = agentToken.agent
		agent.online = false
		agent.save()
		
		agentToken.delete()
	}
	
	
	/**
	 * Réception d'un message. Ce service ne fait presque rien à part vérifier le bon format des datas.
	 * Le système de message asynchrone avec des règles de routage est utilisé pour lancer le bon service en fonction des datas
	 * Il authentifie l'agent pour éviter de le faire dans les autres services 
	 * 
	 * @param message
	 * @param agentToken
	 * 
	 * @return
	 * @throws SmartHomeException
	 */
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	@AsynchronousMessage()
	Agent receiveMessage(AgentEndPointMessage message, AgentToken agentToken) throws SmartHomeException {
		if (!agentToken.attached) {
			agentToken.attach()	
		}
		
		if (! message.data) {
			throw new SmartHomeException("Data is empty !")
		}
		
		return agentToken.agent
	}
	
	
	
	/**
	 * Envoi d'un message à l'agent en passant par le websocket. Les messages doivent être dirigés
	 * sur le bon serveur dans un environnement clusterisé car seul un serveur est connecté au websocket
	 * 
	 * @param agent
	 * @param message
	 * @return
	 * @throws SmartHomeException
	 */
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	def sendMessage(Agent agent, Map data) throws SmartHomeException {
		if (!agent.attached) {
			agent.attach()
		}
		
		if (!agent.tokens.size()) {
			throw new SmartHomeException("Agent not subscribe !")
		}
		
		def token = agent.tokens[0]
		
		if (!token.serverId || !token.websocketKey) {
			throw new SmartHomeException("Websocket not bind !")
		}
		
		// prépare le message avec les infos de connexion pour permette aussi à l'agent d'authentifier les messages recus
		AgentEndPointMessage message = new AgentEndPointMessage(mac: agent.mac, token: token.token, 
			username: agent.user.username, applicationKey: agent.user.applicationKey, data: data, 
			websocketKey: token.websocketKey)
		
		// il faut envoyer le message au bon serveur dans la bonne Queue 
		// on se sert du serverId qu'on passe en routingKey
		// Seul le bon serveur ayant le websocket va recevoir le message à traiter
		this.sendAsynchronousMessage(SmartHomeCoreConstantes.DIRECT_EXCHANGE,
			ClassUtils.prefixAMQ(this) + '.sendMessage.' + token.serverId, message, ExchangeType.DIRECT)
	}
	
	
	/**
	 * Méthode bas niveau pour envoyer un message à l'agent. cette méthode ne doit pas être appelée directement (utiliser sendMessage)
	 * Cette méthode doit être appelée sur le bon serveur ayant le websocket
	 * 
	 * 
	 * @param token
	 * @param websocketKey
	 * @param message
	 * @return
	 * @throws SmartHomeException
	 */
	def sendMessageToWebsocket(String token, String websocketKey, String message) throws SmartHomeException {
		AgentEndPoint endPoint = EndPointUtils.newEndPoint(AgentEndPoint)
		endPoint.sendMessage(token, websocketKey, message)
	}
	
	
	/**
	 * Envoi d'un message à tous les websockets liés au shell pour la remontée des datas de l'agent
	 * 
	 * @param datas
	 */
	void shellMessage(Agent agent, def datas) {
		ShellEndPoint endPoint = EndPointUtils.newEndPoint(ShellEndPoint)
		endPoint.sendMessage(agent, datas)
	}
	
	
	/**
	 * Envoi d'un message à tous les websockets liés au téléinfo pour la remontée des datas de l'agent
	 * 
	 * @param datas
	 */
	void teleinfoMessage(Agent agent, def datas) {
		TeleinfoEndPoint endPoint = EndPointUtils.newEndPoint(TeleinfoEndPoint)
		endPoint.sendMessage(agent, datas)
	}
	
	
	/**
	 * Utile pour les environnements sans session hibernate automatique
	 * Ex : Camel ESB
	 * 
	 * @param id
	 * @return
	 */
	Agent findById(Serializable id) {
		Agent.get(id)
	}
	
	
	/**
	 * Retrouve le token d'un agent
	 * 
	 * @param token
	 * @return
	 */
	AgentToken findAgentToken(String token) {
		AgentToken.findByToken(token)	
	}
	
	
	/**
	 * Utile pour les environnements sans session hibernate automatique
	 * Ex : Camel ESB
	 *
	 * @param device
	 * @return
	 */
	def findByDevice(Device device) {
		if (!device.attached) {
			device.attach()
		}
		
		return device.agent
	}
}
