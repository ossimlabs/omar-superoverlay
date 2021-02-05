package omar.superoverlay

import grails.validation.Validateable
import org.springframework.beans.factory.annotation.Value

/**
 * Command object to enforce business rules for SuperOverlay Controller and Service
 */
class KmlQueryCommand implements Validateable {
    Integer maxFeatures = 10
    String footprints = "on"
    String bbox = "-180,-90,180,90"
    List<Double> listBBOX

    @Value('${superoverlay.service.default-features}')
    Integer configDefaultFeatures

    @Value('${superoverlay.service.max-features}')
    Integer configMaxFeatures

    @Value('${superoverlay.service.min-features}')
    Integer configMinFeatures

    static constraints = {
        footprints blank: false, nullable: true
        maxFeatures(nullable: false, validator: { val, obj, errors ->
            if (!obj.validateMaxFeatures(val)) { errors.rejectValue('maxFeatures', 'invalid range') }
        })
        bbox(blank: false, nullable: true, validator: { val, obj, errors ->
            if (!obj.validateBbox(val)) { errors.rejectValue('bbox', 'invalid format') }
        })
    }

    /**
     * Enforce maxFeatures range [0,100].
     * If maxFeatures is lower or higher it will be modified to be at the upper or lower boundary.
     *
     * @param maxFeatures param
     */
    boolean validateMaxFeatures(val) {
        if (val < configMinFeatures)
            return false
        if (val > configMaxFeatures)
            maxFeatures = configMaxFeatures
        return true
    }

    /**
     * Enforce bbox param is provided that it has 4 boundaries, BBOX size is at most -180,-90,180,90, converted
     * bbox param to List<Double> if valid.
     *
     * @param bbox param
     * @return true if bbox param is valid and converted to List<Double>
     */
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

    /**
     * Get the list representation of the maxFeatures param
     *
     * @return bbox param as a list
     */
    List<Double> getBBOX() {
        return listBBOX
    }
 }
