package smarthome

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    public final static String LOCAL_CONFIG_LOCATION = "smarthome.config.location"

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {
        String configPath = System.properties[LOCAL_CONFIG_LOCATION]

        if (configPath) {
            File configFile = new File(configPath)
            if (! configFile.exists()) {
                throw new RuntimeException("Since " + LOCAL_CONFIG_LOCATION + " corresponding configuration file is required: " + configPath)
            }
            Resource resourceConfig = new FileSystemResource(configPath)
            YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()
            ypfb.setResources([resourceConfig] as Resource[])
            ypfb.afterPropertiesSet()
            Properties properties = ypfb.getObject()
            environment.propertySources.addFirst(new PropertiesPropertySource(LOCAL_CONFIG_LOCATION, properties))
        }
    }
}