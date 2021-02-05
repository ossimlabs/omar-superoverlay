package omar.superoverlay

import grails.testing.gorm.DataTest
import grails.testing.spring.AutowiredTest
import grails.testing.web.controllers.ControllerUnitTest
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.net.http.HttpResponse

class SuperOverlayControllerSpec extends Specification implements ControllerUnitTest<SuperOverlayController>{

    void 'KmlQueryCommand default maxFeatures, default footprints, default bbox'() {
        when:
        def cmd = new KmlQueryCommand(footprints: '')
        cmd.setConfigDefaultFeatures(10)
        cmd.setConfigMaxFeatures(100)
        cmd.setConfigMinFeatures(0)
        cmd.validate()

        then:
        cmd.errors['footprints'].code == 'blank'
        cmd.getMaxFeatures() == 10
        cmd.getBBOX() == [-180, -90, 180, 90]
    }

    void 'KmlQueryCommand maxFeatures > upper limit'() {
        when:
        def cmd = new KmlQueryCommand(maxFeatures: 101)
        cmd.setConfigDefaultFeatures(10)
        cmd.setConfigMaxFeatures(100)
        cmd.setConfigMinFeatures(0)
        cmd.validate()

        then:
        cmd.hasErrors() == false
        cmd.getMaxFeatures() == 100
    }

    void 'KmlQueryCommand bbox: 1,2,3,4'() {
        when:
        def cmd = new KmlQueryCommand(bbox: '1,2,3,4')
        cmd.validate()

        then:
        cmd.getBBOX() == [1, 2, 3, 4]
    }

    void 'KmlQueryCommand bbox: -181,-91,181,91'() {
        when:
        // if ( b[0] < -180 ) { b[0] = -180 }
        // if ( b[1] < -90 ) { b[1] = -90 }
        // if ( b[2] > 180 ) { b[2] = 180 }
        // if ( b[3] > 90 ) { b[3] = 90 }
        def cmd = new KmlQueryCommand(bbox: '-181,-91,181,91')
        cmd.validate()

        then:
        cmd.getBBOX() == [-180, -90, 180, 90]
    }

    void 'KmlQueryCommand bbox: 1,2,3,4,5'() {
        when:
        def cmd = new KmlQueryCommand(bbox: '1,2,3,4,5')
        cmd.validate()

        then:
        cmd.getBBOX() == null
    }
    void 'KmlQueryCommand bbox: 1,2,3'() {
        when:
        def cmd = new KmlQueryCommand(bbox: '1,2,3')
        cmd.validate()

        then:
        cmd.getBBOX() == null
    }

    void 'kmlQuery status UNPROCESSABLE_ENTITY (422) for invalid bbox: 1,2'() {
        when:
        params.bbox = "1,2"
        controller.kmlQuery()

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

    void 'kmlQuery status OK (200) valid bbox: 1,2,3,4'() {
        given:
        controller.superOverlayService = Stub(SuperOverlayService) {
            kmlQuery(_) >> "testing"
        }
        params.bbox = "1,2,3,4"

        when:
        controller.kmlQuery()

        then:
        status == HttpStatus.OK.value()
    }

    void 'kmlQuery status UNPROCESSABLE_ENTITY (422) for maxFeatures < 0'() {
        when:
        params.maxFeatures = "-1"
        controller.kmlQuery()

        then:
        status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

}
