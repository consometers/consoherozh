// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// not used in grails 3, embedded at the end.
// grails.config.locations = [SmartHomeSecurityDefaultConfig]

// need a Map else '<<' (leftShift) fails later.
grails.config.locations = []

if (System.env["smarthome.config.location"]) {
	grails.config.locations << "file:" + System.env["smarthome.config.location"]
	println "Use external configuration from system.env : " + System.env["smarthome.config.location"]
} else if (System.properties["smarthome.config.location"]) {
	grails.config.locations << "file:" + System.properties["smarthome.config.location"]
	println "Use external configuration from system.properties : " + System.properties["smarthome.config.location"]
}

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format

	all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
	atom:          'application/atom+xml',
	css:           'text/css',
	csv:           'text/csv',
	form:          'application/x-www-form-urlencoded',
	html:          ['text/html', 'application/xhtml+xml'],
	js:            'text/javascript',
	json:          ['application/json', 'text/json'],
	multipartForm: 'multipart/form-data',
	rss:           'application/rss+xml',
	text:          'text/plain',
	hal:           ['application/hal+json', 'application/hal+xml'],
	xml:           ['text/xml', 'application/xml']]


// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
	views {
		gsp {
			encoding = 'UTF-8'
			htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
			codecs {
				expression = 'html' // escapes values inside ${}
				scriptlet = 'none' // escapes output from scriptlets in GSPs // FIXME Cyril, did not manage to use the raw() thing with ${}
				taglib = 'none' // escapes output from taglibs
				staticparts = 'none' // escapes output from static template parts
			}
		}
		// escapes all not-encoded output at final stage of outputting
		// filteringCodecForContentType.'text/html' = 'html'
	}
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password', 'newPassword', 'confirmPassword', 'newPassword']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

region.factory_class = org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory


// conflict in dev
// grails.serverURL = "https://www.consoherozh.fr"

environments {

	development {
		grails.logging.jul.usebridge = true
		grails.plugin.springsecurity.debug.useFilter = true

	}


	production {
		grails.logging.jul.usebridge = false

	}
}


grails.cache.enabled = true


// ---------------------------------------------------------------------
// 	USER CONFIGURATION (override by grails.config.locations if not empty)
// ---------------------------------------------------------------------

smarthome {
	cluster.serverId = System.properties["smarthome.cluster.serverId"]

	pagination {
		defaultMax = 25
		maxBackend = 500
	}
}


grails.databinding.dateFormats = ['yyyy-MM-dd', 'dd/MM/yyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:ss'Z'"]


quartz.scheduler.instanceName = "SmarthomeQuartzScheduler"
quartz.scheduler.instanceId = "AUTO"
quartz.threadPool.class = "org.quartz.simpl.SimpleThreadPool"
quartz.threadPool.threadCount = 5

quartz.jobStore.isClustered = true
quartz.jobStore.tablePrefix = "quartz.QRTZ_"
quartz.jobStore.class = "org.quartz.impl.jdbcjobstore.JobStoreTX"
quartz.jobStore.driverDelegateClass = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate"
quartz.jobStore.dataSource = "smarthomeDataSource"
quartz.jobStore.clusterCheckinInterval = 20000

environments {
	development {
		quartz.dataSource.smarthomeDataSource.driver = "org.postgresql.Driver"
		quartz.dataSource.smarthomeDataSource.URL = "jdbc:postgresql://localhost:5432/smarthome"
		quartz.dataSource.smarthomeDataSource.user = "postgres"
		quartz.dataSource.smarthomeDataSource.password = "${System.properties['smarthome.datasource.password']}"
		quartz.dataSource.smarthomeDataSource.maxConnections = 2
	}
	production {
		quartz.dataSource.smarthomeDataSource.jndiURL = "java:comp/env/smartHomeDataSource"
		quartz.scheduler.skipUpdateCheck = true
	}
}

// HACK, dunno where smtp.port has to be configured ...
environments {
	development {
		    smtp {
		    	 port = 465
			 hostname = "localhost"
			 from = "dev@smarthome.dev"
			 username = "dev@smarthome.dev"
			 password =" whatever"
		}
	}
}

// HACK pour rabbitmq

// ---------------------------------------------------------------------
// 	USER CONFIGURATION (override by grails.config.locations if not empty)
// ---------------------------------------------------------------------


rabbitmq {
	connectionfactory {
		username = 'guest'
		password = 'guest'
		hostname = 'localhost'
	}
	
	messageDirectory = '/tmp/RabbitMQ'
}

// ---------------------------------------------------------------------
// merged from DataSource.groovy when upgrading from grails 2.x tro 3.x
// ---------------------------------------------------------------------


hibernate {
	generate_statistics = false
	cache.use_second_level_cache = true
	cache.use_query_cache = false
	// not anymore Hibernate > 4.0
	// cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
	// cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
	cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
	singleSession = true // configure OSIV singleSession mode
	flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
	development {
		dataSource {
			driverClassName = "org.postgresql.Driver"
			dialect = org.hibernate.dialect.PostgreSQL82Dialect
			dbCreate = "update"
			//dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://localhost:5432/smarthome"
			username = "postgres"
			password = "${System.properties['smarthome.datasource.password']}"
			properties {
				validationQuery = "SELECT 1"
				maxActive = 10
				removeAbandoned = true
				logAbandoned = true
			}
		}
	}
	production { dataSource { jndiName = "java:comp/env/smartHomeDataSource" } }
}


//HACK activity
environments {
   development {
   	     // grails.logging.jul.usebridge = true
	      activiti {
	     	     //processEngineName = "activiti-engine-dev"
	     	     databaseSchemaUpdate = "true"
	     }

	     // grails.serverURL = "http://localhost:8080/MaterialManagement/dashboard/main"
   }
}


// Since grails 3 use spring boot , grails.config.location are not in use by defaut
// in particular previous SmartHomeSecurityDefautConfig.groovy won't be parsed.
// embed [SmartHomeSecurityDefautConfig] them directly in application as a first dev step

// Adapted from those added by the Spring Security Core plugin in grails 3.

grails.plugin.springsecurity.userLookup.userDomainClassName = 'smarthome.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'smarthome.security.UserRole'
grails.plugin.springsecurity.authority.className = 'smarthome.security.Role'
grails.plugin.springsecurity.useRoleGroups = false

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
		[pattern: '/',               access: ['permitAll']],
		[pattern: '/error',          access: ['permitAll']],
		[pattern: '/index',          access: ['permitAll']],
		[pattern: '/index.gsp',      access: ['permitAll']],
		[pattern: '/shutdown',       access: ['permitAll']],
		[pattern: '/assets/**',      access: ['permitAll']],
		[pattern: '/**/js/**',       access: ['permitAll']],
		[pattern: '/**/css/**',      access: ['permitAll']],
		[pattern: '/**/images/**',   access: ['permitAll']],
		[pattern: '/**/favicon.ico', access: ['permitAll']],
]

grails.plugin.springsecurity.filterChain.chainMap = [
		[pattern: '/assets/**',      filters: 'none'],
		[pattern: '/**/js/**',       filters: 'none'],
		[pattern: '/**/css/**',      filters: 'none'],
		[pattern: '/**/images/**',   filters: 'none'],
		[pattern: '/**/favicon.ico', filters: 'none'],
		[pattern: '/**',             filters: 'JOINED_FILTERS']
]

// Configuration supplémentaire de Spring Security
grails.plugin.springsecurity.rejectIfNoRule = true // bloque par défaut toutes les URLS sauf celle mappées par annotation ou dans la map "staticRules"
grails.plugin.springsecurity.password.algorithm = 'bcrypt' /// encryption des mots de passe
grails.plugin.springsecurity.useSessionFixationPrevention = true // Session Fixation Prevention
grails.plugin.springsecurity.logout.postOnly = false // permet de faire des GET pour logout
grails.plugin.springsecurity.useSwitchUserFilter  = true // permet de basculer sur un autre utilisateur


// Spring ACL
grails.plugin.springsecurity.acl.permissionClass = smarthome.security.SmartHomePermission


