package com.example.mxaudiorecogition;

public class FingerprintData {

    private int hash;

    private int time;

    public FingerprintData() {
        super();
    }

    public FingerprintData(int hash, int time) {
        super();
        this.hash = hash;
        this.time = time;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hash;
        result = prime * result + time;
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
        if (hash != other.hash)
            return false;
        if (time != other.time)
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
        builder.append("]");
        return builder.toString();
    }
}

