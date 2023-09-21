<%-- 
    Document   : operationanalytics
    Created on : Nov 6, 2016, 3:20:02 AM
    Author     : Punit
    This page shows the values of any interesting operation analytics can could be useful for the administration.
    The options and the contents of the page are self explanatory.
--%>

<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
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
        <h1>Operation Analytics Results</h1>
        <table border="1"><tr>
        <th>Parameter</th>
        <th>Value</th>
        </tr>
        <% //following code reads the informaton from the Map received from the backend and shows the contents in a table
            Map<String, String> map=(Map)request.getAttribute("OperationAnalytics");%>
        <tr><td>Most Frequent Unsupported Ticker</td><td><%= map.get("frequentUnsupportedTicker")%></td></tr>
        <tr><td>Most Frequent Supported Ticker</td><td><%= map.get("mostUsedValidTicker")%></td></tr>
        <tr><td>Most Popular Client</td><td><%= map.get("mostPopularUserDevice")%></td></tr>
        <tr><td>Average Client API Latency(in milliseconds)</td><td><%= map.get("averageClientAPILatency")%></td></tr>
        <tr><td>Average Third Party API Latency(in milliseconds)</td><td><%= map.get("averageThirdPartyAPILatency")%></td></tr>
        </table>
    </body>
</html>
