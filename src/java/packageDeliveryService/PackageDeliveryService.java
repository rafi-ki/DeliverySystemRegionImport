/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packageDeliveryService;

import java.util.List;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.skspackage.schema._2013.deliveryservice.ArrayOfPackage;
import org.skspackage.schema._2013.deliveryservice.Package;
/**
 *
 * @author rafael,dominik
 */
@WebService(serviceName = "PackageService", portName = "Soap_DeliveryService", endpointInterface = "org.skspackage._2013.deliveryservice.DeliveryService", targetNamespace = "http://sksPackage.org/2013/DeliveryService", wsdlLocation = "WEB-INF/wsdl/PackageDeliveryService/deliveryservice.wsdl")
public class PackageDeliveryService {

    public org.skspackage.schema._2013.deliveryservice.ArrayOfPackage getPackagesForRegion(java.lang.String regionKey) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        EntityManager entityManager = factory.createEntityManager();
        PackageChooser chooser = new PackageChooserDB(entityManager);
        ArrayOfPackage allPackages = new ArrayOfPackage();
        chooser.getDevileredPackageByRegion(regionKey, allPackages.getPackage());
        return allPackages;
//        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
