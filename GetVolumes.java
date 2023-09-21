package com.example.punit.stocksvolumemonitor;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Backend class to be called from MainActivity
 * Created by Punit on 10/30/2016.
 */

public class GetVolumes {

    MainActivity mainActivity=null;//Instance of the MainActivity

    /**
     * Method to be called from MainActivity
     * @param ticker
     * @param mainActivity
     */
    public void search(String ticker, MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;//set the main activity for the thread
        new AsyncTickerVolumeQuery().execute(ticker);//call the asynchronous thread with the input
    }

    /**
     * Class to create a separate thread for the search in the background
     */
    private class AsyncTickerVolumeQuery extends AsyncTask<String, Void, StockInfo>
    {
        /**
         * Method to do the search in the background
         * @param urls
         * @return
         */
        protected StockInfo doInBackground(String... urls) {
            return search(urls[0]);
        }

        /**
         * Method to call the main activity with the information fetched from the network
         * @param volumeInfo
         */
        protected void onPostExecute(StockInfo volumeInfo) {
            mainActivity.getVolume(volumeInfo);
        }

        /**
         * Search method - called in background
         * @param ticker
         * @return
         */
        private StockInfo search(String ticker)
        {
            StockInfo stockInfo =null;//initialize a null stockInfo handler

            try {
                //fetch the stock info from the servlet deployed in heroku
                stockInfo = getStockInfoFromJSON("https://dry-retreat-37615.herokuapp.com/FinanceWebService///"+ticker);
                //stockInfo = getStringFromJSON("http://10.0.0.67:8011/Project4Task2/FinanceWebService///"+ticker);

            } catch (Exception e) {
                    e.printStackTrace();
            }
            return stockInfo;//return the stock info
        }

        /**
         * This method fetches the information from the urlStr and converts the JSON object returned to StockInfo
         * @param urlStr
         * @return
         * @throws Exception
         */
        private StockInfo getStockInfoFromJSON(String urlStr) throws Exception {
            URL url = new URL(urlStr);//create a URL from urlStr
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();//open the connection
            connection.setRequestMethod("GET");//set the method to be GET method

            String volume=null;//initialize volume string
            String ticker=null;//initialize ticker string
            String totalVolume=null;//initialize totalVolume string
            try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));//wrap the connection with InputStream and then with Buffered Reader
            String responseMessage=null;//initialize the response Message object

            JsonReader reader= javax.json.Json.createReader(br);//wrap the buffered reader with Json reader
            JsonObject obj=reader.readObject();//read the json object from reader

            volume=obj.getString("volumeChange");//fetch the average volume change
            ticker=obj.getString("ticker");//fetch the ticker symbol
            totalVolume=obj.getString("totalVolume");//fetch the total volume
            } catch (Exception e) {
                e.printStackTrace();
            }

            connection.disconnect();//disconnect the connection
            return new StockInfo(ticker,volume,totalVolume);//create a new stockInfo object and return it
        }
    }

}
