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

grails {
	project.groupId = appName // change this to alter the default package name and Maven publishing destination

	mime {
		// The ACCEPT header will not be used for content negotiation for user agents containing the following strings
		// defaults to the 4 major rendering engines
		disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
		types = [ // the first one is the default format

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
	}

	// URL Mapping Cache Max Size, defaults to 5000
	//urlmapping.cache.maxsize = 1000

	// The default scope for controllers. May be prototype, session or singleton.
	// If unspecified, controllers are prototype scoped.
	controllers.defaultScope = 'singleton'

	// GSP settings
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

	converters.encoding = "UTF-8"
	scaffolding.templates.domainSuffix = 'Instance'
	json.legacy.builder = false
	enable.native2ascii = true
	// packages to include in Spring bean scanning
	spring.bean.packages = []
	// whether to disable processing of multi part requests
	web.disable.multipart = false

	// request parameters to mask when logging exceptions
	exceptionresolver.params.exclude = ['password', 'newPassword', 'confirmPassword', 'newPassword']

	// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
	hibernate.cache.queries = false

	cache.enabled = true
}

// conflict in dev
// grails.serverURL = "https://www.consoherozh.fr"

environments {

	development {
		grails {
			logging.jul.usebridge = true
			plugin.springsecurity.debug.useFilter = true
		}
	}

	production {
		grails.logging.jul.usebridge = false

	}
}

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


quartz {
	scheduler {
		instanceName = "SmarthomeQuartzScheduler"
		instanceId = "AUTO"
	}
	threadPool.class = "org.quartz.simpl.SimpleThreadPool"
	threadPool.threadCount = 5
	jobStore.class = "org.quartz.impl.jdbcjobstore.JobStoreTX"
	jobStore {
		isClustered = true
		tablePrefix = "quartz.QRTZ_"
		driverDelegateClass = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate"
		dataSource = "smarthomeDataSource"
		clusterCheckinInterval = 20000
	}
}

environments {
	development {
		quartz {
			dataSource {
				smarthomeDataSource.URL = "${System.properties['smarthome.datasource.url']}"
				smarthomeDataSource {
					driver = "org.postgresql.Driver"
					user = "postgres"
					password = "${System.properties['smarthome.datasource.password']}"
					maxConnections = 2
				}
			}
		}
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
	cache {
		use_second_level_cache = true
		use_query_cache = false
		region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory'
	}
	singleSession = true // configure OSIV singleSession mode
	// https://gorm.grails.org/6.1.x/hibernate/manual/#upgradeNotes
	// HACK default mode should be MANUAL where gorm activates COMMIT internally but not working, then forcing COMMIT
	flush.mode = COMMIT
}

// environment specific settings
environments {
	development {
		dataSource {
			driverClassName = "org.postgresql.Driver"
			dialect = org.hibernate.dialect.PostgreSQL82Dialect
			// for migration plugin
			dbCreate = none
			//dbCreate = "update"
			//dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			// ex : jdbc:postgresql://localhost:5432/smarthome
			url = "${System.properties['smarthome.datasource.url']}"
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
	// smartHomeDataSource will be defined in context.xml bean properties
	production { dataSource { jndiName = "java:comp/env/smartHomeDataSource" } }
}


//HACK activity
environments {
   development {
   	     // grails.logging.jul.usebridge = true
	      activiti {
	     	     processEngineName = "activiti-engine-dev"
	     	     databaseSchemaUpdate = "true"
	     }

	     // grails.serverURL = "http://localhost:8080/MaterialManagement/dashboard/main"
   }
}


// Since grails 3 use spring boot , grails.config.location are not in use by defaut
// in particular previous SmartHomeSecurityDefautConfig.groovy won't be parsed.
// embed [SmartHomeSecurityDefautConfig] them directly in application as a first dev step

// Adapted from those added by the Spring Security Core plugin in grails 3.

grails {
	plugin {
		springsecurity {
			userLookup {
				userDomainClassName = 'smarthome.security.User'
				authorityJoinClassName = 'smarthome.security.UserRole'
			}
			authority {
				className = 'smarthome.security.Role'
			}
			useRoleGroups = false
			controllerAnnotations.staticRules = [
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
			filterChain {
				chainMap = [
						[pattern: '/assets/**',      filters: 'none'],
						[pattern: '/**/js/**',       filters: 'none'],
						[pattern: '/**/css/**',      filters: 'none'],
						[pattern: '/**/images/**',   filters: 'none'],
						[pattern: '/**/favicon.ico', filters: 'none'],
						[pattern: '/**',             filters: 'JOINED_FILTERS']
				]
			}
			// Configuration supplémentaire de Spring Security
			rejectIfNoRule = true // bloque par défaut toutes les URLS sauf celle mappées par annotation ou dans la map "staticRules"
			password {
				algorithm = 'bcrypt'
			}
			useSessionFixationPrevention = true // Session Fixation Prevention
			logout {
				postOnly = false
			} // permet de faire des GET pour logout
			useSwitchUserFilter  = true // permet de basculer sur un autre utilisateur

			// Spring ACL
			acl {
				permissionClass = smarthome.security.SmartHomePermission
			}
		}
	}
}


// temporarily while chasing org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration$SpelView errors
server.error.whitelabel.enabled=false

// Whether to translate GORM events into Reactor events
// Disabled by default for performance reasons
gorm.reactor.events=false
