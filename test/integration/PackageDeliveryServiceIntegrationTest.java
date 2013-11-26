/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package integration;

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
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 *
 * @author rafael,dominik
 */
public class PackageDeliveryServiceIntegrationTest {
    
    private static EntityManager entityManager;
    private static DirectedPackageRepository packageRepo;
    private static DeliveryRegionRepository devRepo;
    
    private static final String TEST_CITY = "city for test";
    private static final String TEST_STREET = "street for test";
    private static final String TEST_POSTAL = "postalcode for test";
    
    public PackageDeliveryServiceIntegrationTest() {
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
        dp.setStreet(TEST_STREET);
        dp.setCity(TEST_CITY);
        dp.setPostalcode(TEST_POSTAL);
        dp.setDelivered(false);
        
        DirectedPackage dp2 = new DirectedPackage();
        dp2.setDeliveryRegion(region);
        dp2.setId(Integer.MAX_VALUE -1);
        dp2.setCity(TEST_CITY);
        dp2.setStreet(TEST_STREET);
        dp2.setPostalcode(TEST_POSTAL);
        dp2.setDelivered(false);
        
        DeliveryRegion region2 = new DeliveryRegion();
        region2.setExternal_id("FalseRegion");
        region2.setId(Integer.MAX_VALUE - 1);
        DirectedPackage falsedp = new DirectedPackage();
        falsedp.setDeliveryRegion(region2);
        falsedp.setId(Integer.MAX_VALUE - 2);
        falsedp.setDelivered(false);
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

     @Test
     public void testPackageDeliveryService() throws Exception{
          // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "http://localhost:8080/DeliverySystemRegionImport/PackageService";
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
        soapConnection.close();
         printSOAPResponse(soapResponse);
         
        boolean gotRightAnswer = hasTwoPackages(soapResponse);
        
        //assert
        assertTrue(gotRightAnswer);
     }
     
    private boolean hasTwoPackages(SOAPMessage response) throws SOAPException
    {
        Node n = response.getSOAPBody().getFirstChild().getFirstChild();
        System.out.println(n.getNodeName());
        System.out.println(n.getChildNodes().getLength());
        return n.getChildNodes().getLength() == 2;
    }
    
    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapElem = soapBody.addChildElement("GetPackagesForRegion", "", "http://sksPackage.org/2013/DeliveryService");
        soapElem.addNamespaceDeclaration("ns2", "http://schema.sksPackage.org/2013/DeliveryService");
        soapElem.addNamespaceDeclaration("ns3", "http://schemas.microsoft.com/2003/10/Serialization/");
        SOAPElement soapRegionKey = soapElem.addChildElement("regionKey");
        soapRegionKey.setTextContent("TrueRegion");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
    
    /**
     * Method used to print the SOAP Response
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
}
