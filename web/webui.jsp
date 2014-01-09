<%-- 
    Document   : configure
    Created on : 08.01.2014, 13:50:39
    Author     : rafael
--%>

<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.Reader"%>
<%@page import="java.io.InputStreamReader"%>
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
    <script src="webui.js" ></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <style>
        .readonly {display:block}
        .editfield {display:none}
        .cancel {display:none}
        .commit {display:none}
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

    <div style="font-weight: bold">Upload Regions:</div>
   <form id="uploadForm" action="webui.jsp" method="POST" enctype="multipart/form-data">
       <input type="file" name="file" id="file" />
       <input type="hidden" name="uploadIndicator" value="true" />
       <input type="submit" value="upload" />
   </form>
   <%
        if (request.getParameter("uploadIndicator") != null)
        {
            Part filePart = request.getPart("file");
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), "UTF-8"));
            String read = reader.readLine();
            
            while(read != null) {
                sb.append(read);
                read =reader.readLine();
            }
            
            weblogic.addRegions(sb.toString());
        }
   %>
   <br />
    <div style="font-weight: bold">Edit Regions:</div>
    <form id="regionForm" action="webui.jsp" method="POST">
        <input id="regionForm_id" type="hidden" name="changedId" />
        <input id="regionForm_method" type="hidden" name="method" />
        <input id="regionForm_externalId" type="hidden" name="externalId" />
        <input id="regionForm_longitude" type="hidden" name="longitude" />
        <input id="regionForm_latitude" type="hidden" name="latitude" />
    </form>
    
     <%
            String updateId = request.getParameter("changedId");
            String method = request.getParameter("method");
            if (updateId != null && method != null)
            {
                if (method.equals("delete"))
                {
                    // TODO delete
                    String regionId = updateId;
                    weblogic.deleteRegion(regionId);
                }
                else
                {
                    String regionId = updateId;
                    String externalId = request.getParameter("externalId");
                    String longitude = request.getParameter("longitude");
                    String latitude = request.getParameter("latitude");
                    
                    weblogic.updateRegion(regionId, externalId, longitude, latitude);
                }
                out.print("update Id is " + updateId);
                out.print(" method is " + method);
            }
        %>
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
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getId()), "externalId", region.getExternal_id());
                    input = weblogic.getEditField(String.valueOf(region.getId()), "externalId", region.getExternal_id());
                    out.print("<td>" + div + input + "</td>");
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getId()), "longitude", String.valueOf(region.getLongitude()));
                    input = weblogic.getEditField(String.valueOf(region.getId()), "longitude", String.valueOf(region.getLongitude()));
                    out.print("<td>" + div + input + "</td>");
                    div = weblogic.getReadOnlyDiv(String.valueOf(region.getId()), "latitude", String.valueOf(region.getLatitude()));
                    input = weblogic.getEditField(String.valueOf(region.getId()), "latitude", String.valueOf(region.getLatitude()));
                    out.print("<td>" + div + input + "</td>");
                    
                    //buttons
                    out.print("<td> <input type='button' id='edit_" + region.getId() + "' value='edit' onClick='editRow(" + region.getId() + ");'");
                    out.print("<td> <input type='button' id='delete_" + region.getId() + "' value='delete' onClick='prepareAndSubmit(" + region.getId() + ", true);'");
                    out.print("<td> <input class='commit' type='button' id='commit_" + region.getId() + "' value='commit' onClick='prepareAndSubmit(" + region.getId() + ", false);'");
                    out.print("<td> <input class='cancel' type='button' id='cancel_" + region.getId() + "' value='cancel' onClick='cancelEditRow(" + region.getId() + ");'");
                    out.print("</tr>");
                }
            %>
        </tbody>
    </table>
        
</body>
</html>
