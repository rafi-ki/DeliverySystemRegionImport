/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import shippingService.util.PackageSorterNearest;

/**
 *
 * @author rafael,dominik
 */
@WebService(serviceName = "ShippingService", portName = "BasicHttpBinding_IShippingService", endpointInterface = "org.skspackage._2013.shippingservice.IShippingService", targetNamespace = "http://sksPackage.org/2013/ShippingService", wsdlLocation = "WEB-INF/wsdl/ShippingService/ShippingService.wsdl")
public class ShippingService {

    public void addPackage(org.skspackage.schema._2013.shippingservice.Package _package) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        EntityManager entityManager = factory.createEntityManager();
        AddPackageService addPackageService = new AddPackageServiceDB(entityManager, new PackageSorterNearest(entityManager));
        addPackageService.addPackage(_package);
    }
}
