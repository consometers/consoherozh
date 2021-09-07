package smarthome.core

import org.springframework.security.access.annotation.Secured

@Secured('permitAll')
class PublicController {

    def legal() { }

    def privacy() { }

    def faq() { }
}
