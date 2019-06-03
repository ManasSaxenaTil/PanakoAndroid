package com.example.mxaudiorecogition.com.mxaudiorecognition.client;


public class FingerprintData {

    private int hash;

    private int time;

    private int timeDelta;

    private int frequency;

    private FingerprintData() {
        super();
    }

    public static FingerprintData createData(int hash, int time, int timeDelta, int frequency) {
        FingerprintData fd = new FingerprintData();
        fd.hash = hash;
        fd.time = time;
        fd.timeDelta = timeDelta;
        fd.frequency = frequency;
        return fd;
    }

    public int getHash() {
        return hash;
    }

    public int getTime() {
        return time;
    }

    public int getTimeDelta() {
        return timeDelta;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frequency;
        result = prime * result + hash;
        result = prime * result + time;
        result = prime * result + timeDelta;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FingerprintData other = (FingerprintData) obj;
        if (frequency != other.frequency)
            return false;
        if (hash != other.hash)
            return false;
        if (time != other.time)
            return false;
        if (timeDelta != other.timeDelta)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FingerprintData [hash=");
        builder.append(hash);
        builder.append(", time=");
        builder.append(time);
        builder.append(", timeDelta=");
        builder.append(timeDelta);
        builder.append(", frequency=");
        builder.append(frequency);
        builder.append("]");
        return builder.toString();
    }

}
