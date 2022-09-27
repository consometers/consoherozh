import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)

    logger('smarthome', DEBUG)
    logger('org.hibernate.SQL', DEBUG)
    logger('org.apache.camel.component',DEBUG)

    logger('org.springframework.security.web.authentication.rememberme',TRACE)
    logger('org.springframework.security.web.authentication',TRACE)


    logger 'org.springframework.security', DEBUG
    logger 'grails.plugin.springsecurity', DEBUG


    logger('org.hibernate',DEBUG)

    logger('net.sf.ehcache.hibernate',INFO)
    logger('grails.app.services',INFO)
    logger('grails.app.controllers',INFO)
       
    logger('org.springframework.jdbc.datasource.DriverManagerDataSource',DEBUG)
    logger('org.grails.orm.hibernate.GrailsHibernateTemplate',DEBUG)

    logger('org.apache.camel',DEBUG)

    // Getting more logs
    root(INFO,  ['STDOUT'])
}
else
{
	root(ERROR, ['STDOUT'])
	logger('smarthome', INFO)
	logger('grails.app', INFO)
	logger('org.apache.camel.component', INFO)
}
