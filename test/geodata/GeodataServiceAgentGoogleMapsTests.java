/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package geodata;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import regionImportService.GeodataServiceAgent;
import regionImportService.GeodataServiceAgentGoogleMaps;

/**
 *
 * @author rafael,dominik
 */
public class GeodataServiceAgentGoogleMapsTests {
    
    private GeodataServiceAgent agentSmith;
    public GeodataServiceAgentGoogleMapsTests() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        agentSmith = new GeodataServiceAgentGoogleMaps();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void testGeoDataServiceCoordinates() 
     {
         System.out.println("encodeCoordinates");
         
         //arrange
         String address = "Wexstrasse 12";
         String postalcode = "1200";
         String city = "Wien";
         double [] expectedCoords = new double []{48.2358049, 16.3658617};
         
         //act
         double result[] = agentSmith.encodeCoordinates(address, postalcode, city);
         
         //assert
         assertEquals(expectedCoords[0], result[0], 0.1);
         assertEquals(expectedCoords[1], result[1], 0.1);
     }
}
