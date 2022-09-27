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

    Device findMainDevice(User user, String type) {

        List<Device> devices = Device.createCriteria().list {
            eq 'user', user
            eq 'label', type
        }

        Device device = null
        if (! devices.isEmpty())
        {
            device = devices.last()
            if ( devices.size() > 1 )
            {
                // multiple devices with same type created for a same user.
                // this is acceptable for database model and can have distinct mac
                // but this indicates a problem in consoherozh usage
                log.warn "multiple devices ( ${devices.size()} ) with same name ${type} for ${user.username}"
            }
        }
        return device
    }

    @Transactional(readOnly = false)
    Device findOrCreateMainDevice(User user, String type) {

        Device device = findMainDevice(user, type)

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
        Device device = findMainDevice(user, type);
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
        Device device = findMainDevice(user, type);
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
