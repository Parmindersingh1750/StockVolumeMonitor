
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Following class is the model to fetch the details from the third party API
 * @author Punit
 */
public class GetFinanceInfoModel {
    
    //create an instance of the logger
    MyInhouseLogger logger=new MyInhouseLogger();
    
    //Map to store the details of the stock temporarily for future deductions
    Map<String, TreeMap<String,Integer>> history=new HashMap<String,TreeMap<String,Integer>>();
    
    /**
     * Initialize the map with information for the four ticker symbols supported by the android APP
     * @param ticker 
     */
    public void initialize(String ticker)
    {
        Map<String,String> loggerMap=new HashMap<String,String>();//create a map to save the log information
        JsonArray arr=null;
        
        try {
            arr = callAPI(ticker,loggerMap);//call the method to get the details of the ticker
        } catch (Exception ex) {
            Logger.getLogger(GetFinanceInfoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        addInfoToHistory(arr,loggerMap);//add the details of the ticker to the log map
        logger.log("ThirdPartyAPIInfo", loggerMap);//log the third party API call initialization information to the database
    }
    
    /**
     * This method will be called from the client flow
     * @param ticker
     * @return
     * @throws Exception 
     */
    public JsonObject getTickerInfo(String ticker) throws Exception
    {
        Map<String,String> loggerMap=new HashMap<String,String>();//create a map to save the log information
        JsonArray arr=callAPI(ticker,loggerMap);//call the API for the ticker symbol received from the client
        
        JsonObject infoObject=doComputationsOnHistory(ticker,arr,loggerMap);//compute the volume information and assign it to json object
        addInfoToHistory(arr,loggerMap);//add the latest ticker information to the map
        logger.log("ThirdPartyAPIInfo", loggerMap);//log the third party API call information to the database
        return infoObject;//return the json object
	
    }
    
    /**
     * This method uses the current volume data and compares it with volume stored previously to make the inferences
     * @param ticker
     * @param arr
     * @return 
     */
    public JsonObject doComputationsOnHistory(String ticker,JsonArray arr,Map<String,String> loggerMap)
    {
        TreeMap<String,Integer> map=history.get(ticker);//get the stored data for the ticker
        Map.Entry<String,Integer> entry=map.lastEntry();//get the last entry 
        int lastRecordedVolume=entry.getValue(); //get the last recorded volume 
        String lastRecordedTimeStr=entry.getKey();//get the last recorded timestamp
        SimpleDateFormat dateformatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");//format string for date
        Date lastRecordedTime=null;
        try {
            lastRecordedTime=dateformatter.parse(lastRecordedTimeStr);//get the last recorded time
        } catch (ParseException ex) {
            Logger.getLogger(GetFinanceInfoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return computeAverageVolume(lastRecordedVolume, lastRecordedTime,arr,loggerMap);//call the method to do the mathematics     
    }
    
    /**
     * Method to compute the average volume since last calculation
     * @param lastRecordedVolume
     * @param lastRecordedTime
     * @param arr
     * @return 
     */
    private JsonObject computeAverageVolume(int lastRecordedVolume,Date lastRecordedTime,JsonArray arr,Map<String,String> loggerMap)
    {
        Date currDateTime= Calendar.getInstance().getTime(); //get the date object
        long timeDifference=currDateTime.getTime()-lastRecordedTime.getTime(); //get the time difference between last saved
        int timeDifferenceInSecs=(int)timeDifference/1000;//convert it into secods
        JsonObject jsonobject = arr.getJsonObject(0);//get the ticker information from array    
        String currVolume=jsonobject.getString("volume");//get the volume from the json object 
        loggerMap.put("volume", currVolume);//add the volume information the logger map
        String ticker=jsonobject.getString("symbol");//get the ticker symbol
        int volumeChange=Integer.valueOf(currVolume)-lastRecordedVolume;//calculate the volume change
        int volumeChangePerSec=volumeChange/timeDifferenceInSecs;//divide it with time to get the average
        JsonBuilderFactory factory = Json.createBuilderFactory(null);//pack the information into json object
        JsonObject value = factory.createObjectBuilder()//following code stores the information in JsonObject value
        .add("volumeChange", new String(volumeChangePerSec+""))
        .add("totalVolume", currVolume)
        .add("ticker", ticker).build();
        return value;//return the information in json object       
    }
    
    /**
     * This method will call the external API
     * @param ticker
     * @return
     * @throws Exception 
     */
    public JsonArray callAPI(String ticker,Map<String,String> loggerMap) throws Exception
    {
        //URL of the API (Note: the token is dummy. API works fine without populating the token)
        URL url = new URL("http://www.enclout.com/api/v1/yahoo_finance/show.json?auth_token=xxxxxx&text="+ticker);
        
        String requestTimeStamp=getTimeStamp();//get the timestamp before calling the API
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//make a connection
        connection.setRequestProperty("User-Agent", //set the client type
                        "Mozilla/5.0 (Linux; Android 6.0; MotoE2(4G-LTE) Build/MPI24.65-39) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.81 Mobile Safari/537.36");
        connection = (HttpURLConnection) url.openConnection();// open the connection
        connection.setRequestMethod("GET");//set the request method to GET
        connection.setRequestProperty("Accept", "text/plain");// set the request type

        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));//read the response
        String responseTimeStamp=getTimeStamp();//create the timestamp. it will serve as the timestamp for the response from third party
        
        loggerMap.put("requestTimeStamp", requestTimeStamp);// put the request timestamp in the map for logging
        loggerMap.put("responseTimeStamp", responseTimeStamp);// put the response timestamp in the map for logging
        loggerMap.put("ticker", ticker);//put the ticker symbol in logging
        
        
        
        JsonReader reader=Json.createReader(br);//wrap the buffered reader with the json reader
        JsonArray arr=reader.readArray();//read the information in the JsonArray object
        
        return arr;//return the json array
    }
    
    /**
     * Following method adds the json information to the history
     * @param arr 
     */
    public void addInfoToHistory(JsonArray arr,Map<String,String> loggerMap)
    {
         for(int i=0;i<arr.size();i++)//go through each element of the json array
        {
            JsonObject jsonobject = arr.getJsonObject(i);//read an object from the array 
            String volume=jsonobject.getString("volume");//read the volume from the object
            String ticker=jsonobject.getString("symbol");//read the ticker symbol from the object
            String timestamp=getTimeStamp();//generate a timestamp
            TreeMap<String,Integer> treeMap=null;//initialize a TreeMap
            if(history.containsKey(ticker))// if the history already contains the information for this ticker
            {
                treeMap=history.get(ticker);//then fetch the corresponding value i.e. corresponding treeMap
            }
            else
            {
                treeMap=new TreeMap();//else create a new one
            }
            treeMap.put(timestamp, Integer.valueOf(volume));//put the information inside that treeMap
            history.put(ticker, treeMap);//store the treeMap in the history map
            loggerMap.put("volume", volume);//add the volume to the logging
        }
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
