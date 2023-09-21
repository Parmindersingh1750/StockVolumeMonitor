<%-- 
    Document   : clientcallapidashboard
    Created on : Nov 6, 2016, 2:20:09 AM
    Author     : Punit
    referred following link for constructing an html table: http://www.w3schools.com/tags/tryit.asp?filename=tryhtml_table_border
    This page displays the attributes of the request response when our rest service is called from the client.
    The options and the contents of the page are self explanatory.
--%>
<%@page import="org.bson.types.ObjectId"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Client Call API Logs</title>
    </head>
    <body>
        <form action="submit" method="POST">
                 Select the Log Information you want to see:<br>      
                 <input type="radio" name="choice" value="RunOperationAnalytics">Run Operation Analytics<br>
                 <input type="radio" name="choice" value="ClientAPILogs">See Client API Info<br>
                 <input type="radio" name="choice" value="ThirdPartyAPILogs">See Third Party API Info<br>
                 <input type="radio" name="choice" value="UnSupportedTickerCallLogs">See Unsupported Ticker Calls<br><br>
                 <input type="submit" value="submit">             
        </form>
        <h1>Client Call API Logs</h1><br>
        <table border="1"><tr>
        <th>Request ID</th>
        <th>Request Timestamp</th>
	<th>Response Timestamp</th>
        <th>Ticker</th>
        <th>User Device</th>
        <th>User Name</th> 
	<th>Average Volume</th>
        <th>Total Volume</th>
        </tr>
        <% //following code goes through each element in the list received from the backend and shows the contents in a tables
            List<Map<String, String>> list=(List)request.getAttribute("ClientAPILogs");
           for (Map map : list) {
               ObjectId objectID=(ObjectId)map.get("_id");
               %><tr><td><%= objectID.toString()%></td>
                   <td><%= map.get("requestTimeStamp")%></td>
                   <td><%= map.get("responseTimeStamp")%></td>
                   <td><%= map.get("ticker")%></td>
                   <td><%= map.get("userDevice")%></td>
                   <td><%= map.get("userName")%></td>
                   <td><%= map.get("responseAvgVolume")%></td>
                   <td><%= map.get("totalVolume")%></td>
               </tr>
               <%} %>
        </table>
        <br>
    </body>
</html>
