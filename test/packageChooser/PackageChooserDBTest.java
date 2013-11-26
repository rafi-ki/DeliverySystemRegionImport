/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packageChooser;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import packageDeliveryService.PackageChooserDB;
import org.skspackage.schema._2013.deliveryservice.Package;

/**
 *
 * @author rafael,dominik
 */
public class PackageChooserDBTest {
    private static EntityManager entityManager;
    private static DirectedPackageRepository packageRepo;
    private static DeliveryRegionRepository devRepo;
    
    public PackageChooserDBTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws RepositoryException{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        entityManager = factory.createEntityManager();
        packageRepo = new DirectedPackageRepositoryDB(entityManager);
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
        
        DeliveryRegion region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE);
        region.setExternal_id("TrueRegion");
        region.setLatitude(10);
        region.setLongitude(10);
        
        DirectedPackage dp = new DirectedPackage();
        dp.setDeliveryRegion(region);
        dp.setId(Integer.MAX_VALUE);
        
        DirectedPackage dp2 = new DirectedPackage();
        dp2.setDeliveryRegion(region);
        dp2.setId(Integer.MAX_VALUE -1);
        
        DeliveryRegion region2 = new DeliveryRegion();
        region2.setExternal_id("FalseRegion");
        region2.setId(Integer.MAX_VALUE - 1);
        DirectedPackage falsedp = new DirectedPackage();
        falsedp.setDeliveryRegion(region2);
        falsedp.setId(Integer.MAX_VALUE - 2);
        
        devRepo.add(region);
        devRepo.add(region2);
        
        packageRepo.add(dp);
        packageRepo.add(dp2);
        packageRepo.add(falsedp);
    }
    
    @AfterClass
    public static void tearDownClass() throws RepositoryException{
        packageRepo.delete(Integer.MAX_VALUE);
        packageRepo.delete(Integer.MAX_VALUE -1);
        packageRepo.delete(Integer.MAX_VALUE -2);
        
        devRepo.delete(Integer.MAX_VALUE);
        devRepo.delete(Integer.MAX_VALUE -1);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void testPackageChooser() {
         System.out.println("Test of PackageChooser");
         PackageChooserDB pc = new PackageChooserDB(entityManager);
         
         List<Package> packagesByRegion = pc.getDevileredPackageByRegion("TrueRegion", new ArrayList<Package>());
         for (Package p : packagesByRegion)
         {
             System.out.println(p.getAddress().getValue().getCity().getValue());
         }
         assertEquals(packagesByRegion.size(), 2);
     }
}
