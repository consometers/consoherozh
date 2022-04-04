package smarthome.consoherozh

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import smarthome.api.ConsoHerozhService
import smarthome.automation.DeviceValueDay
import smarthome.automation.deviceType.Linky
import smarthome.core.AbstractController
import smarthome.datachallenge.DataChallengeService

import java.text.SimpleDateFormat

@Secured("isAuthenticated()")
class ConsoHerozhController extends AbstractController {

    DataChallengeService dataChallengeService
    ConsoHerozhService consoHerozhService

    def dashboard() {
        def user = authenticatedUser
        Linky linky = dataChallengeService.getLinky(user)

        def isoDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        def linkyDays
        if (linky) {
            linkyDays = DeviceValueDay.createCriteria().list {
                eq 'device', linky.device
                // between 'dateValue', dateDebut, dateFin
                eq "name", "basesum"
                order 'dateValue'
            }
            linkyDays = linkyDays.collect { [
                date: isoDateFormat.format(it.dateValue),
                value: it.value
            ] }
        } else {
            linkyDays = []
        }

        def electricityIndices = consoHerozhService.getIndices(user, "Électricité")
        electricityIndices = electricityIndices.collect { [
                date: isoDateFormat.format(it.dateValue),
                value: it.value
        ] }
        def waterIndices = consoHerozhService.getIndices(user, "Eau")
        waterIndices = waterIndices.collect { [
                date: isoDateFormat.format(it.dateValue),
                value: it.value
        ] }
        def gasIndices = consoHerozhService.getIndices(user, "Gaz")
        gasIndices = gasIndices.collect { [
                date: isoDateFormat.format(it.dateValue),
                value: it.value
        ] }

        render(view: 'dashboard', model: [
                linkyDays: linkyDays as JSON,
                electricityIndices: electricityIndices as JSON,
                waterIndices: waterIndices as JSON,
                gasIndices: gasIndices as JSON,
                linky: linky
        ])
    }

    def recordIndex(String type, String date, int index) {
        def user = authenticatedUser
        consoHerozhService.recordIndex(user, type, date, index)
        render([])
    }

    def removeIndex(String type, String date) {
        def user = authenticatedUser
        consoHerozhService.removeIndex(user, type, date)
        render([])
    }
}
