package com.example.mxaudiorecogition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

public class TimedAudioDispatcher extends AudioDispatcher {

    public TimedAudioDispatcher(TarsosDSPAudioInputStream stream, int audioBufferSize, int bufferOverlap) {
        super(stream, audioBufferSize, bufferOverlap);
    }

    public void run(int timeout, TimeUnit unit) throws TimeoutException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            service.submit(() -> run()).get(timeout, unit);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            // Do nothing here!
        }
        service.shutdownNow();
        if (!isStopped()) {
            stop();
            throw new TimeoutException("Timeout reached while generating fingerprints");
        }
    }

}
