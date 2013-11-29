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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import shippingService.util.PackageSorter;
import shippingService.util.PackageSorterNearest;

/**
 *
 * @author rafael
 */
public class PackageSorterTest {
    
    private static EntityManager entityManager;
    private static DeliveryRegionRepository devRepo;
    
    public PackageSorterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        entityManager = factory.createEntityManager();
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
        
        DeliveryRegion region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE);
        region.setExternal_id("10");
        region.setLatitude(0);
        region.setLongitude(0);
        devRepo.add(region);
        
        region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE - 1);
        region.setExternal_id("100");
        region.setLatitude(0);
        region.setLongitude(10);
        devRepo.add(region);
        
        region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE - 2);
        region.setExternal_id("100");
        region.setLatitude(10);
        region.setLongitude(10);
        devRepo.add(region);
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception{
        devRepo.delete(Integer.MAX_VALUE);
        devRepo.delete(Integer.MAX_VALUE - 1);
        devRepo.delete(Integer.MAX_VALUE - 2);
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
     public void testPackageSorterNearest() {
         System.out.println("PackageSorterNearest");
         
         PackageSorter sorter = new PackageSorterNearest(entityManager);
         DirectedPackage pack = new DirectedPackage();
         
         sorter.sortPackage(pack, new double[]{8.9,10});
         
         assertEquals((long)Integer.MAX_VALUE - 2, pack.getDeliveryRegion().getId());
     }
}
