package smarthome.automation

import org.springframework.security.access.annotation.Secured
import smarthome.core.AbstractController
import smarthome.plugin.NavigableAction
import smarthome.plugin.NavigationEnum
import smarthome.security.User

class ChauffageController extends AbstractController {

    ChauffageService chauffageService

    @NavigableAction(label = "Chauffages", navigation = NavigationEnum.configuration, header = "Administrateur")
    def search(String chauffageSearch) {
        List<Chauffage> chauffages = chauffageService.list(chauffageSearch, this.getPagination([:]))
        render view: 'search', model: [recordsTotal:chauffages.size(),chauffages:chauffages]
    }

    @Secured("hasRole('ROLE_ADMIN')")
    def add() {
        String libelle = params.libelle
        if (libelle) {
            chauffageService.create(params.libelle)
        }
        redirect action: 'search'
    }
}
