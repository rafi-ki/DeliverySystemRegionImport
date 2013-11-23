/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packageDeliveryService;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.skspackage.schema._2013.deliveryservice.Address;
import org.skspackage.schema._2013.deliveryservice.Package;

/**
 *
 * @author rafael
 */
public class PackageChooserDB implements PackageChooser{
    
    private static final Logger LOGGER = Logger.getLogger(PackageChooserDB.class.getName());
    
    private final EntityManager entityManager;
    private final DirectedPackageRepository packageRepo;
    private final DeliveryRegionRepository devRepo;
    public PackageChooserDB(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        this.packageRepo = new DirectedPackageRepositoryDB(entityManager);
        this.devRepo = new DeliveryRegionRepositoryDB(entityManager);
    }

    @Override
    public List<Package> getDevileredPackageByRegion(String externalId, List<Package> p) {
        
        try{
            DeliveryRegion region = devRepo.getByExternalId(externalId);

            long regionId = region.getId();
            Iterable<DirectedPackage> packagesByRegion = packageRepo.getDirectedPackageByRegionId(regionId);
            
            for(DirectedPackage curPack : packagesByRegion)
            {
                if (!curPack.isDelivered())
                {
                    Package newPackage = new Package();
                    //TODO old
                    QName name = new QName("City");
                    Address address = new Address();
                    address.setCity(new JAXBElement<>(name, String.class, curPack.getCity()));
                    name = new QName("PostalCode");
                    address.setPostalCode(new JAXBElement<>(name, String.class, curPack.getPostalcode()));
                    name = new QName("Street");
                    address.setStreet(new JAXBElement<>(name, String.class, curPack.getStreet()));
                    name = new QName("Address");
                    newPackage.setAddress(new JAXBElement<>(name, Address.class, address));
                    p.add(newPackage); // add package at the end
                    
                    packageRepo.setPackageAsDelivered(curPack.getId());
                }
            }
            
            LOGGER.info("added packages by region successfully");
        } catch(RepositoryException re)
        {
            LOGGER.log(Level.SEVERE, "Could not get list of Packages by specific region", re);
        }
        
        return p;
    }
}
