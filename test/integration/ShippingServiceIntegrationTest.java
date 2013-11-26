/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integration;

import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import com.mycompany.deliverysystem.repositories.RepositoryException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author rafael,dominik
 */
public class ShippingServiceIntegrationTest {
    
    private static final String testCity = "city for test";
    private static final String testStreet = "street for test";
    private static final String postalCode = "postalcode for test";
    
    private static EntityManager entityManager;
    private static DirectedPackageRepository packageRepo;
    private static DeliveryRegionRepository devRepo;
        
    public ShippingServiceIntegrationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        entityManager = factory.createEntityManager();
        packageRepo = new DirectedPackageRepositoryDB(entityManager);
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
    }
    
    @AfterClass
    public static void tearDownClass() throws RepositoryException{
        Iterable<DirectedPackage> packages = packageRepo.getAll();
        long idToDelete = -1;
        for (DirectedPackage p : packages)
        {
            if (p.getStreet().equals(testStreet) && p.getCity().equals(testCity) && p.getPostalcode().equals(postalCode))
            {
                idToDelete = p.getId();
                break;
            }
        }
        
        if (idToDelete > 0) // just delete if 
            packageRepo.delete(idToDelete);
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
     public void testAddPackageShippingService() throws Exception{
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "http://localhost:8080/DeliverySystemRegionImport/ShippingService";
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

        soapConnection.close();

        Iterable<DirectedPackage> allPackages = packageRepo.getAll();
        boolean found = false;
        for (DirectedPackage dp : allPackages)
        {
            if (dp.getStreet().equals(testStreet) && dp.getCity().equals(testCity) && dp.getPostalcode().equals(postalCode))
            {
                found = true;
                break;
            }
        }

        //assert
        assertTrue(found);
     }
    
    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ship", "http://sksPackage.org/2013/ShippingService");
        envelope.addNamespaceDeclaration("ship1", "http://schema.sksPackage.org/2013/ShippingService");
        
        
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("AddPackage", "ship");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("package", "ship");
        SOAPElement soapBodyAddress = soapBodyElem1.addChildElement("Address", "ship1");
        soapBodyAddress.addChildElement("City", "ship1").setTextContent(testCity);
        soapBodyAddress.addChildElement("Street", "ship1").setTextContent(testStreet);
        soapBodyAddress.addChildElement("PostalCode", "ship1").setTextContent(postalCode);
        soapBodyAddress.addChildElement("Country", "ship1").setTextContent("Austria");
        soapBodyAddress.addChildElement("Id", "ship1").setTextContent("1");
        
        soapBodyElem1.addChildElement("Id", "ship1").setTextContent("1");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
}
