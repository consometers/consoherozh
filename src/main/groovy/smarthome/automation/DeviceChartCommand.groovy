package smarthome.automation

import smarthome.automation.deviceType.AbstractDeviceType
import grails.validation.Validateable


class DeviceChartCommand extends AbstractChartCommand<DeviceChartCommand>
implements Validateable {
	Device device
	AbstractDeviceType deviceImpl
	List<Device> compareDevices = []
	List<List> compareValues = []
	String metaName
	// request to rebuild idle values in database for period
	Boolean buildIdle = false
	
	static constraints = {
		deviceImpl nullable: true
		navigation nullable: true
		dateDebutUser nullable: true
	}
	
	
	@Override
	void navigation() {
		// pas de chart à la journée si plusieurs devices car les heures risquent de ne pas correspondre
		// et puis trop de données à gérer
		if (compareDevices && viewMode == ChartViewEnum.day) {
			viewMode = ChartViewEnum.month	
		}
		
		super.navigation()
	}


	@Override
	DeviceChartCommand cloneForLastYear() {
		DeviceChartCommand command = (DeviceChartCommand)  super.cloneForLastYear()
		command.deviceImpl = deviceImpl
		command.device = device
		return command
	}
	
	
	@Override
	DeviceChartCommand clone() {
		DeviceChartCommand command = (DeviceChartCommand) super.clone()
		command.deviceImpl = deviceImpl
		command.device = device
		return command
	}
}
