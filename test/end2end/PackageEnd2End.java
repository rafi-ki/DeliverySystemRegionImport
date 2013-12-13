/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package end2end;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
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
 * @author dominik,rafael
 */

public class PackageEnd2End {

    private static final String testCity = "Wien";
    private static final String testStreet = "Wexstrasse 12";
    private static final String postalCode = "1200";
    
    private static EntityManager entityManager;
    private static DirectedPackageRepository packageRepo;
    private static DeliveryRegionRepository devRepo;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        entityManager = factory.createEntityManager();
        packageRepo = new DirectedPackageRepositoryDB(entityManager);
        devRepo = new DeliveryRegionRepositoryDB(entityManager);
        
         DeliveryRegion region = new DeliveryRegion();
        region.setId(Integer.MAX_VALUE);
        region.setExternal_id("FalseRegion");
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
        region.setExternal_id("TrueRegion");
        region.setLatitude(45);
        region.setLongitude(15);
        devRepo.add(region);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        
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
        
        
        devRepo.delete(Integer.MAX_VALUE);
        devRepo.delete(Integer.MAX_VALUE - 1);
        devRepo.delete(Integer.MAX_VALUE - 2);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
     @Test
     public void testAddPackageAndGetItByRegionOverTheService() throws Exception{
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "http://localhost:8080/DeliverySystemRegionImport/ShippingService";
        SOAPMessage soapResponse = soapConnection.call(createShippingSOAPRequest(), url);

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
        
        
          // Create SOAP Connection
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        url = "http://localhost:8080/DeliverySystemRegionImport/PackageService";
        soapResponse = soapConnection.call(createSOAPRequest(), url);
        soapConnection.close();
         printSOAPResponse(soapResponse);
         
        boolean gotRightAnswer = hasOnePackage(soapResponse);
        
        //assert
        assertTrue(gotRightAnswer);
     }
    
    private static SOAPMessage createShippingSOAPRequest() throws Exception {
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
     private boolean hasOnePackage(SOAPMessage response) throws SOAPException
    {
        Node n = response.getSOAPBody().getFirstChild().getFirstChild();
        System.out.println(n.getNodeName());
        System.out.println(n.getChildNodes().getLength());
        return n.getChildNodes().getLength() == 1;
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