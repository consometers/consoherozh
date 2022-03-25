package smarthome.automation

import grails.gorm.transactions.Transactional
import smarthome.core.AbstractService
import smarthome.core.QueryUtils

@Transactional
class ChauffageService extends AbstractService {

    Chauffage create(String libelle) {
        List<Chauffage> chauffages = listChauffageByLibelle(libelle);
        if (chauffages.isEmpty())
        {
            Chauffage chauffage = new Chauffage(libelle:libelle)
            save(chauffage)
        }
        else {
            return chauffages.first();
        }
    }

    List<Chauffage> list(String chauffageSearch, Map pagination) {
        return Chauffage.createCriteria().list(pagination) {
            if (chauffageSearch) {
                ilike 'agentModel', QueryUtils.decorateMatchAll(chauffageSearch)
            }
        }
    }

    List<Chauffage> listChauffageByLibelle( String libelle) {
        return Chauffage.createCriteria().list {
            eq 'libelle', libelle
        }
    }

}
