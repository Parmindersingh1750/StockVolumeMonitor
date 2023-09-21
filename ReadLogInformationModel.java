
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This the the model class that will read information for the dashboard. This
 * class will be used only for the reading from the database.
 *
 * @author Punit Citation:
 * https://www.mkyong.com/mongodb/java-mongodb-insert-a-document/
 * http://www.mkyong.com/mongodb/java-mongodb-hello-world-example/
 */
public class ReadLogInformationModel {

    private static final String DBURL = "mongodb://dummyuser:dummypassword@ds145667.mlab.com:45667/mydsproject";

    //following map will store the third party information retrieved from the database
    private List<Map> thirdPartyAPIInfo = new ArrayList<Map>();

    //following map will store the client API call information retrieved from the database
    private List<Map> clientAPIInfo = new ArrayList<Map>();

    //following map will store the client API call information for unsupported tickers retrieved from the database
    private List<Map> unSupportedTickerInfo = new ArrayList<Map>();

    private DB db = null;//DB object

    /**
     * Constructor to initialize the URLs and the DB object
     */
    public ReadLogInformationModel() {
        MongoClientURI uri = new MongoClientURI(DBURL);//create a URI for the mongoDB
        MongoClient client = new MongoClient(uri);//create a client for the mongoDB
        db = client.getDB(uri.getDatabase());//create the database object
    }

    /**
     * following method return the third party call logs in a list
     *
     * @return
     */
    public ArrayList readThirdPartyAPIInfo() {
        DBCollection table = db.getCollection("ThirdPartyAPIInfo");//create an instance of the collection
        DBCursor cursor = table.find();//assign a db cursor to the collection
        ArrayList<Map<String, String>> list = new ArrayList();//create a list
        while (cursor.hasNext()) {//go through the entire collection and populate the information in the list
            list.add((Map) cursor.next());//and populate the information in the list
        }
        return list;//return the list
    }

    /**
     * following method return the client API call logs in a list
     *
     * @return
     */
    public ArrayList readClientAPIInfo() {
        DBCollection table = db.getCollection("ClientAPIInfo");//create an instance of the collection
        DBCursor cursor = table.find();//assign a db cursor to the collection
        ArrayList<Map<String, String>> list = new ArrayList();//create a list
        while (cursor.hasNext()) {//go through the entire collection and populate the information in the list
            list.add((Map) cursor.next());//and populate the information in the list
        }
        return list;//return the list
    }

    /**
     * following method return the client API call logs for unsupported ticket
     * in a list
     *
     * @return
     */
    public ArrayList readUnsupportedTickerInfo() {
        DBCollection table = db.getCollection("UnSupportedTickerInfo");//create an instance of the collection
        DBCursor cursor = table.find();//assign a db cursor to the collection
        ArrayList<Map<String, String>> list = new ArrayList();//create a list
        while (cursor.hasNext()) {//go through the entire collection and populate the information in the list
            list.add((Map) cursor.next());//and populate the information in the list
        }
        return list;//return the list
    }

    /**
     * following method returns any interesting information based on the data
     * retrieved from the database
     *
     * @return
     */
    public Map<String, String> performAnalyticalOperations() {
        List<Map> clientInfoList = readClientAPIInfo();//read the client API information from the logs
        List<Map> thirdPartyAPIInfoList = readThirdPartyAPIInfo();     //read the third party api information from the logs
        Map<String, String> map = new HashMap<>();//create an empy map

        String frequentUnsupportedTicker = getMostUsedUnsupportedTicker();    //call the method to retrieve the most called unsupported ticker
        map.put("frequentUnsupportedTicker", frequentUnsupportedTicker);//save it in the map

        String mostUsedValidTicker = getMostUsedValidTicker(clientInfoList);//call the method to retrieve the most called supported ticker
        map.put("mostUsedValidTicker", mostUsedValidTicker);//save it in the map

        String mostPopularUserDevice = getMostPopularUserDevice(clientInfoList);//call the method to deduce the most popular user device
        map.put("mostPopularUserDevice", mostPopularUserDevice);//save it in the map

        long averageClientAPILatency = getAverageAPILatency(clientInfoList);//call the method to retrieve the client API latency
        map.put("averageClientAPILatency", Long.toString(averageClientAPILatency));//save it in the map

        long averageThirdPartyAPILatency = getAverageAPILatency(thirdPartyAPIInfoList);//call the method to retrieve the third party api latency
        map.put("averageThirdPartyAPILatency", Long.toString(averageThirdPartyAPILatency));//save it in the logs

        return map;//return the map to the servlet
    }

    /**
     * method to compute the average API latency
     *
     * @param list
     * @return
     */
    public long getAverageAPILatency(List<Map> list) {
        long timeDifference = 0;//initialize the time difference as a long
        for (Map map : list) {//go through each record on the collection list
            Set<String> keys = map.keySet();//get the keys set
            for (String key : keys) {//read one key at a time
                System.out.println(key + ":" + map.get(key));//entered only for debugging purposes      
                String requestTimeStamp = map.get("requestTimeStamp").toString();//read the request time stamp
                String responseTimeStamp = map.get("responseTimeStamp").toString();//read the response time stamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");//declare the format for time
                try {
                    Date requestTimeStampDate = dateFormat.parse(requestTimeStamp);//parse the request timestamp as a date
                    Date responseTimeStampDate = dateFormat.parse(responseTimeStamp);//parse the response timestamp as a date
                    timeDifference = responseTimeStampDate.getTime() - requestTimeStampDate.getTime();//compute the time difference between two          
                } catch (ParseException ex) {//log any parsing exception in console logs
                    Logger.getLogger(ReadLogInformationModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return timeDifference / list.size();//return the average time difference (latency)
    }

    /**
     * Method to retrieve the most used valid ticker
     * @param list
     * @return 
     */
    public String getMostUsedValidTicker(List<Map> list) {

        Map countMap = countTickers(list);//create a map which contains each ticker and the number of times it was called from client
        String mostUsedValidTicker = getKeyWithHighestValue(countMap);//deduce the most called ticker based on data in countMap
        return mostUsedValidTicker;//return the ticker
    }

    /**
     * method to retrieve the most called unsupported ticker
     * @return 
     */
    public String getMostUsedUnsupportedTicker() {
        List<Map> list = readUnsupportedTickerInfo();//read all the data from unsupported ticker call logs
        Map countMap = countTickers(list);//create a map which contains each ticker and the number of times it was called from client
        String mostUsedUnsupportedTicker = getKeyWithHighestValue(countMap);//deduce the most called ticker based on data in countMap
        return mostUsedUnsupportedTicker;//return the ticker
    }

    /**
     * method to retrieve the most used client device
     * @param list
     * @return 
     */
    public String getMostPopularUserDevice(List<Map> list) {
        Map countMap = countDevicesUsed(list);//create a map which contains each device and the number of times it was used from client
        String mostUsedDevice = getKeyWithHighestValue(countMap);//deduce the most used device based on data in countMap
        return mostUsedDevice;//return the device
    }

    /**
     * Method to count the ticker based on log data read from the collection stored in DB
     * @param list
     * @return 
     */
    public Map<String, Integer> countTickers(List<Map> list) {
        Map<String, Integer> countMap = new HashMap<String, Integer>();//create a hashmap
        for (Map map : list) {//go through each element in the collection list
            Set<String> keys = map.keySet();//get the key set
            for (String key : keys) {//go through each key in the set
                System.out.println(key + ":" + map.get(key));//print data for debugging purposes
                if (key.equals("ticker")) {//if the key is named ticker
                    String ticker = map.get(key).toString();//retrieve the value of ticker
                    if (countMap.containsKey(ticker)) {//if the ticker exists in countMap
                        Integer count = countMap.get(ticker);//then retrieve the current count
                        count = count + 1;//and increment it with 1
                        countMap.put(ticker, count);//put the new count back to map
                    } else {
                        countMap.put(ticker, 1);//if the ticker doesn't exist in map before, then just set the count as 1
                    }
                }
            }
        }
        return countMap;//return the countMap
    }

    /**
     * This method accept a map with key and its value that represents the count of times and returns the key which has the highest count
     * @param unsortedMap
     */
    public String getKeyWithHighestValue(Map<String, Integer> unsortedMap) {
        int highestValue = 0;//set the highest count to 0
        String key = null;//initialize the key
        for (Map.Entry<String, Integer> entry : unsortedMap.entrySet()) {//loop through all the entries in the map
            if (entry.getValue() > highestValue) {//if the read entry has highest value
                highestValue = entry.getValue();//then assign the value as new highest value
                key = entry.getKey();// and set the key to current key
            }
        }
        return key;//return the key
    }

    /**
     * following method counts the number of times a device was used based on the records of logs in the list collections
     * @param list
     * @return 
     */
    public Map<String, Integer> countDevicesUsed(List<Map> list) {
        Map<String, Integer> countMap = new HashMap<String, Integer>();//create a countMap 
        for (Map map : list) {//go through each entry in the list
            Set<String> keys = map.keySet();//fetch the key set from the map
            for (String key : keys) {//loop through all the keys in the set
                System.out.println(key + ":" + map.get(key));//print data for debugging purposes
                if (key.equals("userDevice")) {//if the key is "userDevice"
                    String userDevice = map.get(key).toString();//then retrieve the value of user device
                    if (countMap.containsKey(userDevice)) {//if the device exists in the count map
                        Integer count = countMap.get(userDevice);//then fetch the count
                        count = count + 1;//and increment it with 1
                        countMap.put(userDevice, count);//save it back in the map
                    } else {
                        countMap.put(userDevice, 1);//else if the user device wasn't in map before then just set it to 1
                    }
                }
            }
        }
        return countMap;//return the countMap
    }
}
