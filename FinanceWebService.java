/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  This is the servlet class which will be invoked from the client. It acts as a controller in the flow.
 * @author Punit
 */

@WebServlet(name = "FinanceWebService", urlPatterns = {"/FinanceWebService/*"})
public class FinanceWebService extends HttpServlet {
    
    //declare a model to communicate with backend
    GetFinanceInfoModel model=null;
    
    //create an instance of the logger. Logger acts as a model
    MyInhouseLogger logger=new MyInhouseLogger();
    
    //create a list of supported tickers
    ArrayList<String> supportedTickers=new ArrayList<String>();
    
     /**
     * Initialize the information of four supported tickers when servlet initializes
     * @throws ServletException 
     */
    @Override
    public void init() throws ServletException {     
        super.init();
        //add four supported tickets to list
        supportedTickers.add("MSFT");//add Microsoft
        supportedTickers.add("AAPL");//add Apple
        supportedTickers.add("DIS");//add Disney
        supportedTickers.add("TSLA");// add Tesla
 
        model=new GetFinanceInfoModel();//assign a model
        model.initialize("MSFT");//initialize Microsoft
        model.initialize("AAPL");//initialize Apple
        model.initialize("DIS");//Initialize Disney
        model.initialize("TSLA");//Initialize Tesla   
    }
            

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestTimeStamp=getTimeStamp();//create a timestamp for the request from the client for the logs
        String userDevice = request.getHeader("User-Agent");//get the user device from which request was received
        String ticker = (request.getPathInfo()).substring(1);//fetch the ticker ID which is the first element in GET request
        if(!supportedTickers.contains(ticker))//if the requested ticker information is for unsupported ticker
        {
            PrintWriter out = response.getWriter();//open output stream
            response.setStatus(404);//set the error code
            logUnSupportedTickerInfo(userDevice,requestTimeStamp,ticker);//log the unsupported ticker
            out.flush();//flush the response stream
            return;//return
        }
        
        //JsonArray arr=null;
        JsonObject obj=null;
        try {
            obj= model.getTickerInfo(ticker);//get the ticker information from the model
        } catch (Exception ex) {
            Logger.getLogger(FinanceWebService.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrintWriter out = response.getWriter();//open output stream
        response.setStatus(200);//set the response to 200
        response.setContentType("application/json");//set the response type to json
        out.print(obj);//return the results on output stream  
        out.flush();//flush the response stream

        logSuccessAPIInfo(userDevice,requestTimeStamp,obj);//log the client request to the database
        
    }

    /**
     * When there is a successful response, following method is called to log the information in the database using model
     * @param userDevice
     * @param requestTimeStamp
     * @param obj 
     */
    public void logSuccessAPIInfo(String userDevice,String requestTimeStamp,JsonObject obj)
    {
        Map<String,String> clientAPIInfo=new HashMap<String,String>();//create a map 
        clientAPIInfo.put("userDevice", userDevice);//add the user device info
        clientAPIInfo.put("requestTimeStamp", requestTimeStamp);//add the request time stamp
        String responseTimeStamp=getTimeStamp();//create another timestamp. This will be response timestamp
        clientAPIInfo.put("responseTimeStamp", responseTimeStamp);//add the response time stamp to map
        clientAPIInfo.put("ticker", obj.getString("ticker"));//get the ticker and add it to map
        clientAPIInfo.put("responseAvgVolume", obj.getString("volumeChange"));//add the volume change
        clientAPIInfo.put("responseTotalVolume", obj.getString("totalVolume"));//add the volume change
        clientAPIInfo.put("userName", "Beta User");//set the user to be beta-user as it is the beta version of API
        logger.log("ClientAPIInfo", clientAPIInfo);//call the logger to save the information in database
    }
    
    /**
     * When there is an unsuccessful response, following method is called to log the information in the database using model
     * @param userDevice
     * @param requestTimeStamp
     * @param ticker 
     */
    public void logUnSupportedTickerInfo(String userDevice, String requestTimeStamp, String ticker)
    {
        Map<String,String> unSupportedTickerInfo=new HashMap<String,String>();//create a map 
        unSupportedTickerInfo.put("userDevice", userDevice);//add the user device info
        unSupportedTickerInfo.put("requestTimeStamp", requestTimeStamp);//add the request time stamp
        String responseTimeStamp=getTimeStamp();//create another timestamp. This will be response timestamp
        unSupportedTickerInfo.put("responseTimeStamp", responseTimeStamp);//add the response time stamp to map
        unSupportedTickerInfo.put("ticker", ticker);//get the ticker and add it to map
        unSupportedTickerInfo.put("userName", "Beta User");//set the user to be beta-user as it is the beta version of API
        logger.log("UnSupportedTickerInfo", unSupportedTickerInfo);//call the logger to save the information in database
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

     /**
     * Following method will return the latest timestamp
     * @return 
     */
    private static String getTimeStamp()
    {
        Date date= Calendar.getInstance().getTime(); //get the date object
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");//specify the timestamp format
        return timeStampFormat.format(date);  //return the timestamp formatted from current time
    }

}
