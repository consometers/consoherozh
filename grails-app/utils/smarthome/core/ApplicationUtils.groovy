package smarthome.core

import javax.servlet.ServletContext;

import grails.core.GrailsApplication;
import org.grails.web.util.GrailsApplicationAttributes;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Méthodes utilitaires sur application
 *
 * @author gregory
 *
 */
class ApplicationUtils implements ApplicationContextAware {

	private static ApplicationContext springContext
	
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		springContext = context
	}
	
	
	/**
	 * Contexte Spring depuis contexte servlet
	 * 
	 * @param servletContext
	 * @return
	 */
	static ApplicationContext getApplicationContextFromServletContext(ServletContext servletContext) {
		return servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
	}
	
	
	/**
	 * Application Grails depuis contexte Spring
	 *
	 * @param applicationContext
	 * @return
	 */
	static GrailsApplication getGrailsApplicationFromApplicationContext(ApplicationContext applicationContext) {
		return applicationContext.getBean("grailsApplication")
	}
	
	
	/**
	 * Injecte les dépendances manuellement sur un bean
	 *
	 * @param bean
	 * @return
	 */
	static Object autowireBean(Object bean) {
		springContext.getAutowireCapableBeanFactory().autowireBean(bean)
		return bean
	}

}
