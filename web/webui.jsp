<%-- 
    Document   : configure
    Created on : 08.01.2014, 13:50:39
    Author     : rafael
--%>

<%@page import="com.mycompany.deliverysystem.entities.DeliveryRegion"%>
<%@page import="com.mycompany.deliverysystem.entities.DirectedPackage"%>
<%@page import="weblogic.WebLogic"%>
<%@page import="javax.persistence.Persistence"%>
<%@page import="javax.persistence.EntityManager"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <%!  WebLogic weblogic = new WebLogic(); %> 
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Hello World</title>
    <style>
        .readonly {display:block}
        .editfield {display:none}
    </style>
</head>
<body>
    <h2> Packages </h2>
    <div style="font-weight: bold">Display Packages:<div>
 <form action="webui.jsp" method="POST">
        <input type="text" name="regionKey" />
        <input type="submit" value="show packages" />
</form>
<table border="0" cellpadding="3">
    <tbody>
    <%
        String key = request.getParameter("regionKey");
        if (key != null)
        {
            Iterable<DirectedPackage> packages = weblogic.getUndeliveredPackages(key);
            if (!packages.iterator().hasNext())
            {
                out.print("<th>no packages with this key</th>");
            }
            else
            {
                out.print("<th>ID</th><th>City</th><th>Street</th><th>PostalCode</th>");
                for (DirectedPackage p : packages)
                {
                    out.print("<tr>");
                    out.print("<td>" + p.getId() + "</td>");
                    out.print("<td>" + p.getCity() + "</td>");
                    out.print("<td>" + p.getStreet() + "</td>");
                    out.print("<td>" + p.getPostalcode() + "</td>");
                }
            }
        }
    %>
    </tbody>
   </table>
    
    <br>
    <div style="font-weight: bold">Deliver Packages:</div>
    <!-- manual package shipping -->
    <form action="webui.jsp" method="POST">
        <input type="text" name="regionKeyForDeliver" />
        <input type="submit" value="deliver packages" />
    </form>
    
    <% 
        String regionKeyForDeliver = request.getParameter("regionKeyForDeliver");
        if (regionKeyForDeliver != null)
        {
            int count = weblogic.deliverPackages(regionKeyForDeliver);
            out.print("<b>");
            if (count > 0)
                out.print(count + " Packages delivered successfully");
            else
                out.print("no packages were found");
            out.print("</b>");
        }
    %>
    
    <h2>Regions</h2>
    <div style="font-weight: bold">Edit Regions:</div>
    <table cellpadding="3">
        <tbody>
        <th>RegionId</th><th>ExternalId</th><th>Longitude</th><th>Latitude</th>
            <%
                Iterable<DeliveryRegion> regions = weblogic.getDeliveryRegions();
                String div = "";
                String input = "";
                for (DeliveryRegion region : regions)
                {
                    out.print("<tr>");
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getId()), "id", String.valueOf(region.getId()));
                    input = weblogic.getEditField(String.valueOf(region.getId()), "id", String.valueOf(region.getId()));
                    out.print("<td>" + div + input + "</td>");
                    div = weblogic.getReadOnlyDiv(region.getExternal_id(), "externalid", region.getExternal_id());
                    input = weblogic.getEditField(region.getExternal_id(), "externalid", region.getExternal_id());
                    out.print("<td>" + div + input + "</td>");
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getLongitude()), "longitude", String.valueOf(region.getLongitude()));
                    input = weblogic.getEditField(String.valueOf(region.getLongitude()), "longitude", String.valueOf(region.getLongitude()));
                    out.print("<td>" + div + input + "</td>");
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getLatitude()), "latitude", String.valueOf(region.getLatitude()));
                    input = weblogic.getEditField(String.valueOf(region.getLatitude()), "latitude", String.valueOf(region.getLatitude()));
                    out.print("<td>" + div + input + "</td>");
                    out.print("</tr>");
                }
            %>
        </tbody>
    </table>
</body>
</html>
