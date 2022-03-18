package smarthome.datachallenge

import grails.gorm.transactions.Transactional
import smarthome.automation.Device
import smarthome.automation.DeviceType
import smarthome.automation.DeviceValueDay
import smarthome.automation.NotificationAccount
import smarthome.automation.NotificationAccountSender
import smarthome.automation.deviceType.Linky
import smarthome.core.Widget
import smarthome.core.WidgetService
import smarthome.core.WidgetUser
import smarthome.security.User

@Transactional
class DataChallengeService {

    def grailsApplication
    WidgetService widgetService

    def serviceMethod() {

    }

    public static final DEFAULT_WIDGETS = [
            "Facebook group":   [col: 0, row: 0],
            "Message":          [col: 1, row: 0],
            "Ma conso":         [col: 1, row: 1],
    ]

    def getWidgets(User user) {
        DEFAULT_WIDGETS.each {
            Widget widget = Widget.findByLibelle(it.key)
            if (!widget) {
                throw new RuntimeException("No default widget named $it.key")
            }
            WidgetUser widgetUser = WidgetUser.findByWidgetAndUser(widget, user)
            if (!widgetUser) {
                widgetUser = widgetService.addWidgetUser(widget, user.id)
                widgetService.moveWidgetUser(widgetUser, it.value.row, it.value.col)
            }
        }
        List<WidgetUser> widgetUsers = widgetService.findAllByUserId(user.id)
        return widgetUsers
    }

    def hasData(User user) {
        Device linky = getLinky(user)
        return DeviceValueDay.countByDevice(linky) > 0
    }

    def getLinky(User user) {
        DeviceType linkyDevice = DeviceType.findByLibelle(grailsApplication.config.enedis.compteurLabel)
        Device device = Device.createCriteria().get {
            eq 'user', user
            eq 'deviceType', linkyDevice
        }
        if (device) {
            Linky linky = device.newDeviceImpl()
            return linky
        } else {
            return null
        }
    }
}
