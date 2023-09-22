
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * MyInhouseLogger - this class will serve as the model to store the logging information the mongo DB
 * @author Parminder and Harkirat
 * Resolved class not found issues after referring to:
 * http://stackoverflow.com/questions/29729331/how-to-resolve-classnotfoundexception-com-mongodb-connection-bufferprovider
 * 
 *  
 * 
 */
public class MyInhouseLogger {
    //URL to the mongoDB
    private static final String dbURL="mongodb://dummyuser:dummypassword@ds145667.mlab.com:45667/mydsproject";
    private DB db=null;//object to store the DB connection
    
    /**
     * Referred to mkyong to learn the usage of mongoDB API
     * Citation: https://www.mkyong.com/mongodb/java-mongodb-insert-a-document/
     * http://www.mkyong.com/mongodb/java-mongodb-hello-world-example/
     */
    public MyInhouseLogger()
    {
        MongoClientURI uri  = new MongoClientURI(dbURL); //create a client URI to the mongoDB
        MongoClient client = new MongoClient(uri);//create the client to the mongo DB
	db = client.getDB(uri.getDatabase());// assign the connection to the db object
    }
    
    /**
     * following method delegates the logging to different methods based on the entity which is being logged
     * @param collectionType
     * @param data 
     */
    public void log(String collectionType,Map<String,String> data)
    {
        if("ClientAPIInfo".equals(collectionType))//if the information is about the API exposed to client
        {
            logClientAPIInfo("ClientAPIInfo",data);//then log its information to corresponding table(collection)
        }
        else if ("ThirdPartyAPIInfo".equals(collectionType))//if the information is about the third party call
        {
            logThirdPartyAPIInfo(data);//then log its information to corresponding table(collection)
        }
        else if("UnSupportedTickerInfo".equals(collectionType))//if the information is about the unsupported ticker call
        {
            logClientAPIInfo("UnSupportedTickerInfo",data);//then log its information to corresponding table(collection)
        }
    }
    
    /**
     * Saves the information to the collection in mongoDB
     * @param collectionName
     * @param data 
     */
    public void logClientAPIInfo(String collectionName, Map<String,String> data)
    {
        DBCollection table=db.getCollection(collectionName);//create a collection object of the mongo DB
        BasicDBObject object=new BasicDBObject();//create an object to be saved in DB
        object.put("requestTimeStamp", data.get("requestTimeStamp"));//retrieve the request timestamp and save in object
        object.put("responseTimeStamp", data.get("responseTimeStamp"));//retrieve the response timestamp and save in object
        object.put("ticker", data.get("ticker"));   //retrieve the ticker and save in object
        object.put("userDevice", data.get("userDevice"));//retrieve the userDevice and save in object
        object.put("userName", data.get("userName"));//retrieve the username and save in object
        if("ClientAPIInfo".equals(collectionName))//if the information is about the success call from client then save the responded average volume and total volume as well
        {
            object.put("responseAvgVolume",data.get("responseAvgVolume"));//retrieve and save the average volume
            object.put("totalVolume",data.get("responseTotalVolume")); //retrieve and save the total volume     
        }       
        table.insert(object);//insert the object in database
    }
    
    /**
     * Log the information of the third party api call to the database
     * @param data 
     */
    public void logThirdPartyAPIInfo(Map<String,String> data)
    {
        DBCollection table=db.getCollection("ThirdPartyAPIInfo");//create a collection object of the mongo DB
        BasicDBObject object=new BasicDBObject();//create an object to be saved in DB
        object.put("requestTimeStamp", data.get("requestTimeStamp"));//retrieve the request timestamp and save in object
        object.put("responseTimeStamp", data.get("responseTimeStamp"));//retrieve the response timestamp and save in object
        object.put("ticker", data.get("ticker"));//retrieve the ticker and save in object
        object.put("volume", data.get("volume"));   //retrieve and save the total volume    
        table.insert(object);  //insert the object in database  
    }
}
