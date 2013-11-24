/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author rafael
 */
@WebService(serviceName = "ShippingService")
public class ShippingService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "addPackage")
    public void addPackage(@WebParam(name = "package") org.skspackage.schema._2013.deliveryservice.Package pack) 
    {
        System.out.println("bla");
    }
}
