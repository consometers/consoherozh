package smarthome.automation.scheduler

import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import smarthome.automation.Event;
import smarthome.automation.EventService;

/**
 * Un job exécuté toutes les minutes (le niveau le plus fin)
 * pour scanner toutes les events planifiés et calculés si un event 
 * doit être déclenché
 * 
 * Ce job n'exécute pas les événements mais se charge uniquement de calculer le nombre d'événements
 * et de créer des sous jobs distribués (EventCronPaginateSubJob) en paginant les event 
 * pour répartir le travail entre plusieurs consumers
 * 
 * @see EventCronPaginateSubJob
 * @author Gregory
 *
 */
class EventCronMainJob implements Job {

	private static final log = LogFactory.getLog(this)
	private static final int MAX_PAGE = 25
	
	
	@Autowired
	SmarthomeScheduler smarthomeScheduler
	
	@Autowired
	EventService eventService
	
	
	@Override
	void execute(JobExecutionContext jobContext) throws JobExecutionException {
		long nbEvent = eventService.countScheduledEvents()
		
		// chaque page est envoyé à un sous job pour traitement
		if (nbEvent) {
			for (int page=0; page <= nbEvent / MAX_PAGE; page++) {
				smarthomeScheduler.scheduleOneShotJob(EventCronPaginateSubJob,
					jobContext.getScheduledFireTime(), [offset: page * MAX_PAGE, max: MAX_PAGE])
			}
		}
		
		log.info "Scheeduling ${nbEvent} events."
	}
	
	
	public void setSmarthomeScheduler(SmarthomeScheduler smarthomeScheduler) {
		this.smarthomeScheduler = smarthomeScheduler;
	}
	
	void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
}
