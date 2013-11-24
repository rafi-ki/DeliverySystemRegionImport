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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author rafael
 */
public class PackageSorterFake implements PackageSorter{

    private static final Logger LOGGER = Logger.getLogger(PackageSorterFake.class.getName());
    private EntityManager entityManager;
    private DeliveryRegionRepository devRepo;
    
    public PackageSorterFake(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
    }
    
    @Override
    public void sortPackage(DirectedPackage pack) {
        try{
            Iterable<DeliveryRegion> allRegions = devRepo.getAll();
            if (allRegions == null)
                LOGGER.severe("there are no regions in the db, so sorter cant assign");
            else
            {
                for (DeliveryRegion region : allRegions)
                {
                    pack.setDeliveryRegion(region); // use the first region
                    break;
                }
            }
            
            LOGGER.info("package with id <" + pack.getId() + "> successfully sorted");
        } 
        catch(RepositoryException re)
        {
            LOGGER.log(Level.SEVERE, "could not sort package with id<" + pack.getId() + ">", re);
        }
    }

    @Override
    public void sortPackages(List<DirectedPackage> packages) {
        LOGGER.info("not implemented yet");
    }
    
}
