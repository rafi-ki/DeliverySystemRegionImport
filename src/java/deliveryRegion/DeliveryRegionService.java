/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package deliveryRegion;

import com.mycompany.deliverysystem.entities.DeliveryRegion;

/**
 *
 * @author rafael
 */
public interface DeliveryRegionService {
    public Iterable<DeliveryRegion> getAllDeliveryRegions();
    public boolean updateAndReorderDeliveryRegion(long regionId, DeliveryRegion region);
    public boolean deleteRegion(long regionId);
}
