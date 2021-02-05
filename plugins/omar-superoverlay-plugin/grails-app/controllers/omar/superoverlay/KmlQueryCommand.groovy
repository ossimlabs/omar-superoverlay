package omar.superoverlay

import grails.validation.Validateable
import org.springframework.beans.factory.annotation.Value

class KmlQueryCommand implements Validateable {
    Integer maxFeatures = 10
    String footprints = "on"
    String bbox = "-180,-90,180,90"
    List<Integer> listBBOX

    @Value('${superoverlay.service.default-features}')
    Integer configDefaultFeatures

    @Value('${superoverlay.service.max-features}')
    Integer configMaxFeatures

    @Value('${superoverlay.service.min-features}')
    Integer configMinFeatures

    static constraints = {
        maxFeatures(nullable: false, validator: { val, obj -> obj.validateMaxFeatures(val) })
        footprints blank: false, nullable: true
        bbox(blank: false, nullable: true, validator: { val, obj, errors ->
            if (!obj.validateBbox(val)) errors.rejectValue('bbox', 'invalid format')
        })
    }

    /**
     * Enforce range [0,100]
     * @param val
     * @return
     */
    void validateMaxFeatures(val) {
        if (val < configMinFeatures) maxFeatures = configMinFeatures
        else if (val > configMaxFeatures) maxFeatures = configMaxFeatures
    }

    boolean validateBbox(val) {
        try {
            def b = val.split(",").collect({ it as Double })
            if ( b.size() == 4 ) {
                if ( b[0] < -180 ) { b[0] = -180 }
                if ( b[1] < -90 ) { b[1] = -90 }
                if ( b[2] > 180 ) { b[2] = 180 }
                if ( b[3] > 90 ) { b[3] = 90 }
                listBBOX = b
                return true
            }
            else {
                return false
            }
        }
        catch(final Exception e)
        {
            return false
        }
    }

    List<Integer> getBBOX() {
        return listBBOX
    }
 }
