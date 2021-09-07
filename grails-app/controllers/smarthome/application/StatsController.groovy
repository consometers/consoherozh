package smarthome.application

import grails.plugin.springsecurity.annotation.Secured
import smarthome.automation.Device
import smarthome.automation.DeviceValueDay
import smarthome.automation.NotificationAccount
import smarthome.automation.NotificationAccountSender
import smarthome.security.User

@Secured("permitAll")
class StatsController {

    def index() {

        use (groovy.time.TimeCategory) {

            def dataConnectAccounts = NotificationAccount.createCriteria().list {
                eq 'notificationAccountSender',  NotificationAccountSender.findByLibelle(grailsApplication.config.enedis.appName)
            }
            for (NotificationAccount account: dataConnectAccounts) {
                account.configToJson()
            }

            def dataConnectAccountsExpired = dataConnectAccounts.findAll { it.jsonConfig.expired }
            def dataConnectAccountsLoadCurvesSince24h = dataConnectAccounts.findAll {
                it.jsonConfig.last_consumption_load_curve &&
                        new Date(it.jsonConfig.last_consumption_load_curve as Long) > 24.hours.ago
            }
            def dataConnectAccountsConsumptionsSince24h = dataConnectAccounts.findAll {
                it.jsonConfig.last_daily_consumption &&
                        new Date(it.jsonConfig.last_daily_consumption as Long) > 24.hours.ago
            }

            def manualCountersElectricity = Device.createCriteria().list { eq 'label', "Électricité" }
            def manualCountersWater = Device.createCriteria().list { eq 'label', "Eau" }
            def manualCountersGas = Device.createCriteria().list { eq 'label', "Gaz" }

            def manualCountersElectricityEntries = manualCountersElectricity.collect {
                def device = it
                DeviceValueDay.createCriteria().get {
                    projections { count() }
                    eq "device", device
                }
            }.sum()

            def manualCountersWaterEntries = manualCountersWater.collect {
                def device = it
                DeviceValueDay.createCriteria().get {
                    projections { count() }
                    eq "device", device
                }
            }.sum()

            def manualCountersGasEntries = manualCountersGas.collect {
                def device = it
                DeviceValueDay.createCriteria().get {
                    projections { count() }
                    eq "device", device
                }
            }.sum()

            render(contentType: 'text/json') {
                [
                        'users_count'                        : User.count(),
                        'users_connected_since_30d': User.createCriteria().get {
                            projections {
                                count()
                            }
                            gt "lastConnexion", 30.days.ago
                        },
                        'data_connect_accounts' : dataConnectAccounts.size(),
                        'data_connect_accounts_expired' : dataConnectAccountsExpired.size(),
                        'data_connect_load_curves_since_24h': dataConnectAccountsLoadCurvesSince24h.size(),
                        'data_connect_consumptions_since_24h': dataConnectAccountsConsumptionsSince24h.size(),
                        'manual_counters_electricity' : manualCountersElectricity.size(),
                        'manual_counters_water' : manualCountersWater.size(),
                        'manual_counters_gas' : manualCountersGas.size(),
                        'manual_counters_electricity_entries': manualCountersElectricityEntries,
                        'manual_counters_water_entries': manualCountersWaterEntries,
                        'manual_counters_gas_entries': manualCountersGasEntries
                ]
            }
        }
    }
}
