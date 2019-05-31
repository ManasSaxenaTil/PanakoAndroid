package com.example.mxaudiorecogition;

import com.example.mxaudiorecogition.com.mxaudiorecognition.client.FingerprintData;

import java.util.ArrayList;
import java.util.List;

public class AudioQueryRequest {

    private List<FingerprintData> fingerPrints;

    public AudioQueryRequest() {
        super();
        fingerPrints = new ArrayList<>();
    }

    public AudioQueryRequest(List<FingerprintData> fingerPrints) {
        super();
        this.fingerPrints = fingerPrints;
    }

    public List<FingerprintData> getFingerPrints() {
        return fingerPrints;
    }

    public void ListFingerPrints(List<FingerprintData> fingerPrints) {
        this.fingerPrints = fingerPrints;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AudioQueryRequest [fingerPrintList=");
        builder.append(fingerPrints);
        builder.append("]");
        return builder.toString();
    }

}
