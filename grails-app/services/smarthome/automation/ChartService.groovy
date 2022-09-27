package smarthome.automation

import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import org.springframework.security.access.prepost.PreAuthorize
import smarthome.automation.Chart
import smarthome.core.AbstractService
import smarthome.core.ClassUtils
import smarthome.core.SmartHomeException
import smarthome.core.chart.ChartTransformer

class ChartService extends AbstractService {

	DeviceValueService deviceValueService
	
	
	/**
	 * Charts user
	 * 
	 * @param command
	 * @param userId
	 * @param pagination
	 * @return
	 */
	List<Chart> listByUser(ChartCommand command, Long userId, Map pagination) {
		Chart.createCriteria().list(pagination) {
			user {
				idEq(userId)
			}
			if (command.groupe) {
				eq 'groupe', command.groupe
			}
			order "label"
		}
	}
	
	
	/**
	 * Edition ACL
	 * 
	 * @param chart
	 * @return
	 */
	@PreAuthorize("hasPermission(#chart, 'OWNER')")
	Chart edit(Chart chart) {
		return chart	
	}
	
	
	/**
	 * Enregistrement d'un Chart
	 *
	 * @param chart
	 *
	 * @return Chart
	 */
	@PreAuthorize("hasPermission(#chart, 'OWNER')")
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	Chart save(Chart chart) throws SmartHomeException {
		// suppression des résultats non bindés
		chart.devices?.removeAll { 
			! it.persist
		}
		
		if (!chart.save()) {
			throw new SmartHomeException("Erreur enregistrement chart", chart);
		}
		
		return chart
	}
	
	
	/**
	 * Suppression d'un chart
	 *
	 * @param chart
	 *
	 * @return Chart
	 */
	@PreAuthorize("hasPermission(#chart, 'OWNER')")
	@Transactional(readOnly = false, rollbackFor = [SmartHomeException])
	void delete(Chart chart) throws SmartHomeException {
		try {
			// flush direct pour catcher une erreur SQL (ex : clé étrangère) et la renvoyer en SmartHomeException
			// sinon l'erreur est déclenchée hors méthode
			chart.delete(flush: true);
		} catch (Exception e) {
			throw new SmartHomeException(e, chart)
		}
	}
	
	
	/**
	 * Charge les valeurs du device depuis sinceHour
	 *
	 * @param command
	 * @return
	 * @throws SmartHomeException
	 */
	Map values(ChartCommand command) throws SmartHomeException {
		log.info "Load values for chart ${command.chart.label} at ${command.dateChart} (${command.viewMode})"
		def map = [:]
		
		command.chart.devices?.each {
			// attention au 3e parametre, il faut lui passer '' pour récupérer les valeurs par défaut dont le name est null
			// car si on passe null, on récupère toutes les valeurs sans distinction du name
			DeviceChartCommand deviceCommand = new DeviceChartCommand(device: it.device,
				deviceImpl: it.device.newDeviceImpl(), metaName: it.metavalue ?: '',
				dateChart: command.dateChart, viewMode: command.viewMode)
			
			def values = deviceValueService.values(deviceCommand)
			
			if (StringUtils.isNotEmpty(it.transformer)) {
				ChartTransformer chartTransformer = ClassUtils.newInstance(it.transformer)
				values = chartTransformer.transform(command, values)
			}
			
			map.put(it, values)
		}
		
		return map
	}
	
	
	/**
	 * Calcul des groupes
	 *
	 * @return
	 */
	List<String> listGroupes(long userId) {
		return Chart.createCriteria().list {
			isNotNull 'groupe'
			eq 'user.id', userId
			projections {
				groupProperty 'groupe'
			}
			order 'groupe'
		}
	}
	
}
