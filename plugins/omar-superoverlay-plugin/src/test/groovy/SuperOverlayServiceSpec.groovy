package omar.superoverlay

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Ignore
import spock.lang.Specification

class SuperOverlayServiceSpec extends Specification implements  DataTest, ServiceUnitTest<SuperOverlayService> {
    void setupSpec() {
        // Leaving dead code here for possible future tests.
        //        LayerInfo layerInfo = new LayerInfo()
        //        WorkspaceInfo workspaceInfo = new WorkspaceInfo()
        //        def workspaceParams = ['foo': 'bar']
        //        StringBuffer kmlFeatures = new StringBuffer()
        //        workspaceInfo.workspaceParams = workspaceParams
        //        layerInfo.workspaceInfo = workspaceInfo
        //        service.geoscriptService = Stub(GeoscriptService) {
        //            findLayerInfo(_) >> layerInfo
        //            parseOptions(_) >> Object
        //            getWorkspace(_) >> workspaceInfo
        //        }
        //        service.kmlService = Stub(KmlService) {
        //            getFeaturesKml(_) >> kmlFeatures
        //        }
    }

    @Ignore
    void 'test validateKmlQueryParams maxFeatures range [0, 100] -- maxFeatures 101'() {
        when:
        // kmlQuery?footprints=on&maxFeatures=101
        def params = [maxFeatures: 101, footprints: "on"]

        then:
        service.validateKmlQueryParams(params) == [maxFeatures: 100, footprints: "on"]
    }
    
    @Ignore
    void 'test validateKmlQueryParams maxFeatures range [0, 100] -- maxFeatures -1'() {
        when:
        // kmlQuery?footprints=on&maxFeatures=-1
        def params = [maxFeatures: -1, footprints: "on"]

        then:
        service.validateKmlQueryParams(params) == [maxFeatures: 0, footprints: "on"]
    }

    @Ignore
    void 'test validateKmlQueryParams maxFeatures range [0, 100] -- maxFeatures 1'() {
        when:
        // kmlQuery?footprints=on&maxFeatures=1
        def params = [maxFeatures: 1, footprints: "on"]

        then:
        service.validateKmlQueryParams(params) == [maxFeatures: 1, footprints: "on"]
    }
}
