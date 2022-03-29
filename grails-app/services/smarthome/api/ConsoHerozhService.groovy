package smarthome.api

import grails.gorm.transactions.Transactional
import smarthome.automation.Device
import smarthome.automation.DeviceType
import smarthome.automation.DeviceValueDay
import smarthome.automation.deviceType.ManualCounter
import smarthome.core.AbstractService
import smarthome.core.DateUtils
import smarthome.security.User

class ConsoHerozhService extends AbstractService {

    def serviceMethod() {

    }

    @Transactional(readOnly = false)
    Device findOrCreateMainDevice(User user, String type) {

        Device device = Device.createCriteria().get {
            eq 'user', user
            eq 'label', type
        }

        if (!device) {
            device = new Device(
                    user: user,
                    unite: 'm³',
                    mac: 'main',
                    label: type,
                    deviceType: DeviceType.findByImplClass(ManualCounter.name))
            device.save(flush:true)
        }

        return device
    }

    def getIndices(User user, String type) {
        Device device = Device.createCriteria().get {
            eq 'user', user
            eq 'label', type
        }
        if (!device) {
            return []
        }
        return DeviceValueDay.findAllByDevice(device)
    }

    @Transactional(readOnly = false)
    def recordIndex(User user, String type, String isoDate, int index) {
        removeIndex(user, type, isoDate)
        Device device = findOrCreateMainDevice(user, type);
        if (!device) {
            return false
        }
        DeviceValueDay value = new DeviceValueDay(
                device: device,
                dateValue: DateUtils.parseDateIso(isoDate),
                name: "basesum",
                value: index)
        value.save()
        return true;
    }

    @Transactional(readOnly = false)
    def removeIndex(User user, String type, String isoDate) {
        Device device = findOrCreateMainDevice(user, type);
        if (!device) {
            return false
        }

        def values = DeviceValueDay.createCriteria().list {
            eq 'device', device
            eq 'dateValue', DateUtils.parseDateIso(isoDate)
        }
        for (value in values) {
            value.delete()
        }
    }
}
