/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Following is the servlet which will be called from the pages for the dashboard. It serves as controller and calls ReadLogInformationModel
 * @author Punit
 */

@WebServlet(name = "ReadLogInformationServlet", urlPatterns = {"/submit", "/getThirdPartyInformation"})
public class ReadLogInformationServlet extends HttpServlet {
    
    ReadLogInformationModel model;//create an object for the model
    
   @Override
    public void init() throws ServletException {   
        super.init();
        model=new ReadLogInformationModel();//initialize the model object
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
        response.setContentType("text/html;charset=UTF-8");//set the response contents as text/html
        String logRequest=request.getParameter("choice");//read the choice selected from the web page
        List list=null;//initialize an empty list
        if(logRequest.equals("ThirdPartyAPILogs"))//if the choice selected is for Third party call logs
        {
            list=model.readThirdPartyAPIInfo();//then read the model to fetch the third party api logs
            request.setAttribute("ThirdPartyLogList", list);//assign the list to the attribute in the request
            RequestDispatcher view = request.getRequestDispatcher("thirdpartyapidashboard.jsp");//set the result page
            view.forward(request, response);//forward the control to the result page
        }
        else if(logRequest.equals("ClientAPILogs"))//if the choice selected is for client api logs
        {
            list=model.readClientAPIInfo();//then read the model to fetch the client api information
            request.setAttribute("ClientAPILogs", list);//assign the list to the attribute in the request
            RequestDispatcher view = request.getRequestDispatcher("clientcallapidashboard.jsp");//set the result page
            view.forward(request, response);//forward the control to the result page
        }
        else if(logRequest.equals("UnSupportedTickerCallLogs"))//if the choice selected is for unsupported ticker logs
        {
            list=model.readUnsupportedTickerInfo();//then read the model to fetch the unsupported ticket information from the logs
            request.setAttribute("UnSupportedTickerCallLogs", list);//assign the list to the attribute in the request
            RequestDispatcher view = request.getRequestDispatcher("unsupportedtickercalldashboard.jsp");//set the result page
            view.forward(request, response);//forward the control to the result page
        }
        else if(logRequest.equals("RunOperationAnalytics"))//if the choice selected is for deducing interesting analytics from the logs
        {
            Map<String,String> map=model.performAnalyticalOperations();//then call the model to run and return the analytical results of the logs
            request.setAttribute("OperationAnalytics", map);//assign the map to the attribute in the request
            RequestDispatcher view = request.getRequestDispatcher("operationanalytics.jsp");//set the result page
            view.forward(request, response);//forward the control to the result page
        }
        
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
        processRequest(request, response);
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

}
