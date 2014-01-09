/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package deliveryRegion;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import regionImportService.GeodataServiceAgent;
import regionImportService.GeodataServiceAgentGoogleMaps;

/**
 *
 * @author rafael
 */
public class DeliveryRegionServiceDB implements DeliveryRegionService{
    
    private static Logger LOGGER = Logger.getLogger(DeliveryRegionServiceDB.class.getName());
    
    private DirectedPackageRepository packageRepo;
    private DeliveryRegionRepository devRepo;
    private GeodataServiceAgent agentSmith;
    
    public DeliveryRegionServiceDB(EntityManager entityManager)
    {
        this.packageRepo = new DirectedPackageRepositoryDB(entityManager);
        this.devRepo = new DeliveryRegionRepositoryDB(entityManager);
        agentSmith = new GeodataServiceAgentGoogleMaps();
    }

    @Override
    public Iterable<DeliveryRegion> getAllDeliveryRegions() {
        try{
            Iterable<DeliveryRegion> regions = devRepo.getAll();
            return regions;
        } catch(RepositoryException e)
        {
            LOGGER.log(Level.SEVERE, "could not get all deliveryRegion in service", e);
        }
        return null;
    }

    @Override
    public boolean updateAndReorderDeliveryRegion(long regionId, DeliveryRegion region) {
        List<DirectedPackage> packages;
        try {
            packages = region.getDirectedPackage();
            
            devRepo.update(regionId, region);
            
            // 2.) get new nearest region
            for (DirectedPackage pack : packages)
            {
                double [] coords = agentSmith.encodeCoordinates(pack.getStreet(), pack.getPostalcode(), pack.getCity());
                DeliveryRegion newRegion = devRepo.getClosestByLocation(coords[0], coords[1]);
                pack.setDeliveryRegion(newRegion);
                packageRepo.update(pack.getId(), pack);
            }
            
            return true;
            
        } catch (RepositoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public boolean deleteRegion(long regionId)
    {
        DeliveryRegion region;
        List<DirectedPackage> packages;
        try {
            region = devRepo.getById(regionId);
            packages = region.getDirectedPackage();
            region.setLatitude(Double.MIN_VALUE);
            region.setLongitude(Double.MIN_VALUE);
            //region.setDirectedPackage(new ArrayList<DirectedPackage>());
            region.setDirectedPackage(null);
            
            // 1.) delete all packages in region, because constraint protects from deleting region
            for (DirectedPackage pack : packages)
            {
                packageRepo.delete(pack.getId());
            }
            
            // 2.) delete old region
            devRepo.delete(regionId);
            
            // 3.) add the packages again
            for (DirectedPackage pack : packages)
            {
                double [] coords = agentSmith.encodeCoordinates(pack.getStreet(), pack.getPostalcode(), pack.getCity());
                DeliveryRegion newRegion = devRepo.getClosestByLocation(coords[0], coords[1]);
                pack.setDeliveryRegion(newRegion);
                packageRepo.add(pack);
            }
            
            return true;
            
        } catch (RepositoryException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return false;
        
        
        
    }
    
}
