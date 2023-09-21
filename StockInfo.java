package com.example.punit.stocksvolumemonitor;

/**
 * This is a POJO object only used to pass the information between MainActivity and GetVolumes classes
 * Created by Punit on 10/31/2016.
 */

public class StockInfo {
    private String ticker;//String to store the ticker
    private String volumeChange;//string to store the average volume change
    private String totalVolume;//string to store the total volume

    /**
     * Constructor to initialize the StockInfo object
     * @param ticker
     * @param volumeChange
     * @param totalVolume
     */
    public StockInfo(String ticker,String volumeChange,String totalVolume)
    {
        this.ticker=ticker;//set the ticker
        this.volumeChange=volumeChange;//set the volume change
        this.totalVolume=totalVolume;//set the total volume
    }

    /**
     * Getter to get the volume change
     * @return
     */
    public String getVolumeChange() {
        return volumeChange;
    }

    /**
     * setter to set the volume change
     */
    public void setVolumeChange(String volumeChange) {
        this.volumeChange = volumeChange;
    }

    /**
     * Getter to get the ticker
     * @return
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * setter to set the ticker
     */
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    /**
     * Getter to get the total volume
     * @return
     */
    public String getTotalVolume() {
        return totalVolume;
    }

    /**
     * setter to set the total volume
     * @return
     */
    public void setTotalVolume(String totalVolume) {
        this.totalVolume = totalVolume;
    }

    /**
     * Override the toString to compose the string for StockInfo as concatenation of class attributes
     * @return
     */
    @Override
    public String toString()
    {
        return ticker+":"+volumeChange+",total Volume:"+totalVolume;
    }
}
