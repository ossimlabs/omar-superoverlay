package omar.superoverlay

import org.springframework.beans.factory.InitializingBean

import io.swagger.annotations.*
import org.springframework.http.HttpStatus

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO
import org.apache.commons.collections.map.CaseInsensitiveMap

import geoscript.filter.Filter
import geoscript.workspace.Workspace


@Api( value = "/superOverlay",
    description = "SuperOverlay Support"
)
class SuperOverlayController implements InitializingBean
{
  def baseDir
  def serverUrl
  def superOverlayService
  def outputKmz = false
  def dataSource
  def geoscriptService

  def index()
  {
    render ""
  }

  @ApiOperation( value = "Create a KML SuperOverlay for viewing in GoogleEarth",
      produces = 'application/vnd.google-earth.kmz',
      httpMethod="GET"
  )
  @ApiImplicitParams( [
      @ApiImplicitParam( name = 'id', value = 'id of the image (can be database id, image id, or index id)', paramType = 'query', dataType = 'string', required = true )
  ] )
  def createKml()
  {
    try
    {
      def rasterEntry

      try
      {
        if ( params.id )
        {
          Workspace.withWorkspace( geoscriptService.getWorkspace(
              dbtype: 'postgis',

              // All these can be blank (except for port for some reason)
              // The dataSource is provided by Hibernate.
              database: '',
              host: '',
              port: 5432,
              user: '',
              password: '',

              'Data Source': dataSource,
              'Expose primary keys': true
          ) ) { omardb ->
            rasterEntry = omardb['raster_entry'].getFeatures(
                new Filter( "in (${params.id})" ).or( "image_id='${params.id}'" ).or( "index_id='${params.id}'" )
            )?.first()
          }
        }
      }
      catch ( Exception e )
      {
        rasterEntry = null
        e.printStackTrace()
      }

      if ( rasterEntry )
      {
        def tempOutputKmz = outputKmz

        // There is currently a bug when a comma separated band list is given for kmz output
        // we will force to false if such a param is given.
        // For some reason if bands= is given and has more than one
        // band indicator it causes X to appear on the google earth window.
        // STill can't locate the reasoning so for now if KMZ is enabled and
        // we see a band= with more than 1 band indicator we will force to false and
        // output as a KML and add a href to the WMS chip service instead of embedding the chip
        // within the KMZ.
        //
        def caseInsensitiveParams = new CaseInsensitiveMap( params )
        if ( tempOutputKmz && caseInsensitiveParams.bands )
        {
          tempOutputKmz = caseInsensitiveParams.bands.split( "," ).length < 2
        }
        // we will return the root document if any level of detail param is null
        //

        def isRoot = ( ( params.level == null ) && ( params.row == null ) && ( params.col == null ) )
        if ( !tempOutputKmz )
        {
          if ( !isRoot )
          {
            try
            {
              def kmlString = superOverlayService.createTileKml( rasterEntry, params )
              //response.setDateHeader("Expires", System.currentTimeMillis()+(24*24*60*60*1000));
              // response.addHeader("Cache-Control", "max-age=120")
              //   response.setHeader("max-age", "120");
              render( contentType: "application/vnd.google-earth.kml+xml", text: kmlString,
                  encoding: "UTF-8" )
            }
            catch ( e )
            {
              e.printStackTrace()
            }
          }
          else
          {
            try
            {
              def kmlString = superOverlayService.createRootKml( rasterEntry, params )
              response.setHeader( "Content-disposition", "attachment; filename=omar-export-${params.id}.kml" )
              render( contentType: "application/vnd.google-earth.kml+xml",
                  text: kmlString,
                  encoding: "UTF-8" )
            }
            catch ( e )
            {
              e.printStackTrace()
            }
          }
        }
        else
        {
          if ( !isRoot )
          {
            def kmlInfoMap = superOverlayService.createTileKmzInfo( rasterEntry, params )
            response.contentType = "application/vnd.google-earth.kmz"
            response.setHeader( "Content-disposition", "attachment; filename=output.kmz" )

            def zos = new ZipOutputStream( response.outputStream )
            //create a new zip entry
            def anEntry = null

            anEntry = new ZipEntry( "doc.kml" );
            //place the zip entry in the ZipOutputStream object
            zos.putNextEntry( anEntry );

            zos << kmlInfoMap.kml
            if ( kmlInfoMap.imagePath )
            {
              anEntry = new ZipEntry( "${kmlInfoMap.imagePath}" );
              //place the zip entry in the ZipOutputStream object
              zos.putNextEntry( anEntry );
              if ( kmlInfoMap.image )
              {
                ImageIO.write( kmlInfoMap.image, kmlInfoMap.format, zos );
              }
            }
            zos.close();
          }
          else
          {
            try
            {
              def kmlString = superOverlayService.createRootKml( rasterEntry, params )
              response.setHeader( "Content-disposition", "attachment; filename=omar-export-${params.id}.kml" )
              render( contentType: "application/vnd.google-earth.kml+xml",
                  text: kmlString,
                  encoding: "UTF-8" )
            }
            catch ( e )
            {
              e.printStackTrace()
            }
          }
        }
      }
    }
    catch ( e )
    {
      e.printStackTrace()
    }
  }

    def getLastImagesKml() {
        def kmlString = superOverlayService.getLastImagesKml(params)
        def filename = "O2 Last ${params.max ?: 10} Images.kml".replace(' ', '_')

        response.setHeader("Content-Disposition", "attachment; filename=${filename}")

        render (
            contentType: "application/vnd.google-earth.kml+xml",
            encoding: "UTF-8",
            text: kmlString
        )
    }

    @ApiOperation(
        produces = 'application/vnd.google-earth.kml',
        value = "Get a KML with the most recent images in your BBOX.",
        httpMethod="GET"
    )
    @ApiImplicitParams([
        @ApiImplicitParam(
            dataType = 'string',
            name = 'BBOX',
            paramType = 'query',
            required = false,
            value = 'An AOI, e.g. minLon,minLat,maxLon,maxLat (usually set automatically by an external GOEINT tool)'
        ),
        @ApiImplicitParam(
            allowableValues = "off,on",
            dataType = 'string',
            defaultValue = "on",
            name = 'footprints',
            paramType = 'query',
            required = false,
            value = 'Whether to include footprints in the results or not.'
        ),
        @ApiImplicitParam(
            dataType = 'integer',
            name = 'maxFeatures',
            paramType = 'query',
            required = false,
            defaultValue = "10",
            value = "The maximum number of images to be returned -- Range [0,100]. Requests greater than the allowable range are limited to the upper range limit."
        )
    ])
    def kmlQuery(@ApiParam (hidden=true, required=false) KmlQueryCommand cmd) {
        cmd.validate()
        if (cmd.errors.hasErrors()) {
          render status: HttpStatus.UNPROCESSABLE_ENTITY
          return
        }
        def kmlString = superOverlayService.kmlQuery(cmd)
        render(
            contentType: "application/vnd.google-earth.kml+xml",
            encoding: "UTF-8",
            text: kmlString
        )
    }

  public void afterPropertiesSet()
  {
//    baseDir = grailsApplication.config.export?.superoverlay?.baseDir
//    outputKmz = grailsApplication.config.export?.superoverlay?.outputKmz
//    if ( outputKmz == null )
//    {
//      outputKmz = false // make it default to false
//    }
//    //serverUrl = grailsApplication.config.export?.superoverlay?.serverUrl
  }
}
