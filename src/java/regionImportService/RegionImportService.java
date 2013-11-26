/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package regionImportService;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import generated.RegionData;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author rafael,dominik
 */
public class RegionImportService {
    private GeodataServiceAgent agentSmith;
    private static Logger LOGGER = Logger.getLogger(RegionImportService.class.getName());
    private DeliveryRegionRepository repo;
    
    public RegionImportService(GeodataServiceAgent agent)
    {
        agentSmith = agent;
        
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        EntityManager entityManager = factory.createEntityManager();
        repo = new DeliveryRegionRepositoryDB(entityManager);
    }
    
    public boolean importRegions(RegionData regData)
    {
        boolean success = true;
        List<RegionData.Region> regionList = regData.getRegion();
        for (RegionData.Region reg : regionList)
        {
            DeliveryRegion devreg = new DeliveryRegion();
            devreg.setExternal_id(reg.getKey());
            // receive coordinates from geodata-agent
            double [] coords = agentSmith.encodeCoordinates(reg.getAddress().getStreet(), reg.getAddress().getPostalCode(), reg.getAddress().getCity());
            devreg.setLatitude(coords[0]);
            devreg.setLongitude(coords[1]);
            try{
                repo.add(devreg);
            }
            catch (RepositoryException ex)
            {
                success = false;
                LOGGER.log(Level.SEVERE, "Error while importing DeliveryRegion", ex);
            }
        }
        return success;
    }
            
}
