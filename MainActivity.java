package com.example.punit.stocksvolumemonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity class
 */
public class MainActivity extends AppCompatActivity {
    //list to store the supported tickers
    List<String> supportedTickers=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add the four tickers supported currently by the application
        supportedTickers.add("MSFT");//Microsoft
        supportedTickers.add("TSLA");//Tesla
        supportedTickers.add("DIS");//Disney
        supportedTickers.add("AAPL");//Apple

        final MainActivity ma = this;//set the main activity


        Button submitButton = (Button)findViewById(R.id.submit);//find the submit button


        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {

                GetVolumes gv = new GetVolumes();//create an instance of the GetVolumes class

                TextView searchView = (EditText)findViewById(R.id.editText);//fetch the editText for user input
                TextView messageText = (TextView)findViewById(R.id.textView6);//fetch the message textview
                if(!supportedTickers.contains(searchView.getText().toString()))//if the ticker input is not supported
                {
                    messageText.setText("Unsupported Ticker");//set the message as unsupported
                }
                else
                {
                    messageText.setText("");//else clear the message in any consecutive request
                }

                gv.search(searchView.getText().toString(), ma); // Call search method to do the asynchronous search
            }
        });
    }

    /*
        method to be called from GetVolumes to return back the volumes of stocks
     */
    public void getVolume(StockInfo stockInfo)
    {
        if(stockInfo!=null) {//if there is stock info returned from the backend
            TextView textView3 = (TextView) findViewById(R.id.textView3);//then fetch the textView3
            textView3.setText(stockInfo.getVolumeChange());//show the average change of volume on it

            TextView textView5 = (TextView) findViewById(R.id.textView5);//fetch the textView3
            textView5.setText(stockInfo.getTotalVolume());//show the total volume for the day on it
        }
    }
}
