/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package regionImportService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author rafael
 */
public class GeodataServiceAgentGoogleMaps implements GeodataServiceAgent{
    private static Logger LOGGER = Logger.getLogger(GeodataServiceAgentGoogleMaps.class.getName());

    @Override
    public double[] encodeCoordinates(String street, String postal, String city) {
        double [] result = new double[2];
        try{
            street = URLEncoder.encode(street, "UTF-8");
            city = URLEncoder.encode(city, "UTF-8");
        }
        catch (Exception ex)
        {
            LOGGER.fine("Could not encode params");
            
        }
        street = street.replace(" ", "+");
        city = city.replace(" ", "+");
        String url = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + postal + ",+" + street + ",+" + city + "&sensor=false";
        HttpURLConnection con = null;
        try{
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty ( "Content-Type", "text/xml" );
 
            // receiving answer is important, otherwise the server would do nothing
            con.getResponseCode();
            con.getResponseMessage();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder entireXML = new StringBuilder();
            String line = br.readLine();
            while(line != null)
            {
                entireXML.append(line);
                line = br.readLine();
            }
            result = parseXMLToCoordinate(entireXML.toString());
        }
        catch (Exception ex)
        {
            LOGGER.log(Level.SEVERE, "Could not receive GeoData because of an error during sending GeoData Request", ex);
        }
        finally{
            con.disconnect();
        }
        return result;
    }
    
    private double[] parseXMLToCoordinate(String xml)
    {
        LOGGER.info("trying to parse XML document received from geodata service agent");
        try{
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            System.out.println("document created");
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            
            //xpath for latitude
            XPathExpression expr = xpath.compile("//GeocodeResponse/result/geometry/location/lat");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            String latitudeResult = nl.item(0).getTextContent();
            
            //xpath for longitude
            expr = xpath.compile("//GeocodeResponse/result/geometry/location/lng");
            nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            String longitudeResult = nl.item(0).getTextContent();
            
            // return valid values
            return new double[]{Double.valueOf(latitudeResult), Double.valueOf(longitudeResult)};
        }
        catch(Exception ex)
        {
            LOGGER.log(Level.SEVERE, "Could not parse xml to extract latitude and longitude");
            return new double[]{0, 0};
        }
    }
}
