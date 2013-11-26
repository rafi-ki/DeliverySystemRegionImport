/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package regionImportService;

/**
 *
 * @author rafael,dominik
 */
public interface GeodataServiceAgent {
    public double[] encodeCoordinates(String street, String postal, String city);
}
