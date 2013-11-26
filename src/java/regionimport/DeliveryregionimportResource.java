/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package regionimport;

import generated.RegionData;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
import regionImportService.GeodataServiceAgentGoogleMaps;
import regionImportService.RegionImportService;

/**
 * REST Web Service
 *
 * @author rafael,dominik
 */
@Path("import")
public class DeliveryregionimportResource {

    @Context
    private UriInfo context;

    private static final Logger LOGGER = Logger.getLogger(DeliveryregionimportResource.class.getName());
    private RegionImportService regionImportService;
    /**
     * Creates a new instance of DeliveryregionimportResource
     */
    public DeliveryregionimportResource() {
        regionImportService = new RegionImportService(new GeodataServiceAgentGoogleMaps());
    }

    /**
     * Retrieves representation of an instance of regionimport.DeliveryregionimportResource
     * @return an instance of generated.RegionData
     */
    @GET
    public String getXml() {
        return "Hello, I'm sorry but this service is not implemented yet!";
    }

    /**
     * PUT method for updating or creating an instance of DeliveryregionimportResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(RegionData content) {
        // do nothing on put request
    }
    
    @POST
    @Consumes(MediaType.TEXT_XML)
    public void postXml(RegionData content) {
        regionImportService.importRegions(content);
    }
}
