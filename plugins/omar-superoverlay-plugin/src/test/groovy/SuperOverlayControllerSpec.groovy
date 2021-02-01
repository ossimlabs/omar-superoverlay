package omar.superoverlay

import grails.testing.spring.AutowiredTest
import grails.testing.web.controllers.ControllerUnitTest
import groovy.json.JsonSlurper
import omar.superoverlay.SuperOverlayController
import spock.lang.Specification

class SuperOverlayControllerSpec extends Specification implements AutowiredTest, ControllerUnitTest<SuperOverlayController> {

    Closure doWithSpring() {
        { ->
            superOverlayService SuperOverlayService
        }
    }

    SuperOverlayService superOverlayService
    JsonSlurper jsonSlurper

    void setup() {
        jsonSlurper = new JsonSlurper()
    }

    void 'test kmlQuery maxFeatures exceeds upper bounds'() {

    }
    void 'test kmlQuery maxFeatures exceeds lower bounds'() {

    }
}
