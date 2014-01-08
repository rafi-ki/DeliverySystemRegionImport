/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package deliveryRegion;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author rafael
 */
public class DeliveryRegionServiceDB implements DeliveryRegionService{
    
    private static Logger LOGGER = Logger.getLogger(DeliveryRegionServiceDB.class.getName());
    
    private final DirectedPackageRepository packageRepo;
    private final DeliveryRegionRepository devRepo;
    
    public DeliveryRegionServiceDB(EntityManager entityManager)
    {
        this.packageRepo = new DirectedPackageRepositoryDB(entityManager);
        this.devRepo = new DeliveryRegionRepositoryDB(entityManager);
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
    public void updateAndReorderDeliveryRegion(long regionId, DeliveryRegion region) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
