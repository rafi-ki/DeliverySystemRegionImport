/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skspackage.schema._2013.shippingservice.Address;
import org.skspackage.schema._2013.shippingservice.Package;
import shippingService.util.PackageSorterFake;

/**
 *
 * @author rafael
 */
public class AddPackageServiceDBTest {
    
    private static EntityManager entityManager;
    private static DirectedPackageRepository packageRepo;
    private static DeliveryRegionRepository devRepo;
    
    public AddPackageServiceDBTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws RepositoryException{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        entityManager = factory.createEntityManager();
        packageRepo = new DirectedPackageRepositoryDB(entityManager);
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
        
        DeliveryRegion region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE);
        region.setExternal_id("testkey");
        region.setLatitude(10);
        region.setLongitude(10);
        devRepo.add(region);
        
        DirectedPackage dp = new DirectedPackage();
        dp.setId(Integer.MAX_VALUE);
        dp.setCity("city of package");
        dp.setStreet("street of package");
        dp.setPostalcode("1201");
        dp.setDeliveryRegion(region);
        packageRepo.add(dp);
    }
    
    @AfterClass
    public static void tearDownClass() throws RepositoryException{
        packageRepo.delete(Integer.MAX_VALUE);
        devRepo.delete(Integer.MAX_VALUE);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void testAddPackage() throws RepositoryException{
         //arrange
         Package newPackage = new Package();
         QName name = new QName("City");
         Address address = new Address();
         address.setCity(new JAXBElement<>(name, String.class, "city of newPackage"));
         name = new QName("PostalCode");
         address.setPostalCode(new JAXBElement<>(name, String.class, "1202"));
         name = new QName("Street");
         address.setStreet(new JAXBElement<>(name, String.class, "street of newPackage"));
         name = new QName("Address");
         newPackage.setAddress(new JAXBElement<>(name, Address.class, address));
         
         AddPackageServiceDB addPackageService = new AddPackageServiceDB(entityManager, new PackageSorterFake(entityManager));
         
         //act
         addPackageService.addPackage(newPackage);
         
         //assert
         Iterable<DirectedPackage> all = packageRepo.getAll();
         long toDelete = 0;
         boolean found = false;
         for (DirectedPackage p : all)
         {
             if (p.getCity().equals("city of newPackage"))
             {
                 found = true;
                 toDelete = p.getId();
             }
         }
         
         assertTrue(found);
         
         //clean up
         packageRepo.delete(toDelete);
     }
}
