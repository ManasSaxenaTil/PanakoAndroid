package com.example.mxaudiorecogition.stats;

public class Stats {
    public double avgTime;
    public long maxTimeTaken;
    public long minTime;
    public int sampleTime;
    public int numberOfSongs;
    public int avgNumberOfHashes;


    public Stats(double avgTime,long maxTimeTaken,int minTime, int sampleTime,int numberOfSongs,int avgNumberOfHashes){
        this.avgTime=avgTime;
        this.maxTimeTaken = maxTimeTaken;
        this.minTime = minTime;
        this.sampleTime= sampleTime;
        this.numberOfSongs=numberOfSongs;
        this.avgNumberOfHashes = avgNumberOfHashes;

    }
}
