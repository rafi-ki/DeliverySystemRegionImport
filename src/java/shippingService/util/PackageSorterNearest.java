/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService.util;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author rafael,dominik
 */
public class PackageSorterNearest implements PackageSorter{
    
    private static final Logger LOGGER = Logger.getLogger(PackageSorterNearest.class.getName());
    
    private EntityManager entityManager;
    private DeliveryRegionRepository devRepo;
    
    public PackageSorterNearest(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
    }
    
    
    @Override
    public void sortPackage(DirectedPackage pack, double[] packageLocation) {
        try{
            DeliveryRegion closestRegion = devRepo.getClosestByLocation(packageLocation[1], packageLocation[0]);
            pack.setDeliveryRegion(closestRegion);
            
            LOGGER.info("sorted package successfully");
        }
        catch(RepositoryException re)
        {
            LOGGER.log(Level.SEVERE, "Could not sort packages due to repository", re);
        }
    }
}
