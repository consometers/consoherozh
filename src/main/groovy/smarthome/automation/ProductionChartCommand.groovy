package smarthome.automation

import grails.validation.Validateable

class ProductionChartCommand extends AbstractChartCommand<ProductionChartCommand>
implements Validateable
{
	List<ProducteurEnergieAction> actions = []


	static constraints = {
		navigation nullable: true
	}
}
