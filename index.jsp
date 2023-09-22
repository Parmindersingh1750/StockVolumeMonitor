<%-- 
    Document   : index.jsp
    Created on : Sep 11, 2016, 12:39:17 AM
    Author     : Parminder and Harkirat
    Description: Contains the first page with the selection options for the user
    The options and the contents of the page are self explanatory.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DS Clicker</title>
    </head>
    <body>
        <h1>Stock Volume Monitor Dashboard</h1>
        
         <form action="submit" method="POST">
                 Select the Log Information you want to see:<br>    
                 <input type="radio" name="choice" value="RunOperationAnalytics">Run Operation Analytics<br>
                 <input type="radio" name="choice" value="ClientAPILogs">See Client API Info<br>
                 <input type="radio" name="choice" value="ThirdPartyAPILogs">See Third Party API Info<br>
                 <input type="radio" name="choice" value="UnSupportedTickerCallLogs">See Unsupported Ticker Calls<br><br>
                 <input type="submit" value="submit">             
        </form>
    </body>
</html>
