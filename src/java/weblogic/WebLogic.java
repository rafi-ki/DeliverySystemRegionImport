/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package weblogic;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import deliveryRegion.DeliveryRegionService;
import deliveryRegion.DeliveryRegionServiceDB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import packageDeliveryService.PackageChooser;
import packageDeliveryService.PackageChooserDB;
import org.skspackage.schema._2013.deliveryservice.Package;

/**
 *
 * @author rafael
 */
public class WebLogic {
    
    private PackageChooser packageChooser;
    private DeliveryRegionService regionService;
    
    public WebLogic()
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        EntityManager entityManager = factory.createEntityManager();
        packageChooser = new PackageChooserDB(entityManager);
        regionService = new DeliveryRegionServiceDB(entityManager);
    }
    
    public Iterable<DirectedPackage> getUndeliveredPackages(String regionKey)
    {
        List<DirectedPackage> p = new ArrayList<DirectedPackage>();
        Iterable<DirectedPackage> packages = packageChooser.getPackagesByRegionKey(regionKey);
        if (packages == null) // no packages found
            return p;
        for (DirectedPackage dp : packages)
        {
            if (!dp.isDelivered())
                p.add(dp);
        }
        return p;
    }
    
    public int deliverPackages(String regionKey)
    {
        List<Package> list = new ArrayList<Package>();
        packageChooser.getDevileredPackageByRegion(regionKey, list);
        if (list.size() > 0) // if something could have been delivered
            return list.size();
        
        return -1; // if not
    }
    
    public Iterable<DeliveryRegion> getDeliveryRegions()
    {
        return regionService.getAllDeliveryRegions();
    }
    
    public String getReadOnlyDiv(String id, String type, String content)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='" + id + "_" + type + "' class='readonly'>");
        sb.append(content);
        sb.append("</div>");
        
        return sb.toString();
    }
    
    public String getEditField(String id, String type, String content)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type='text' id='" + id + "_" + type + "_input' class='editfield' value='" + content + "'/>");
        return sb.toString();
    }
    
    public void updateRegion(String id, String externalId, String longitude, String latitude)
    {
        System.out.println(longitude + ", " + latitude + ", " + externalId);
        DeliveryRegion region = new DeliveryRegion();
        region.setId(Long.parseLong(id));
        region.setExternal_id(externalId);
        region.setLongitude(Double.parseDouble(longitude));
        region.setLatitude(Double.parseDouble(latitude));
        
        regionService.updateAndReorderDeliveryRegion(Long.parseLong(id), region);
    }
    
    public void deleteRegion(String id)
    {
        long regionId = Long.parseLong(id);
        regionService.deleteRegion(regionId);
    }
}
