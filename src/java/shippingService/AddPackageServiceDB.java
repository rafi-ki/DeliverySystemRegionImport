/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService;

import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.skspackage.schema._2013.deliveryservice.Address;
import org.skspackage.schema._2013.deliveryservice.Package;
import shippingService.util.PackageSorter;

/**
 *
 * @author rafael
 */
public class AddPackageServiceDB implements AddPackageService{

    private static final Logger LOGGER = Logger.getLogger(AddPackageServiceDB.class.getName());
    
    private final EntityManager entityManager;
    private final DirectedPackageRepository packageRepo;
    private final DeliveryRegionRepository devRepo;
    private PackageSorter sorter;
    
    public AddPackageServiceDB(EntityManager entityManager, PackageSorter sorter)
    {
        this.entityManager = entityManager;
        this.packageRepo = new DirectedPackageRepositoryDB(entityManager);
        this.devRepo = new DeliveryRegionRepositoryDB(entityManager);
        this.sorter = sorter;
    }
    
    @Override
    public void addPackage(Package p) {
        DirectedPackage newPackage = new DirectedPackage();
        
        Address address = p.getAddress().getValue();
        newPackage.setCity(address.getCity().getValue());
        newPackage.setStreet(address.getStreet().getValue());
        newPackage.setPostalcode(address.getPostalCode().getValue());
        
        sorter.sortPackage(newPackage);
        
        try{
            packageRepo.add(newPackage);
            LOGGER.info("added package successfully");
        }
        catch(RepositoryException re)
        {
            LOGGER.log(Level.SEVERE, "could not add package", re);
        }
    }
    
}
