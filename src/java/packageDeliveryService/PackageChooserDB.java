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
    
    private Logger LOGGER = Logger.getLogger(PackageChooserDB.class.getName());
    
    private EntityManager entityManager;
    private DirectedPackageRepository packageRepo;
    private DeliveryRegionRepository devRepo;
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
                Package newPackage = new Package();
                QName name = new QName("", "");
                Address address = new Address();
                address.setCity(new JAXBElement<String>(name, String.class, curPack.getCity()));
//                address.setCountry(new JAXBElement<String>(name, String.class, curPack.get));
                address.setPostalCode(new JAXBElement<String>(name, String.class, curPack.getPostalcode()));
                address.setStreet(new JAXBElement<String>(name, String.class, curPack.getStreet()));
                JAXBElement<Address> elem = new JAXBElement<Address>(name, Address.class, address);
                newPackage.setAddress(elem);
                
                System.out.println(curPack.getCity() + ", " + curPack.getStreet());
                p.add(newPackage); // add package at the end
            }
        } catch(RepositoryException re)
        {
            LOGGER.log(Level.SEVERE, "Could not get list of Packages by specific region", re);
        }
        
        return p;
    }
}
