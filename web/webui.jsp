<%-- 
    Document   : configure
    Created on : 08.01.2014, 13:50:39
    Author     : rafael
--%>

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
</head>
<body>
DeliverySystem WebGUI!<br/>

 <form action="configure.jsp" method="POST">
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
    <!-- manual package shipping -->
    <form action="configure.jsp" method="POST">
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
</body>
</html>
