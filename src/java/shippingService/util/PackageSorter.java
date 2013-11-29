/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shippingService.util;

import com.mycompany.deliverysystem.entities.DirectedPackage;

/**
 *
 * @author rafael,dominik
 */
public interface PackageSorter {
    public void sortPackage(DirectedPackage pack, double[] packageLocation);
}
