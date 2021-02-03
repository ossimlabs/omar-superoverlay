package omar.superoverlay

import grails.testing.spring.AutowiredTest
import grails.testing.web.services.ServiceUnitTest
import groovy.json.JsonSlurper
import omar.superoverlay.SuperOverlayController
import spock.lang.Specification

class SuperOverlayControllerSpec extends Specification implements AutowiredTest, ServiceUnitTest<SuperOverlayService> {

//    Closure doWithSpring() {
//        { ->
//            superOverlayService SuperOverlayService
//        }
//    }
//
//    SuperOverlayService superOverlayService
//    JsonSlurper jsonSlurper
//
//    void setup() {
//        jsonSlurper = new JsonSlurper()
//    }

    void 'test kmlQuery maxFeatures exceeds upper bounds'() {
        when:
        // kmlQuery?footprints=on&maxFeatures=101
        params.maxFeatures = 101
        params.footprints = "on"
        controller.kmlQuery()

        then:
        status != 200
    }
    void 'test kmlQuery maxFeatures exceeds lower bounds'() {
        when:
        // kmlQuery?footprints=on&maxFeatures=0
        params.maxFeatures = -1
        params.footprints = "on"
        controller.kmlQuery()

        then:
        status != 200
    }
}
